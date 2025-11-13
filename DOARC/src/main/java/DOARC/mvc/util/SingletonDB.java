package DOARC.mvc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonDB {
    private static SingletonDB instancia;
    private static String url = "jdbc:postgresql://localhost:5432/DOARC";
    private static String user = "postgres";
    private static String password = "@randa16!";
    private Connection connection;

    private SingletonDB() {
        // Construtor privado
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("DEBUG: Driver PostgreSQL carregado com sucesso");
        } catch (ClassNotFoundException e) {
            System.out.println("DEBUG: Driver PostgreSQL não encontrado: " + e.getMessage());
        }
    }

    public static SingletonDB getInstancia() {
        if (instancia == null) {
            instancia = new SingletonDB();
        }
        return instancia;
    }

    public boolean conectar() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("DEBUG: Conexão estabelecida com sucesso");
            }
            return true;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao conectar: " + e.getMessage());
            return false;
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("DEBUG: Conexão estava fechada, reconectando...");
                conectar();
            }
            return connection;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao obter conexão: " + e.getMessage());
            return null;
        }
    }

    public boolean Desconectar() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("DEBUG: Conexão fechada com sucesso");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao desconectar: " + e.getMessage());
        }
        return false;
    }

    public boolean isConectado() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}