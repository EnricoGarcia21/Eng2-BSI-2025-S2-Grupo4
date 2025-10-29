package DOARC.mvc.util;

public class SingletonDB {
    private static Conexao conexao=null;

    private SingletonDB() {
    }

    public static boolean conectar()
    {
        conexao=new Conexao();
        return conexao.conectar("jdbc:postgresql://localhost:5432/","DOARC","postgres","2112e");
    }
    public static Conexao getConexao() {
        return conexao;
    }
}
