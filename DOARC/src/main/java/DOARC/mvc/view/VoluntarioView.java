package DOARC.mvc.view;

import DOARC.mvc.controller.VoluntarioController;
import DOARC.mvc.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/apis/voluntario")
@CrossOrigin(origins = "*")
public class VoluntarioView {

    @Autowired
    private VoluntarioController voluntarioController;

    // ===== PERFIL DO VOLUNTÁRIO =====

    @GetMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> obterPerfil(@PathVariable int id) {
        try {
            Map<String, Object> voluntario = voluntarioController.getVoluntario(id);

            if (voluntario.containsKey("erro")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(voluntario);
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter perfil: " + e.getMessage());
            Map<String, Object> error = Map.of("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/perfil/{id}")
    public ResponseEntity<Map<String, Object>> atualizarPerfil(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            // ✅ Agora usa o controller para buscar
            Voluntario existente = voluntarioController.buscarVoluntarioPorId(id);

            if (existente == null) {
                response.put("erro", "Voluntário não encontrado");
                return ResponseEntity.notFound().build();
            }

            // Atualizar apenas campos permitidos (não críticos)
            if (dados.containsKey("vol_nome")) {
                existente.setVol_nome((String) dados.get("vol_nome"));
            }
            if (dados.containsKey("vol_telefone")) {
                existente.setVol_telefone((String) dados.get("vol_telefone"));
            }
            if (dados.containsKey("vol_rua")) {
                existente.setVol_rua((String) dados.get("vol_rua"));
            }
            if (dados.containsKey("vol_bairro")) {
                existente.setVol_bairro((String) dados.get("vol_bairro"));
            }
            if (dados.containsKey("vol_cidade")) {
                existente.setVol_cidade((String) dados.get("vol_cidade"));
            }
            if (dados.containsKey("vol_numero")) {
                existente.setVol_numero((String) dados.get("vol_numero"));
            }
            if (dados.containsKey("vol_cep")) {
                existente.setVol_cep((String) dados.get("vol_cep"));
            }
            if (dados.containsKey("vol_uf")) {
                existente.setVol_uf((String) dados.get("vol_uf"));
            }

            // ✅ Agora usa o controller para atualizar
            Voluntario atualizado = voluntarioController.atualizarPerfil(existente);

            if (atualizado != null) {
                response.put("success", true);
                response.put("message", "Perfil atualizado com sucesso!");

                // Retornar dados atualizados
                Map<String, Object> voluntarioMap = new HashMap<>();
                voluntarioMap.put("vol_id", atualizado.getVol_id());
                voluntarioMap.put("vol_nome", atualizado.getVol_nome());
                voluntarioMap.put("vol_cpf", atualizado.getVol_cpf());
                voluntarioMap.put("vol_email", atualizado.getVol_email());
                voluntarioMap.put("vol_telefone", atualizado.getVol_telefone());
                voluntarioMap.put("vol_datanasc", atualizado.getVol_datanasc());
                voluntarioMap.put("vol_rua", atualizado.getVol_rua());
                voluntarioMap.put("vol_bairro", atualizado.getVol_bairro());
                voluntarioMap.put("vol_cidade", atualizado.getVol_cidade());
                voluntarioMap.put("vol_numero", atualizado.getVol_numero());
                voluntarioMap.put("vol_cep", atualizado.getVol_cep());
                voluntarioMap.put("vol_uf", atualizado.getVol_uf());
                voluntarioMap.put("vol_sexo", atualizado.getVol_sexo());

                response.put("voluntario", voluntarioMap);
                return ResponseEntity.ok(response);
            } else {
                response.put("erro", "Erro ao atualizar perfil");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar perfil: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ===== CAMPANHAS DO VOLUNTÁRIO =====

    // Injete o CampanhaController se ele existir
    // @Autowired
    // private CampanhaController campanhaController;

    @GetMapping("/minhas-campanhas/{voluntarioId}")
    public ResponseEntity<List<Map<String, Object>>> obterMinhasCampanhas(@PathVariable int voluntarioId) {
        try {
            // TODO: Quando o CampanhaController estiver pronto, use:
            // List<Map<String, Object>> campanhas = campanhaController.listarCampanhasPorVoluntario(voluntarioId);
            // return ResponseEntity.ok(campanhas);

            return ResponseEntity.ok(new ArrayList<>());
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter campanhas do voluntário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // ===== DASHBOARD DO VOLUNTÁRIO =====

    @GetMapping("/dashboard/{voluntarioId}")
    public ResponseEntity<Map<String, Object>> obterDashboard(@PathVariable int voluntarioId) {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // ✅ Agora usa o controller
            Voluntario voluntario = voluntarioController.buscarVoluntarioPorId(voluntarioId);

            if (voluntario != null) {
                dashboard.put("nome", voluntario.getVol_nome());
                dashboard.put("email", voluntario.getVol_email());
            }

            // Estatísticas básicas (pode ser expandido)
            dashboard.put("totalCampanhas", 0); // Implementar contagem se necessário
            dashboard.put("campanhasAtivas", 0);
            dashboard.put("ultimoLogin", new Date());

            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            System.err.println("❌ Erro ao obter dashboard: " + e.getMessage());
            Map<String, Object> error = Map.of("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}