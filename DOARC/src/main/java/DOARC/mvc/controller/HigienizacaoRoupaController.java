package DOARC.mvc.controller;

import DOARC.mvc.model.HigienizacaoRoupa;
import DOARC.mvc.observer.AlertaHigienizacaoObserver;
import DOARC.mvc.observer.HigienizacaoObserver;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class HigienizacaoRoupaController {

    @Autowired
    private HigienizacaoRoupa higienizacaoModel;

    private List<HigienizacaoObserver> observers = new ArrayList<>();

    public HigienizacaoRoupaController() {
        this.observers.add(new AlertaHigienizacaoObserver());
    }

    @Scheduled(fixedRate = 60000)
    public void verificarAgendaAutomaticamente() {
        this.getHigienizacaoRoupa("");
    }

    private void notificarObservadores(HigienizacaoRoupa h) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dataAgendada = LocalDateTime.parse(h.getHigDataAgendada() + " " + h.getHigHora(), dtf);
            LocalDateTime agora = LocalDateTime.now();

            long diasRestantes = ChronoUnit.DAYS.between(agora, dataAgendada);
            long horasRestantes = ChronoUnit.HOURS.between(agora, dataAgendada);

            for (HigienizacaoObserver ob : observers) {

                String statusAnterior = h.getHigUltimoAlerta();

                ob.atualizar(h, diasRestantes, horasRestantes);


                if (statusAnterior != null && !statusAnterior.equals(h.getHigUltimoAlerta())) {
                    this.higienizacaoModel.alterar(h, getConexao());
                    System.out.println("[SISTEMA]: Status de alerta atualizado no banco para " + h.getHigUltimoAlerta() + " (ID: " + h.getHigId() + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar notificação automática: " + e.getMessage());
        }
    }


    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public List<Map<String, Object>> getHigienizacaoRoupa(String filtro) {
        Conexao conexao = getConexao();
        List<HigienizacaoRoupa> lista = higienizacaoModel.consultar(filtro != null ? filtro : "", conexao);

        List<Map<String, Object>> result = new ArrayList<>();

        if (lista != null) {
            for (HigienizacaoRoupa h : lista) {

                notificarObservadores(h);

                Map<String, Object> json = new HashMap<>();
                json.put("id", h.getHigId());
                json.put("data_agendada", h.getHigDataAgendada());
                json.put("descricao_roupa", h.getHigDescricaoRoupa());
                json.put("vol_id", h.getVolId());
                json.put("local", h.getHigLocal());
                json.put("hora", h.getHigHora());
                json.put("valor_pago", h.getHigValorPago());
                json.put("ultimo_alerta", h.getHigUltimoAlerta());
                result.add(json);
            }
        }
        return result;
    }

    public Map<String, Object> getHigienizacaoRoupa(int id) {
        Conexao conexao = getConexao();
        HigienizacaoRoupa h = higienizacaoModel.consultar(id, conexao);

        if (h == null) return Map.of("erro", "Registro não encontrado");

        notificarObservadores(h);

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
    }

    public Map<String, Object> addHigienizacaoRoupa(String dataAgendada, String descricaoRoupa, int volId,
                                                    String local, String hora, double valorPago) {

        HigienizacaoRoupa nova = new HigienizacaoRoupa(dataAgendada, descricaoRoupa, volId, local, hora, valorPago);
        nova.setHigUltimoAlerta("NENHUM");

        Conexao conexao = getConexao();
        HigienizacaoRoupa gravada = higienizacaoModel.gravar(nova, conexao);

        if (gravada == null) return Map.of("erro", "Erro ao cadastrar");

        return Map.of("id", gravada.getHigId(), "mensagem", "Cadastrado com sucesso!");
    }

    public Map<String, Object> updtHigienizacaoRoupa(int id, String dataAgendada, String descricaoRoupa, int volId,
                                                     String local, String hora, double valorPago) {
        Conexao conexao = getConexao();
        HigienizacaoRoupa existente = higienizacaoModel.consultar(id, conexao);
        if (existente == null) return Map.of("erro", "Não encontrado");

        existente.setHigDataAgendada(dataAgendada);
        existente.setHigDescricaoRoupa(descricaoRoupa);
        existente.setVolId(volId);
        existente.setHigLocal(local);
        existente.setHigHora(hora);
        existente.setHigValorPago(valorPago);

        HigienizacaoRoupa atualizada = higienizacaoModel.alterar(existente, conexao);
        return (atualizada != null) ? Map.of("mensagem", "Atualizado com sucesso!") : Map.of("erro", "Erro ao atualizar");
    }

    public Map<String, Object> deletarHigienizacaoRoupa(int id) {
        Conexao conexao = getConexao();
        HigienizacaoRoupa h = higienizacaoModel.consultar(id, conexao);
        if (h == null) return Map.of("erro", "Não encontrado");

        return higienizacaoModel.apagar(h, conexao) ? Map.of("mensagem", "Removido!") : Map.of("erro", "Erro ao remover");
    }
}