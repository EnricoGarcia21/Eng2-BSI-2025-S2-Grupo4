package DOARC.mvc.view;

import DOARC.mvc.controller.CampRespController;
import DOARC.mvc.model.CampResponsavel;
<<<<<<< HEAD
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
=======
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis/campresponsavel")
<<<<<<< HEAD
@CrossOrigin
=======
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
public class CampRespView {

    @Autowired
    private CampRespController controller;

<<<<<<< HEAD
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
=======
    // ============================
    // ✅ VINCULAR VOLUNTÁRIO À CAMPANHA (POST)
    // ============================
    @PostMapping("/registrar")
    public ResponseEntity<Object> add(@RequestBody CampResponsavel campResponsavel) {

        Map<String, Object> json = controller.addCampResponsavel(campResponsavel);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ============================
    // ✅ ALTERAR VINCULAÇÃO (PUT)
    // ============================
    @PutMapping("/alterar")
    public ResponseEntity<Object> alterar(@RequestBody CampResponsavel campResponsavel) {

        Map<String, Object> json = controller.updtCampResponsavel(campResponsavel);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ============================
    // ✅ LISTAR TODAS AS VINCULAÇÕES (GET)
    // ============================
    @GetMapping
    public ResponseEntity<Object> listar() {
        List<Map<String, Object>> lista = controller.getCampResponsavel();
        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma vinculação encontrada"))
                : ResponseEntity.ok(lista);
    }

    // ============================
    // ✅ BUSCAR VINCULAÇÃO ESPECÍFICA (GET)
    // ============================
    @GetMapping("/{camId}/{voluntarioId}")
    public ResponseEntity<Object> get(@PathVariable int camId, @PathVariable int voluntarioId) {
        Map<String, Object> json = controller.getCampResponsavel(camId, voluntarioId);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ============================
    // ✅ BUSCAR COM FILTRO (GET)
    // ============================
    @GetMapping("/filtro")
    public ResponseEntity<Object> getComFiltro(@RequestParam String filtro) {
        List<Map<String, Object>> lista = controller.getCampResponsavel(filtro);
        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma vinculação encontrada com o filtro especificado"))
                : ResponseEntity.ok(lista);
    }

    // ============================
    // ✅ DELETAR VINCULAÇÃO (DELETE)
    // ============================
    @DeleteMapping("/{camId}/{voluntarioId}")
    public ResponseEntity<Object> deletar(@PathVariable int camId, @PathVariable int voluntarioId) {
        Map<String, Object> json = controller.deletarCampResponsavel(camId, voluntarioId);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()));
    }

    // ============================
    // ✅ LISTAR CAMPANHAS POR VOLUNTÁRIO (GET)
    // ============================
    @GetMapping("/voluntario/{voluntarioId}")
    public ResponseEntity<Object> porVoluntario(@PathVariable int voluntarioId) {
        List<Map<String, Object>> lista = controller.getCampanhasPorVoluntario(voluntarioId);

        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada para esse voluntário"))
                : ResponseEntity.ok(lista);
    }

    // ============================
    // ✅ LISTAR VOLUNTÁRIOS POR CAMPANHA (GET)
    // ============================
    @GetMapping("/campanha/{campanhaId}")
    public ResponseEntity<Object> porCampanha(@PathVariable int campanhaId) {
        List<Map<String, Object>> lista = controller.getVoluntariosPorCampanha(campanhaId);

        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhum voluntário encontrado para essa campanha"))
                : ResponseEntity.ok(lista);
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    }
}