package DOARC.mvc.view;

import DOARC.mvc.controller.VerificarNecessidadeController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/verificacao")
public class VerificarNecessidadeView {

    @Autowired
    private VerificarNecessidadeController verificacaoController;

    // --- LISTAR TODOS (GET) ---
    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(required = false) String filtro) {
        List<Map<String, Object>> lista = verificacaoController.getVerificacao(filtro);
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.ok(new Mensagem("Nenhum registro de verificação de necessidade encontrado."));
    }

    // --- BUSCAR POR ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<Object> getVerificacaoId(@PathVariable int id) {
        Map<String, Object> json = verificacaoController.getVerificacao(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- CADASTRO (POST) - CORRIGIDO para dona_id e Integer ---
    @PostMapping
    public ResponseEntity<Object> addVerificacao(@RequestParam String data,
                                                 @RequestParam String observacao,
                                                 @RequestParam String resultado,
                                                 @RequestParam(required = false) Integer vol_id,
                                                 @RequestParam(required = false) Integer dona_id) { // CORREÇÃO

        int finalVolId = (vol_id != null) ? vol_id : 0;
        int finalDonaId = (dona_id != null) ? dona_id : 0; // CORREÇÃO

        Map<String, Object> json = verificacaoController.addVerificacao(data, observacao, resultado, finalVolId, finalDonaId); // CORREÇÃO

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- ALTERAÇÃO (PUT) - CORRIGIDO para dona_id e Integer ---
    @PutMapping
    public ResponseEntity<Object> updtVerificacao(@RequestParam int id,
                                                  @RequestParam String data,
                                                  @RequestParam String observacao,
                                                  @RequestParam String resultado,
                                                  @RequestParam(required = false) Integer vol_id,
                                                  @RequestParam(required = false) Integer dona_id) { // CORREÇÃO

        int finalVolId = (vol_id != null) ? vol_id : 0;
        int finalDonaId = (dona_id != null) ? dona_id : 0; // CORREÇÃO

        Map<String, Object> json = verificacaoController.updtVerificacao(id, data, observacao, resultado, finalVolId, finalDonaId); // CORREÇÃO

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- DELETAR (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarVerificacao(@PathVariable int id) {
        Map<String, Object> json = verificacaoController.deletarVerificacao(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}