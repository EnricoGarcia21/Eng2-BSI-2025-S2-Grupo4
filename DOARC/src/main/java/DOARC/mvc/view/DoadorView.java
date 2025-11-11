package DOARC.mvc.view;

import DOARC.mvc.controller.DoadorController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/doador")
public class DoadorView {

    @Autowired
    private DoadorController doadorController;

    @PostMapping
    public ResponseEntity<Object> addDoador(@RequestParam String doa_nome,
                                            @RequestParam String doa_telefone,
                                            @RequestParam String doa_email,
                                            @RequestParam String doa_cep,
                                            @RequestParam String doa_uf,
                                            @RequestParam String doa_cidade,
                                            @RequestParam String doa_bairro,
                                            @RequestParam String doa_rua,
                                            @RequestParam String doa_cpf,
                                            @RequestParam String doa_datanasc,
                                            @RequestParam String doa_sexo,
                                            @RequestParam String doa_site) {

        Map<String, Object> json = doadorController.addDoador(doa_nome, doa_telefone, doa_email, doa_cep, doa_uf,
                doa_cidade, doa_bairro, doa_rua, doa_cpf, doa_datanasc, doa_sexo, doa_site);

        if (json.get("erro") == null) {
            return ResponseEntity.ok(new Mensagem("Doador cadastrado com sucesso!"));
        }
        return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping
    public ResponseEntity<Object> updtDoador(@RequestParam int doa_id,
                                             @RequestParam String doa_nome,
                                             @RequestParam String doa_telefone,
                                             @RequestParam String doa_email,
                                             @RequestParam String doa_cep,
                                             @RequestParam String doa_uf,
                                             @RequestParam String doa_cidade,
                                             @RequestParam String doa_bairro,
                                             @RequestParam String doa_rua,
                                             @RequestParam String doa_cpf,
                                             @RequestParam String doa_datanasc,
                                             @RequestParam String doa_sexo,
                                             @RequestParam String doa_site) {

        Map<String, Object> json = doadorController.updtDoador(doa_id, doa_nome, doa_telefone, doa_email, doa_cep, doa_uf,
                doa_cidade, doa_bairro, doa_rua, doa_cpf, doa_datanasc, doa_sexo, doa_site);

        if (json.get("erro") == null) {
            return ResponseEntity.ok(new Mensagem("Doador alterado com sucesso!"));
        }
        return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = doadorController.getDoador();

        if (lista != null && !lista.isEmpty()) {
            return ResponseEntity.ok(lista);
        }
        return ResponseEntity.badRequest().body(new Mensagem("Nenhum doador encontrado."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDoadorId(@PathVariable int id) {
        Map<String, Object> json = doadorController.getDoador(id);

        if (json.get("erro") == null) {
            return ResponseEntity.ok(json);
        }
        return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarDoador(@PathVariable int id) {
        Map<String, Object> json = doadorController.deletarDoador(id);

        if (json.get("erro") == null) {
            return ResponseEntity.ok(new Mensagem("Doador excluído com sucesso!"));
        }
        return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }
}