package DOARC.mvc.view;

import DOARC.mvc.controller.VoluntarioController;
import DOARC.mvc.controller.AcessoController;
import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/apis/voluntario")
@CrossOrigin
public class VoluntarioView {

    @Autowired
    private VoluntarioController voluntarioController;

    @Autowired
    private AcessoController acessoController;

    private boolean podeAcessar(HttpServletRequest request, int voluntarioId) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        if (authenticated == null || !authenticated) return false;

        String role = (String) request.getAttribute("role");
        if ("ADMIN".equalsIgnoreCase(role) || "Administrador".equalsIgnoreCase(role)) return true;

        String email = (String) request.getAttribute("email");
        if (email != null) {
            try {
                Login usuarioLogado = acessoController.buscarPorEmail(email);
                if (usuarioLogado != null) return usuarioLogado.getVoluntarioId() == voluntarioId;
            } catch (Exception e) { return false; }
        }
        return false;
    }

    private boolean isAdmin(HttpServletRequest request) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        String role = (String) request.getAttribute("role");
        return authenticated != null && authenticated && ("ADMIN".equalsIgnoreCase(role));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarTodos(HttpServletRequest request) {
        try {
            if (!isAdmin(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
            }

            // CORREÇÃO: Converte List<Voluntario> para List<Map>
            List<Voluntario> listaEntidades = voluntarioController.listarTodos();
            List<Map<String, Object>> listaMap = listaEntidades.stream()
                    .map(v -> voluntarioController.voluntarioToMap(v))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(listaMap);
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar voluntários: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody Voluntario voluntario, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!isAdmin(request)) {
                response.put("erro", "Acesso negado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // O controller retorna o objeto gravado
            Voluntario gravado = voluntarioController.cadastrarVoluntario(voluntario);

            if (gravado != null && gravado.getVol_id() > 0) {
                response.put("success", true);
                response.put("message", "Voluntário cadastrado com sucesso!");
                response.put("id", gravado.getVol_id());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("erro", "Falha ao gravar voluntário (CPF duplicado ou erro no banco)");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("erro", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(@PathVariable int id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!isAdmin(request)) {
                response.put("erro", "Acesso negado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            boolean removido = voluntarioController.removerVoluntario(id);
            if (removido) {
                response.put("success", true);
                response.put("message", "Voluntário removido com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Voluntário não encontrado ou erro ao remover");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> obterPerfil(@PathVariable int id, HttpServletRequest request) {
        try {
            if (!podeAcessar(request, id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", "Acesso negado"));
            }
            Map<String, Object> voluntario = voluntarioController.getVoluntario(id);
            if (voluntario.containsKey("erro")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(voluntario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro interno"));
        }
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> atualizarPerfil(@PathVariable int id, @RequestBody Map<String, Object> dados, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!podeAcessar(request, id)) {
                response.put("erro", "Acesso negado");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            Voluntario existente = voluntarioController.buscarVoluntarioPorId(id);
            if (existente == null) {
                response.put("erro", "Voluntário não encontrado");
                return ResponseEntity.notFound().build();
            }

            if (dados.containsKey("vol_nome")) existente.setVol_nome((String) dados.get("vol_nome"));
            if (dados.containsKey("vol_telefone")) existente.setVol_telefone((String) dados.get("vol_telefone"));
            if (dados.containsKey("vol_bairro")) existente.setVol_bairro((String) dados.get("vol_bairro"));
            if (dados.containsKey("vol_cidade")) existente.setVol_cidade((String) dados.get("vol_cidade"));

            Voluntario atualizado = voluntarioController.atualizarPerfil(existente);

            if (atualizado != null) {
                response.put("success", true);
                response.put("message", "Perfil atualizado com sucesso!");
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Erro ao atualizar perfil");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/equipe")
    public ResponseEntity<List<Map<String, Object>>> listarEquipe(HttpServletRequest request) {
        try {
            Boolean authenticated = (Boolean) request.getAttribute("authenticated");
            if (authenticated == null || !authenticated) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // CORREÇÃO: Converte List<Voluntario> para List<Map>
            List<Voluntario> listaEntidades = voluntarioController.listarTodos();
            List<Map<String, Object>> voluntarios = listaEntidades.stream()
                    .map(v -> voluntarioController.voluntarioToMap(v))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(voluntarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @GetMapping("/dashboard/{voluntarioId}")
    public ResponseEntity<Map<String, Object>> obterDashboard(@PathVariable int voluntarioId, HttpServletRequest request) {
        try {
            if (!podeAcessar(request, voluntarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", "Acesso negado"));
            }
            Map<String, Object> dashboard = new HashMap<>();
            Voluntario voluntario = voluntarioController.buscarVoluntarioPorId(voluntarioId);

            if (voluntario != null) {
                dashboard.put("nome", voluntario.getVol_nome());
                dashboard.put("email", voluntario.getVol_email());
            }
            dashboard.put("totalCampanhas", 0);
            dashboard.put("campanhasAtivas", 0);
            dashboard.put("ultimoLogin", new java.util.Date());

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("erro", "Erro interno"));
        }
    }
}