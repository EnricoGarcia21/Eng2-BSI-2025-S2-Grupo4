package DOARC.mvc;

import DOARC.mvc.util.SingletonDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DOARCApplication {

    public static void main(String[] args) {
        if(!SingletonDB.conectar()){
            System.out.println("Nao foi possivel conectar no banco");
        }
        SpringApplication.run(DOARCApplication.class, args);
    }

}
