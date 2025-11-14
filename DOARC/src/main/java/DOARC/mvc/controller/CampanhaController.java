package DOARC.mvc.controller;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampanhaController {

    @Autowired
    private Campanha campanhaModel;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    private Map<String, Object> toJson(Campanha c) {
        Map<String, Object> json = new HashMap<>();
        json.put("cam_id", c.getCam_id());
        json.put("cam_data_ini", c.getCam_data_ini());
        json.put("cam_data_fim", c.getCam_data_fim());
        json.put("voluntario_vol_id", c.getVoluntario_vol_id());
        json.put("cam_desc", c.getCam_desc());
        json.put("cam_meta_arrecadacao", c.getCam_meta_arrecadacao());
        json.put("cam_valor_arrecadado", c.getCam_valor_arrecadado());
        return json;
    }

    // ✅ VALIDAÇÃO DOS DADOS (Omitida por brevidade)
    private String validarCampanha(Campanha c) {
        if (c.getCam_data_ini() == null || c.getCam_data_ini().isBlank()) {
            return "Data de início é obrigatória";
        }
        if (c.getCam_data_fim() == null || c.getCam_data_fim().isBlank()) {
            return "Data de fim é obrigatória";
        }
        if (c.getCam_desc() == null || c.getCam_desc().isBlank()) {
            return "Descrição é obrigatória";
        }
        if (c.getVoluntario_vol_id() <= 0) {
            return "ID do voluntário inválido";
        }
        if (c.getCam_meta_arrecadacao() == null || c.getCam_meta_arrecadacao() < 0) {
            return "Meta de arrecadação inválida";
        }
        if (c.getCam_valor_arrecadado() == null) {
            c.setCam_valor_arrecadado(0.0); // Define 0 se não informado
        }
        if (c.getCam_valor_arrecadado() < 0) {
            return "Valor arrecadado não pode ser negativo";
        }
        return null; // Sem erros
    }

    // ===================================
    // ✅ ADD (COM VALIDAÇÃO)
    // ===================================
    public Map<String, Object> addCampanha(Campanha nova) {

        // Validar dados
        String erroValidacao = validarCampanha(nova);
        if (erroValidacao != null) {
            return Map.of("erro", erroValidacao);
        }

        try {
            Conexao conexao = getConexao();

            if (conexao == null) {
                return Map.of("erro", "Erro ao conectar ao banco de dados");
            }

            Campanha gravada = campanhaModel.gravar(nova, conexao);

            if (gravada == null) {
                String msgErro = conexao.getMensagemErro();
                return Map.of("erro", "Erro ao cadastrar a Campanha: " +
                        (msgErro != null ? msgErro : "Erro desconhecido"));
            }

            return toJson(gravada);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao cadastrar a Campanha: " + e.getMessage());
        }
    }

    // ===================================
    // ✅ UPDATE (COM VALIDAÇÃO)
    // ===================================
    public Map<String, Object> updtCampanha(Campanha campanha) {

        // Validar dados
        String erroValidacao = validarCampanha(campanha);
        if (erroValidacao != null) {
            return Map.of("erro", erroValidacao);
        }

        if (campanha.getCam_id() <= 0) {
            return Map.of("erro", "ID da campanha é obrigatório");
        }

        try {
            Conexao conexao = getConexao();

            if (conexao == null) {
                return Map.of("erro", "Erro ao conectar ao banco de dados");
            }

            Campanha existente = campanhaModel.consultar(campanha.getCam_id(), conexao);
            if (existente == null) {
                return Map.of("erro", "Campanha não encontrada");
            }

            existente.setCam_data_ini(campanha.getCam_data_ini());
            existente.setCam_data_fim(campanha.getCam_data_fim());
            existente.setVoluntario_vol_id(campanha.getVoluntario_vol_id());
            existente.setCam_desc(campanha.getCam_desc());
            existente.setCam_meta_arrecadacao(campanha.getCam_meta_arrecadacao());
            existente.setCam_valor_arrecadado(campanha.getCam_valor_arrecadado());

            Campanha alterada = campanhaModel.alterar(existente, conexao);

            if (alterada == null) {
                String msgErro = conexao.getMensagemErro();
                return Map.of("erro", "Erro ao atualizar a Campanha: " +
                        (msgErro != null ? msgErro : "Erro desconhecido"));
            }

            return toJson(alterada);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao atualizar a Campanha: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getCampanha() {
        Conexao conexao = getConexao();
        List<Campanha> lista = campanhaModel.consultar("", conexao);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Campanha c : lista) result.add(toJson(c));
        return result;
    }

    public Map<String, Object> getCampanha(int id) {
        Conexao conexao = getConexao();
        Campanha c = campanhaModel.consultar(id, conexao);
        return (c == null) ? Map.of("erro", "Campanha não encontrada") : toJson(c);
    }

    public Map<String, Object> deletarCampanha(int id) {
        Conexao conexao = getConexao();
        Campanha c = campanhaModel.consultar(id, conexao);
        if (c == null) return Map.of("erro", "Campanha não encontrada");

        return campanhaModel.apagar(c, conexao)
                ? Map.of("mensagem", "Campanha removida com sucesso")
                : Map.of("erro", "Erro ao remover campanha");
    }

    public List<Map<String, Object>> getCampanhasPorVoluntario(int voluntarioId) {
        Conexao conexao = getConexao();
        List<Campanha> lista = campanhaModel.getCampanhasPorVoluntario(voluntarioId, conexao);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Campanha c : lista) result.add(toJson(c));
        return result;
    }
}