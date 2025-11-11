package DOARC.mvc.util;

public class SingletonDB {
    private static Conexao conexao = null;

    private SingletonDB() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Erro FATAL: Driver do PostgreSQL não encontrado no Classpath.");
            e.printStackTrace();
        }
    }

    public static boolean conectar() {
        if (conexao == null) {
            try {
                new SingletonDB();
                conexao = new Conexao();

                return conexao.conectar("jdbc:postgresql://localhost:5432/", "DOARC", "postgres", "postgres123");
            } catch (Exception e) {
                System.err.println("Erro ao tentar conectar via Singleton: " + e.getMessage());
                conexao = null;
                return false;
            }
        }
        return conexao.getEstadoConexao();
    }

    public static Conexao getConexao() {
        if (conexao == null) {
            conectar();
        }
        return conexao;
    }
}