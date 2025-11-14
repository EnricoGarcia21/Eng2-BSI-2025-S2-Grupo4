package DOARC.mvc.view;

import DOARC.mvc.controller.CampRespController;
import DOARC.mvc.model.CampResponsavel;
import DOARC.mvc.util.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis/campresponsavel")
public class CampRespView {

    @Autowired
    private CampRespController controller;

    // ============================
    // ✅ VINCULAR VOLUNTÁRIO À CAMPANHA (POST)
    // ============================
    @PostMapping("/registrar")
    public ResponseEntity<Object> add(@RequestBody CampResponsavel campResponsavel) {

        Map<String, Object> json = controller.addCampResponsavel(campResponsavel);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ============================
    // ✅ ALTERAR VINCULAÇÃO (PUT)
    // ============================
    @PutMapping("/alterar")
    public ResponseEntity<Object> alterar(@RequestBody CampResponsavel campResponsavel) {

        Map<String, Object> json = controller.updtCampResponsavel(campResponsavel);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ============================
    // ✅ LISTAR TODAS AS VINCULAÇÕES (GET)
    // ============================
    @GetMapping
    public ResponseEntity<Object> listar() {
        List<Map<String, Object>> lista = controller.getCampResponsavel();
        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma vinculação encontrada"))
                : ResponseEntity.ok(lista);
    }

    // ============================
    // ✅ BUSCAR VINCULAÇÃO ESPECÍFICA (GET)
    // ============================
    @GetMapping("/{camId}/{voluntarioId}")
    public ResponseEntity<Object> get(@PathVariable int camId, @PathVariable int voluntarioId) {
        Map<String, Object> json = controller.getCampResponsavel(camId, voluntarioId);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(json);
    }

    // ============================
    // ✅ BUSCAR COM FILTRO (GET)
    // ============================
    @GetMapping("/filtro")
    public ResponseEntity<Object> getComFiltro(@RequestParam String filtro) {
        List<Map<String, Object>> lista = controller.getCampResponsavel(filtro);
        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma vinculação encontrada com o filtro especificado"))
                : ResponseEntity.ok(lista);
    }

    // ============================
    // ✅ DELETAR VINCULAÇÃO (DELETE)
    // ============================
    @DeleteMapping("/{camId}/{voluntarioId}")
    public ResponseEntity<Object> deletar(@PathVariable int camId, @PathVariable int voluntarioId) {
        Map<String, Object> json = controller.deletarCampResponsavel(camId, voluntarioId);

        return json.containsKey("erro")
                ? ResponseEntity.badRequest().body(new Mensagem(json.get("erro").toString()))
                : ResponseEntity.ok(new Mensagem(json.get("mensagem").toString()));
    }

    // ============================
    // ✅ LISTAR CAMPANHAS POR VOLUNTÁRIO (GET)
    // ============================
    @GetMapping("/voluntario/{voluntarioId}")
    public ResponseEntity<Object> porVoluntario(@PathVariable int voluntarioId) {
        List<Map<String, Object>> lista = controller.getCampanhasPorVoluntario(voluntarioId);

        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhuma campanha encontrada para esse voluntário"))
                : ResponseEntity.ok(lista);
    }

    // ============================
    // ✅ LISTAR VOLUNTÁRIOS POR CAMPANHA (GET)
    // ============================
    @GetMapping("/campanha/{campanhaId}")
    public ResponseEntity<Object> porCampanha(@PathVariable int campanhaId) {
        List<Map<String, Object>> lista = controller.getVoluntariosPorCampanha(campanhaId);

        return lista.isEmpty()
                ? ResponseEntity.ok(new Mensagem("Nenhum voluntário encontrado para essa campanha"))
                : ResponseEntity.ok(lista);
    }
}