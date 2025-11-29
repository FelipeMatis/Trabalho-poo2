package br.edu.adega.adegamaster.model.dao;

import br.edu.adega.adegamaster.model.domain.Categoria;
import br.edu.adega.adegamaster.model.domain.Produto;
import br.edu.adega.adegamaster.model.domain.TipoProduto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    // Nota: Estes DAOs não precisam ser instanciados aqui se forem usados apenas para a exceção.
    // O ProdutoDAO já carrega os dados de Categoria/TipoProduto via JOIN.
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final TipoProdutoDAO tipoProdutoDAO = new TipoProdutoDAO(); // caso queira usar direto

    // --- Método auxiliar para mapear ResultSet para Produto (Refatoração futura - item 3.1) ---
    // Criar este método é uma refatoração valiosa (Extract Method) para o Trabalho 3.
    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));

        BigDecimal preco = rs.getBigDecimal("preco");
        p.setPreco(preco != null ? preco : BigDecimal.ZERO);

        p.setQuantidade(rs.getInt("quantidade"));
        p.setDescricao(rs.getString("descricao"));

        // Mapeamento de Categoria
        int cid = rs.getInt("categoria_id");
        Categoria cat = null;
        String catNome = rs.getString("categoria_nome");
        String catDesc = rs.getString("categoria_descricao");
        // Verifica se o ID ou Nome da categoria vieram preenchidos (significa que o JOIN funcionou)
        if (cid > 0 && catNome != null) {
            cat = new Categoria(cid, catNome, catDesc);
        }
        p.setCategoria(cat);

        // Mapeamento de TipoProduto
        int tid = rs.getInt("tipo_id");
        String tipoNome = rs.getString("tipo_nome");
        if (tid > 0 && tipoNome != null) {
            TipoProduto tipo = new TipoProduto(tid, tipoNome);
            // p.setTipoProduto(tipo); // Requer que a classe Produto tenha este campo.
        }
        return p;
    }
    // ------------------------------------------------------------------------------------------

    /**
     * Lista todos os produtos — traz também categoria e tipo (se existirem).
     */
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();

        String sql =
                "SELECT p.id, p.nome, p.preco, p.quantidade, p.categoria_id, p.descricao, " +
                        "       c.nome AS categoria_nome, c.descricao AS categoria_descricao, " +
                        "       p.tipo_id, t.nome AS tipo_nome " +
                        "FROM produto p " +
                        "LEFT JOIN categoria c ON p.categoria_id = c.id " +
                        "LEFT JOIN tipo_produto t ON p.tipo_id = t.id " +
                        "ORDER BY p.nome";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Código de mapeamento transferido para o método auxiliar (mapResultSetToProduto)
                Produto p = mapResultSetToProduto(rs);
                produtos.add(p);
            }

        } catch (SQLException e) {
            // REFATORADO: Lança a exceção customizada
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao listar produtos.", e);
        }

        return produtos;
    }

    /**
     * Busca produto por id — traz categoria e tipo.
     */
    public Produto buscarPorId(int id) {
        String sql =
                "SELECT p.id, p.nome, p.preco, p.quantidade, p.categoria_id, p.descricao, " +
                        "       c.nome AS categoria_nome, c.descricao AS categoria_descricao, " +
                        "       p.tipo_id, t.nome AS tipo_nome " +
                        "FROM produto p " +
                        "LEFT JOIN categoria c ON p.categoria_id = c.id " +
                        "LEFT JOIN tipo_produto t ON p.tipo_id = t.id " +
                        "WHERE p.id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Código de mapeamento transferido para o método auxiliar (mapResultSetToProduto)
                    return mapResultSetToProduto(rs);
                }
            }

        } catch (SQLException e) {
            // REFATORADO: Lança a exceção customizada
            System.err.println("❌ Erro ao buscar produto por ID: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao buscar produto por ID.", e);
        }

        return null;
    }

    /**
     * Insere produto. Usa RETURNING id (Postgres).
     */
    public boolean inserir(Produto produto) {
        String sql =
                "INSERT INTO produto (nome, preco, quantidade, categoria_id, descricao, tipo_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produto.getNome());

            BigDecimal preco = produto.getPreco();
            if (preco != null) ps.setBigDecimal(2, preco);
            else ps.setNull(2, Types.NUMERIC);

            ps.setInt(3, produto.getQuantidade());

            Categoria cat = produto.getCategoria();
            if (cat != null && cat.getId() > 0) {
                ps.setInt(4, cat.getId());
            } else {
                ps.setInt(4, 1); // default
            }

            ps.setString(5, produto.getDescricao());

            // tipo_id opcional
            ps.setNull(6, Types.INTEGER);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            // REFATORADO: Lança a exceção customizada
            System.err.println("❌ Erro ao inserir produto: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao inserir produto.", e);
        }

        return false;
    }

    /**
     * Atualiza produto.
     */
    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ?, quantidade = ?, categoria_id = ?, descricao = ?, tipo_id = ? WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produto.getNome());

            BigDecimal preco = produto.getPreco();
            if (preco != null) ps.setBigDecimal(2, preco);
            else ps.setNull(2, Types.NUMERIC);

            ps.setInt(3, produto.getQuantidade());

            Categoria cat = produto.getCategoria();
            if (cat != null && cat.getId() > 0) ps.setInt(4, cat.getId());
            else ps.setInt(4, 1); // default

            ps.setString(5, produto.getDescricao());

            // tipo_id opcional
            ps.setNull(6, Types.INTEGER);

            ps.setInt(7, produto.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // REFATORADO: Lança a exceção customizada
            System.err.println("❌ Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao atualizar produto.", e);
        }
    }

    /**
     * Exclui produto por id.
     */
    public boolean excluir(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // REFATORADO: Lança a exceção customizada
            System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao excluir produto.", e);
        }
    }
}