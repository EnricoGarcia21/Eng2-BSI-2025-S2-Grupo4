package DOARC.mvc.view;

import DOARC.mvc.control.ProdutoControl;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/produto")
public class ProdutoView {

    @Autowired
    private ProdutoControl produtoControl;

    @PostMapping
    public ResponseEntity<Object> addProduto(@RequestParam String prod_nome,
                                            @RequestParam(required = false, defaultValue = "") String prod_descricao,
                                            @RequestParam(required = false, defaultValue = "") String prod_informacoes_adicionais,
                                            @RequestParam int prod_quant,
                                            @RequestParam int cat_id) {
        Map<String, Object> json = produtoControl.addProduto(prod_nome, prod_descricao,
                prod_informacoes_adicionais, prod_quant, cat_id);

        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping
    public ResponseEntity<Object> updtProduto(@RequestParam int prod_id,
                                             @RequestParam String prod_nome,
                                             @RequestParam(required = false, defaultValue = "") String prod_descricao,
                                             @RequestParam(required = false, defaultValue = "") String prod_informacoes_adicionais,
                                             @RequestParam int prod_quant,
                                             @RequestParam int cat_id) {
        Map<String, Object> json = produtoControl.updtProduto(prod_id, prod_nome, prod_descricao,
                prod_informacoes_adicionais, prod_quant, cat_id);

        return json.get("erro") == null
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(required = false) String filtro,
                                         @RequestParam(required = false) Integer categoria_id) {
        List<Map<String, Object>> lista;

        if (categoria_id != null && categoria_id > 0) {
            lista = produtoControl.getProdutosPorCategoria(categoria_id);
        } else if (filtro != null && !filtro.trim().isEmpty()) {
            lista = produtoControl.buscarProdutos(filtro);
        } else {
            lista = produtoControl.getProdutos();
        }

        return ResponseEntity.ok(lista != null ? lista : new ArrayList<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProdutoId(@PathVariable int id) {
        Map<String, Object> json = produtoControl.getProduto(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarProduto(@PathVariable int id) {
        Map<String, Object> json = produtoControl.deletarProduto(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
