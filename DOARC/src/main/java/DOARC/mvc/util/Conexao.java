package DOARC.mvc.util;

import java.sql.*;

public class Conexao {

    private Connection connect;
    private String erro;

    // Configurações do banco (Mantenha igual ao seu application.properties)
    private final String URL_BASE = "jdbc:postgresql://localhost:5432/";
    private final String BANCO = "DOARC";
    private final String USUARIO = "postgres";
    private final String SENHA = "postgres123";

    // 1. Construtor Vazio: Conecta sozinho (Usado pelas DAOs)
    public Conexao() {
        this.erro = "";
        this.connect = null;
        this.conectar(URL_BASE, BANCO, USUARIO, SENHA);
    }

    // 2. Construtor Novo: Aceita conexão externa (Correção para o SingletonDB)
    public Conexao(Connection c) {
        this.connect = c;
        this.erro = "";
    }

    public Connection getConnect() {
        return connect;
    }

    public boolean conectar(String local, String banco, String usuario, String senha) {
        boolean conectado = false;
        try {
            Class.forName("org.postgresql.Driver");
            String url = local + banco;
            connect = DriverManager.getConnection(url, usuario, senha);
            conectado = true;
        } catch (ClassNotFoundException cnfex) {
            erro = "Driver não encontrado: " + cnfex.toString();
        } catch (SQLException sqlex) {
            erro = "Impossivel conectar com a base de dados: " + sqlex.toString();
        } catch (Exception ex) {
            erro = "Outro erro: " + ex.toString();
        }
        return conectado;
    }

    public String getMensagemErro() {
        return erro;
    }

    public boolean getEstadoConexao() {
        return (connect != null);
    }

    public boolean manipular(String sql) {
        boolean executou = false;
        try {
            Statement statement = connect.createStatement();
            int result = statement.executeUpdate(sql);
            statement.close();
            if (result >= 1)
                executou = true;
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
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
            rs = null;
        }
        return rs;
    }

    public int getMaxPK(String tabela, String chave) {
        String sql = "select max(" + chave + ") from " + tabela;
        int max = 0;
        ResultSet rs = consultar(sql);
        try {
            if (rs != null && rs.next())
                max = rs.getInt(1);
        } catch (SQLException sqlex) {
            erro = "Erro: " + sqlex.toString();
            max = -1;
        }
        return max;
    }
}