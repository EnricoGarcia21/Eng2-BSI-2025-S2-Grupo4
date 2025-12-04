package DOARC.mvc.controller;

import DOARC.mvc.model.CampResponsavel;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampRespController {

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    private Map<String, Object> toJson(CampResponsavel cr) {
        Map<String, Object> json = new HashMap<>();
        json.put("cam_id", cr.getCam_id());
        json.put("voluntario_vol_id", cr.getVoluntario_vol_id());
        json.put("DATA_INICIO", cr.getDATA_INICIO());
        json.put("DATA_FIM", cr.getDATA_FIM());
        json.put("Obs_texto", cr.getObs_texto());
        return json;
    }

    private String validarCampResponsavel(CampResponsavel cr) {
        if (cr.getCam_id() <= 0) return "ID da campanha é obrigatório";
        if (cr.getVoluntario_vol_id() <= 0) return "ID do voluntário é obrigatório";
        if (cr.getDATA_INICIO() == null) return "Data de início é obrigatória";
        if (cr.getDATA_FIM() == null) return "Data de fim é obrigatória";
        return null;
    }

    public Map<String, Object> addCampResponsavel(CampResponsavel nova) {
        String erroValidacao = validarCampResponsavel(nova);
        if (erroValidacao != null) return Map.of("erro", erroValidacao);

        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> existentes = CampResponsavel.getPorChave(
                    nova.getCam_id(), nova.getVoluntario_vol_id(), conexao);

            if (!existentes.isEmpty()) {
                return Map.of("erro", "Este voluntário já está vinculado a esta campanha");
            }

            CampResponsavel gravada = nova.gravar(conexao);
            if (gravada == null) return Map.of("erro", "Erro ao vincular voluntário");

            return toJson(gravada);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao vincular: " + e.getMessage());
        }
    }

    public Map<String, Object> updtCampResponsavel(CampResponsavel campResp) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> existentes = CampResponsavel.getPorChave(
                    campResp.getCam_id(), campResp.getVoluntario_vol_id(), conexao);

            if (existentes.isEmpty()) return Map.of("erro", "Vinculação não encontrada");

            CampResponsavel existente = existentes.get(0);
            existente.setDATA_INICIO(campResp.getDATA_INICIO());
            existente.setDATA_FIM(campResp.getDATA_FIM());
            existente.setObs_texto(campResp.getObs_texto());

            CampResponsavel alterada = existente.alterar(conexao);
            if (alterada == null) return Map.of("erro", "Erro ao atualizar");

            return toJson(alterada);
        } catch (Exception e) {
            return Map.of("erro", "Erro: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getCampResponsavel() {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = CampResponsavel.get("", conexao);
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            return List.of(Map.of("erro", "Erro: " + e.getMessage()));
        }
    }

    public List<Map<String, Object>> getCampResponsavel(String filtro) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = CampResponsavel.get(filtro, conexao);
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            return List.of(Map.of("erro", "Erro: " + e.getMessage()));
        }
    }

    public Map<String, Object> getCampResponsavel(int camId, int voluntarioId) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = CampResponsavel.getPorChave(camId, voluntarioId, conexao);
            if (lista.isEmpty()) return Map.of("erro", "Vinculação não encontrada");
            return toJson(lista.get(0));
        } catch (Exception e) {
            return Map.of("erro", "Erro: " + e.getMessage());
        }
    }

    public Map<String, Object> deletarCampResponsavel(int camId, int voluntarioId) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> existentes = CampResponsavel.getPorChave(camId, voluntarioId, conexao);
            if (existentes.isEmpty()) return Map.of("erro", "Vinculação não encontrada");

            boolean sucesso = existentes.get(0).apagar(conexao);
            return sucesso ? Map.of("mensagem", "Removido com sucesso") : Map.of("erro", "Erro ao remover");
        } catch (Exception e) {
            return Map.of("erro", "Erro: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getCampanhasPorVoluntario(int voluntarioId) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = CampResponsavel.getPorVoluntario(voluntarioId, conexao);
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            return List.of(Map.of("erro", "Erro: " + e.getMessage()));
        }
    }

    public List<Map<String, Object>> getVoluntariosPorCampanha(int campanhaId) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = CampResponsavel.get("camp_id = " + campanhaId, conexao);
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("erro", "Erro ao buscar voluntários: " + e.getMessage()));
        }
    }
}