package DOARC.mvc.view;

import DOARC.mvc.controller.VoluntarioController;
import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis/voluntario")
@CrossOrigin
public class VoluntarioView {

    @Autowired
    private VoluntarioController voluntarioController;

    @Autowired
    private Login loginModel;

    private boolean podeAcessar(HttpServletRequest request, int voluntarioId) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        if (authenticated == null || !authenticated) return false;
        String role = (String) request.getAttribute("role");
        if ("ADMIN".equalsIgnoreCase(role) || "Administrador".equalsIgnoreCase(role)) return true;
        String email = (String) request.getAttribute("email");
        if (email != null) {
            try {
                Login usuarioLogado = loginModel.buscarPorLogin(email, SingletonDB.conectar());
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

            List<Voluntario> voluntarios = voluntarioController.listarTodos();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Voluntario v : voluntarios) {
                Map<String, Object> map = new HashMap<>();
                map.put("vol_id", v.getVol_id());
                map.put("vol_nome", v.getVol_nome());
                map.put("vol_telefone", v.getVol_telefone());
                map.put("vol_email", v.getVol_email());
                map.put("vol_cidade", v.getVol_cidade());
                map.put("vol_bairro", v.getVol_bairro());
                result.add(map);
            }

            return ResponseEntity.ok(result);

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

            // 1. Cria APENAS o Voluntário (sem login automático)
            Voluntario criado = voluntarioController.cadastrarVoluntario(voluntario);

            if (criado != null && criado.getVol_id() > 0) {
                response.put("success", true);
                response.put("message", "Voluntário cadastrado com sucesso! (Aguardando criação de credenciais pelo usuário)");
                response.put("vol_id", criado.getVol_id());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                response.put("erro", "Erro ao criar voluntário");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao criar voluntário: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletar(
            @PathVariable int id,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Apenas admins podem deletar voluntários
            if (!isAdmin(request)) {
                response.put("erro", "Acesso negado");
                response.put("codigo", "FORBIDDEN");
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
            System.err.println("❌ Erro ao remover voluntário: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== PERFIL DO VOLUNTÁRIO =====

    @GetMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> obterPerfil(
            @PathVariable int id,
            HttpServletRequest request) {
        try {
            // ✅ CORREÇÃO IDOR: Verifica se o usuário pode acessar este perfil
            if (!podeAcessar(request, id)) {
                Map<String, Object> error = Map.of(
                    "erro", "Acesso negado",
                    "codigo", "FORBIDDEN"
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Map<String, Object> voluntario = voluntarioController.getVoluntario(id);

            if (voluntario.containsKey("erro")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(voluntario);
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter perfil: " + e.getMessage());
            Map<String, Object> error = Map.of("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> atualizarPerfil(
            @PathVariable int id,
            @RequestBody Map<String, Object> dados,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ CORREÇÃO IDOR: Verifica se o usuário pode atualizar este perfil
            if (!podeAcessar(request, id)) {
                response.put("erro", "Acesso negado");
                response.put("codigo", "FORBIDDEN");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // ✅ Agora usa o controller para buscar
            Voluntario existente = voluntarioController.buscarVoluntarioPorId(id);

            if (existente == null) {
                response.put("erro", "Voluntário não encontrado");
                return ResponseEntity.notFound().build();
            }

            // Atualizar apenas campos que existem no DB
            if (dados.containsKey("vol_nome")) {
                existente.setVol_nome((String) dados.get("vol_nome"));
            }
            if (dados.containsKey("vol_telefone")) {
                existente.setVol_telefone((String) dados.get("vol_telefone"));
            }
            if (dados.containsKey("vol_bairro")) {
                existente.setVol_bairro((String) dados.get("vol_bairro"));
            }
            if (dados.containsKey("vol_cidade")) {
                existente.setVol_cidade((String) dados.get("vol_cidade"));
            }

            // ✅ Agora usa o controller para atualizar
            Voluntario atualizado = voluntarioController.atualizarPerfil(existente);

            if (atualizado != null) {
                response.put("success", true);
                response.put("message", "Perfil atualizado com sucesso!");

                // Retornar dados atualizados (apenas campos que existem no DB)
                Map<String, Object> voluntarioMap = new HashMap<>();
                voluntarioMap.put("vol_id", atualizado.getVol_id());
                voluntarioMap.put("vol_nome", atualizado.getVol_nome());
                voluntarioMap.put("vol_telefone", atualizado.getVol_telefone());
                voluntarioMap.put("vol_email", atualizado.getVol_email());
                voluntarioMap.put("vol_cidade", atualizado.getVol_cidade());
                voluntarioMap.put("vol_bairro", atualizado.getVol_bairro());

                response.put("voluntario", voluntarioMap);
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Erro ao atualizar perfil");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar perfil: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== CAMPANHAS DO VOLUNTÁRIO =====

    // Injete o CampanhaController se ele existir
    // @Autowired
    // private CampanhaController campanhaController;

    @GetMapping("/minhas-campanhas/{voluntarioId}")
    public ResponseEntity<List<Map<String, Object>>> obterMinhasCampanhas(
            @PathVariable int voluntarioId,
            HttpServletRequest request) {
        try {
            // ✅ CORREÇÃO IDOR: Verifica se o usuário pode acessar as campanhas deste voluntário
            if (!podeAcessar(request, voluntarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ArrayList<>());
            }


            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter campanhas do voluntário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
// ===== ÁREA DO VOLUNTÁRIO (PÚBLICO PARA LOGADOS) =====

    @GetMapping("/equipe")
    public ResponseEntity<List<Map<String, Object>>> listarEquipe(HttpServletRequest request) {
        try {
            // Apenas verifica se está logado (não precisa ser ADMIN)
            Boolean authenticated = (Boolean) request.getAttribute("authenticated");
            if (authenticated == null || !authenticated) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<Voluntario> voluntarios = voluntarioController.listarTodos();
            List<Map<String, Object>> result = new ArrayList<>();

            for (Voluntario v : voluntarios) {
                Map<String, Object> map = new HashMap<>();
                // Retorna apenas dados públicos de contato
                map.put("vol_id", v.getVol_id());
                map.put("vol_nome", v.getVol_nome());
                map.put("vol_email", v.getVol_email());
                map.put("vol_telefone", v.getVol_telefone());
                result.add(map);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            System.err.println("❌ Erro ao listar equipe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
    // ===== DASHBOARD DO VOLUNTÁRIO =====

    @GetMapping("/dashboard/{voluntarioId}")
    public ResponseEntity<Map<String, Object>> obterDashboard(
            @PathVariable int voluntarioId,
            HttpServletRequest request) {
        try {
            // ✅ CORREÇÃO IDOR: Verifica se o usuário pode acessar o dashboard deste voluntário
            if (!podeAcessar(request, voluntarioId)) {
                Map<String, Object> error = Map.of(
                    "erro", "Acesso negado",
                    "codigo", "FORBIDDEN"
                );
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }

            Map<String, Object> dashboard = new HashMap<>();

            // ✅ Agora usa o controller
            Voluntario voluntario = voluntarioController.buscarVoluntarioPorId(voluntarioId);

            if (voluntario != null) {
                dashboard.put("nome", voluntario.getVol_nome());
                dashboard.put("email", voluntario.getVol_email());
            }

            // Estatísticas básicas (pode ser expandido)
            dashboard.put("totalCampanhas", 0); // Implementar contagem se necessário
            dashboard.put("campanhasAtivas", 0);
            dashboard.put("ultimoLogin", new Date());

            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            System.err.println("❌ Erro ao obter dashboard: " + e.getMessage());
            Map<String, Object> error = Map.of("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}