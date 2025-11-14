package DOARC.mvc.util;

import java.sql.SQLException;
import javax.sql.DataSource;

// **NOTA**: Este SingletonDB não funcionará corretamente sem injetar o DataSource do Spring
// em algum ponto do seu código principal/configuração.

public class SingletonDB {
    private static Conexao conexao=null;
    private static DataSource dataSource; // Deve ser setado pelo Spring

    private SingletonDB() {
    }

    // Método para ser chamado externamente pelo Spring (ex: em uma classe de configuração)
    // para injetar o DataSource gerenciado.
    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static Conexao conectar()
    {
        if(conexao==null){
            // Se o DataSource não foi setado, lançamos um erro ou retornamos null.
            if(dataSource == null) {
                throw new IllegalStateException("DataSource do Spring não foi injetado/configurado no SingletonDB.");
            }
            try {
                // Obtém a conexão real (gerenciada pelo pool) do Spring
                conexao = new Conexao(dataSource.getConnection());
            } catch (SQLException e) {
                System.err.println("Erro ao obter a conexão do DataSource: " + e.getMessage());
                return null;
            }
        }
        return conexao;
    }

}