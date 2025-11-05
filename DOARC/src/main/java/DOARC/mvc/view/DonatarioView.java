package DOARC.mvc.view;

import DOARC.mvc.controller.DonatarioController;
import DOARC.mvc.util.Mensagem; // Assumindo que esta classe existe
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/donatario")
public class DonatarioView {

    @Autowired
    private DonatarioController donatarioController;

    // --- CADASTRO (POST) ---
    @PostMapping
    public ResponseEntity<Object> addDonatario(@RequestParam String don_nome,
                                               @RequestParam String don_data_nasc,
                                               @RequestParam String don_rua,
                                               @RequestParam String don_bairro,
                                               @RequestParam String don_cidade,
                                               @RequestParam String don_telefone,
                                               @RequestParam String don_cep,
                                               @RequestParam String don_uf,
                                               @RequestParam String don_email,
                                               @RequestParam String don_sexo) {

        Map<String, Object> json = donatarioController.addDonatario(don_nome, don_data_nasc, don_rua, don_bairro,
                don_cidade, don_telefone, don_cep, don_uf,
                don_email, don_sexo);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Donatário cadastrado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- ALTERAÇÃO (PUT) ---
    @PutMapping
    public ResponseEntity<Object> updtDonatario(@RequestParam int don_id,
                                                @RequestParam String don_nome,
                                                @RequestParam String don_data_nasc,
                                                @RequestParam String don_rua,
                                                @RequestParam String don_bairro,
                                                @RequestParam String don_cidade,
                                                @RequestParam String don_telefone,
                                                @RequestParam String don_cep,
                                                @RequestParam String don_uf,
                                                @RequestParam String don_email,
                                                @RequestParam String don_sexo) {

        Map<String, Object> json = donatarioController.updtDonatario(don_id, don_nome, don_data_nasc, don_rua, don_bairro,
                don_cidade, don_telefone, don_cep, don_uf, don_email, don_sexo);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Donatário alterado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- LISTAR TODOS (GET) ---
    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = donatarioController.getDonatario();
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.ok(new Mensagem("Nenhum donatário encontrado.")); // Alterado para 200 OK com mensagem, se lista vazia.
    }

    // --- BUSCAR POR ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDonatarioId(@PathVariable int id) {
        Map<String, Object> json = donatarioController.getDonatario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- DELETAR (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarDonatario(@PathVariable int id) {
        Map<String, Object> json = donatarioController.deletarDonatario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}