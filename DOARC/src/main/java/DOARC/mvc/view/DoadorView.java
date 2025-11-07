package DOARC.mvc.view;

import DOARC.mvc.controller.DoadorController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/doadores")
public class DoadorView {

    @Autowired
    private DoadorController doadorController;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDoador(@PathVariable int id) {
        Map<String, Object> json = doadorController.getDoador(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> listarDoadores() {
        Map<String, Object> json = doadorController.listarDoadores();
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/cidade/{cidade}")
    public ResponseEntity<Object> getDoadoresPorCidade(@PathVariable String cidade) {
        Map<String, Object> json = doadorController.getDoadoresPorCidade(cidade);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/bairro/{bairro}")
    public ResponseEntity<Object> getDoadoresPorBairro(@PathVariable String bairro) {
        Map<String, Object> json = doadorController.getDoadoresPorBairro(bairro);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/{id}/nome")
    public ResponseEntity<Object> getNomeDoador(@PathVariable int id) {
        Map<String, Object> json = doadorController.getNomeDoador(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}