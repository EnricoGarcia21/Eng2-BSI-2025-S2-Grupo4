package DOARC.mvc.view;

import DOARC.mvc.controller.AgendamentoService;
import DOARC.mvc.model.AgendarDoados;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/agendamento")
public class AgendamentoView {

    private final AgendamentoService agendamentoService;

    @Autowired
    public AgendamentoView(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllAgendamentos(@RequestParam(required = false) String filtro) {
        List<Map<String, Object>> lista = agendamentoService.listarAgendamentosComDetalhes(filtro);


        return ResponseEntity.ok(lista);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getAgendamentoId(@PathVariable int id) {

        Map<String, Object> resultado = agendamentoService.getAgendamentoDetalhe(id);

        if (resultado.get("erro") == null) {
            return ResponseEntity.ok(resultado);
        }
        return ResponseEntity.badRequest().body(new Mensagem(resultado.get("erro").toString()));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Object> alterarAgendamento(@PathVariable int id, @RequestBody AgendarDoados agendamento) throws SQLException {


        String agDataStr = (agendamento.getAgData() != null)
                ? agendamento.getAgData().toString()
                : null;

        String agHoraStr = (agendamento.getAgHora() != null)
                ? agendamento.getAgHora().toString()
                : "00:00";

        Map<String, Object> resultado = agendamentoService.alterarAgendamento(
                id,
                agDataStr,
                agHoraStr,
                agendamento.getAgObs(),
                agendamento.getDoaId(),
                agendamento.getVoluntarioId(),
                agendamento.getDonatarioId()
        );

        if (resultado.get("erro") == null) {
            return ResponseEntity.ok(new Mensagem(resultado.get("sucesso").toString()));
        }
        return ResponseEntity.badRequest().body(new Mensagem(resultado.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> apagarAgendamento(@PathVariable int id) {
        Map<String, Object> resultado = agendamentoService.apagarAgendamento(id);

        if (resultado.get("erro") == null) {
            return ResponseEntity.ok(new Mensagem(resultado.get("sucesso").toString()));
        }
        return ResponseEntity.badRequest().body(new Mensagem(resultado.get("erro").toString()));
    }
}