package DOARC.mvc.view;

import DOARC.mvc.controller.AdminController;
import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
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
@CrossOrigin
public class AdminView {

    @Autowired
    private AdminController adminController;

    @Autowired
    private Login loginModel;

    @Autowired
    private Voluntario voluntarioModel;

    // Conexão REMOVIDA

    @GetMapping("/voluntarios")
    public ResponseEntity<List<Map<String, Object>>> listarVoluntarios() {
        try {
            List<Map<String, Object>> voluntarios = adminController.listarUsuarios();
            return ResponseEntity.ok(voluntarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @GetMapping("/voluntarios/{id}")
    public ResponseEntity<Map<String, Object>> obterVoluntario(@PathVariable int id) {
        try {
            Map<String, Object> voluntario = adminController.getVoluntario(id);
            if (voluntario.containsKey("erro")) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(voluntario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro interno"));
        }
    }

    @PutMapping("/voluntarios/{id}")
    public ResponseEntity<Map<String, Object>> atualizarVoluntario(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();
        try {
            Voluntario voluntario = new Voluntario();
            voluntario.setVol_id(id);
            voluntario.setVol_nome((String) dados.get("vol_nome"));
            voluntario.setVol_telefone((String) dados.get("vol_telefone"));
            voluntario.setVol_email((String) dados.get("vol_email"));
            voluntario.setVol_cidade((String) dados.get("vol_cidade"));
            voluntario.setVol_bairro((String) dados.get("vol_bairro"));

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
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/logins")
    public ResponseEntity<List<Map<String, Object>>> listarLogins() {
        try {
            // Correção: Chamada sem conexão
            List<Login> logins = loginModel.consultar("");
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @PutMapping("/logins/{voluntarioId}/status")
    public ResponseEntity<Map<String, Object>> alterarStatusLogin(@PathVariable int voluntarioId, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();
        try {
            String novoStatus = (String) dados.get("status");
            if (novoStatus == null || novoStatus.length() != 1) {
                response.put("erro", "Status inválido");
                return ResponseEntity.badRequest().body(response);
            }
            // Correção: Chamada sem conexão
            boolean sucesso = loginModel.atualizarStatus(voluntarioId, novoStatus.charAt(0));

            if (sucesso) {
                response.put("success", true);
                response.put("message", "Status atualizado!");
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Erro ao atualizar status");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("erro", "Erro interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/logins/{voluntarioId}/nivel")
    public ResponseEntity<Map<String, Object>> alterarNivelAcesso(@PathVariable int voluntarioId, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();
        try {
            String novoNivel = (String) dados.get("nivelAcesso");
            if (novoNivel == null) {
                response.put("erro", "Nível inválido");
                return ResponseEntity.badRequest().body(response);
            }
            // Correção: Chamada sem conexão
            Login login = loginModel.consultar(voluntarioId);
            if (login == null) {
                response.put("erro", "Login não encontrado");
                return ResponseEntity.notFound().build();
            }
            login.setNivelAcesso(novoNivel);
            Login atualizado = loginModel.alterar(login);

            if (atualizado != null) {
                response.put("success", true);
                response.put("message", "Nível atualizado!");
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Erro ao atualizar");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("erro", "Erro interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        try {
            Map<String, Object> stats = new HashMap<>();
            // Correção: Chamadas sem conexão
            List<Voluntario> voluntarios = voluntarioModel.consultar("");
            stats.put("totalVoluntarios", voluntarios.size());

            List<Login> logins = loginModel.consultar("");
            long loginsAtivos = logins.stream().filter(l -> l.getStatus() == 'A').count();
            long admins = logins.stream().filter(l -> "ADMIN".equals(l.getNivelAcesso())).count();

            stats.put("loginsAtivos", loginsAtivos);
            stats.put("totalAdmins", admins);
            stats.put("totalUsers", logins.size() - admins);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro interno"));
        }
    }
}