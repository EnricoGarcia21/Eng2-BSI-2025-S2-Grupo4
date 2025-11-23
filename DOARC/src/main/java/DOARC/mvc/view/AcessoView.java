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


    @PostMapping("/logar")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();
        String email = dados.get("email");
        String senha = dados.get("senha");

        // Validações básicas
        if (email == null || email.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Email é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }

        if (senha == null || senha.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Senha é obrigatória");
            return ResponseEntity.badRequest().body(response);
        }

        // Verifica se usuário existe
        Login usuario = acessoController.buscarPorEmail(email);

        if (usuario == null) {
            response.put("success", false);
            response.put("erro", "Usuário não encontrado. Verifique o email ou crie uma conta.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Verifica se conta está ativa
        if (usuario.getStatus() != 'A') {
            response.put("success", false);
            response.put("erro", "Conta inativa. Entre em contato com o administrador.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Tenta autenticar
        String token = acessoController.autenticarGerarToken(email, senha);

        if (token != null) {
            // Buscar dados do voluntário para pegar o nome
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
            response.put("success", false);
            response.put("erro", "Senha incorreta. Tente novamente.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @PostMapping("/registrar")
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();

        // Extrair dados do request
        String nome = dados.get("nome");
        String cpf = dados.get("cpf");
        String telefone = dados.get("telefone");
        String dataNascimentoStr = dados.get("dataNascimento");

        String rua = dados.getOrDefault("rua", "");
        String numero = dados.getOrDefault("numero", "");
        String bairro = dados.getOrDefault("bairro", "");
        String cidade = dados.getOrDefault("cidade", "");
        String cep = dados.getOrDefault("cep", "");
        String uf = dados.getOrDefault("uf", "");
        String complemento = dados.getOrDefault("complemento", "");

        String email = dados.get("email");
        String senha = dados.get("senha");
        String nivelAcesso = dados.getOrDefault("nivelAcesso", "USER");
        String sexo = dados.getOrDefault("sexo", "O");

        // Validações básicas
        if (nome == null || nome.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Nome é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }

        if (email == null || email.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Email é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }

        if (senha == null || senha.length() < 6) {
            response.put("success", false);
            response.put("erro", "Senha deve ter no mínimo 6 caracteres");
            return ResponseEntity.badRequest().body(response);
        }

        if (telefone == null || telefone.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Telefone é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }

        // Converter data de nascimento
        LocalDate dataNascimento = null;
        try {
            if (dataNascimentoStr != null && !dataNascimentoStr.isBlank()) {
                dataNascimento = LocalDate.parse(dataNascimentoStr);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("erro", "Data de nascimento inválida");
            return ResponseEntity.badRequest().body(response);
        }

        // Chamar controller para registrar
        Login novoLogin = acessoController.registrarVoluntarioCompleto(
                nome, cpf, telefone, dataNascimento,
                rua, numero, bairro, cidade, cep, uf, complemento,
                sexo, email, senha, nivelAcesso
        );

        if (novoLogin != null) {
            response.put("success", true);
            response.put("message", "Conta criada com sucesso! Você será redirecionado para fazer login.");
            response.put("user", Map.of(
                    "email", novoLogin.getLogin(),
                    "role", novoLogin.getNivelAcesso(),
                    "voluntarioId", novoLogin.getVoluntarioId()
            ));

            // Tentar enviar email de boas-vindas (não bloquear em caso de erro)
            try {
                emailNotification.enviarEmailBemVindo(email, nome);
            } catch (Exception ignored) {
                System.err.println("⚠️ Erro ao enviar email de boas-vindas (não crítico)");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            response.put("success", false);
            response.put("erro", "Erro ao criar conta. Email pode já estar cadastrado ou dados inválidos.");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * POST /apis/acesso/registrar-admin
     * Cria admin (uso interno)
     */
    @PostMapping("/registrar-admin")
    public ResponseEntity<Map<String, Object>> registrarAdmin(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();
        String login = dados.get("login");
        String senha = dados.get("senha");

        if (login == null || login.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Login é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }

        if (senha == null || senha.length() < 6) {
            response.put("success", false);
            response.put("erro", "Senha deve ter no mínimo 6 caracteres");
            return ResponseEntity.badRequest().body(response);
        }

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
            response.put("success", false);
            response.put("erro", "Erro ao criar conta de administrador. Login pode já existir.");
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/alterar-senha")
    public ResponseEntity<Map<String, Object>> alterarSenha(@RequestBody Map<String, String> dados) {
        Map<String, Object> response = new HashMap<>();
        String email = dados.get("email");
        String senhaAtual = dados.get("senhaAtual");
        String novaSenha = dados.get("novaSenha");

        if (email == null || email.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Email é obrigatório");
            return ResponseEntity.badRequest().body(response);
        }

        if (senhaAtual == null || senhaAtual.trim().isEmpty()) {
            response.put("success", false);
            response.put("erro", "Senha atual é obrigatória");
            return ResponseEntity.badRequest().body(response);
        }

        if (novaSenha == null || novaSenha.length() < 6) {
            response.put("success", false);
            response.put("erro", "Nova senha deve ter no mínimo 6 caracteres");
            return ResponseEntity.badRequest().body(response);
        }

        boolean sucesso = acessoController.atualizarSenha(email, senhaAtual, novaSenha);

        if (sucesso) {
            response.put("success", true);
            response.put("message", "Senha alterada com sucesso!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("erro", "Senha atual incorreta ou erro ao alterar");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
