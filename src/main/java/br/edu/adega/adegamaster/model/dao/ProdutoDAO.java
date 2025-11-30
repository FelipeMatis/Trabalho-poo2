package br.edu.adega.adegamaster.model.dao;

import br.edu.adega.adegamaster.model.domain.Categoria;
import br.edu.adega.adegamaster.model.domain.Produto;


import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
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
        // Verifica se o ID ou Nome da categoria vieram preenchidos (significa que o JOIN funcionou)
        if (cid > 0 && catNome != null) {
            cat = new Categoria(cid, catNome, catDesc);
        }
        p.setCategoria(cat);

        return p;
    }
    // ------------------------------------------------------------------------------------------

    /**
     * Lista todos os produtos — traz categoria.
     */
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();

        String sql =
                "SELECT p.id, p.nome, p.preco, p.quantidade, p.categoria_id, p.descricao, " +
                        "       c.nome AS categoria_nome, c.descricao AS categoria_descricao " +
                        "FROM produto p " +
                        "LEFT JOIN categoria c ON p.categoria_id = c.id " +
                        // A cláusula LEFT JOIN tipo_produto t ON p.tipo_id = t.id FOI REMOVIDA.
                        "ORDER BY p.nome";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Produto p = mapResultSetToProduto(rs);
                produtos.add(p);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao listar produtos.", e);
        }

        return produtos;
    }

    /**
     * Busca produto por id — traz categoria.
     */
    public Produto buscarPorId(int id) {
        String sql =
                "SELECT p.id, p.nome, p.preco, p.quantidade, p.categoria_id, p.descricao, " +
                        "       c.nome AS categoria_nome, c.descricao AS categoria_descricao " +
                        "FROM produto p " +
                        "LEFT JOIN categoria c ON p.categoria_id = c.id " +
                        "WHERE p.id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduto(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar produto por ID: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao buscar produto por ID.", e);
        }

        return null;
    }

    public boolean inserir(Produto produto) {
        String sql =
                "INSERT INTO produto (nome, preco, quantidade, categoria_id, descricao) " +
                        "VALUES (?, ?, ?, ?, ?) RETURNING id";

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

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
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
        String sql = "UPDATE produto SET nome = ?, preco = ?, quantidade = ?, categoria_id = ?, descricao = ? WHERE id = ?";

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

            ps.setInt(6, produto.getId()); // O ID agora é o parâmetro 6

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
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
            System.err.println("❌ Erro ao excluir produto: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao excluir produto.", e);
        }
    }
}