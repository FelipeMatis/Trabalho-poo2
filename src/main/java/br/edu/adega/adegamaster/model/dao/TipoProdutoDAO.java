package br.edu.adega.adegamaster.dao;

import br.edu.adega.adegamaster.domain.TipoProduto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoProdutoDAO {

    private Connection conexao;

    public TipoProdutoDAO() {
        this.conexao = Conexao.getConexao();
    }

    public void inserir(TipoProduto tipo) throws SQLException {
        String sql = "INSERT INTO tipo_produto (nome) VALUES (?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tipo.getNome());
            stmt.execute();
        }
    }

    public void atualizar(TipoProduto tipo) throws SQLException {
        String sql = "UPDATE tipo_produto SET nome=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tipo.getNome());
            stmt.setInt(2, tipo.getId());
            stmt.execute();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM tipo_produto WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    public List<TipoProduto> listarTodos() {
        List<TipoProduto> tipos = new ArrayList<>();
        String sql = "SELECT id, nome FROM tipo_produto ORDER BY nome";
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                TipoProduto tipo = new TipoProduto();
                tipo.setId(rs.getInt("id"));
                tipo.setNome(rs.getString("nome"));
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar tipos de produto: " + e.getMessage());
        }
        return tipos;
    }

    public TipoProduto buscarPorId(int id) {
        String sql = "SELECT id, nome FROM tipo_produto WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    TipoProduto tipo = new TipoProduto();
                    tipo.setId(rs.getInt("id"));
                    tipo.setNome(rs.getString("nome"));
                    return tipo;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar TipoProduto por ID: " + e.getMessage());
        }
        return null;
    }
}