package DOARC.mvc.view;

import DOARC.mvc.controller.VoluntarioController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/voluntario")
public class VoluntarioView {

    @Autowired
    private VoluntarioController voluntarioController;

    // --- CADASTRO (POST) ---
    @PostMapping
    public ResponseEntity<Object> addVoluntario(@RequestParam String vol_nome,
                                                @RequestParam String vol_data_nasc,
                                                @RequestParam String vol_rua,
                                                @RequestParam String vol_bairro,
                                                @RequestParam String vol_cidade,
                                                @RequestParam String vol_telefone,
                                                @RequestParam String vol_cep,
                                                @RequestParam String vol_uf,
                                                @RequestParam String vol_email,
                                                @RequestParam String vol_sexo,
                                                @RequestParam String vol_numero,
                                                @RequestParam String vol_cpf) {

        Map<String, Object> json = voluntarioController.addVoluntario(vol_nome, vol_data_nasc, vol_rua, vol_bairro,
                vol_cidade, vol_telefone, vol_cep, vol_uf, vol_email, vol_sexo, vol_numero, vol_cpf);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Voluntário cadastrado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- ALTERAÇÃO (PUT) ---
    @PutMapping
    public ResponseEntity<Object> updtVoluntario(@RequestParam int vol_id,
                                                 @RequestParam String vol_nome,
                                                 @RequestParam String vol_data_nasc,
                                                 @RequestParam String vol_rua,
                                                 @RequestParam String vol_bairro,
                                                 @RequestParam String vol_cidade,
                                                 @RequestParam String vol_telefone,
                                                 @RequestParam String vol_cep,
                                                 @RequestParam String vol_uf,
                                                 @RequestParam String vol_email,
                                                 @RequestParam String vol_sexo,
                                                 @RequestParam String vol_numero,
                                                 @RequestParam String vol_cpf) {

        Map<String, Object> json = voluntarioController.updtVoluntario(vol_id, vol_nome, vol_data_nasc, vol_rua, vol_bairro,
                vol_cidade, vol_telefone, vol_cep, vol_uf, vol_email, vol_sexo, vol_numero, vol_cpf);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Voluntário alterado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- LISTAR TODOS (GET) ---
    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = voluntarioController.getVoluntarios();
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.ok(new Mensagem("Nenhum voluntário encontrado.")); // Alterado para 200 OK com mensagem, se lista vazia.
    }

    // --- BUSCAR POR ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<Object> getVoluntarioId(@PathVariable int id) {
        Map<String, Object> json = voluntarioController.getVoluntarioById(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- DELETAR (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarVoluntario(@PathVariable int id) {
        Map<String, Object> json = voluntarioController.deletarVoluntario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}