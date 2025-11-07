package DOARC.mvc.view;

import DOARC.mvc.controller.AgendarRetiradaController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/agendar-retirada")
public class AgendarRetiradaView {

    @Autowired
    private AgendarRetiradaController agendarController;

    @PostMapping
    public ResponseEntity<Object> agendarRetirada(
            @RequestParam String dataRetiro,
            @RequestParam String horaRetiro,
            @RequestParam String obsRetiro,
            @RequestParam int volId,
            @RequestParam int doaId) {

        Map<String, Object> json = agendarController.agendarRetirada(dataRetiro, horaRetiro, obsRetiro, volId, doaId);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> listarAgendamentos() {
        Map<String, Object> json = agendarController.listarAgendamentos();
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarAgendamento(@PathVariable int id) {
        Map<String, Object> json = agendarController.buscarAgendamento(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> atualizarAgendamento(
            @PathVariable int id,
            @RequestParam String dataRetiro,
            @RequestParam String horaRetiro,
            @RequestParam String obsRetiro,
            @RequestParam int volId,
            @RequestParam int doaId) {

        Map<String, Object> json = agendarController.atualizarAgendamento(id, dataRetiro, horaRetiro, obsRetiro, volId, doaId);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> cancelarAgendamento(@PathVariable int id) {
        Map<String, Object> json = agendarController.cancelarAgendamento(id);
        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}