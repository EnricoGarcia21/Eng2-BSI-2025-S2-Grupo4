package DOARC.mvc.util;

public class SingletonDB {
    private static Conexao conexao=null;

    private SingletonDB() {
    }

    public static Conexao conectar()
    {
        if(conexao==null){
            conexao=new Conexao();
            conexao.conectar("jdbc:postgresql://localhost:5432/","DOARC","postgres","1234");
        }
        return conexao;
    }

}