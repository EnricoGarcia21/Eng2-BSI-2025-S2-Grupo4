package DOARC.mvc.view;

import DOARC.mvc.control.DonatarioControl;
import DOARC.mvc.util.Mensagem;
import java.util.ArrayList;
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
    private DonatarioControl donatarioControl;

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

        Map<String, Object> json = donatarioControl.addDonatario(don_nome, don_data_nasc, don_rua, don_bairro,
                don_cidade, don_telefone, don_cep, don_uf,
                don_email, don_sexo);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Donatário cadastrado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

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

        Map<String, Object> json = donatarioControl.updtDonatario(don_id, don_nome, don_data_nasc, don_rua, don_bairro,
                don_cidade, don_telefone, don_cep, don_uf, don_email, don_sexo);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Donatário alterado com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestParam(required = false) String filtro) {
        List<Map<String, Object>> lista;

        if (filtro != null && !filtro.trim().isEmpty()) {
            lista = donatarioControl.buscarDonatarios(filtro);
        } else {
            lista = donatarioControl.getDonatarios();
        }

        return ResponseEntity.ok(lista != null ? lista : new ArrayList<>());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDonatarioId(@PathVariable int id) {
        Map<String, Object> json = donatarioControl.getDonatario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarDonatario(@PathVariable int id) {
        Map<String, Object> json = donatarioControl.deletarDonatario(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}
