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

    @PostMapping
    public ResponseEntity<Object> addVoluntario(@RequestParam String vol_nome,
                                                @RequestParam String vol_bairro,
                                                @RequestParam String vol_numero,
                                                @RequestParam String vol_rua,
                                                @RequestParam String vol_telefone,
                                                @RequestParam String vol_cidade,
                                                @RequestParam String vol_cep,
                                                @RequestParam String vol_uf,
                                                @RequestParam String vol_email,
                                                @RequestParam String vol_cpf,
                                                @RequestParam String vol_dataNasc,
                                                @RequestParam String vol_sexo) {

        Map<String, Object> json = voluntarioController.addVoluntario(
                vol_nome, vol_bairro, vol_numero, vol_rua, vol_telefone,
                vol_cidade, vol_cep, vol_uf, vol_email, vol_cpf, vol_dataNasc, vol_sexo
        );

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Voluntário cadastrado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping
    public ResponseEntity<Object> updtVoluntario(@RequestParam int vol_id,
                                                 @RequestParam String vol_nome,
                                                 @RequestParam String vol_bairro,
                                                 @RequestParam String vol_numero,
                                                 @RequestParam String vol_rua,
                                                 @RequestParam String vol_telefone,
                                                 @RequestParam String vol_cidade,
                                                 @RequestParam String vol_cep,
                                                 @RequestParam String vol_uf,
                                                 @RequestParam String vol_email,
                                                 @RequestParam String vol_cpf,
                                                 @RequestParam String vol_dataNasc,
                                                 @RequestParam String vol_sexo) {

        Map<String, Object> json = voluntarioController.updtVoluntario(
                vol_id, vol_nome, vol_bairro, vol_numero, vol_rua, vol_telefone,
                vol_cidade, vol_cep, vol_uf, vol_email, vol_cpf, vol_dataNasc, vol_sexo
        );

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Voluntário alterado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = voluntarioController.getVoluntarios();
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.badRequest().body(new Mensagem("Nenhum voluntário encontrado."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getVoluntarioId(@PathVariable int id) {
        Map<String, Object> json = voluntarioController.getVoluntario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarVoluntario(@PathVariable int id) {
        Map<String, Object> json = voluntarioController.deletarVoluntario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem("Voluntário excluído com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
