package DOARC.mvc.view;

import DOARC.mvc.controller.CampRespController;
import DOARC.mvc.model.CampResponsavel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis/campresponsavel")
@CrossOrigin
public class CampRespView {

    @Autowired
    private CampRespController controller;

    // Helper para verificar se é Admin
    private boolean isAdmin(HttpServletRequest request) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        String role = (String) request.getAttribute("role");
        return authenticated != null && authenticated && ("ADMIN".equalsIgnoreCase(role));
    }

    // ============================
    // ✅ VINCULAR VOLUNTÁRIO À CAMPANHA (POST) - PROTEGIDO
    // ============================
    @PostMapping("/registrar")
    public ResponseEntity<Object> add(@RequestBody CampResponsavel campResponsavel, HttpServletRequest request) {
        // 🔒 PROTEÇÃO
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Acesso negado: Apenas administradores podem gerenciar equipes."));
        }

        Map<String, Object> json = controller.addCampResponsavel(campResponsavel);

        if (json.containsKey("erro")) {
            return ResponseEntity.badRequest().body(json);
        }
        return ResponseEntity.ok(json);
    }

    // ============================
    // ✅ ALTERAR VINCULAÇÃO (PUT) - PROTEGIDO
    // ============================
    @PutMapping("/alterar")
    public ResponseEntity<Object> alterar(@RequestBody CampResponsavel campResponsavel, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("erro", "Acesso negado"));
        }

        Map<String, Object> json = controller.updtCampResponsavel(campResponsavel);
        if (json.containsKey("erro")) return ResponseEntity.badRequest().body(json);
        return ResponseEntity.ok(json);
    }

    // ============================
    // ✅ DELETAR VINCULAÇÃO (DELETE) - PROTEGIDO
    // ============================
    @DeleteMapping("/{camId}/{voluntarioId}")
    public ResponseEntity<Object> deletar(@PathVariable int camId, @PathVariable int voluntarioId, HttpServletRequest request) {
        // 🔒 PROTEÇÃO
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", "Acesso negado: Apenas administradores podem remover membros."));
        }

        Map<String, Object> json = controller.deletarCampResponsavel(camId, voluntarioId);

        if (json.containsKey("erro")) {
            return ResponseEntity.badRequest().body(json);
        }
        return ResponseEntity.ok(json);
    }

    // ===== MÉTODOS DE LEITURA (Públicos para logados) =====

    @GetMapping
    public ResponseEntity<Object> listar() {
        List<Map<String, Object>> lista = controller.getCampResponsavel();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{camId}/{voluntarioId}")
    public ResponseEntity<Object> get(@PathVariable int camId, @PathVariable int voluntarioId) {
        Map<String, Object> json = controller.getCampResponsavel(camId, voluntarioId);
        if (json.containsKey("erro")) return ResponseEntity.badRequest().body(json);
        return ResponseEntity.ok(json);
    }

    @GetMapping("/voluntario/{voluntarioId}")
    public ResponseEntity<Object> porVoluntario(@PathVariable int voluntarioId) {
        List<Map<String, Object>> lista = controller.getCampanhasPorVoluntario(voluntarioId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/campanha/{campanhaId}")
    public ResponseEntity<Object> porCampanha(@PathVariable int campanhaId) {
        List<Map<String, Object>> lista = controller.getVoluntariosPorCampanha(campanhaId);
        return ResponseEntity.ok(lista);
    }
}