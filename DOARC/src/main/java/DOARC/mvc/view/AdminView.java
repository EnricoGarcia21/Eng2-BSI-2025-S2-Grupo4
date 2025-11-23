package DOARC.mvc.view;

import DOARC.mvc.controller.AdminController;
import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/apis/admin")
<<<<<<< HEAD
@CrossOrigin
=======
@CrossOrigin(origins = "*")
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
public class AdminView {


    @Autowired
    private AdminController adminController;

    @Autowired
    private Login loginModel;

    @Autowired
    private Voluntario voluntarioModel;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    // ===== GESTÃO DE VOLUNTÁRIOS =====

    @GetMapping("/voluntarios")
    public ResponseEntity<List<Map<String, Object>>> listarVoluntarios() {
        try {
            List<Map<String, Object>> voluntarios = adminController.listarUsuarios();
            return ResponseEntity.ok(voluntarios);
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar voluntários: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @GetMapping("/voluntarios/{id}")
    public ResponseEntity<Map<String, Object>> obterVoluntario(@PathVariable int id) {
        try {
            Map<String, Object> voluntario = adminController.getVoluntario(id);

            if (voluntario.containsKey("erro")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(voluntario);
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter voluntário: " + e.getMessage());
            Map<String, Object> error = Map.of("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/voluntarios/{id}")
    public ResponseEntity<Map<String, Object>> atualizarVoluntario(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
<<<<<<< HEAD
            // Criar objeto Voluntario (apenas campos que existem no DB)
            Voluntario voluntario = new Voluntario();
            voluntario.setVol_id(id);
            voluntario.setVol_nome((String) dados.get("vol_nome"));
            voluntario.setVol_telefone((String) dados.get("vol_telefone"));
            voluntario.setVol_email((String) dados.get("vol_email"));
            voluntario.setVol_cidade((String) dados.get("vol_cidade"));
            voluntario.setVol_bairro((String) dados.get("vol_bairro"));
=======
            // Criar objeto Voluntario
            Voluntario voluntario = new Voluntario();
            voluntario.setVol_id(id);
            voluntario.setVol_nome((String) dados.get("vol_nome"));
            voluntario.setVol_cpf((String) dados.get("vol_cpf"));
            voluntario.setVol_email((String) dados.get("vol_email"));
            voluntario.setVol_telefone((String) dados.get("vol_telefone"));
            voluntario.setVol_datanasc((String) dados.get("vol_datanasc"));
            voluntario.setVol_rua((String) dados.get("vol_rua"));
            voluntario.setVol_bairro((String) dados.get("vol_bairro"));
            voluntario.setVol_cidade((String) dados.get("vol_cidade"));
            voluntario.setVol_numero((String) dados.get("vol_numero"));
            voluntario.setVol_cep((String) dados.get("vol_cep"));
            voluntario.setVol_uf((String) dados.get("vol_uf"));
            voluntario.setVol_sexo((String) dados.get("vol_sexo"));
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

            Map<String, Object> resultado = adminController.updtVoluntario(voluntario);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Voluntário atualizado com sucesso!");
            response.put("voluntario", resultado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar voluntário: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/voluntarios/{id}")
    public ResponseEntity<Map<String, Object>> removerVoluntario(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> resultado = adminController.deletarVoluntario(id);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Voluntário removido com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao remover voluntário: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== GESTÃO DE LOGINS =====

    @GetMapping("/logins")
    public ResponseEntity<List<Map<String, Object>>> listarLogins() {
        try {
            List<Login> logins = loginModel.consultar("", getConexao());
            List<Map<String, Object>> response = new ArrayList<>();

            for (Login login : logins) {
                Map<String, Object> loginMap = new HashMap<>();
                loginMap.put("loginId", login.getLoginId());
                loginMap.put("voluntarioId", login.getVoluntarioId());
                loginMap.put("login", login.getLogin());
                loginMap.put("nivelAcesso", login.getNivelAcesso());
                loginMap.put("status", login.getStatus());
                response.add(loginMap);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar logins: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @PutMapping("/logins/{voluntarioId}/status")
    public ResponseEntity<Map<String, Object>> alterarStatusLogin(@PathVariable int voluntarioId, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            String novoStatus = (String) dados.get("status");

            if (novoStatus == null || novoStatus.length() != 1) {
                response.put("erro", "Status deve ser um caractere (A=Ativo, I=Inativo)");
                return ResponseEntity.badRequest().body(response);
            }

            boolean sucesso = loginModel.atualizarStatus(voluntarioId, novoStatus.charAt(0), getConexao());

            if (sucesso) {
                response.put("success", true);
                response.put("message", "Status do login atualizado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Erro ao atualizar status do login");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao alterar status do login: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/logins/{voluntarioId}/nivel")
    public ResponseEntity<Map<String, Object>> alterarNivelAcesso(@PathVariable int voluntarioId, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            String novoNivel = (String) dados.get("nivelAcesso");

            if (novoNivel == null || (!novoNivel.equals("USER") && !novoNivel.equals("ADMIN"))) {
                response.put("erro", "Nível de acesso deve ser USER ou ADMIN");
                return ResponseEntity.badRequest().body(response);
            }

            Login login = loginModel.consultar(voluntarioId, getConexao());
            if (login == null) {
                response.put("erro", "Login não encontrado");
                return ResponseEntity.notFound().build();
            }

            login.setNivelAcesso(novoNivel);
            Login atualizado = loginModel.alterar(login, getConexao());

            if (atualizado != null) {
                response.put("success", true);
                response.put("message", "Nível de acesso atualizado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Erro ao atualizar nível de acesso");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao alterar nível de acesso: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== ESTATÍSTICAS =====

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // Contar voluntários
            List<Voluntario> voluntarios = voluntarioModel.consultar("", getConexao());
            stats.put("totalVoluntarios", voluntarios.size());

            // Contar logins ativos
            List<Login> logins = loginModel.consultar("", getConexao());
            long loginsAtivos = logins.stream().filter(l -> l.getStatus() == 'A').count();
            long admins = logins.stream().filter(l -> "ADMIN".equals(l.getNivelAcesso())).count();

            stats.put("loginsAtivos", loginsAtivos);
            stats.put("totalAdmins", admins);
            stats.put("totalUsers", logins.size() - admins);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            System.err.println("❌ Erro ao obter estatísticas: " + e.getMessage());
            Map<String, Object> error = Map.of("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
