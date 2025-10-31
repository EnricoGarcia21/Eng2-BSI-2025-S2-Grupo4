package DOARC.mvc.view;

import DOARC.mvc.controller.CampanhaController;
import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/apis/campanha")
public class CampanhaView {

    @Autowired
    private CampanhaController campanhaController;

    // ➤ CADASTRAR CAMPANHA
    @PostMapping
    public ResponseEntity<Object> addCampanha(@RequestBody Campanha campanha) {
        try {
            Map<String, Object> json = campanhaController.addCampanha(
                    campanha.getCam_data_ini(),
                    campanha.getCam_data_fim(),
                    campanha.getVoluntario_vol_id(),
                    campanha.getCam_desc(),
                    campanha.getCam_meta_arrecadacao(),
                    campanha.getCam_valor_arrecadado()
            );

            if (json.get("erro") == null) {
                return ResponseEntity.ok(new Mensagem("Campanha cadastrada com sucesso!"));
            } else {
                return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Mensagem("Erro interno: " + e.getMessage()));
        }
    }

    // ➤ ATUALIZAR CAMPANHA
    @PutMapping
    public ResponseEntity<Object> updtCampanha(@RequestBody Campanha campanha) {
        try {
            Map<String, Object> json = campanhaController.updtCampanha(
                    campanha.getCam_id(),
                    campanha.getCam_data_ini(),
                    campanha.getCam_data_fim(),
                    campanha.getVoluntario_vol_id(),
                    campanha.getCam_desc(),
                    campanha.getCam_meta_arrecadacao(),
                    campanha.getCam_valor_arrecadado()
            );

            if (json.get("erro") == null) {
                return ResponseEntity.ok(new Mensagem("Campanha alterada com sucesso!"));
            } else {
                return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Mensagem("Erro interno: " + e.getMessage()));
        }
    }

    // ➤ LISTAR TODAS
    @GetMapping
    public ResponseEntity<Object> getAll() {
        try {
            List<Map<String, Object>> lista = campanhaController.getCampanhas();
            if (lista != null && !lista.isEmpty()) {
                return ResponseEntity.ok(lista);
            } else {
                return ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Mensagem("Erro interno: " + e.getMessage()));
        }
    }

    // ➤ BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCampanhaId(@PathVariable int id) {
        try {
            Map<String, Object> json = campanhaController.getCampanha(id);
            if (json.get("erro") == null) {
                return ResponseEntity.ok(json);
            } else {
                return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Mensagem("Erro interno: " + e.getMessage()));
        }
    }

    // ➤ EXCLUIR CAMPANHA
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarCampanha(@PathVariable int id) {
        try {
            Map<String, Object> json = campanhaController.deletarCampanha(id);
            if (json.get("erro") == null) {
                return ResponseEntity.ok(new Mensagem("Campanha excluída com sucesso!"));
            } else {
                return ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Mensagem("Erro interno: " + e.getMessage()));
        }
    }

    // ➤ BUSCAR CAMPANHAS POR VOLUNTÁRIO
    @GetMapping("/voluntario/{voluntarioId}")
    public ResponseEntity<Object> getCampanhasPorVoluntario(@PathVariable int voluntarioId) {
        try {
            List<Map<String, Object>> lista = campanhaController.getCampanhasPorVoluntario(voluntarioId);
            if (lista != null && !lista.isEmpty()) {
                return ResponseEntity.ok(lista);
            } else {
                return ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada para este voluntário."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new Mensagem("Erro interno: " + e.getMessage()));
        }
    }
}
