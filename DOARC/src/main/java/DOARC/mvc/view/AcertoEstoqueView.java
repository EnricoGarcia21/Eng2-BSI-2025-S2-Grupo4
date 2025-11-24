package DOARC.mvc.view;

import DOARC.mvc.control.AcertoEstoqueControl;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/acerto-estoque")
public class AcertoEstoqueView {

    @Autowired
    private AcertoEstoqueControl acertoEstoqueControl;

    /**
     * Registra um acerto de estoque
     * @param ac_data Data do acerto (YYYY-MM-DD)
     * @param ac_motivo Motivo (Perda, Ajuste, Roubo, Doação, Vencimento, Outro)
     * @param ac_obs Observações detalhadas
     * @param ac_tipo Tipo (Entrada ou Saída)
     * @param ac_quantidade Quantidade
     * @param vol_id ID do voluntário responsável
     * @param prod_id ID do produto
     * @return ResponseEntity com dados do acerto ou erro
     */
    @PostMapping
    public ResponseEntity<Object> registrarAcerto(@RequestParam String ac_data,
                                                   @RequestParam String ac_motivo,
                                                   @RequestParam String ac_obs,
                                                   @RequestParam String ac_tipo,
                                                   @RequestParam double ac_quantidade,
                                                   @RequestParam int vol_id,
                                                   @RequestParam int prod_id) {
        Map<String, Object> json = acertoEstoqueControl.registrarAcerto(
            ac_data,
            ac_motivo,
            ac_obs,
            ac_tipo,
            ac_quantidade,
            vol_id,
            prod_id
        );

        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(required = false) Integer produto_id) {
        List<Map<String, Object>> lista;

        if (produto_id != null && produto_id > 0) {
            lista = acertoEstoqueControl.getAcertosPorProduto(produto_id);
        } else {
            lista = acertoEstoqueControl.getAcertos();
        }

        return ResponseEntity.ok(lista != null ? lista : new ArrayList<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAcertoId(@PathVariable int id) {
        Map<String, Object> json = acertoEstoqueControl.getAcerto(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
