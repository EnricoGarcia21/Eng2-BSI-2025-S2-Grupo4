package DOARC.mvc.view;

import DOARC.mvc.control.CompraControl;
import DOARC.mvc.util.Mensagem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/compra")
public class CompraView {

    @Autowired
    private CompraControl compraControl;

    /**
     * Registra uma compra/arrecadação
     * @param comp_data_compra Data da compra (YYYY-MM-DD)
     * @param comp_desc Descrição/Observações
     * @param vol_id ID do voluntário responsável
     * @param com_valor_total Valor total (0 para arrecadação)
     * @param com_fornecedor Fornecedor (vazio para arrecadação)
     * @param produtos JSON string com lista de produtos: [{"prodId": 1, "qtde": 10, "valorUnit": 5.50}, ...]
     * @return ResponseEntity com dados da compra ou erro
     */
    @PostMapping
    public ResponseEntity<Object> registrarCompra(@RequestParam String comp_data_compra,
                                                  @RequestParam(required = false, defaultValue = "") String comp_desc,
                                                  @RequestParam int vol_id,
                                                  @RequestParam(defaultValue = "0") double com_valor_total,
                                                  @RequestParam(required = false, defaultValue = "") String com_fornecedor,
                                                  @RequestParam String produtos) {
        try {
            // Parse JSON string para List<Map>
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> produtosLista = objectMapper.readValue(
                produtos,
                new TypeReference<List<Map<String, Object>>>() {}
            );

            Map<String, Object> json = compraControl.registrarCompra(
                comp_data_compra,
                comp_desc,
                vol_id,
                com_valor_total,
                com_fornecedor,
                produtosLista
            );

            return json.get("erro") == null
                    ? ResponseEntity.ok(json)
                    : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));

        } catch (Exception e) {
            System.err.println("Erro ao processar compra: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Mensagem("Erro ao processar compra: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = compraControl.getCompras();
        return ResponseEntity.ok(lista != null ? lista : new ArrayList<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCompraId(@PathVariable int id) {
        Map<String, Object> json = compraControl.getCompra(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
