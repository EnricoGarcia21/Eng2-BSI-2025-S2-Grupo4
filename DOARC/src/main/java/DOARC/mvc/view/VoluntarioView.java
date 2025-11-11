package DOARC.mvc.view;

import DOARC.mvc.controller.VoluntarioController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/apis/voluntario")
public class VoluntarioView {
    @Autowired
    private VoluntarioController voluntarioController;

    @PostMapping
    public ResponseEntity<Object> addVoluntario(@RequestBody Map<String, Object> dados) {

        Map<String, Object> resultado = voluntarioController.addVoluntario(dados);

        return resultado.containsKey("erro")
                ? ResponseEntity.badRequest().body(resultado)
                : ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateVoluntario(@PathVariable int id,
                                                   @RequestBody Map<String, Object> dados) {

        Map<String, Object> resultado = voluntarioController.updtVoluntario(id, dados);

        return resultado.containsKey("erro")
                ? ResponseEntity.badRequest().body(resultado)
                : ResponseEntity.ok(resultado);
    }

    @GetMapping
    public ResponseEntity<Object> listar() {
        return ResponseEntity.ok(voluntarioController.getVoluntarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscar(@PathVariable int id) {

        Map<String, Object> json = voluntarioController.getVoluntarioById(id);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(json)
                : ResponseEntity.ok(json);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletar(@PathVariable int id) {

        Map<String, Object> json = voluntarioController.deletarVoluntario(id);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(json)
                : ResponseEntity.ok(json);
    }
}
