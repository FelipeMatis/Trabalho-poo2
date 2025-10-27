package br.edu.adega.adegamaster.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:postgresql://localhost:5432/adegaMaster";
    private static final String USER = "seu_usuario_postgres"; // << MUDAR AQUI
    private static final String PASSWORD = "sua_senha_postgres"; // << MUDAR AQUI

    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexão bem-sucedida!"); // Apenas para teste inicial
            return connection;
        } catch (SQLException e) {
            // Tratamento de exceção (Requisito: 5.b)
            System.err.println("Erro de SQL: " + e.getMessage());
            throw new RuntimeException("Erro ao conectar ao banco de dados.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver JDBC não encontrado.", e);
        }
    }

    // Método para fechar conexões, útil para o DAO
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}