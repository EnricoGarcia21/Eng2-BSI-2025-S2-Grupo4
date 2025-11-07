package DOARC.mvc.view;

import DOARC.mvc.controller.VoluntarioController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/voluntarios")
public class VoluntarioView {

    @Autowired
    private VoluntarioController voluntarioController;

    // GET - Buscar voluntário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getVoluntario(@PathVariable int id) {
        Map<String, Object> json = voluntarioController.getVoluntario(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // GET - Listar todos os voluntários
    @GetMapping
    public ResponseEntity<Object> listarVoluntarios() {
        Map<String, Object> json = voluntarioController.listarVoluntarios();
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // GET - Buscar voluntários por cidade
    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<Object> getVoluntariosPorCidade(@PathVariable String cidade) {
        Map<String, Object> json = voluntarioController.getVoluntariosPorCidade(cidade);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // GET - Buscar apenas nome do voluntário (para dropdowns)
    @GetMapping("/{id}/nome")
    public ResponseEntity<Object> getNomeVoluntario(@PathVariable int id) {
        Map<String, Object> json = voluntarioController.getNomeVoluntario(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}