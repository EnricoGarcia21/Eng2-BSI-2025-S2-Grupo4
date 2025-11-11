package DOARC.mvc;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import DOARC.mvc.util.SingletonDB;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DOARCApplication {
    public static void main(String[] args) {
        if (!SingletonDB.conectar()) {
            System.err.println("FATAL ERROR: Não foi possível conectar ao PostgreSQL. Encerrando aplicação.");

        } else {
            System.out.println("INFO: Conexão com o PostgreSQL estabelecida via Singleton.");
        }

        SpringApplication.run(DOARCApplication.class, args);
    }
}