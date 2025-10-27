package DOARC.mvc.view;

import DOARC.mvc.controller.DonatarioController;
import DOARC.mvc.model.Donatario;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/donatario")
public class DonatarioView {

    @Autowired
    private DonatarioController donatarioController;
    @PostMapping
    public ResponseEntity<Object> addDonatario(@RequestBody Map<String, Object> body) {
        // Aceita JSON com chaves como: nome, dataNasc, rua, bairro, cidade, telefone, cep, uf, email, sexo
        String nome = getString(body, "nome", "don_nome", "donNome");
        String dataNasc = getString(body, "dataNasc", "don_data_nasc", "donDataNasc");
        String rua = getString(body, "rua", "don_rua", "donRua");
        String bairro = getString(body, "bairro", "don_bairro", "donBairro");
        String cidade = getString(body, "cidade", "don_cidade", "donCidade");
        String telefone = getString(body, "telefone", "don_telefone", "donTelefone");
        String cep = getString(body, "cep", "don_cep", "donCep");
        String uf = getString(body, "uf", "don_uf", "donUf");
        String email = getString(body, "email", "don_email", "donEmail");
        String sexo = getString(body, "sexo", "don_sexo", "donSexo");

        Map<String, Object> json = donatarioController.addDonatario(nome, dataNasc, rua, bairro,
                cidade, telefone, cep, uf, email, sexo);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Donatário cadastrado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

        @PutMapping
        public ResponseEntity<Object> updtDonatario(@RequestBody Map<String, Object> body) {
                // aceita JSON com 'id' ou 'don_id' e demais campos como no POST
                Integer id = null;
                Object idObj = body.get("id");
                if (idObj == null) idObj = body.get("don_id");
                if (idObj instanceof Number) id = ((Number) idObj).intValue();
                else if (idObj instanceof String) {
                        try { id = Integer.parseInt((String) idObj); } catch (NumberFormatException ignored) {}
                }
                if (id == null) return ResponseEntity.badRequest().body(new Mensagem("Campo 'id' é obrigatório."));

                String nome = getString(body, "nome", "don_nome", "donNome");
                String dataNasc = getString(body, "dataNasc", "don_data_nasc", "donDataNasc");
                String rua = getString(body, "rua", "don_rua", "donRua");
                String bairro = getString(body, "bairro", "don_bairro", "donBairro");
                String cidade = getString(body, "cidade", "don_cidade", "donCidade");
                String telefone = getString(body, "telefone", "don_telefone", "donTelefone");
                String cep = getString(body, "cep", "don_cep", "donCep");
                String uf = getString(body, "uf", "don_uf", "donUf");
                String email = getString(body, "email", "don_email", "donEmail");
                String sexo = getString(body, "sexo", "don_sexo", "donSexo");

                Map<String, Object> json = donatarioController.updtDonatario(id, nome, dataNasc, rua, bairro,
                                cidade, telefone, cep, uf, email, sexo);

                return json.get("erro") == null
                                ? ResponseEntity.ok(new Mensagem("Donatário alterado com sucesso!"))
                                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
        }

        // Helper para extrair o primeiro valor não-nulo entre várias chaves possíveis
        private String getString(Map<String, Object> body, String... keys) {
                for (String k : keys) {
                        Object o = body.get(k);
                        if (o != null) return o.toString();
                }
                return null;
        }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = donatarioController.getDonatario();
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.badRequest().body(new Mensagem("Nenhum donatário encontrado."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDonatarioId(@PathVariable int id) {
        Map<String, Object> json = donatarioController.getDonatario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarDonatario(@PathVariable int id) {
        Map<String, Object> json = donatarioController.deletarDonatario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem("Donatário excluído com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
