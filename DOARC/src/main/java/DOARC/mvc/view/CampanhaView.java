package DOARC.mvc.view;

import DOARC.mvc.controller.CampanhaController;
import DOARC.mvc.model.Campanha;
import DOARC.mvc.model.EmailNotification;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/apis/campanha")
@CrossOrigin
public class CampanhaView {

    @Autowired
    private CampanhaController campanhaController;

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private EmailNotification emailNotification;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    // Helper para verificar se é Admin
    private boolean isAdmin(HttpServletRequest request) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        String role = (String) request.getAttribute("role");
        return authenticated != null && authenticated && ("ADMIN".equalsIgnoreCase(role));
    }

    // ===== LEITURA (Público para todos os logados) =====

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarCampanhas() {
        try {
            List<Map<String, Object>> campanhas = campanhaController.getCampanha();
            return ResponseEntity.ok(campanhas);
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar campanhas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obterCampanha(@PathVariable int id) {
        try {
            Map<String, Object> campanha = campanhaController.getCampanha(id);
            if (campanha.containsKey("erro")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(campanha);
        } catch (Exception e) {
            Map<String, Object> error = Map.of("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/voluntario/{voluntarioId}")
    public ResponseEntity<List<Map<String, Object>>> listarCampanhasPorVoluntario(@PathVariable int voluntarioId) {
        try {
            List<Map<String, Object>> campanhas = campanhaController.getCampanhasPorVoluntario(voluntarioId);
            return ResponseEntity.ok(campanhas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // ===== ESCRITA (Restrito para ADMIN) =====

    @PostMapping
    public ResponseEntity<Map<String, Object>> lancarCampanha(@RequestBody Map<String, Object> dados, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 🔒 PROTEÇÃO BACKEND
        if (!isAdmin(request)) {
            response.put("erro", "Acesso negado: Apenas administradores podem criar campanhas.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            System.out.println("🚀 Lançando nova campanha...");

            Campanha novaCampanha = new Campanha();
            novaCampanha.setCam_data_ini((String) dados.get("cam_data_ini"));
            novaCampanha.setCam_data_fim((String) dados.get("cam_data_fim"));

            // Tratamento seguro para Integer
            Object volIdObj = dados.get("voluntario_vol_id");
            if (volIdObj instanceof Number) {
                novaCampanha.setVoluntario_vol_id(((Number) volIdObj).intValue());
            } else if (volIdObj instanceof String) {
                novaCampanha.setVoluntario_vol_id(Integer.parseInt((String) volIdObj));
            }

            novaCampanha.setCam_desc((String) dados.get("cam_desc"));

            Object metaObj = dados.get("cam_meta_arrecadacao");
            if (metaObj instanceof Number) {
                novaCampanha.setCam_meta_arrecadacao(((Number) metaObj).doubleValue());
            } else if (metaObj instanceof String) {
                novaCampanha.setCam_meta_arrecadacao(Double.parseDouble((String) metaObj));
            }

            novaCampanha.setCam_valor_arrecadado(0.0);

            Map<String, Object> resultado = campanhaController.addCampanha(novaCampanha);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            // Notificação por e-mail
            new Thread(() -> {
                try {
                    List<Voluntario> voluntarios = voluntarioModel.consultar("", getConexao());
                    List<String> emails = voluntarios.stream()
                            .map(Voluntario::getVol_email)
                            .filter(email -> email != null && !email.trim().isEmpty())
                            .collect(Collectors.toList());

                    if (!emails.isEmpty()) {
                        String titulo = "Nova Campanha: " + novaCampanha.getCam_desc();
                        String descricao = String.format("Meta: R$ %.2f", novaCampanha.getCam_meta_arrecadacao());
                        emailNotification.enviarNotificacaoCampanha(emails, titulo, descricao);
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao enviar notificação: " + e.getMessage());
                }
            }).start();

            response.put("success", true);
            response.put("message", "Campanha lançada com sucesso!");
            response.put("campanha", resultado);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("erro", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizarCampanha(@PathVariable int id, @RequestBody Map<String, Object> dados, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 🔒 PROTEÇÃO BACKEND
        if (!isAdmin(request)) {
            response.put("erro", "Acesso negado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            Campanha campanha = new Campanha();
            campanha.setCam_id(id);
            campanha.setCam_data_ini((String) dados.get("cam_data_ini"));
            campanha.setCam_data_fim((String) dados.get("cam_data_fim"));

            Object volIdObj = dados.get("voluntario_vol_id");
            if (volIdObj instanceof Number) campanha.setVoluntario_vol_id(((Number) volIdObj).intValue());

            campanha.setCam_desc((String) dados.get("cam_desc"));

            Object metaObj = dados.get("cam_meta_arrecadacao");
            if (metaObj instanceof Number) campanha.setCam_meta_arrecadacao(((Number) metaObj).doubleValue());

            Map<String, Object> resultado = campanhaController.updtCampanha(campanha);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Campanha atualizada!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("erro", "Erro interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarCampanha(@PathVariable int id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 🔒 PROTEÇÃO BACKEND
        if (!isAdmin(request)) {
            response.put("erro", "Acesso negado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            Map<String, Object> resultado = campanhaController.deletarCampanha(id);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Campanha removida!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("erro", "Erro interno");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}