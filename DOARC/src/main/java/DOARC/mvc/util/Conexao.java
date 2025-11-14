package DOARC.mvc.util;

import java.sql.*;

public class Conexao
{
    private Connection connect;
    private String erro;

    // Construtor corrigido: espera a conexão (java.sql.Connection) injetada/fornecida.
    public Conexao(Connection connect)
    {
        this.erro="";
        this.connect=connect;
    }

    public Connection getConnect() {
        return connect;
    }

    // Método conectar() removido pois o Spring/HikariCP fará a conexão.

    public String getMensagemErro() {
        return erro;
    }
    public boolean getEstadoConexao() {
        return (connect!=null);
    }
    public boolean manipular(String sql)
    {   boolean executou=false;
        try {
            Statement statement = connect.createStatement();
            int result = statement.executeUpdate( sql );
            statement.close();
            if(result>=1)
                executou=true;
        }
        catch ( SQLException sqlex )
        {  erro="Erro: "+sqlex.toString();
        }
        return executou;
    }
    public ResultSet consultar(String sql)
    {   ResultSet rs=null;
        try {
            Statement statement = connect.createStatement();
            rs = statement.executeQuery( sql );
        }
        catch ( SQLException sqlex )
        { erro="Erro: "+sqlex.toString();
            rs = null;
        }
        return rs;
    }
    public int getMaxPK(String tabela,String chave)
    {
        String sql="select max("+chave+") from "+tabela;
        int max=0;
        ResultSet rs= consultar(sql);
        try
        {
            if(rs.next())
                max=rs.getInt(1);
        }
        catch (SQLException sqlex)
        {
            erro="Erro: " + sqlex.toString();
            max = -1;
        }
        return max;
    }
}