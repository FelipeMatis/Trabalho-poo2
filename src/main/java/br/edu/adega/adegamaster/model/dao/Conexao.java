package br.edu.adega.adegamaster.model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexão com PostgreSQL.
 * - Lê variáveis de ambiente primeiro (recomendado)
 * - Se não existir, usa valores padrão codificados (substitua).
 */
public class Conexao {

    // ----- VALORES PADRÃO (substitua se quiser) -----
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "5432";
    private static final String DEFAULT_DB   = "AdegaMaster";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASS = "postgres";
    // ------------------------------------------------

    // Nome da variável de ambiente (ex.: set no SO / IntelliJ run config)
    private static final String ENV_HOST = "DB_HOST";
    private static final String ENV_PORT = "DB_PORT";
    private static final String ENV_DB   = "DB_NAME";
    private static final String ENV_USER = "DB_USER";
    private static final String ENV_PASS = "DB_PASS";
    private static final String ENV_SSLMODE = "DB_SSLMODE"; // opcional: require, disable, prefer, etc.

    private static String getEnvOrDefault(String envName, String defaultValue) {
        String v = System.getenv(envName);
        return (v != null && !v.isBlank()) ? v : defaultValue;
    }

    private static String buildUrl() {
        String host = getEnvOrDefault(ENV_HOST, DEFAULT_HOST);
        String port = getEnvOrDefault(ENV_PORT, DEFAULT_PORT);
        String db   = getEnvOrDefault(ENV_DB, DEFAULT_DB);
        String sslmode = System.getenv(ENV_SSLMODE); // null ok

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
        if (sslmode != null && !sslmode.isBlank()) {
            url += "?sslmode=" + sslmode;
        }
        return url;
    }

    /**
     * Retorna uma conexão com o banco.
     * Lê credenciais das variáveis de ambiente (DB_USER, DB_PASS) ou usa defaults.
     */
    public static Connection getConexao() throws SQLException {
        String url = buildUrl();
        String user = getEnvOrDefault(ENV_USER, DEFAULT_USER);
        String pass = getEnvOrDefault(ENV_PASS, DEFAULT_PASS);

        // Se quiser passar propriedades adicionais:
        // Properties props = new Properties();
        // props.setProperty("user", user);
        // props.setProperty("password", pass);
        // props.setProperty("loginTimeout", "10");
        // return DriverManager.getConnection(url, props);

        return DriverManager.getConnection(url, user, pass);
    }

    /**
     * Teste rápido — execute este main para validar a conexão.
     * Para testes locais é recomendado exportar as variáveis de ambiente:
     *   Windows (cmd): set DB_HOST=localhost
     *   Linux/Mac: export DB_HOST=localhost
     *
     * Ou configure no Run Configuration do IntelliJ.
     */
    public static void main(String[] args) {
        System.out.println("URL = " + buildUrl()); // útil para debug (não exponha em produção)
        try (Connection c = getConexao()) {
            System.out.println("Conexão OK -> " + c.getMetaData().getDatabaseProductName()
                    + " " + c.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("Falha na conexão: " + e.getMessage());
            e.printStackTrace();
            // dicas rápidas:
            System.err.println("Dicas: verifique URL, usuário/senha e se o Postgres está rodando na porta correta.");
        }
    }
}
