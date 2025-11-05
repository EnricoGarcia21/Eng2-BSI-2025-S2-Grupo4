package DOARC.mvc;

import DOARC.mvc.util.SingletonDB;
import DOARC.mvc.util.Conexao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DOARCApplication {

    public static void main(String[] args) {
        Conexao conexao = SingletonDB.conectar();

        if (conexao == null || !conexao.getEstadoConexao()) {
            System.out.println("❌ Não foi possível conectar ao banco de dados!");
        } else {
            System.out.println("✅ Conectado ao banco de dados com sucesso!");
        }

        SpringApplication.run(DOARCApplication.class, args);
    }
}
