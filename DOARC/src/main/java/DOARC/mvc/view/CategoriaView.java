package DOARC.mvc.view;

import DOARC.mvc.control.CategoriaControl;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/categoria")
public class CategoriaView {

    @Autowired
    private CategoriaControl categoriaControl;

    @PostMapping
    public ResponseEntity<Object> addCategoria(@RequestParam String cat_nome,
                                               @RequestParam(required = false, defaultValue = "") String cat_especificacao) {
        Map<String, Object> json = categoriaControl.addCategoria(cat_nome, cat_especificacao);

        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping
    public ResponseEntity<Object> updtCategoria(@RequestParam int cat_id,
                                                @RequestParam String cat_nome,
                                                @RequestParam(required = false, defaultValue = "") String cat_especificacao) {
        Map<String, Object> json = categoriaControl.updtCategoria(cat_id, cat_nome, cat_especificacao);

        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(required = false) String filtro) {
        List<Map<String, Object>> lista;

        if (filtro != null && !filtro.trim().isEmpty()) {
            lista = categoriaControl.buscarCategorias(filtro);
        } else {
            lista = categoriaControl.getCategorias();
        }

        return ResponseEntity.ok(lista != null ? lista : new ArrayList<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCategoriaId(@PathVariable int id) {
        Map<String, Object> json = categoriaControl.getCategoria(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarCategoria(@PathVariable int id) {
        Map<String, Object> json = categoriaControl.deletarCategoria(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
