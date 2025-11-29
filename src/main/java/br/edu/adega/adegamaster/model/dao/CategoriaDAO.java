package br.edu.adega.adegamaster.model.dao;

import br.edu.adega.adegamaster.model.domain.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT id, nome, descricao FROM categoria ORDER BY nome";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categoria c = new Categoria();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                c.setDescricao(rs.getString("descricao"));
                lista.add(c);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar categorias: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public Categoria buscarPorId(int id) {
        String sql = "SELECT id, nome, descricao FROM categoria WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Categoria c = new Categoria();
                    c.setId(rs.getInt("id"));
                    c.setNome(rs.getString("nome"));
                    c.setDescricao(rs.getString("descricao"));
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar categoria: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int inserir(Categoria categoria) {
        String sql = "INSERT INTO categoria (nome, descricao) VALUES (?, ?) RETURNING id";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    categoria.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            // fallback sem RETURNING
            try {
                String sql2 = "INSERT INTO categoria (nome, descricao) VALUES (?, ?)";
                try (Connection conn = Conexao.getConexao();
                     PreparedStatement ps2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
                    ps2.setString(1, categoria.getNome());
                    ps2.setString(2, categoria.getDescricao());
                    int rows = ps2.executeUpdate();
                    if (rows > 0) {
                        try (ResultSet keys = ps2.getGeneratedKeys()) {
                            if (keys.next()) {
                                int id = keys.getInt(1);
                                categoria.setId(id);
                                return id;
                            }
                        }
                    }
                }
            } catch (SQLException ex2) {
                System.err.println("❌ Erro ao inserir categoria (fallback): " + ex2.getMessage());
                ex2.printStackTrace();
            }
        }
        return -1;
    }

    public boolean atualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nome = ?, descricao = ? WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoria.getNome());
            ps.setString(2, categoria.getDescricao());
            ps.setInt(3, categoria.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar categoria: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM categoria WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Erro ao excluir categoria: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
