package DOARC.mvc.controller;

import DOARC.mvc.dao.CampanhaDAO;
import DOARC.mvc.model.Campanha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampanhaController {

    @Autowired
    private CampanhaDAO campModel;

    // Retorna todas as campanhas cadastradas
    public List<Map<String, Object>> getCampanhas() {
        try {
            List<Campanha> lista = campModel.get("");
            List<Map<String, Object>> list = new ArrayList<>();

            for (Campanha camp : lista) {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("cam_id", camp.getCam_id());
                json.put("cam_data_ini", camp.getCam_data_ini());
                json.put("cam_data_fim", camp.getCam_data_fim());
                json.put("voluntario_vol_id", camp.getVoluntario_vol_id());
                json.put("cam_desc", camp.getCam_desc());
                json.put("cam_meta_arrecadacao", camp.getCam_meta_arrecadacao());
                json.put("cam_valor_arrecadado", camp.getCam_valor_arrecadado());
                list.add(json);
            }
            return list;
        } catch (Exception e) {
            return Collections.singletonList(Map.of("erro", "Erro ao buscar campanhas: " + e.getMessage()));
        }
    }

    // Retorna apenas uma campanha por ID
    public Map<String, Object> getCampanha(int id) {
        try {
            Campanha camp = campModel.get(id);

            if (camp == null) {
                return Map.of("erro", "Campanha não encontrada");
            }

            Map<String, Object> json = new LinkedHashMap<>();
            json.put("cam_id", camp.getCam_id());
            json.put("cam_data_ini", camp.getCam_data_ini());
            json.put("cam_data_fim", camp.getCam_data_fim());
            json.put("voluntario_vol_id", camp.getVoluntario_vol_id());
            json.put("cam_desc", camp.getCam_desc());
            json.put("cam_meta_arrecadacao", camp.getCam_meta_arrecadacao());
            json.put("cam_valor_arrecadado", camp.getCam_valor_arrecadado());
            return json;
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar campanha: " + e.getMessage());
        }
    }

    // Adiciona uma nova campanha
    public Map<String, Object> addCampanha(String cam_data_ini, String cam_data_fim, int voluntario_vol_id,
                                           String cam_desc, Double cam_meta_arrecadacao, Double cam_valor_arrecadado) {
        try {
            // Validações básicas
            if (cam_meta_arrecadacao == null || cam_meta_arrecadacao < 0) {
                return Map.of("erro", "Meta de arrecadação deve ser um valor positivo");
            }
            if (cam_valor_arrecadado == null || cam_valor_arrecadado < 0) {
                return Map.of("erro", "Valor arrecadado deve ser um valor positivo");
            }

            Campanha nova = new Campanha(0, cam_data_ini, cam_data_fim, voluntario_vol_id,
                    cam_desc, cam_meta_arrecadacao, cam_valor_arrecadado);

            Campanha gravada = campModel.gravar(nova);

            if (gravada == null) {
                return Map.of("erro", "Erro ao cadastrar a campanha");
            }

            Map<String, Object> json = new LinkedHashMap<>();
            json.put("cam_id", gravada.getCam_id());
            json.put("cam_data_ini", gravada.getCam_data_ini());
            json.put("cam_data_fim", gravada.getCam_data_fim());
            json.put("voluntario_vol_id", gravada.getVoluntario_vol_id());
            json.put("cam_desc", gravada.getCam_desc());
            json.put("cam_meta_arrecadacao", gravada.getCam_meta_arrecadacao());
            json.put("cam_valor_arrecadado", gravada.getCam_valor_arrecadado());
            return json;
        } catch (Exception e) {
            return Map.of("erro", "Erro ao cadastrar campanha: " + e.getMessage());
        }
    }

    // Atualiza uma campanha existente
    public Map<String, Object> updtCampanha(int cam_id, String cam_data_ini, String cam_data_fim, int voluntario_vol_id,
                                            String cam_desc, Double cam_meta_arrecadacao, Double cam_valor_arrecadado) {
        try {
            // Validações básicas
            if (cam_meta_arrecadacao == null || cam_meta_arrecadacao < 0) {
                return Map.of("erro", "Meta de arrecadação deve ser um valor positivo");
            }
            if (cam_valor_arrecadado == null || cam_valor_arrecadado < 0) {
                return Map.of("erro", "Valor arrecadado deve ser um valor positivo");
            }

            Campanha existente = campModel.get(cam_id);

            if (existente == null) {
                return Map.of("erro", "Campanha não encontrada");
            }

            existente.setCam_data_ini(cam_data_ini);
            existente.setCam_data_fim(cam_data_fim);
            existente.setVoluntario_vol_id(voluntario_vol_id);
            existente.setCam_desc(cam_desc);
            existente.setCam_meta_arrecadacao(cam_meta_arrecadacao);
            existente.setCam_valor_arrecadado(cam_valor_arrecadado);

            Campanha atualizado = campModel.alterar(existente);

            if (atualizado == null) {
                return Map.of("erro", "Erro ao atualizar a campanha");
            }

            Map<String, Object> json = new LinkedHashMap<>();
            json.put("cam_id", atualizado.getCam_id());
            json.put("cam_data_ini", atualizado.getCam_data_ini());
            json.put("cam_data_fim", atualizado.getCam_data_fim());
            json.put("voluntario_vol_id", atualizado.getVoluntario_vol_id());
            json.put("cam_desc", atualizado.getCam_desc());
            json.put("cam_meta_arrecadacao", atualizado.getCam_meta_arrecadacao());
            json.put("cam_valor_arrecadado", atualizado.getCam_valor_arrecadado());
            return json;
        } catch (Exception e) {
            return Map.of("erro", "Erro ao atualizar campanha: " + e.getMessage());
        }
    }

    // Deleta uma campanha
    public Map<String, Object> deletarCampanha(int cam_id) {
        try {
            Campanha c = campModel.get(cam_id);

            if (c == null) {
                return Map.of("erro", "Campanha não encontrada");
            }

            boolean deletado = campModel.apagar(c);

            return deletado
                    ? Map.of("mensagem", "Campanha removida com sucesso")
                    : Map.of("erro", "Erro ao remover a campanha");
        } catch (Exception e) {
            return Map.of("erro", "Erro ao deletar campanha: " + e.getMessage());
        }
    }
    // No CampanhaController.java, adicione este método:
    public List<Map<String, Object>> getCampanhasPorVoluntario(int voluntarioId) {
        try {
            List<Campanha> lista = campModel.getCampanhasPorVoluntario(voluntarioId);
            List<Map<String, Object>> list = new ArrayList<>();

            for (Campanha camp : lista) {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("cam_id", camp.getCam_id());
                json.put("cam_data_ini", camp.getCam_data_ini());
                json.put("cam_data_fim", camp.getCam_data_fim());
                json.put("voluntario_vol_id", camp.getVoluntario_vol_id());
                json.put("cam_desc", camp.getCam_desc());
                json.put("cam_meta_arrecadacao", camp.getCam_meta_arrecadacao());
                json.put("cam_valor_arrecadado", camp.getCam_valor_arrecadado());
                list.add(json);
            }
            return list;
        } catch (Exception e) {
            return Collections.singletonList(Map.of("erro", "Erro ao buscar campanhas do voluntário: " + e.getMessage()));
        }
    }
}