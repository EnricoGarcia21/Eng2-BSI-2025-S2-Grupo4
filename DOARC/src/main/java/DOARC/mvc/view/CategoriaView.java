package DOARC.mvc.view;

import DOARC.mvc.controller.CategoriaController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/categoria")
public class CategoriaView {

    @Autowired
    private CategoriaController categoriaController;

    @PostMapping
    public ResponseEntity<Object> addCategoria(@RequestParam String cat_nome) {
        Map<String, Object> json = categoriaController.addCategoria(cat_nome);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Categoria cadastrada com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping
    public ResponseEntity<Object> updtCategoria(@RequestParam int cat_id,
                                                @RequestParam String cat_nome) {
        Map<String, Object> json = categoriaController.updtCategoria(cat_id, cat_nome);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Categoria alterada com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = categoriaController.getCategorias();
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.badRequest().body(new Mensagem("Nenhuma categoria encontrada."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCategoriaId(@PathVariable int id) {
        Map<String, Object> json = categoriaController.getCategoria(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarCategoria(@PathVariable int id) {
        Map<String, Object> json = categoriaController.deletarCategoria(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem("Categoria excluída com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
