package br.edu.adega.adegamaster.model.dao;

import br.edu.adega.adegamaster.model.domain.TipoProduto;
import java.sql.Connection;
import java.sql.PreparedStatement; // Requisito: 5.a - Prepared Statements
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoProdutoDAO {

    // Método 1: CREATE (Inserir) - Requisito: 3.a
    public boolean inserir(TipoProduto tipo) {
        String sql = "INSERT INTO TipoProduto(nome_tipo) VALUES(?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipo.getNomeTipo()); // 1. O primeiro '?' é o nome
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            // Tratamento de Exceção (Requisito: 5.b)
            System.err.println("Erro ao inserir TipoProduto: " + ex.getMessage());
            return false;
        } finally {
            Conexao.closeConnection(conn); // Fechar a conexão
        }
    }

    // Método 2: READ (Listar/Consultar) - Requisito: 3.a
    public List<TipoProduto> listar() {
        String sql = "SELECT * FROM TipoProduto";
        List<TipoProduto> retorno = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultado = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);
            resultado = stmt.executeQuery();
            while (resultado.next()) {
                TipoProduto tipo = new TipoProduto();
                tipo.setIdTipoProduto(resultado.getInt("id_tipo_produto"));
                tipo.setNomeTipo(resultado.getString("nome_tipo"));
                retorno.add(tipo);
            }
        } catch (SQLException ex) {
            System.err.println("Erro ao listar TipoProdutos: " + ex.getMessage());
        } finally {
            // Fechar recursos aqui
        }
        return retorno;
    }


    // Método 3: UPDATE (Alterar)
    public boolean alterar(TipoProduto tipo) {
        // Usa o ID para saber qual linha atualizar
        String sql = "UPDATE TipoProduto SET nome_tipo=? WHERE id_tipo_produto=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipo.getNomeTipo());       // 1. Novo nome
            stmt.setInt(2, tipo.getIdTipoProduto());     // 2. ID para a cláusula WHERE
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Erro ao alterar TipoProduto: " + ex.getMessage());
            return false;
        } finally {
            Conexao.closeConnection(conn);
        }
    }

    // Método 4: DELETE (Excluir)
    public boolean remover(TipoProduto tipo) {
        String sql = "DELETE FROM TipoProduto WHERE id_tipo_produto=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, tipo.getIdTipoProduto()); // 1. ID para a cláusula WHERE
            stmt.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Erro ao remover TipoProduto: " + ex.getMessage());
            return false;
        } finally {
            Conexao.closeConnection(conn);
        }
    }
}