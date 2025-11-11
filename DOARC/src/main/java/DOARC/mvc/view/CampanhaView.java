package DOARC.mvc.view;

import DOARC.mvc.controller.CampanhaController;
import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis/campanha")
public class CampanhaView {

    @Autowired
    private CampanhaController controller;

    // ============================
    // ✅ REGISTRAR CAMPANHA (POST) - Recebe objeto Campanha
    // ============================
    @PostMapping("/registrar")
    public ResponseEntity<Object> add(@RequestBody Campanha campanha) {

        // Chama o método atualizado do Controller
        Map<String, Object> json = controller.addCampanha(campanha);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ============================
    // ✅ ALTERAR CAMPANHA (PUT) - Recebe objeto Campanha
    // ============================
    @PutMapping("/alterar")
    public ResponseEntity<Object> alterar(@RequestBody Campanha campanha) {

        // Chama o método atualizado do Controller
        Map<String, Object> json = controller.updtCampanha(campanha);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ... (Métodos listar, get, deletar, porVoluntario permanecem iguais)

    @GetMapping
    public ResponseEntity<Object> listar() {
        List<Map<String, Object>> lista = controller.getCampanha();
        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada"))
                : ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable int id) {
        Map<String,Object> json = controller.getCampanha(id);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable int id) {
        Map<String,Object> json = controller.deletarCampanha(id);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()));
    }

    @GetMapping("/voluntario/{id}")
    public ResponseEntity<Object> porVoluntario(@PathVariable int id) {
        List<Map<String,Object>> lista = controller.getCampanhasPorVoluntario(id);

        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada para esse voluntário"))
                : ResponseEntity.ok(lista);
    }
}