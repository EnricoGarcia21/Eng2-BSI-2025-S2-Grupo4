package DOARC.mvc;

import DOARC.mvc.util.SingletonDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DOARCApplication {

    public static void main(String[] args) {
        SingletonDB conexao = SingletonDB.getInstancia();
        if(!conexao.conectar()){
            System.out.println("DEBUG: Não foi possível conectar ao banco de dados");
        } else {
            System.out.println("DEBUG: Conexão com o banco estabelecida com sucesso");
        }
        SpringApplication.run(DOARCApplication.class, args);
    }
}