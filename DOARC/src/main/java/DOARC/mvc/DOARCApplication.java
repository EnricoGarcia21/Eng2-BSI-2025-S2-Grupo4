package DOARC.mvc;

import DOARC.mvc.util.SingletonDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DOARCApplication {

    public static void main(String[] args) {
        if(!SingletonDB.conectar()){
            System.out.println("Nao foi possivel conectar");
        }
        SpringApplication.run(DOARCApplication.class, args);
        PasswordEncoder encoder = new PasswordEncoder();
        String senha = "minhaSenha123";
        String hash = encoder.encode(senha);
        
        System.out.println("Senha: " + senha);
        System.out.println("Hash: " + hash);
        System.out.println("Match: " + encoder.matches(senha, hash));
        System.out.println("Wrong Match: " + encoder.matches("senhaErrada", hash));
    }

}
