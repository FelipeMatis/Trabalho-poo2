package br.edu.adega.adegamaster.model.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * Conexão com PostgreSQL utilizando HikariCP (Connection Pool).
 */
public class Conexao {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "5432";
    private static final String DEFAULT_DB   = "AdegaMaster";
    private static final String DEFAULT_USER = "postgres";
    private static final String DEFAULT_PASS = "postgres";

    private static final String ENV_HOST = "DB_HOST";
    private static final String ENV_PORT = "DB_PORT";
    private static final String ENV_DB   = "DB_NAME";
    private static final String ENV_USER = "DB_USER";
    private static final String ENV_PASS = "DB_PASS";
    private static final String ENV_SSLMODE = "DB_SSLMODE";

    private static HikariDataSource dataSource;

    static {
        initPool();
    }

    private static String getEnvOrDefault(String envName, String defaultValue) {
        String v = System.getenv(envName);
        return (v != null && !v.isBlank()) ? v : defaultValue;
    }

    private static String buildUrl() {
        String host = getEnvOrDefault(ENV_HOST, DEFAULT_HOST);
        String port = getEnvOrDefault(ENV_PORT, DEFAULT_PORT);
        String db   = getEnvOrDefault(ENV_DB, DEFAULT_DB);
        String sslmode = System.getenv(ENV_SSLMODE);

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
        if (sslmode != null && !sslmode.isBlank()) {
            url += "?sslmode=" + sslmode;
        }
        return url;
    }

    private static void initPool() {
        try {
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(buildUrl());
            config.setUsername(getEnvOrDefault(ENV_USER, DEFAULT_USER));
            config.setPassword(getEnvOrDefault(ENV_PASS, DEFAULT_PASS));

            // Configurações do HikariCP
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(10); // Quantas conexões o pool pode manter no máximo
            config.setMinimumIdle(5);      // Quantas conexões o pool tenta manter ociosas
            config.setConnectionTimeout(3000); // 3 segundos para obter uma conexão antes de falhar

            dataSource = new HikariDataSource(config);
            System.out.println("DEBUG: HikariCP - Pool de conexões inicializado com sucesso.");

        } catch (Exception e) {
            System.err.println("FATAL: Falha ao inicializar o HikariCP (Pool de Conexões).");
            e.printStackTrace();
            throw new RuntimeException("Não foi possível iniciar o pool de conexões.", e);
        }
    }


    public static Connection getConexao() throws SQLException {
        // Retorna uma conexão pronta do pool. O try-with-resources nos DAOs a devolverá automaticamente.
        return dataSource.getConnection();
    }

    public static void main(String[] args) {
        System.out.println("URL = " + buildUrl());
        try (Connection c = getConexao()) {
            System.out.println("Conexão OK -> " + c.getMetaData().getDatabaseProductName()
                    + " " + c.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("Falha na conexão: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Dicas: verifique URL, usuário/senha e se o Postgres está rodando na porta correta.");
        }
    }
}