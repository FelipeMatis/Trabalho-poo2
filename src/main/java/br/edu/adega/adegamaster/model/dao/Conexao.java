package br.edu.adega.adegamaster.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:postgresql://localhost:5432/empresa_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "sua_senha_aqui";

    public static Connection getConexao() {
        Connection conexao = null;
        try {
            Class.forName("org.postgresql.Driver");
            conexao = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
            return conexao;
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados. Verifique usuário e senha!");
            throw new RuntimeException("Erro ao conectar ao banco de dados.", e);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado.");
            throw new RuntimeException("Driver JDBC não encontrado.", e);
        }
    }

    public static void fecharConexao(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}