package DOARC.mvc.util;

import java.sql.*;

public class Conexao {
    private Connection connect;
    private String erro;

    public Conexao() {
        erro = "";
        connect = null;
        // Conectar automaticamente usando as configurações padrão
        conectarAutomatico();
    }

    public Connection getConnect() {
        // Verificar se a conexão ainda está válida
        try {
            if (connect != null && !connect.isClosed()) {
                return connect;
            } else {
                // Reconectar se a conexão foi perdida
                conectarAutomatico();
                return connect;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar conexão: " + e.getMessage());
            conectarAutomatico();
            return connect;
        }
    }

    private void conectarAutomatico() {
        // Usar configurações padrão do application.properties
        String url = "jdbc:postgresql://localhost:5432/doarc_db";
        String usuario = "postgres";
        String senha = "postgres";

        conectar(url, usuario, senha);
    }

    public boolean conectar(String local, String banco, String usuario, String senha) {
        boolean conectado = false;
        try {
            String url = local + banco;
            connect = DriverManager.getConnection(url, usuario, senha);
            conectado = true;
            System.out.println("✅ Conexão estabelecida com: " + url);
        } catch (SQLException sqlex) {
            erro = "Impossível conectar com a base de dados: " + sqlex.toString();
            System.err.println("❌ " + erro);
        } catch (Exception ex) {
            erro = "Outro erro: " + ex.toString();
            System.err.println("❌ " + erro);
        }
        return conectado;
    }

    public boolean conectar(String url, String usuario, String senha) {
        boolean conectado = false;
        try {
            connect = DriverManager.getConnection(url, usuario, senha);
            conectado = true;
            System.out.println("✅ Conexão estabelecida com: " + url);
        } catch (SQLException sqlex) {
            erro = "Impossível conectar com a base de dados: " + sqlex.toString();
            System.err.println("❌ " + erro);
        } catch (Exception ex) {
            erro = "Outro erro: " + ex.toString();
            System.err.println("❌ " + erro);
        }
        return conectado;
    }

    public String getMensagemErro() {
        return erro;
    }

    public boolean getEstadoConexao() {
        try {
            return (connect != null && !connect.isClosed());
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean manipular(String sql) { // inserir, alterar, excluir
        boolean executou = false;
        try {
            Statement statement = connect.createStatement();
            int result = statement.executeUpdate(sql);
            statement.close();
            if (result >= 1)
                executou = true;
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
            System.err.println("❌ Erro ao manipular SQL: " + erro);
        }
        return executou;
    }

    public ResultSet consultar(String sql) {
        ResultSet rs = null;
        try {
            Statement statement = connect.createStatement();
            rs = statement.executeQuery(sql);
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
            System.err.println("❌ Erro ao consultar SQL: " + erro);
            rs = null;
        }
        return rs;
    }

    public int getMaxPK(String tabela, String chave) {
        String sql = "select max(" + chave + ") from " + tabela;
        int max = 0;
        ResultSet rs = consultar(sql);
        try {
            if (rs.next())
                max = rs.getInt(1);
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
            System.err.println("❌ Erro ao obter max PK: " + erro);
            max = -1;
        }
        return max;
    }

    public void fecharConexao() {
        try {
            if (connect != null && !connect.isClosed()) {
                connect.close();
                System.out.println("✅ Conexão fechada");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao fechar conexão: " + e.getMessage());
        }
    }
}