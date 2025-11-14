package DOARC.mvc.view;

import DOARC.mvc.controller.AcessoController;
import DOARC.mvc.model.EmailNotification;
import DOARC.mvc.model.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/apis/acesso")
@CrossOrigin(origins = "*")
public class AcessoView {

    @Autowired
    private AcessoController acessoController;

    @Autowired
    private EmailNotification emailNotification;

    @PostMapping("/logar")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();
        String email = dados.get("email");
        String senha = dados.get("senha");

        String token = acessoController.autenticarGerarToken(email, senha);
        if (token != null) {
            Login usuario = acessoController.buscarPorEmail(email);
            response.put("success", true);
            response.put("token", token);
            response.put("usuario", Map.of(
                    "email", usuario.getLogin(),
                    "nivelAcesso", usuario.getNivelAcesso(),
                    "voluntarioId", usuario.getVoluntarioId()
            ));
            return ResponseEntity.ok(response);
        } else {
            response.put("erro", "Email ou senha incorretos");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();

        String nome = dados.get("nome");
        String cpf = dados.get("cpf");
        String telefone = dados.get("telefone");
        String dataNascimentoStr = dados.get("dataNascimento");
        String endereco = dados.get("endereco");
        String email = dados.get("email");
        String senha = dados.get("senha");
        String nivelAcesso = dados.getOrDefault("nivelAcesso", "USER");
        String sexo = dados.getOrDefault("sexo", "O"); // PADRÃO SEGURO

        LocalDate dataNascimento = null;
        try {
            if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                dataNascimento = LocalDate.parse(dataNascimentoStr);
            }
        } catch (Exception e) {
            response.put("erro", "Data de nascimento inválida");
            return ResponseEntity.badRequest().body(response);
        }

        Login novoLogin = acessoController.registrarVoluntarioCompleto(
                nome, cpf, telefone, dataNascimento, endereco, sexo, email, senha, nivelAcesso
        );

        if (novoLogin != null) {
            response.put("success", true);
            response.put("user", Map.of(
                    "email", novoLogin.getLogin(),
                    "role", novoLogin.getNivelAcesso(),
                    "voluntarioId", novoLogin.getVoluntarioId()
            ));

            try {
                emailNotification.enviarEmailBemVindo(email, nome);
            } catch (Exception ignored) {}

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("erro", "Erro ao criar conta. Verifique os dados e tente novamente.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/registrar-admin")
    public ResponseEntity<Map<String, Object>> registrarAdmin(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();
        String login = dados.get("login");
        String senha = dados.get("senha");

        Login novoAdmin = acessoController.registrarAdmin(login, senha);
        if (novoAdmin != null) {
            response.put("success", true);
            response.put("user", Map.of(
                    "email", novoAdmin.getLogin(),
                    "role", novoAdmin.getNivelAcesso(),
                    "voluntarioId", novoAdmin.getVoluntarioId()
            ));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("erro", "Erro ao criar conta de administrador");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<Map<String, Object>> alterarSenha(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();
        String email = dados.get("email");
        String senhaAtual = dados.get("senhaAtual");
        String novaSenha = dados.get("novaSenha");

        boolean sucesso = acessoController.atualizarSenha(email, senhaAtual, novaSenha);
        if (sucesso) {
            response.put("success", true);
            response.put("message", "Senha alterada com sucesso!");
            return ResponseEntity.ok(response);
        } else {
            response.put("erro", "Senha atual incorreta ou erro ao alterar");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
