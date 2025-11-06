package DOARC.mvc.view;

import DOARC.mvc.controller.CampanhaController;
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

    // --- CADASTRO (POST) ---
    @PostMapping
    public ResponseEntity<Object> addCampanha(@RequestParam String cam_data_ini,
                                              @RequestParam String cam_data_fim,
                                              @RequestParam int voluntario_vol_id,
                                              @RequestParam String cam_desc,
                                              @RequestParam Double cam_meta_arrecadacao,
                                              @RequestParam Double cam_valor_arrecadado) {

        Map<String, Object> json = campanhaController.addCampanha(cam_data_ini, cam_data_fim, voluntario_vol_id,
                cam_desc, cam_meta_arrecadacao, cam_valor_arrecadado);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Campanha cadastrada com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- ALTERAÇÃO (PUT) ---
    @PutMapping
    public ResponseEntity<Object> updtCampanha(@RequestParam int cam_id,
                                               @RequestParam String cam_data_ini,
                                               @RequestParam String cam_data_fim,
                                               @RequestParam int voluntario_vol_id,
                                               @RequestParam String cam_desc,
                                               @RequestParam Double cam_meta_arrecadacao,
                                               @RequestParam Double cam_valor_arrecadado) {

        Map<String, Object> json = campanhaController.updtCampanha(cam_id, cam_data_ini, cam_data_fim, voluntario_vol_id,
                cam_desc, cam_meta_arrecadacao, cam_valor_arrecadado);

        return json.get("erro") == null
                ? ResponseEntity.ok(new Mensagem("Campanha alterada com sucesso!"))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- LISTAR TODOS (GET) ---
    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Map<String, Object>> lista = campanhaController.getCampanha();
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada."));
    }

    // --- BUSCAR POR ID (GET) ---
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCampanhaId(@PathVariable int id) {
        Map<String, Object> json = campanhaController.getCampanha(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(json)
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- DELETAR (DELETE) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletarCampanha(@PathVariable int id) {
        Map<String, Object> json = campanhaController.deletarCampanha(id);
        return (json.get("erro") == null)
                ? ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()))
                : ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()));
    }

    // --- BUSCAR CAMPANHAS POR VOLUNTÁRIO (GET) ---
    @GetMapping("/voluntario/{voluntario_id}")
    public ResponseEntity<Object> getCampanhasPorVoluntario(@PathVariable int voluntario_id) {
        List<Map<String, Object>> lista = campanhaController.getCampanhasPorVoluntario(voluntario_id);
        return (lista != null && !lista.isEmpty())
                ? ResponseEntity.ok(lista)
                : ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada para este voluntário."));
    }
}