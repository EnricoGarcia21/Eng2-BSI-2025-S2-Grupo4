package DOARC.mvc.controller;

import DOARC.mvc.model.HigienizacaoRoupa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HigienizacaoRoupaController {

    @Autowired // Controller recebe a Model
    private HigienizacaoRoupa higienizacaoModel;

    // --- GET ALL ---
    public List<Map<String, Object>> getHigienizacaoRoupa(String filtro) {
        List<HigienizacaoRoupa> lista = higienizacaoModel.consultar(filtro != null ? filtro : "");
        List<Map<String, Object>> result = new ArrayList<>();

        for (HigienizacaoRoupa h : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", h.getHigId());
            json.put("data_agendada", h.getHigDataAgendada());
            json.put("descricao_roupa", h.getHigDescricaoRoupa());
            json.put("vol_id", h.getVolId());
            json.put("local", h.getHigLocal());
            json.put("hora", h.getHigHora());
            json.put("valor_pago", h.getHigValorPago());
            result.add(json);
        }
        return result;
    }

    // --- GET BY ID ---
    public Map<String, Object> getHigienizacaoRoupa(int id) {
        HigienizacaoRoupa h = higienizacaoModel.consultar(id);
        if (h == null) return Map.of("erro", "Registro de Higienização não encontrado");

        Map<String, Object> json = new HashMap<>();
        json.put("id", h.getHigId());
        json.put("data_agendada", h.getHigDataAgendada());
        json.put("descricao_roupa", h.getHigDescricaoRoupa());
        json.put("vol_id", h.getVolId());
        json.put("local", h.getHigLocal());
        json.put("hora", h.getHigHora());
        json.put("valor_pago", h.getHigValorPago());
        return json;
    }

    // --- ADD ---
    public Map<String, Object> addHigienizacaoRoupa(String dataAgendada, String descricaoRoupa, int volId,
                                                    String local, String hora, double valorPago) {

        HigienizacaoRoupa nova = new HigienizacaoRoupa(dataAgendada, descricaoRoupa, volId, local, hora, valorPago);

        HigienizacaoRoupa gravada = higienizacaoModel.gravar(nova);
        if (gravada == null) return Map.of("erro", "Erro ao cadastrar a Higienização de Roupa");

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravada.getHigId());
        json.put("data_agendada", gravada.getHigDataAgendada());
        json.put("mensagem", "Registro de Higienização cadastrado com sucesso!");
        return json;
    }

    // --- UPDATE ---
    public Map<String, Object> updtHigienizacaoRoupa(int id, String dataAgendada, String descricaoRoupa, int volId,
                                                     String local, String hora, double valorPago) {

        HigienizacaoRoupa existente = higienizacaoModel.consultar(id);
        if (existente == null) return Map.of("erro", "Registro de Higienização não encontrado");

        existente.setHigDataAgendada(dataAgendada);
        existente.setHigDescricaoRoupa(descricaoRoupa);
        existente.setVolId(volId);
        existente.setHigLocal(local);
        existente.setHigHora(hora);
        existente.setHigValorPago(valorPago);

        HigienizacaoRoupa atualizada = higienizacaoModel.alterar(existente);
        if (atualizada == null) return Map.of("erro", "Erro ao atualizar a Higienização de Roupa");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizada.getHigId());
        json.put("data_agendada", atualizada.getHigDataAgendada());
        json.put("mensagem", "Registro de Higienização atualizado com sucesso!");
        return json;
    }

    // --- DELETE ---
    public Map<String, Object> deletarHigienizacaoRoupa(int id) {
        HigienizacaoRoupa h = higienizacaoModel.consultar(id);
        if (h == null) return Map.of("erro", "Registro de Higienização não encontrado");

        boolean deletado = higienizacaoModel.apagar(h);
        return deletado ? Map.of("mensagem", "Registro de Higienização removido com sucesso") : Map.of("erro", "Erro ao remover o Registro de Higienização");
    }
}