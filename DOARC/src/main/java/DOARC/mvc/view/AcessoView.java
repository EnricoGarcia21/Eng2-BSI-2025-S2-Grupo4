package DOARC.mvc.view;

import DOARC.mvc.controller.AcessoController;
import DOARC.mvc.controller.VoluntarioController;
import DOARC.mvc.security.JWTTokenProvider;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/acesso")
public class AcessoView{
    
    @Autowired
    private AcessoController acessoController;

    @Autowired
    private VoluntarioController voluntarioController;

    @PostMapping("/logar")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> credenciais) {
        String login = credenciais.get("login");
        String senha = credenciais.get("senha");
        
        if (login == null || senha == null) {
            return ResponseEntity.badRequest().body(new Mensagem("Login e senha são obrigatórios"));
        }
        
        Map<String, Object> resultado = acessoController.autenticar(login, senha);
        
        boolean sucesso = (boolean) resultado.get("sucesso");
        
        if (sucesso) {
            Map<String, Object> usuario = (Map<String, Object>) resultado.get("usuario");
            String nivelAcesso = (String) usuario.get("nivelAcesso");
            String voluntarioId = String.valueOf(usuario.get("voluntarioId")); // ← USA VOLUNTARIO_ID
            
            String token = JWTTokenProvider.getToken(login, nivelAcesso, voluntarioId);
            
            return ResponseEntity.ok(Map.of(
                "mensagem", "Login realizado com sucesso",
                "token", token,
                "usuario", usuario
            ));
        } else {
            String mensagem = (String) resultado.get("mensagem");
            return ResponseEntity.badRequest().body(new Mensagem(mensagem));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<Object> registrar(@RequestBody Map<String, String> dados) {
        try {
            // Validações básicas
            if (dados.get("nome") == null || dados.get("email") == null || dados.get("senha") == null) {
                return ResponseEntity.badRequest().body(new Mensagem("Nome, email e senha são obrigatórios"));
            }

            // Cadastra o voluntário
            Map<String, Object> resultadoVoluntario = voluntarioController.addVoluntario(
                dados.get("nome"),
                dados.get("bairro") != null ? dados.get("bairro") : "Centro",
                dados.get("numero") != null ? dados.get("numero") : "S/N",
                dados.get("rua") != null ? dados.get("rua") : "Não informado",
                dados.get("telefone"),
                dados.get("cidade") != null ? dados.get("cidade") : "São Paulo",
                dados.get("cep") != null ? dados.get("cep") : "00000-000",
                dados.get("uf") != null ? dados.get("uf") : "SP",
                dados.get("email"),
                dados.get("cpf"),
                dados.get("dataNascimento"),
                dados.get("sexo") != null ? dados.get("sexo") : "M"
            );

            if (resultadoVoluntario.get("erro") != null) {
                return ResponseEntity.badRequest().body(new Mensagem(resultadoVoluntario.get("erro").toString()));
            }

            // Recupera o ID do voluntário cadastrado (AGORA É A CHAVE PRIMÁRIA DO LOGIN)
            Integer voluntarioId = (Integer) resultadoVoluntario.get("id");
            
            // Cria as credenciais de login
            Map<String, Object> resultadoCredenciais = acessoController.criarCredenciais(
                voluntarioId, // ← AGORA É A PK DA TABELA LOGIN
                dados.get("email"),
                dados.get("senha"),
                "VOLUNTARIO"
            );

            if (!(boolean) resultadoCredenciais.get("sucesso")) {
                System.out.println("Aviso: " + resultadoCredenciais.get("mensagem"));
                // Mesmo com aviso, retorna sucesso pois o voluntário foi cadastrado
            }

            return ResponseEntity.ok(new Mensagem("Cadastro realizado com sucesso! Você já pode fazer login."));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Mensagem("Erro ao realizar cadastro: " + e.getMessage()));
        }
    }
}