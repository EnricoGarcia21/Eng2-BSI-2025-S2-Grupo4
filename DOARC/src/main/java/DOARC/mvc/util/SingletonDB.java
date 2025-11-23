package DOARC.mvc.util;

import java.sql.SQLException;
import javax.sql.DataSource;



public class SingletonDB {
    private static Conexao conexao=null;
    private static DataSource dataSource; // Deve ser setado pelo Spring

    private SingletonDB() {
    }


    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static Conexao conectar()
    {
        if(conexao==null){

            if(dataSource == null) {
                throw new IllegalStateException("DataSource do Spring não foi injetado/configurado no SingletonDB.");
            }
            try {

                conexao = new Conexao(dataSource.getConnection());
            } catch (SQLException e) {
                System.err.println("Erro ao obter a conexão do DataSource: " + e.getMessage());
                return null;
            }
        }
        return conexao;
    }

}