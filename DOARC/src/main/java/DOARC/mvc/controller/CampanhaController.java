package DOARC.mvc.controller;

import DOARC.mvc.model.Campanha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampanhaController {

    @Autowired // Controller recebe a Model (não o DAO)
    private Campanha campanhaModel;

    public List<Map<String, Object>> getCampanha() {
        // Chama o método consultar da Model
        List<Campanha> lista = campanhaModel.consultar("");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Campanha c : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("cam_id", c.getCam_id());
            json.put("cam_data_ini", c.getCam_data_ini());
            json.put("cam_data_fim", c.getCam_data_fim());
            json.put("voluntario_vol_id", c.getVoluntario_vol_id());
            json.put("cam_desc", c.getCam_desc());
            json.put("cam_meta_arrecadacao", c.getCam_meta_arrecadacao());
            json.put("cam_valor_arrecadado", c.getCam_valor_arrecadado());
            result.add(json);
        }
        return result;
    }

    public Map<String, Object> getCampanha(int id) {
        // Chama o método consultar da Model
        Campanha c = campanhaModel.consultar(id);
        if (c == null) return Map.of("erro", "Campanha não encontrada");

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

    public Map<String, Object> addCampanha(String cam_data_ini, String cam_data_fim, int voluntario_vol_id,
                                           String cam_desc, Double cam_meta_arrecadacao, Double cam_valor_arrecadado) {
        // Controller instancia a Model
        Campanha novo = new Campanha(cam_data_ini, cam_data_fim, voluntario_vol_id, cam_desc, cam_meta_arrecadacao, cam_valor_arrecadado);

        // Chama o método gravar da Model
        Campanha gravado = campanhaModel.gravar();
        if (gravado == null) return Map.of("erro", "Erro ao cadastrar a Campanha");

        Map<String, Object> json = new HashMap<>();
        json.put("cam_id", gravado.getCam_id());
        json.put("cam_data_ini", gravado.getCam_data_ini());
        json.put("cam_data_fim", gravado.getCam_data_fim());
        json.put("voluntario_vol_id", gravado.getVoluntario_vol_id());
        json.put("cam_desc", gravado.getCam_desc());
        json.put("cam_meta_arrecadacao", gravado.getCam_meta_arrecadacao());
        json.put("cam_valor_arrecadado", gravado.getCam_valor_arrecadado());
        return json;
    }

    public Map<String, Object> updtCampanha(int cam_id, String cam_data_ini, String cam_data_fim, int voluntario_vol_id,
                                            String cam_desc, Double cam_meta_arrecadacao, Double cam_valor_arrecadado) {
        // Chama o método consultar da Model
        Campanha existente = campanhaModel.consultar(cam_id);
        if (existente == null) return Map.of("erro", "Campanha não encontrada");

        existente.setCam_data_ini(cam_data_ini);
        existente.setCam_data_fim(cam_data_fim);
        existente.setVoluntario_vol_id(voluntario_vol_id);
        existente.setCam_desc(cam_desc);
        existente.setCam_meta_arrecadacao(cam_meta_arrecadacao);
        existente.setCam_valor_arrecadado(cam_valor_arrecadado);

        // Chama o método alterar da Model
        Campanha atualizado = existente.alterar();
        if (atualizado == null) return Map.of("erro", "Erro ao atualizar a Campanha");

        Map<String, Object> json = new HashMap<>();
        json.put("cam_id", atualizado.getCam_id());
        json.put("cam_data_ini", atualizado.getCam_data_ini());
        json.put("cam_data_fim", atualizado.getCam_data_fim());
        json.put("voluntario_vol_id", atualizado.getVoluntario_vol_id());
        json.put("cam_desc", atualizado.getCam_desc());
        json.put("cam_meta_arrecadacao", atualizado.getCam_meta_arrecadacao());
        json.put("cam_valor_arrecadado", atualizado.getCam_valor_arrecadado());
        return json;
    }

    public Map<String, Object> deletarCampanha(int id) {
        // Chama o método consultar da Model
        Campanha c = campanhaModel.consultar(id);
        if (c == null) return Map.of("erro", "Campanha não encontrada");

        // Chama o método apagar da Model
        boolean deletado = c.apagar();
        return deletado ? Map.of("mensagem", "Campanha removida com sucesso") : Map.of("erro", "Erro ao remover a Campanha");
    }

    public List<Map<String, Object>> getCampanhasPorVoluntario(int voluntarioId) {
        // Chama o método consultar da Model com filtro específico
        List<Campanha> lista = campanhaModel.consultar("voluntario_vol_id = " + voluntarioId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Campanha c : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("cam_id", c.getCam_id());
            json.put("cam_data_ini", c.getCam_data_ini());
            json.put("cam_data_fim", c.getCam_data_fim());
            json.put("voluntario_vol_id", c.getVoluntario_vol_id());
            json.put("cam_desc", c.getCam_desc());
            json.put("cam_meta_arrecadacao", c.getCam_meta_arrecadacao());
            json.put("cam_valor_arrecadado", c.getCam_valor_arrecadado());
            result.add(json);
        }
        return result;
    }
}