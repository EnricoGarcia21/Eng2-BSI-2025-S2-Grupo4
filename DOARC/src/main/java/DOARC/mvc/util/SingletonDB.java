package DOARC.mvc.util;

import java.sql.SQLException;
import javax.sql.DataSource;

<<<<<<< HEAD

=======
// **NOTA**: Este SingletonDB não funcionará corretamente sem injetar o DataSource do Spring
// em algum ponto do seu código principal/configuração.
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

public class SingletonDB {
    private static Conexao conexao=null;
    private static DataSource dataSource; // Deve ser setado pelo Spring

    private SingletonDB() {
    }

<<<<<<< HEAD

=======
    // Método para ser chamado externamente pelo Spring (ex: em uma classe de configuração)
    // para injetar o DataSource gerenciado.
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static Conexao conectar()
    {
        if(conexao==null){
<<<<<<< HEAD

=======
            // Se o DataSource não foi setado, lançamos um erro ou retornamos null.
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            if(dataSource == null) {
                throw new IllegalStateException("DataSource do Spring não foi injetado/configurado no SingletonDB.");
            }
            try {
<<<<<<< HEAD

=======
                // Obtém a conexão real (gerenciada pelo pool) do Spring
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                conexao = new Conexao(dataSource.getConnection());
            } catch (SQLException e) {
                System.err.println("Erro ao obter a conexão do DataSource: " + e.getMessage());
                return null;
            }
        }
        return conexao;
    }

}