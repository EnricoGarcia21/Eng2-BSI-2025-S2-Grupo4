package DOARC.mvc.view;

import DOARC.mvc.controller.HigienizacaoRoupaController;
import DOARC.mvc.util.Mensagem; // Assumindo que esta classe existe
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/higienizacao")
public class HigienizacaoRoupaView {

    @Autowired
    private HigienizacaoRoupaController higienizacaoController;

    // --- LISTAR TODOS (GET) ---
    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(required = false) String filtro) {
        List<Map<String, Object>> lista = higienizacaoController.getHigienizacaoRoupa(filtro);
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.ok(new Mensagem("Nenhum registro de higienização encontrado."));
    }

    // --- BUSCAR POR ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<Object> getHigienizacaoId(@PathVariable int id) {
        Map<String, Object> json = higienizacaoController.getHigienizacaoRoupa(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- CADASTRO (POST) ---
    @PostMapping
    public ResponseEntity<Object> addHigienizacao(@RequestParam String data_agendada,
                                                  @RequestParam String descricao_roupa,
                                                  @RequestParam int vol_id,
                                                  @RequestParam String local,
                                                  @RequestParam String hora,
                                                  @RequestParam double valor_pago) {

        Map<String, Object> json = higienizacaoController.addHigienizacaoRoupa(data_agendada, descricao_roupa, vol_id,
                local, hora, valor_pago);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- ALTERAÇÃO (PUT) ---
    @PutMapping
    public ResponseEntity<Object> updtHigienizacao(@RequestParam int id,
                                                   @RequestParam String data_agendada,
                                                   @RequestParam String descricao_roupa,
                                                   @RequestParam int vol_id,
                                                   @RequestParam String local,
                                                   @RequestParam String hora,
                                                   @RequestParam double valor_pago) {

        Map<String, Object> json = higienizacaoController.updtHigienizacaoRoupa(id, data_agendada, descricao_roupa, vol_id,
                local, hora, valor_pago);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- DELETAR (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarHigienizacao(@PathVariable int id) {
        Map<String, Object> json = higienizacaoController.deletarHigienizacaoRoupa(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}