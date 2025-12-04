package DOARC.mvc.controller;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CampanhaController {

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

    private String validarCampanha(Campanha c) {
        if (c.getCam_data_ini() == null || c.getCam_data_ini().isBlank()) return "Data de início é obrigatória";
        if (c.getCam_data_fim() == null || c.getCam_data_fim().isBlank()) return "Data de fim é obrigatória";
        if (c.getCam_desc() == null || c.getCam_desc().isBlank()) return "Descrição é obrigatória";
        if (c.getVoluntario_vol_id() <= 0) return "ID do voluntário inválido";
        if (c.getCam_meta_arrecadacao() == null || c.getCam_meta_arrecadacao() < 0) return "Meta de arrecadação inválida";

        try {
            LocalDate dtInicio = LocalDate.parse(c.getCam_data_ini());
            LocalDate dtFim = LocalDate.parse(c.getCam_data_fim());
            LocalDate hoje = LocalDate.now();

            if (c.getCam_id() == 0 && dtInicio.isBefore(hoje)) {
                return "A data de início deve ser igual ou posterior à data atual.";
            }
            if (dtFim.isBefore(dtInicio)) {
                return "A data final não pode ser menor que a data de início.";
            }
        } catch (Exception e) {
            return "Formato de data inválido.";
        }

        if (c.getCam_valor_arrecadado() == null) c.setCam_valor_arrecadado(0.0);
        if (c.getCam_valor_arrecadado() < 0) return "Valor arrecadado não pode ser negativo";

        return null;
    }

    public Map<String, Object> addCampanha(Campanha nova) {
        String erroValidacao = validarCampanha(nova);
        if (erroValidacao != null) return Map.of("erro", erroValidacao);

        try {
            Conexao conexao = getConexao();
            Campanha gravada = nova.gravar(conexao);

            if (gravada == null) {
                return Map.of("erro", "Erro ao cadastrar a Campanha.");
            }
            return toJson(gravada);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao cadastrar a Campanha: " + e.getMessage());
        }
    }

    public Map<String, Object> updtCampanha(Campanha campanha) {
        String erroValidacao = validarCampanha(campanha);
        if (erroValidacao != null) return Map.of("erro", erroValidacao);
        if (campanha.getCam_id() <= 0) return Map.of("erro", "ID da campanha é obrigatório");

        try {
            Conexao conexao = getConexao();
            Campanha existente = Campanha.get(campanha.getCam_id(), conexao);
            if (existente == null) return Map.of("erro", "Campanha não encontrada");

            existente.setCam_data_ini(campanha.getCam_data_ini());
            existente.setCam_data_fim(campanha.getCam_data_fim());
            existente.setVoluntario_vol_id(campanha.getVoluntario_vol_id());
            existente.setCam_desc(campanha.getCam_desc());
            existente.setCam_meta_arrecadacao(campanha.getCam_meta_arrecadacao());
            existente.setCam_valor_arrecadado(campanha.getCam_valor_arrecadado());

            Campanha alterada = existente.alterar(conexao);

            if (alterada == null) return Map.of("erro", "Erro ao atualizar a Campanha.");
            return toJson(alterada);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao atualizar a Campanha: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getCampanha() {
        Conexao conexao = getConexao();
        List<Campanha> lista = Campanha.get("", conexao);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Campanha c : lista) result.add(toJson(c));
        return result;
    }

    public Map<String, Object> getCampanha(int id) {
        Conexao conexao = getConexao();
        Campanha c = Campanha.get(id, conexao);
        return (c == null) ? Map.of("erro", "Campanha não encontrada") : toJson(c);
    }

    public Map<String, Object> deletarCampanha(int id) {
        Conexao conexao = getConexao();
        Campanha c = Campanha.get(id, conexao);
        if (c == null) return Map.of("erro", "Campanha não encontrada");

        return c.apagar(conexao)
                ? Map.of("mensagem", "Campanha removida com sucesso")
                : Map.of("erro", "Erro ao remover campanha");
    }

    public List<Map<String, Object>> getCampanhasPorVoluntario(int voluntarioId) {
        Conexao conexao = getConexao();
        List<Campanha> lista = Campanha.getPorVoluntario(voluntarioId, conexao);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Campanha c : lista) result.add(toJson(c));
        return result;
    }
}