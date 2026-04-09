package DOARC.mvc.controller;

import DOARC.mvc.model.HigienizacaoRoupa;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HigienizacaoRoupaController {

    @Autowired
    private HigienizacaoRoupa higienizacaoModel;

    // Método privado para centralizar a conexão com o banco
    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    // LISTAR TODOS (Com ou sem filtro)
    public List<Map<String, Object>> getHigienizacaoRoupa(String filtro) {
        Conexao conexao = getConexao();
        List<HigienizacaoRoupa> lista = higienizacaoModel.consultar(filtro != null ? filtro : "", conexao);

        if (lista == null) return new ArrayList<>();

        return lista.stream().map(h -> {
            Map<String, Object> json = new HashMap<>();
            json.put("id", h.getHigId());
            json.put("data_agendada", h.getHigDataAgendada());
            json.put("descricao_roupa", h.getHigDescricaoRoupa());
            json.put("vol_id", h.getVolId());
            json.put("local", h.getHigLocal());
            json.put("hora", h.getHigHora());
            json.put("valor_pago", h.getHigValorPago());
            json.put("ultimo_alerta", h.getHigUltimoAlerta());
            return json;
        }).collect(Collectors.toList());
    }

    // BUSCAR POR ID
    public Map<String, Object> getHigienizacaoRoupa(int id) {
        Conexao conexao = getConexao();
        HigienizacaoRoupa h = higienizacaoModel.consultar(id, conexao);

        if (h == null) return Map.of("erro", "Registro não encontrado");

        return Map.of(
                "id", h.getHigId(),
                "data_agendada", h.getHigDataAgendada(),
                "descricao_roupa", h.getHigDescricaoRoupa(),
                "vol_id", h.getVolId(),
                "local", h.getHigLocal(),
                "hora", h.getHigHora(),
                "valor_pago", h.getHigValorPago(),
                "ultimo_alerta", h.getHigUltimoAlerta()
        );
    }

    // ADICIONAR NOVO
    public Map<String, Object> addHigienizacaoRoupa(String dataAgendada, String descricaoRoupa, int volId,
                                                    String local, String hora, double valorPago) {

        HigienizacaoRoupa nova = new HigienizacaoRoupa(dataAgendada, descricaoRoupa, volId, local, hora, valorPago);
        nova.setHigUltimoAlerta("NENHUM"); // Valor padrão de sistema

        Conexao conexao = getConexao();
        HigienizacaoRoupa gravada = higienizacaoModel.gravar(nova, conexao);

        if (gravada == null) return Map.of("erro", "Erro ao cadastrar no banco de dados.");

        return Map.of("id", gravada.getHigId(), "mensagem", "Agendamento realizado com sucesso!");
    }

    // ATUALIZAR EXISTENTE
    public Map<String, Object> updtHigienizacaoRoupa(int id, String dataAgendada, String descricaoRoupa, int volId,
                                                     String local, String hora, double valorPago) {
        Conexao conexao = getConexao();
        HigienizacaoRoupa existente = higienizacaoModel.consultar(id, conexao);

        if (existente == null) return Map.of("erro", "Registro não encontrado para atualização.");

        // Atualiza os campos do objeto
        existente.setHigDataAgendada(dataAgendada);
        existente.setHigDescricaoRoupa(descricaoRoupa);
        existente.setVolId(volId);
        existente.setHigLocal(local);
        existente.setHigHora(hora);
        existente.setHigValorPago(valorPago);

        HigienizacaoRoupa atualizada = higienizacaoModel.alterar(existente, conexao);

        return (atualizada != null)
                ? Map.of("mensagem", "Dados atualizados com sucesso!")
                : Map.of("erro", "Erro ao processar atualização no banco.");
    }

    // DELETAR
    public Map<String, Object> deletarHigienizacaoRoupa(int id) {
        Conexao conexao = getConexao();
        HigienizacaoRoupa h = higienizacaoModel.consultar(id, conexao);

        if (h == null) return Map.of("erro", "Registro não encontrado.");

        return higienizacaoModel.apagar(h, conexao)
                ? Map.of("mensagem", "Registro removido com sucesso!")
                : Map.of("erro", "Não foi possível remover o registro.");
    }
}