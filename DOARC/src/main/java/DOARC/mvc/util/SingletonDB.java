package DOARC.mvc.util;

public class SingletonDB {
    private static Conexao conexao=null;
    private static SingletonDB instancia;
    private SingletonDB() {
    }

    public static boolean conectar()
    {
        conexao=new Conexao();
        return conexao.conectar("jdbc:postgresql://localhost:5432/","DOARC","postgres","@randa16!");
    }
    public static Conexao getConexao() {
        return conexao;
    }
    public static SingletonDB getInstancia(){
        if(instancia==null)
            instancia=new SingletonDB();
        return instancia;
    }
    public boolean Desconectar() {
        if (conexao!=null) {
            boolean resultado=conexao.desconectar();
            conexao=null;
            return resultado;
        }
        return false;
    }
}
