package DOARC.mvc.view;

import DOARC.mvc.controller.AcessoController;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/acesso")
public class AcessoView {

    @Autowired
    private AcessoController acessoController;

    // --- AUTENTICAR (POST) ---
    @PostMapping("/logar")
    public ResponseEntity<Object> logar(@RequestParam String login,
                                        @RequestParam String senha) {

        Map<String, Object> json = acessoController.autenticar(login, senha);

        return (boolean) json.get("sucesso")
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("mensagem").toString()));
    }

    // --- CRIAR CREDENCIAIS (POST) ---
    @PostMapping("/credenciais")
    public ResponseEntity<Object> criarCredenciais(@RequestParam int voluntario_id,
                                                   @RequestParam String login,
                                                   @RequestParam String senha,
                                                   @RequestParam String nivel_acesso) {

        Map<String, Object> json = acessoController.criarCredenciais(voluntario_id, login, senha, nivel_acesso);

        return (boolean) json.get("sucesso")
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("mensagem").toString()));
    }

    // --- ALTERAR SENHA (PUT) ---
    @PutMapping("/senha")
    public ResponseEntity<Object> alterarSenha(@RequestParam int voluntario_id,
                                               @RequestParam String senha_atual,
                                               @RequestParam String nova_senha) {

        Map<String, Object> json = acessoController.alterarSenha(voluntario_id, senha_atual, nova_senha);

        return (boolean) json.get("sucesso")
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("mensagem").toString()));
    }

    // --- ALTERAR STATUS (PUT) ---
    @PutMapping("/status")
    public ResponseEntity<Object> alterarStatus(@RequestParam int voluntario_id,
                                                @RequestParam char status) {

        Map<String, Object> json = acessoController.alterarStatus(voluntario_id, status);

        return (boolean) json.get("sucesso")
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("mensagem").toString()));
    }

    // --- DELETAR CREDENCIAIS (DELETE) ---
    @DeleteMapping("/{voluntario_id}")
    public ResponseEntity<Object> deletarCredenciais(@PathVariable int voluntario_id) {
        Map<String, Object> json = acessoController.deletarCredenciais(voluntario_id);

        return (boolean) json.get("sucesso")
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("mensagem").toString()));
    }
}