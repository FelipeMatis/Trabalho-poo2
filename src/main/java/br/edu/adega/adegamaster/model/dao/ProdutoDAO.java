package br.edu.adega.adegamaster.model.dao;

import br.edu.adega.adegamaster.model.domain.Produto;
import br.edu.adega.adegamaster.model.domain.TipoProduto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ProdutoDAO {

    // Método 1: CREATE (Inserir Novo Produto)
    public boolean inserir(Produto produto) {
        String sql = "INSERT INTO Produto(nome, preco, estoque, descricao, id_tipo_produto) VALUES(?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);

            // 1. Dados do Produto
            stmt.setString(1, produto.getNome());
            stmt.setBigDecimal(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            stmt.setString(4, produto.getDescricao());

            // 2. Chave Estrangeira: Pega o ID do objeto TipoProduto
            stmt.setInt(5, produto.getTipoProduto().getIdTipoProduto());

            stmt.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Erro ao inserir Produto: " + ex.getMessage());
            return false;
        } finally {
            Conexao.closeConnection(conn);
        }
    }

    // Método 2: READ (Listar Todos os Produtos com o Tipo)
    public List<Produto> listar() {
        // SQL com JOIN para trazer os dados de Produto (p) e TipoProduto (tp)
        String sql = "SELECT p.*, tp.nome_tipo FROM Produto p JOIN TipoProduto tp ON p.id_tipo_produto = tp.id_tipo_produto";
        List<Produto> retorno = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultado = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);
            resultado = stmt.executeQuery();

            while (resultado.next()) {
                Produto produto = new Produto();

                // Popula o objeto Produto
                produto.setIdProduto(resultado.getInt("id_produto"));
                produto.setNome(resultado.getString("nome"));
                produto.setPreco(resultado.getBigDecimal("preco"));
                produto.setEstoque(resultado.getInt("estoque"));
                produto.setDescricao(resultado.getString("descricao"));

                // Popula o objeto TipoProduto (relacionamento)
                TipoProduto tipo = new TipoProduto();
                tipo.setIdTipoProduto(resultado.getInt("id_tipo_produto"));
                tipo.setNomeTipo(resultado.getString("nome_tipo"));

                // Associa os dois objetos
                produto.setTipoProduto(tipo);

                retorno.add(produto);
            }
        } catch (SQLException ex) {
            System.err.println("Erro ao listar Produtos: " + ex.getMessage());
        } finally {
            // Requisito de fechar recursos
            try {
                if (resultado != null) resultado.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar recursos: " + e.getMessage());
            }
            Conexao.closeConnection(conn);
        }
        return retorno;
    }

    // Método 3: UPDATE (Alterar Produto Existente)
    public boolean alterar(Produto produto) {
        String sql = "UPDATE Produto SET nome=?, preco=?, estoque=?, descricao=?, id_tipo_produto=? WHERE id_produto=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);

            // Parâmetros de 1 a 5 (novos dados)
            stmt.setString(1, produto.getNome());
            stmt.setBigDecimal(2, produto.getPreco());
            stmt.setInt(3, produto.getEstoque());
            stmt.setString(4, produto.getDescricao());
            stmt.setInt(5, produto.getTipoProduto().getIdTipoProduto());

            // Parâmetro 6 (o ID do produto a ser alterado)
            stmt.setInt(6, produto.getIdProduto());

            stmt.execute();
            return true;
        } catch (SQLException ex) {
            System.err.println("Erro ao alterar Produto: " + ex.getMessage());
            return false;
        } finally {
            Conexao.closeConnection(conn);
        }
    }

    // Método 4: DELETE (Excluir Produto)
    public boolean remover(Produto produto) {
        String sql = "DELETE FROM Produto WHERE id_produto=?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Conexao.getConnection();
            stmt = conn.prepareStatement(sql);

            // Parâmetro 1 (o ID do produto a ser excluído)
            stmt.setInt(1, produto.getIdProduto());

            stmt.execute();
            return true;
        } catch (SQLException ex) {
            // Cuidado: Este erro pode ocorrer se houver outra tabela com FK para Produto.
            System.err.println("Erro ao remover Produto: " + ex.getMessage());
            return false;
        } finally {
            Conexao.closeConnection(conn);
        }
    }
}