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

    // ✅ VALIDAÇÃO DOS DADOS (Omitida por brevidade)
    private String validarCampResponsavel(CampResponsavel cr) {
        if (cr.getCam_id() <= 0) {
            return "ID da campanha é obrigatório";
        }
        if (cr.getVoluntario_vol_id() <= 0) {
            return "ID do voluntário é obrigatório";
        }
        if (cr.getDATA_INICIO() == null || cr.getDATA_INICIO().isBlank()) {
            return "Data de início é obrigatória";
        }
        if (cr.getDATA_FIM() == null || cr.getDATA_FIM().isBlank()) {
            return "Data de fim é obrigatória";
        }

        // Validação adicional: data fim não pode ser anterior à data início
        if (cr.getDATA_INICIO() != null && cr.getDATA_FIM() != null) {
            try {
                if (cr.getDATA_FIM().compareTo(cr.getDATA_INICIO()) < 0) {
                    return "Data de fim não pode ser anterior à data de início";
                }
            } catch (Exception e) {
                // Se houver erro na comparação, apenas ignora
            }
        }

        return null; // Sem erros
    }

    // ===================================
    // ✅ ADD (SEGURO: Consulta por chave composta)
    // ===================================
    public Map<String, Object> addCampResponsavel(CampResponsavel nova) {

        // Validar dados
        String erroValidacao = validarCampResponsavel(nova);
        if (erroValidacao != null) {
            return Map.of("erro", erroValidacao);
        }

        try {
            Conexao conexao = getConexao();

            if (conexao == null) {
                return Map.of("erro", "Erro ao conectar ao banco de dados");
            }

            // ✅ CORREÇÃO: Usando o método seguro do Model/DAO
            List<CampResponsavel> existentes = campRespModel.consultarPorChaveComposta(
                    nova.getCam_id(),
                    nova.getVoluntario_vol_id(),
                    conexao
            );

            if (!existentes.isEmpty()) {
                return Map.of("erro", "Este voluntário já está vinculado a esta campanha");
            }

            CampResponsavel gravada = campRespModel.gravar(nova, conexao);

            if (gravada == null) {
                return Map.of("erro", "Erro ao vincular voluntário à campanha.");
            }

            return toJson(gravada);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao vincular voluntário à campanha: " + e.getMessage());
        }
    }

    // ===================================
    // ✅ UPDATE (SEGURO: Consulta por chave composta)
    // ===================================
    public Map<String, Object> updtCampResponsavel(CampResponsavel campResp) {

        // Validar dados
        String erroValidacao = validarCampResponsavel(campResp);
        if (erroValidacao != null) {
            return Map.of("erro", erroValidacao);
        }

        try {
            Conexao conexao = getConexao();

            if (conexao == null) {
                return Map.of("erro", "Erro ao conectar ao banco de dados");
            }

            // ✅ CORREÇÃO: Usando o método seguro do Model/DAO
            List<CampResponsavel> existentes = campRespModel.consultarPorChaveComposta(
                    campResp.getCam_id(),
                    campResp.getVoluntario_vol_id(),
                    conexao
            );

            if (existentes.isEmpty()) {
                return Map.of("erro", "Vinculação não encontrada");
            }

            CampResponsavel existente = existentes.get(0);
            existente.setDATA_INICIO(campResp.getDATA_INICIO());
            existente.setDATA_FIM(campResp.getDATA_FIM());
            existente.setObs_texto(campResp.getObs_texto());

            CampResponsavel alterada = campRespModel.alterar(existente, conexao);

            if (alterada == null) {
                return Map.of("erro", "Erro ao atualizar a vinculação.");
            }

            return toJson(alterada);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao atualizar a vinculação: " + e.getMessage());
        }
    }

    // ===================================
    // ✅ GET ALL
    // ===================================
    public List<Map<String, Object>> getCampResponsavel() {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = campRespModel.consultar("", conexao);
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("erro", "Erro ao buscar vinculações: " + e.getMessage()));
        }
    }

    // ===================================
    // ✅ GET BY FILTRO
    // ===================================
    public List<Map<String, Object>> getCampResponsavel(String filtro) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = campRespModel.consultar(filtro, conexao);
            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("erro", "Erro ao buscar vinculações: " + e.getMessage()));
        }
    }

    // ===================================
    // ✅ GET ESPECÍFICO (SEGURO: Consulta por chave composta)
    // ===================================
    public Map<String, Object> getCampResponsavel(int camId, int voluntarioId) {
        try {
            Conexao conexao = getConexao();

            // ✅ CORREÇÃO: Usando o método seguro do Model/DAO
            List<CampResponsavel> lista = campRespModel.consultarPorChaveComposta(
                    camId,
                    voluntarioId,
                    conexao
            );

            if (lista.isEmpty()) {
                return Map.of("erro", "Vinculação não encontrada");
            }

            return toJson(lista.get(0));

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao buscar vinculação: " + e.getMessage());
        }
    }

    // ===================================
    // ✅ DELETE
    // ===================================
    public Map<String, Object> deletarCampResponsavel(int camId, int voluntarioId) {
        try {
            Conexao conexao = getConexao();

            // ✅ CORREÇÃO: Usando o método seguro do Model/DAO
            List<CampResponsavel> existentes = campRespModel.consultarPorChaveComposta(
                    camId,
                    voluntarioId,
                    conexao
            );

            if (existentes.isEmpty()) {
                return Map.of("erro", "Vinculação não encontrada");
            }

            boolean sucesso = campRespModel.apagar(existentes.get(0), conexao);

            return sucesso
                    ? Map.of("mensagem", "Vinculação removida com sucesso")
                    : Map.of("erro", "Erro ao remover vinculação");

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao remover vinculação: " + e.getMessage());
        }
    }

    // ===================================
    // ✅ GET CAMPANHAS POR VOLUNTÁRIO
    // ===================================
    public List<Map<String, Object>> getCampanhasPorVoluntario(int voluntarioId) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = campRespModel.getCampanhasPorVoluntario(voluntarioId, conexao);

            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("erro", "Erro ao buscar campanhas do voluntário: " + e.getMessage()));
        }
    }

    // ===================================
    // ✅ GET VOLUNTÁRIOS POR CAMPANHA
    // ===================================
    public List<Map<String, Object>> getVoluntariosPorCampanha(int campanhaId) {
        try {
            Conexao conexao = getConexao();
            List<CampResponsavel> lista = campRespModel.consultar("cam_id = " + campanhaId, conexao);

            List<Map<String, Object>> result = new ArrayList<>();
            for (CampResponsavel cr : lista) result.add(toJson(cr));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(Map.of("erro", "Erro ao buscar voluntários da campanha: " + e.getMessage()));
        }
    }
}