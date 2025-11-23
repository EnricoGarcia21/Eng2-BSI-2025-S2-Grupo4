package DOARC.mvc.controller;

import DOARC.mvc.model.CampResponsavel;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CampRespController {

    @Autowired
    private CampResponsavel campRespModel;

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

    // Validação dos dados
    private String validarCampResponsavel(CampResponsavel cr) {
        if (cr.getCam_id() <= 0) return "ID da campanha é obrigatório";
        if (cr.getVoluntario_vol_id() <= 0) return "ID do voluntário é obrigatório";
        if (cr.getDATA_INICIO() == null) return "Data de início é obrigatória";
        if (cr.getDATA_FIM() == null) return "Data de fim é obrigatória";
        return null;
    }

    // ADD
    public Map<String, Object> addCampResponsavel(CampResponsavel nova) {
        String erroValidacao = validarCampResponsavel(nova);
        if (erroValidacao != null) return Map.of("erro", erroValidacao);

        try {
            Conexao conexao = getConexao();
            // Verifica duplicidade
            List<CampResponsavel> existentes = campRespModel.consultarPorChaveComposta(
                    nova.getCam_id(), nova.getVoluntario_vol_id(), conexao);

            if (!existentes.isEmpty()) {
                return Map.of("erro", "Este voluntário já está vinculado a esta campanha");
            }

            CampResponsavel gravada = campRespModel.gravar(nova, conexao);
            if (gravada == null) return Map.of("erro", "Erro ao vincular voluntário");

            return toJson(gravada);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao vincular: " + e.getMessage());
        }
    }

    // UPDATE
    public Map<String, Object> updtCampResponsavel(CampResponsavel campResp) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> existentes = campRespModel.consultarPorChaveComposta(
                    campResp.getCam_id(), campResp.getVoluntario_vol_id(), conexao);

            if (existentes.isEmpty()) return Map.of("erro", "Vinculação não encontrada");

            CampResponsavel existente = existentes.get(0);
            existente.setDATA_INICIO(campResp.getDATA_INICIO());
            existente.setDATA_FIM(campResp.getDATA_FIM());
            existente.setObs_texto(campResp.getObs_texto());

            CampResponsavel alterada = campRespModel.alterar(existente, conexao);
            if (alterada == null) return Map.of("erro", "Erro ao atualizar");

            return toJson(alterada);
        } catch (Exception e) {
            return Map.of("erro", "Erro: " + e.getMessage());
        }
    }

    // GET ALL
    public List<Map<String, Object>> getCampResponsavel() {
        try {
            List<CampResponsavel> lista = campRespModel.consultar("", getConexao());
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            return List.of(Map.of("erro", "Erro: " + e.getMessage()));
        }
    }

    // GET FILTRO
    public List<Map<String, Object>> getCampResponsavel(String filtro) {
        try {
            List<CampResponsavel> lista = campRespModel.consultar(filtro, getConexao());
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            return List.of(Map.of("erro", "Erro: " + e.getMessage()));
        }
    }

    // GET SPECIFIC
    public Map<String, Object> getCampResponsavel(int camId, int voluntarioId) {
        try {
            List<CampResponsavel> lista = campRespModel.consultarPorChaveComposta(camId, voluntarioId, getConexao());
            if (lista.isEmpty()) return Map.of("erro", "Vinculação não encontrada");
            return toJson(lista.get(0));
        } catch (Exception e) {
            return Map.of("erro", "Erro: " + e.getMessage());
        }
    }

    // DELETE
    public Map<String, Object> deletarCampResponsavel(int camId, int voluntarioId) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> existentes = campRespModel.consultarPorChaveComposta(camId, voluntarioId, conexao);
            if (existentes.isEmpty()) return Map.of("erro", "Vinculação não encontrada");

            boolean sucesso = campRespModel.apagar(existentes.get(0), conexao);
            return sucesso ? Map.of("mensagem", "Removido com sucesso") : Map.of("erro", "Erro ao remover");
        } catch (Exception e) {
            return Map.of("erro", "Erro: " + e.getMessage());
        }
    }

    // GET BY VOLUNTARIO
    public List<Map<String, Object>> getCampanhasPorVoluntario(int voluntarioId) {
        try {
            List<CampResponsavel> lista = campRespModel.getCampanhasPorVoluntario(voluntarioId, getConexao());
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            return List.of(Map.of("erro", "Erro: " + e.getMessage()));
        }
    }

    // ✅ CORREÇÃO: GET BY CAMPANHA (Nome da coluna corrigido para 'camp_id')
    public List<Map<String, Object>> getVoluntariosPorCampanha(int campanhaId) {
        try {
            // O filtro deve usar o nome da coluna no BANCO DE DADOS ('camp_id'), não a variável Java ('cam_id')
            // O DAO concatena essa string direto no SQL: "WHERE " + filtro
            List<CampResponsavel> lista = campRespModel.consultar("camp_id = " + campanhaId, getConexao());

            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("erro", "Erro ao buscar voluntários: " + e.getMessage()));
        }
    }
}