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

        try {
            String email = dados.get("email");
            String senha = dados.get("senha");

            System.out.println("🔐 Tentativa de login: " + email);

            if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
                response.put("erro", "Email e senha são obrigatórios");
                return ResponseEntity.badRequest().body(response);
            }

            String token = acessoController.autenticarGerarToken(email, senha);

            if (token != null) {
                // Buscar dados do usuário para retornar
                Login usuario = acessoController.buscarPorEmail(email);

                response.put("success", true);
                response.put("message", "Login realizado com sucesso");
                response.put("token", token);
                response.put("user", Map.of(
                        "email", usuario.getLogin(),
                        "role", usuario.getNivelAcesso(),
                        "voluntarioId", usuario.getVoluntarioId()
                ));

                System.out.println("✅ Login bem-sucedido: " + email);
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Email ou senha incorretos");
                System.out.println("❌ Login falhou: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro no login: " + e.getMessage());
            e.printStackTrace();
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            String nome = dados.get("nome");
            String cpf = dados.get("cpf");
            String telefone = dados.get("telefone");
            String dataNascimento = dados.get("dataNascimento");
            String endereco = dados.get("endereco");
            String email = dados.get("email");
            String senha = dados.get("senha");
            String nivelAcesso = dados.getOrDefault("nivelAcesso", "USER");

            System.out.println("📝 Tentativa de registro: " + email);

            // Converter string para LocalDate
            LocalDate dataNasc = null;
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                dataNasc = LocalDate.parse(dataNascimento);
            }

            Login novoLogin = acessoController.registrarVoluntarioCompleto(
                    nome, cpf, telefone, dataNasc, endereco, email, senha, nivelAcesso
            );

            if (novoLogin != null) {
                response.put("success", true);
                response.put("message", "Cadastro realizado com sucesso!");
                response.put("user", Map.of(
                        "email", novoLogin.getLogin(),
                        "role", novoLogin.getNivelAcesso(),
                        "voluntarioId", novoLogin.getVoluntarioId()
                ));

                // Enviar email de boas-vindas (assíncrono)
                try {
                    emailNotification.enviarEmailBemVindo(email, nome);
                } catch (Exception emailError) {
                    System.err.println("⚠️ Erro ao enviar email de boas-vindas: " + emailError.getMessage());
                }

                System.out.println("✅ Registro bem-sucedido: " + email);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("erro", "Erro ao criar conta. Verifique os dados e tente novamente.");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro no registro: " + e.getMessage());
            e.printStackTrace();
            response.put("erro", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/registrar-admin")
    public ResponseEntity<Map<String, Object>> registrarAdmin(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            String login = dados.get("login");
            String senha = dados.get("senha");

            System.out.println("👑 Tentativa de registro de admin: " + login);

            Login novoAdmin = acessoController.registrarAdmin(login, senha);

            if (novoAdmin != null) {
                response.put("success", true);
                response.put("message", "Admin cadastrado com sucesso!");
                response.put("user", Map.of(
                        "email", novoAdmin.getLogin(),
                        "role", novoAdmin.getNivelAcesso(),
                        "voluntarioId", novoAdmin.getVoluntarioId()
                ));

                System.out.println("✅ Admin registrado: " + login);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("erro", "Erro ao criar conta de administrador");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro no registro de admin: " + e.getMessage());
            e.printStackTrace();
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/alterar-senha")
    public ResponseEntity<Map<String, Object>> alterarSenha(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
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

        } catch (Exception e) {
            System.err.println("❌ Erro ao alterar senha: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
