package DOARC.mvc.controller;

import DOARC.mvc.model.HigienizacaoRoupa;
import DOARC.mvc.util.Conexao; // Importação da classe Conexao
import DOARC.mvc.util.SingletonDB; // Importação da classe para obter a Conexao
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HigienizacaoRoupaController {

    @Autowired
    private HigienizacaoRoupa higienizacaoModel;

    // --- NOVO MÉTODO: ONDE A CONEXÃO É INSTANCIADA ---
    private Conexao getConexao() {
        // O Controller agora é o responsável por chamar o SingletonDB para obter a Conexão
        return SingletonDB.conectar();
    }
    // --------------------------------------------------

    // --- GET ALL ---
    public List<Map<String, Object>> getHigienizacaoRoupa(String filtro) {
        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        List<HigienizacaoRoupa> lista = higienizacaoModel.consultar(filtro != null ? filtro : "", conexao); // 2. PASSA a Conexão

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
        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        HigienizacaoRoupa h = higienizacaoModel.consultar(id, conexao); // 2. PASSA a Conexão
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

        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        HigienizacaoRoupa gravada = higienizacaoModel.gravar(nova, conexao); // 2. PASSA a Conexão

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

        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão

        // É preciso consultar com a conexao antes de alterar
        HigienizacaoRoupa existente = higienizacaoModel.consultar(id, conexao); // 2. PASSA a Conexão
        if (existente == null) return Map.of("erro", "Registro de Higienização não encontrado");

        existente.setHigDataAgendada(dataAgendada);
        existente.setHigDescricaoRoupa(descricaoRoupa);
        existente.setVolId(volId);
        existente.setHigLocal(local);
        existente.setHigHora(hora);
        existente.setHigValorPago(valorPago);

        HigienizacaoRoupa atualizada = higienizacaoModel.alterar(existente, conexao); // 2. PASSA a Conexão
        if (atualizada == null) return Map.of("erro", "Erro ao atualizar a Higienização de Roupa");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizada.getHigId());
        json.put("data_agendada", atualizada.getHigDataAgendada());
        json.put("mensagem", "Registro de Higienização atualizado com sucesso!");
        return json;
    }

    // --- DELETE ---
    public Map<String, Object> deletarHigienizacaoRoupa(int id) {
        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        HigienizacaoRoupa h = higienizacaoModel.consultar(id, conexao); // 2. PASSA a Conexão

        if (h == null) return Map.of("erro", "Registro de Higienização não encontrado");

        boolean deletado = higienizacaoModel.apagar(h, conexao); // 2. PASSA a Conexão
        return deletado ? Map.of("mensagem", "Registro de Higienização removido com sucesso") : Map.of("erro", "Erro ao remover o Registro de Higienização");
    }
}