package DOARC.mvc.view;

import DOARC.mvc.controller.AcessoController;
import DOARC.mvc.model.EmailNotification;
import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/apis/acesso")
@CrossOrigin
public class AcessoView {

    @Autowired
    private AcessoController acessoController;

    @Autowired
    private EmailNotification emailNotification;

    @GetMapping("/verificar-cpf/{cpf}")
    public ResponseEntity<Map<String, Object>> verificarCpf(@PathVariable String cpf) {
        try {
            Map<String, Object> resultado = acessoController.verificarVoluntarioParaCadastro(cpf);
            if (resultado.containsKey("erro")) {
                return ResponseEntity.badRequest().body(resultado);
            }
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro ao verificar CPF"));
        }
    }

    @PostMapping("/logar")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();
        String email = dados.get("email");
        String senha = dados.get("senha");

        if (email == null || senha == null) {
            response.put("erro", "Email e senha obrigatórios");
            return ResponseEntity.badRequest().body(response);
        }

        Login usuario = acessoController.buscarPorEmail(email);

        if (usuario == null) {
            response.put("erro", "Usuário não encontrado.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        if (usuario.getStatus() != 'A') {
            response.put("erro", "Conta inativa.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        String token = acessoController.autenticarGerarToken(email, senha);

        if (token != null) {
            Voluntario voluntario = acessoController.buscarVoluntarioPorId(usuario.getVoluntarioId());
            String nomeVoluntario = (voluntario != null) ? voluntario.getVol_nome() : "Voluntário";

            response.put("success", true);
            response.put("token", token);
            response.put("usuario", Map.of(
                    "email", usuario.getLogin(),
                    "nivelAcesso", usuario.getNivelAcesso(),
                    "voluntarioId", usuario.getVoluntarioId(),
                    "nome", nomeVoluntario
            ));
            return ResponseEntity.ok(response);
        } else {
            response.put("erro", "Senha incorreta.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            String nome = dados.get("nome");
            String cpf = dados.get("cpf");
            String telefone = dados.get("telefone");
            String dataNascimentoStr = dados.get("dataNascimento");
            String email = dados.get("email");
            String senha = dados.get("senha");
            String sexo = dados.getOrDefault("sexo", "O");
            String rua = dados.getOrDefault("rua", "");
            String numero = dados.getOrDefault("numero", "");
            String bairro = dados.getOrDefault("bairro", "");
            String cidade = dados.getOrDefault("cidade", "");
            String cep = dados.getOrDefault("cep", "");
            String uf = dados.getOrDefault("uf", "");
            String complemento = dados.getOrDefault("complemento", "");
            String nivelAcesso = dados.getOrDefault("nivelAcesso", "USER");

            LocalDate dataNascimento = null;
            if (dataNascimentoStr != null && !dataNascimentoStr.isEmpty()) {
                dataNascimento = LocalDate.parse(dataNascimentoStr);
            }

            Login novoLogin = acessoController.registrarVoluntarioCompleto(
                    nome, cpf, telefone, dataNascimento,
                    rua, numero, bairro, cidade, cep, uf, complemento,
                    sexo, email, senha, nivelAcesso
            );

            if (novoLogin != null) {
                response.put("success", true);
                response.put("message", "Conta criada com sucesso!");
                try {
                    emailNotification.enviarEmailBemVindo(email, nome);
                } catch (Exception ignored) {}
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("erro", "Erro ao criar conta. Verifique se o CPF/Email já estão em uso.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("erro", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("erro", "Erro ao criar admin.");
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
            return ResponseEntity.ok(response);
        } else {
            response.put("erro", "Erro ao alterar senha.");
            return ResponseEntity.badRequest().body(response);
        }
    }
}