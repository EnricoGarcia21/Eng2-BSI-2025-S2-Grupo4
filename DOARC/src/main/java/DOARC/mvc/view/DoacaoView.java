package DOARC.mvc.view;

import DOARC.mvc.controller.DoacaoController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/doacoes")
public class DoacaoView {

    @Autowired
    private DoacaoController doacaoController;

    @PostMapping
    public ResponseEntity<Object> receberDoacao(
            @RequestParam int volId,
            @RequestParam String dataDoacao,
            @RequestParam String obsDoacao,
            @RequestParam BigDecimal valorDoado,
            @RequestParam int doaId) {

        Map<String, Object> json = doacaoController.receberDoacao(volId, dataDoacao, obsDoacao, valorDoado, doaId);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> listarDoacoes() {
        Map<String, Object> json = doacaoController.listarDoacoes();
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarDoacao(@PathVariable int id) {
        Map<String, Object> json = doacaoController.buscarDoacao(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarDoacao(
            @PathVariable int id,
            @RequestParam int volId,
            @RequestParam String dataDoacao,
            @RequestParam String obsDoacao,
            @RequestParam BigDecimal valorDoado,
            @RequestParam int doaId) {

        Map<String, Object> json = doacaoController.atualizarDoacao(id, volId, dataDoacao, obsDoacao, valorDoado, doaId);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> excluirDoacao(@PathVariable int id) {
        Map<String, Object> json = doacaoController.excluirDoacao(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/voluntario/{volId}")
    public ResponseEntity<Object> listarDoacoesPorVoluntario(@PathVariable int volId) {
        Map<String, Object> json = doacaoController.listarDoacoesPorVoluntario(volId);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/doador/{doaId}")
    public ResponseEntity<Object> listarDoacoesPorDoador(@PathVariable int doaId) {
        Map<String, Object> json = doacaoController.listarDoacoesPorDoador(doaId);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/estatisticas/total")
    public ResponseEntity<Object> getTotalDoacoesPeriodo(
            @RequestParam String dataInicio,
            @RequestParam String dataFim) {

        Map<String, Object> json = doacaoController.getTotalDoacoesPeriodo(dataInicio, dataFim);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}