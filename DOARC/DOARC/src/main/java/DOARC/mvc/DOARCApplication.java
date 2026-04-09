package DOARC.mvc;

import DOARC.mvc.util.SingletonDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class DOARCApplication {

    public static void main(String[] args) {
        if(!SingletonDB.conectar().getEstadoConexao()){
            System.out.println("Nao foi possivel conectar no banco");
        }
        SpringApplication.run(DOARCApplication.class, args);
    }

}
