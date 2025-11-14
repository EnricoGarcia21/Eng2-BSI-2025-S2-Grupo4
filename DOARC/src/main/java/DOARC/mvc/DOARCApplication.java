// Exemplo de DOARCApplication.java (Ajustado)

package DOARC.mvc;

import DOARC.mvc.util.SingletonDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class DOARCApplication {

    // O Spring injetará o DataSource aqui automaticamente
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(DOARCApplication.class, args);
    }

    // Este Bean será executado logo após o Spring inicializar o contexto.
    @Bean
    public CommandLineRunner initDataSource() {
        return args -> {
            System.out.println("Injetando DataSource no SingletonDB...");
            // Chama o método estático para setar o DataSource
            SingletonDB.setDataSource(dataSource);

            // Aqui você pode testar se a conexão funciona:
            // DOARC.mvc.util.Conexao conexao = SingletonDB.conectar();
            // if (conexao != null) {
            //    System.out.println("Conexão do SingletonDB obtida com sucesso!");
            // }
        };
    }
}