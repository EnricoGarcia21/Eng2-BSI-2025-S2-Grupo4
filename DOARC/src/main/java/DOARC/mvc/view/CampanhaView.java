package DOARC.mvc.view;

import DOARC.mvc.controller.CampanhaController;
import DOARC.mvc.model.Campanha;
import DOARC.mvc.model.EmailNotification;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
<<<<<<< HEAD
import DOARC.mvc.util.SingletonDB;
import jakarta.servlet.http.HttpServletRequest;
=======
import DOARC.mvc.util.Mensagem;
import DOARC.mvc.util.SingletonDB;
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
@CrossOrigin
public class CampanhaView {

=======
public class CampanhaView {


>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    @Autowired
    private CampanhaController campanhaController;

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private EmailNotification emailNotification;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

<<<<<<< HEAD
    // Helper para verificar se é Admin
    private boolean isAdmin(HttpServletRequest request) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        String role = (String) request.getAttribute("role");
        return authenticated != null && authenticated && ("ADMIN".equalsIgnoreCase(role));
    }

    // ===== LEITURA (Público para todos os logados) =====

    @GetMapping
=======
    // ===== ENDPOINTS PÚBLICOS (para voluntários) =====

    @GetMapping("/listar")
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
            if (campanha.containsKey("erro")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(campanha);
        } catch (Exception e) {
=======

            if (campanha.containsKey("erro")) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(campanha);
        } catch (Exception e) {
            System.err.println("❌ Erro ao obter campanha: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
=======
            System.err.println("❌ Erro ao listar campanhas do voluntário: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }

<<<<<<< HEAD
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

=======
    // ===== ENDPOINTS ADMINISTRATIVOS =====

    @PostMapping // ✅ CORRIGIDO: Mapeia para /apis/campanha (POST)
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
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            Map<String, Object> resultado = campanhaController.addCampanha(novaCampanha);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

<<<<<<< HEAD
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
=======
            // ===== ENVIAR NOTIFICAÇÕES POR EMAIL (Omitido por brevidade) =====
            try {
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
                        resultado.put("aviso", "Campanha criada, mas houve erro no envio de algumas notificações");
                    }
                } else {
                    resultado.put("aviso", "Campanha criada, mas nenhum voluntário para notificar");
                }

            } catch (Exception emailError) {
                System.err.println("⚠️ Erro ao enviar notificações: " + emailError.getMessage());
                resultado.put("aviso", "Campanha criada, mas houve erro no envio de notificações");
            }
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

            response.put("success", true);
            response.put("message", "Campanha lançada com sucesso!");
            response.put("campanha", resultado);

<<<<<<< HEAD
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("erro", "Erro interno: " + e.getMessage());
=======
            System.out.println("✅ Campanha lançada com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao lançar campanha: " + e.getMessage());
            e.printStackTrace();
            response.put("erro", "Erro interno do servidor: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
<<<<<<< HEAD
    public ResponseEntity<Map<String, Object>> atualizarCampanha(@PathVariable int id, @RequestBody Map<String, Object> dados, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 🔒 PROTEÇÃO BACKEND
        if (!isAdmin(request)) {
            response.put("erro", "Acesso negado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
=======
    public ResponseEntity<Map<String, Object>> atualizarCampanha(@PathVariable int id, @RequestBody Map<String, Object> dados) {
        // ... (método omitido)
        Map<String, Object> response = new HashMap<>();

        try {
            // Criar objeto Campanha com ID
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            Campanha campanha = new Campanha();
            campanha.setCam_id(id);
            campanha.setCam_data_ini((String) dados.get("cam_data_ini"));
            campanha.setCam_data_fim((String) dados.get("cam_data_fim"));
<<<<<<< HEAD

            Object volIdObj = dados.get("voluntario_vol_id");
            if (volIdObj instanceof Number) campanha.setVoluntario_vol_id(((Number) volIdObj).intValue());

            campanha.setCam_desc((String) dados.get("cam_desc"));

            Object metaObj = dados.get("cam_meta_arrecadacao");
            if (metaObj instanceof Number) campanha.setCam_meta_arrecadacao(((Number) metaObj).doubleValue());
=======
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
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

            Map<String, Object> resultado = campanhaController.updtCampanha(campanha);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
<<<<<<< HEAD
            response.put("message", "Campanha atualizada!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("erro", "Erro interno");
=======
            response.put("message", "Campanha atualizada com sucesso!");
            response.put("campanha", resultado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar campanha: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
<<<<<<< HEAD
    public ResponseEntity<Map<String, Object>> deletarCampanha(@PathVariable int id, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 🔒 PROTEÇÃO BACKEND
        if (!isAdmin(request)) {
            response.put("erro", "Acesso negado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

=======
    public ResponseEntity<Map<String, Object>> deletarCampanha(@PathVariable int id) {
        // ... (método omitido)
        Map<String, Object> response = new HashMap<>();

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        try {
            Map<String, Object> resultado = campanhaController.deletarCampanha(id);

            if (resultado.containsKey("erro")) {
                response.put("erro", resultado.get("erro"));
                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
<<<<<<< HEAD
            response.put("message", "Campanha removida!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("erro", "Erro interno");
=======
            response.put("message", "Campanha removida com sucesso!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao deletar campanha: " + e.getMessage());
            response.put("erro", "Erro interno do servidor");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}