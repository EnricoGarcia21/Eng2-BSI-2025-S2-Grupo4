package DOARC.mvc.view;

import DOARC.mvc.controller.CampanhaController;
import DOARC.mvc.model.Campanha;
import DOARC.mvc.model.EmailNotification;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.Mensagem;
import DOARC.mvc.util.SingletonDB;
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

    // ===== ENDPOINTS PÚBLICOS (para voluntários) =====

    @GetMapping("/listar")
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
            System.err.println("❌ Erro ao obter campanha: " + e.getMessage());
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
            System.err.println("❌ Erro ao listar campanhas do voluntário: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

    // ===== ENDPOINTS ADMINISTRATIVOS =====

    @PostMapping("/lancar")
    public ResponseEntity<Map<String, Object>> lancarCampanha(@RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("🚀 Lançando nova campanha...");

            // Criar objeto Campanha
            Campanha novaCampanha = new Campanha();
            novaCampanha.setCam_data_ini((String) dados.get("cam_data_ini"));
            novaCampanha.setCam_data_fim((String) dados.get("cam_data_fim"));
            novaCampanha.setVoluntario_vol_id(((Number) dados.get("voluntario_vol_id")).intValue());
            novaCampanha.setCam_desc((String) dados.get("cam_desc"));

            // Converter valores numéricos
            Object metaObj = dados.get("cam_meta_arrecadacao");
            if (metaObj instanceof Number) {
                novaCampanha.setCam_meta_arrecadacao(((Number) metaObj).doubleValue());
            }

            Object valorObj = dados.get("cam_valor_arrecadado");
            if (valorObj instanceof Number) {
                novaCampanha.setCam_valor_arrecadado(((Number) valorObj).doubleValue());
            } else {
                novaCampanha.setCam_valor_arrecadado(0.0);
            }

            // Salvar campanha
            Map<String, Object> resultado = campanhaController.addCampanha(novaCampanha);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            // ===== ENVIAR NOTIFICAÇÕES POR EMAIL =====
            try {
                System.out.println("📧 Enviando notificações por email...");

                // Buscar todos os voluntários ativos
                List<Voluntario> voluntarios = voluntarioModel.consultar("", getConexao());
                List<String> emails = voluntarios.stream()
                        .map(Voluntario::getVol_email)
                        .filter(email -> email != null && !email.trim().isEmpty())
                        .collect(Collectors.toList());

                if (!emails.isEmpty()) {
                    String titulo = "Nova Campanha: " + novaCampanha.getCam_desc();
                    String descricao = String.format(
                            "Data de início: %s\nData de fim: %s\nMeta: R$ %.2f",
                            novaCampanha.getCam_data_ini(),
                            novaCampanha.getCam_data_fim(),
                            novaCampanha.getCam_meta_arrecadacao()
                    );

                    boolean emailEnviado = emailNotification.enviarNotificacaoCampanha(emails, titulo, descricao);

                    if (emailEnviado) {
                        System.out.println("✅ Notificações enviadas para " + emails.size() + " voluntários");
                        resultado.put("notificacoes_enviadas", emails.size());
                    } else {
                        System.err.println("⚠️ Erro ao enviar algumas notificações");
                        resultado.put("aviso", "Campanha criada, mas houve erro no envio de algumas notificações");
                    }
                } else {
                    System.out.println("⚠️ Nenhum email de voluntário encontrado");
                    resultado.put("aviso", "Campanha criada, mas nenhum voluntário para notificar");
                }

            } catch (Exception emailError) {
                System.err.println("⚠️ Erro ao enviar notificações: " + emailError.getMessage());
                resultado.put("aviso", "Campanha criada, mas houve erro no envio de notificações");
            }

            response.put("success", true);
            response.put("message", "Campanha lançada com sucesso!");
            response.put("campanha", resultado);

            System.out.println("✅ Campanha lançada com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao lançar campanha: " + e.getMessage());
            e.printStackTrace();
            response.put("erro", "Erro interno do servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizarCampanha(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Criar objeto Campanha com ID
            Campanha campanha = new Campanha();
            campanha.setCam_id(id);
            campanha.setCam_data_ini((String) dados.get("cam_data_ini"));
            campanha.setCam_data_fim((String) dados.get("cam_data_fim"));
            campanha.setVoluntario_vol_id(((Number) dados.get("voluntario_vol_id")).intValue());
            campanha.setCam_desc((String) dados.get("cam_desc"));

            Object metaObj = dados.get("cam_meta_arrecadacao");
            if (metaObj instanceof Number) {
                campanha.setCam_meta_arrecadacao(((Number) metaObj).doubleValue());
            }

            Object valorObj = dados.get("cam_valor_arrecadado");
            if (valorObj instanceof Number) {
                campanha.setCam_valor_arrecadado(((Number) valorObj).doubleValue());
            }

            Map<String, Object> resultado = campanhaController.updtCampanha(campanha);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Campanha atualizada com sucesso!");
            response.put("campanha", resultado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar campanha: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletarCampanha(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Map<String, Object> resultado = campanhaController.deletarCampanha(id);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Campanha removida com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao deletar campanha: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}