package br.edu.adega.adegamaster.model.dao;

import br.edu.adega.adegamaster.model.domain.TipoProduto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoProdutoDAO {

    /**
     * Lista todos os tipos de produto, ordenados por nome.
     */
    public List<TipoProduto> listar() {
        List<TipoProduto> lista = new ArrayList<>();
        String sql = "SELECT id, nome FROM tipo_produto ORDER BY nome";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoProduto t = new TipoProduto();
                t.setId(rs.getInt("id"));
                t.setNome(rs.getString("nome"));
                lista.add(t);
            }

        } catch (SQLException e) {
            // REFATORADO: Lança a exceção customizada
            System.err.println("❌ Erro ao listar tipos: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao listar Tipos de Produto.", e);
        }

        System.out.println("DEBUG: TipoProdutoDAO.listar() retornou " + lista.size() + " tipos.");
        for (TipoProduto t : lista) System.out.println("  -> " + t.getId() + " : " + t.getNome());
        return lista;
    }


    /**
     * Busca um TipoProduto por ID. Retorna null se não encontrar.
     */
    public TipoProduto buscarPorId(int id) {
        String sql = "SELECT id, nome FROM tipo_produto WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoProduto t = new TipoProduto();
                    t.setId(rs.getInt("id"));
                    t.setNome(rs.getString("nome"));
                    return t;
                }
            }

        } catch (SQLException e) {
            // REFATORADO: Lança a exceção customizada
            System.err.println("❌ Erro ao buscar tipo por id: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao buscar Tipo de Produto por ID.", e);
        }
        return null;
    }

    /**
     * Insere e retorna o id gerado (ou lança exceção em caso de falha).
     */
    public int inserir(TipoProduto tipo) {
        String sql = "INSERT INTO tipo_produto (nome) VALUES (?) RETURNING id";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo.getNome());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    tipo.setId(id);
                    return id;
                }
            }

        } catch (SQLException e) {
            // REFATORADO: Lógica de fallback encapsulada para garantir que o erro final seja ExceptionDAO
            try {
                String sql2 = "INSERT INTO tipo_produto (nome) VALUES (?)";
                try (Connection conn = Conexao.getConexao();
                     PreparedStatement ps2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
                    ps2.setString(1, tipo.getNome());
                    int rows = ps2.executeUpdate();
                    if (rows > 0) {
                        try (ResultSet keys = ps2.getGeneratedKeys()) {
                            if (keys.next()) {
                                int id = keys.getInt(1);
                                tipo.setId(id);
                                return id;
                            }
                        }
                    }
                }
            } catch (SQLException ex2) {
                System.err.println("❌ Erro ao inserir tipo (fallback): " + ex2.getMessage());
                ex2.printStackTrace();
                // Se o fallback falhar, lança a exceção customizada.
                throw new ExceptionDAO("Falha de persistência ao inserir Tipo de Produto.", ex2);
            }
        }
        return -1; // Retorna -1 se a inserção/fallback ocorreu, mas não conseguiu o ID
    }

    /**
     * Atualiza um tipo. Retorna true se atualizou algo.
     */
    public boolean atualizar(TipoProduto tipo) {
        String sql = "UPDATE tipo_produto SET nome = ? WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipo.getNome());
            ps.setInt(2, tipo.getId());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // REFATORADO: Não retorna false; lança a exceção
            System.err.println("❌ Erro ao atualizar tipo: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao atualizar Tipo de Produto.", e);
        }
    }

    /**
     * Exclui um tipo por id. Retorna true se excluiu.
     */
    public boolean excluir(int id) {
        String sql = "DELETE FROM tipo_produto WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // REFATORADO: Não retorna false; lança a exceção
            System.err.println("❌ Erro ao excluir tipo: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionDAO("Falha de persistência ao excluir Tipo de Produto.", e);
        }
    }
}