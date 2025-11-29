package br.edu.adega.adegamaster.model.dao;

import br.edu.adega.adegamaster.model.domain.Categoria;
import br.edu.adega.adegamaster.model.domain.Produto;
import br.edu.adega.adegamaster.model.domain.TipoProduto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final TipoProdutoDAO tipoProdutoDAO = new TipoProdutoDAO(); // caso queira usar direto

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
                Produto p = new Produto();
                p.setId(rs.getInt("id"));
                p.setNome(rs.getString("nome"));

                BigDecimal preco = rs.getBigDecimal("preco");
                p.setPreco(preco != null ? preco : BigDecimal.ZERO);

                p.setQuantidade(rs.getInt("quantidade"));
                p.setDescricao(rs.getString("descricao"));

                // Categoria (pode ser null)
                int cid = rs.getInt("categoria_id");
                Categoria cat = null;
                String catNome = rs.getString("categoria_nome");
                String catDesc = rs.getString("categoria_descricao");
                if (!rs.wasNull() && catNome != null) {
                    cat = new Categoria(cid, catNome, catDesc);
                }
                p.setCategoria(cat);

                // TipoProduto (opcional)
                int tid = rs.getInt("tipo_id");
                TipoProduto tipo = null;
                String tipoNome = rs.getString("tipo_nome");
                if (!rs.wasNull() && tipoNome != null) {
                    tipo = new TipoProduto(tid, tipoNome);
                }
                // Se quiser guardar tipo no Produto, adicione campo e setter no Produto.
                // Ex: p.setTipoProduto(tipo);

                produtos.add(p);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
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
                    Produto p = new Produto();
                    p.setId(rs.getInt("id"));
                    p.setNome(rs.getString("nome"));

                    BigDecimal preco = rs.getBigDecimal("preco");
                    p.setPreco(preco != null ? preco : BigDecimal.ZERO);

                    p.setQuantidade(rs.getInt("quantidade"));
                    p.setDescricao(rs.getString("descricao"));

                    int cid = rs.getInt("categoria_id");
                    Categoria cat = null;
                    String catNome = rs.getString("categoria_nome");
                    String catDesc = rs.getString("categoria_descricao");
                    if (!rs.wasNull() && catNome != null) {
                        cat = new Categoria(cid, catNome, catDesc);
                    }
                    p.setCategoria(cat);

                    int tid = rs.getInt("tipo_id");
                    String tipoNome = rs.getString("tipo_nome");
                    if (!rs.wasNull() && tipoNome != null) {
                        TipoProduto tipo = new TipoProduto(tid, tipoNome);
                        // p.setTipoProduto(tipo); // se Produto tiver campo tipoProduto
                    }

                    return p;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar produto por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Insere produto. Usa RETURNING id (Postgres).
     * IMPORTANTE: categoria_id é NOT NULL no seu schema — então sempre passe uma categoria válida.
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

            // categoria é obrigatório -> use id da categoria; se null, lançar erro ou usar default
            Categoria cat = produto.getCategoria();
            if (cat != null && cat.getId() > 0) {
                ps.setInt(4, cat.getId());
            } else {
                // Se você tem categoria default com id=1, pode usar:
                ps.setInt(4, 1);
            }

            ps.setString(5, produto.getDescricao());

            // tipo_id opcional
            // se Produto tiver tipoProduto, use produto.getTipoProduto().getId()
            ps.setNull(6, Types.INTEGER); // por padrão null (altere se tiver tipo)

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao inserir produto: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("❌ Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
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
            System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
