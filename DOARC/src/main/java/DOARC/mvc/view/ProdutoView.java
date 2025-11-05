package DOARC.mvc.view;

import DOARC.mvc.controller.ProdutoController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/produtos")
public class ProdutoView {

    @Autowired
    private ProdutoController produtoController;

    @PostMapping
    public ResponseEntity<Object> addProduto(@RequestBody Map<String, Object> payload) {
        try {
            String nome = (String) payload.get("nome");
            String descricao = (String) payload.get("descricao");
            String informacoesAdicionais = (String) payload.get("informacoesAdicionais");
            int quantidade = ((Number) payload.get("quantidade")).intValue();
            int categoriaId = ((Number) payload.get("categoriaId")).intValue();

            Map<String, Object> json = produtoController.addProduto(
                    nome, descricao, informacoesAdicionais, quantidade, categoriaId
            );

            return json.get("erro") == null
                    ? ResponseEntity.status(201).body(json)
                    : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Mensagem("Erro ao processar dados: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updtProduto(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        try {
            String nome = (String) payload.get("nome");
            String descricao = (String) payload.get("descricao");
            String informacoesAdicionais = (String) payload.get("informacoesAdicionais");
            int quantidade = ((Number) payload.get("quantidade")).intValue();
            int categoriaId = ((Number) payload.get("categoriaId")).intValue();

            Map<String, Object> json = produtoController.updtProduto(
                    id, nome, descricao, informacoesAdicionais, quantidade, categoriaId
            );

            return json.get("erro") == null
                    ? ResponseEntity.ok(json)
                    : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Mensagem("Erro ao processar dados: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAllProdutos(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String nome) {

        List<Map<String, Object>> lista = produtoController.getProdutos(categoriaId, nome);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProdutoId(@PathVariable int id) {
        Map<String, Object> json = produtoController.getProduto(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.status(404).body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarProduto(@PathVariable int id) {
        Map<String, Object> json = produtoController.deletarProduto(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
