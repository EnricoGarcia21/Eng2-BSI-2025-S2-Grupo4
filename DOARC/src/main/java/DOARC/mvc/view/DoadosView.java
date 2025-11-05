package DOARC.mvc.view;

import DOARC.mvc.controller.DoadosController; // <-- Importa o Controller novo
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/doacoes") // <-- Nova rota base
public class DoadosView {

    @Autowired
    private DoadosController doadosController; // <-- Injeta o Controller novo


    @PostMapping("/efetuar")
    public ResponseEntity<Object> efetuarDoacao(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Envia o payload para o Controller
            Map<String, Object> json = doadosController.efetuarDoacao(payload);

            return json.get("erro") == null
                    ? ResponseEntity.status(201).body(json)
                    : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Mensagem("Erro ao processar dados: " + e.getMessage()));
        }
    }

}