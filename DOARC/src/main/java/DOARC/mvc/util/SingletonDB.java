package DOARC.mvc.util;

import java.sql.Connection;
import java.sql.SQLException;

public class SingletonDB {
    private static Conexao conexao = null;
    private static Connection connection = null;

    private SingletonDB() {
    }

    public static boolean conectar() {
        if (conexao == null) {
            conexao = new Conexao();
            boolean conectado = conexao.conectar("jdbc:postgresql://localhost:5432/", "DOARC", "postgres", "32653665");
            if (conectado) {
                connection = conexao.getConnect();
            }
            return conectado;
        }
        return true; // Já está conectado
    }

    public static Conexao getConexao() {
        // Lazy initialization - conecta automaticamente se ainda não conectou
        if (conexao == null || connection == null) {
            conectar();
        }
        return conexao;
    }

    public static Connection getConnection() {
        // Retorna a conexão estática, conecta se necessário
        if (connection == null || !isConnectionValid()) {
            conectar();
        }
        return connection;
    }

    private static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
