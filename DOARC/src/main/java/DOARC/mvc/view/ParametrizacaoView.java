package DOARC.mvc.view;

import DOARC.mvc.controller.ParametrizacaoController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/parametrizacao")
public class ParametrizacaoView {

    @Autowired
    private ParametrizacaoController parametrizacaoController;

    @PostMapping
    public ResponseEntity<Object> addParametrizacao(
            @RequestParam String razaoSocial,
            @RequestParam String nomeFantasia,
            @RequestParam String cnpj,
            @RequestParam String rua,
            @RequestParam String numero,
            @RequestParam String bairro,
            @RequestParam String cidade,
            @RequestParam String uf,
            @RequestParam String cep,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String site,
            @RequestParam(required = false) MultipartFile file) {

        Map<String, Object> json = parametrizacaoController.addParam(razaoSocial, nomeFantasia, cnpj, rua, numero,
                bairro, cidade, uf, cep, telefone, email, site, file); // REMOVIDO: estado

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Empresa cadastrada com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @PutMapping
    public ResponseEntity<Object> updtParametrizacao(
            @RequestParam int id,
            @RequestParam String razaoSocial,
            @RequestParam String nomeFantasia,
            @RequestParam String cnpj,
            @RequestParam String rua,
            @RequestParam String numero,
            @RequestParam String bairro,
            @RequestParam String cidade,
            @RequestParam String uf, // REMOVIDO: estado
            @RequestParam String cep,
            @RequestParam String telefone,
            @RequestParam String email,
            @RequestParam String site,
            @RequestParam(required = false) MultipartFile file) {

        Map<String, Object> json = parametrizacaoController.updtParam(id, razaoSocial, nomeFantasia, cnpj, rua, numero,
                bairro, cidade, uf, cep, telefone, email, site, file); // REMOVIDO: estado

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Empresa alterada com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getParametrizacaoId(@PathVariable int id) {
        Map<String, Object> json = parametrizacaoController.getParam(id);
        return (json != null && json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(
                json != null && json.get("erro") != null
                        ? json.get("erro").toString()
                        : "Empresa não encontrada."
        ));
    }
}