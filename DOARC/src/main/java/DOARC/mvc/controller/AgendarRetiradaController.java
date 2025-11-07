package DOARC.mvc.controller;

import DOARC.mvc.model.AgendarRetirada;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgendarRetiradaController {
    @Autowired
    private AgendarRetirada agendarModel;

    public Map<String, Object> agendarRetirada(String dataRetiro, String horaRetiro, String obsRetiro,
                                               int volId, int doaId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                AgendarRetirada agendar = new AgendarRetirada(dataRetiro, horaRetiro, obsRetiro, volId, doaId);
                AgendarRetirada resultado = agendarModel.gravar(agendar, conexao);

                if (resultado != null) {
                    json.put("agendaId", resultado.getAgendaId());
                    json.put("dataRetiro", resultado.getDataRetiro());
                    json.put("horaRetiro", resultado.getHoraRetiro());
                    json.put("obsRetiro", resultado.getObsRetiro());
                    json.put("volId", resultado.getVolId());
                    json.put("doaId", resultado.getDoaId());
                    json.put("mensagem", "Retirada agendada com sucesso!");
                } else {
                    json.put("erro", "Erro ao agendar retirada");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao agendar: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> listarAgendamentos() {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<AgendarRetirada> agendamentos = agendarModel.get(null, conexao);
                json.put("agendamentos", agendamentos);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao listar: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> buscarAgendamento(int agendaId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                AgendarRetirada agendar = agendarModel.get(agendaId, conexao);
                if (agendar != null) {
                    json.put("agendaId", agendar.getAgendaId());
                    json.put("dataRetiro", agendar.getDataRetiro());
                    json.put("horaRetiro", agendar.getHoraRetiro());
                    json.put("obsRetiro", agendar.getObsRetiro());
                    json.put("volId", agendar.getVolId());
                    json.put("doaId", agendar.getDoaId());
                } else {
                    json.put("erro", "Agendamento não encontrado");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> atualizarAgendamento(int agendaId, String dataRetiro, String horaRetiro,
                                                    String obsRetiro, int volId, int doaId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                AgendarRetirada agendar = new AgendarRetirada(agendaId, dataRetiro, horaRetiro, obsRetiro, volId, doaId);
                AgendarRetirada resultado = agendarModel.alterar(agendar, conexao);

                if (resultado != null) {
                    json.put("agendaId", resultado.getAgendaId());
                    json.put("dataRetiro", resultado.getDataRetiro());
                    json.put("horaRetiro", resultado.getHoraRetiro());
                    json.put("obsRetiro", resultado.getObsRetiro());
                    json.put("volId", resultado.getVolId());
                    json.put("doaId", resultado.getDoaId());
                    json.put("mensagem", "Agendamento atualizado com sucesso!");
                } else {
                    json.put("erro", "Erro ao atualizar agendamento");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao atualizar: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> cancelarAgendamento(int agendaId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                AgendarRetirada agendar = new AgendarRetirada();
                agendar.setAgendaId(agendaId);
                boolean resultado = agendarModel.apagar(agendar, conexao);

                if (resultado) {
                    json.put("mensagem", "Agendamento cancelado com sucesso!");
                } else {
                    json.put("erro", "Erro ao cancelar agendamento");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao cancelar: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }
}