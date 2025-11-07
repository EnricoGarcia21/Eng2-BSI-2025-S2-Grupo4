package DOARC.mvc.model;

import DOARC.mvc.dao.AgendarRetiradaDAO;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AgendarRetirada {
    @Autowired
    private AgendarRetiradaDAO dao;

    private int agendaId;
    private String dataRetiro;
    private String horaRetiro;
    private String obsRetiro;
    private int volId; // FK voluntario
    private int doaId; // FK doados

    // Construtores
    public AgendarRetirada() {}

    public AgendarRetirada(String dataRetiro, String horaRetiro, String obsRetiro, int volId, int doaId) {
        this.dataRetiro = dataRetiro;
        this.horaRetiro = horaRetiro;
        this.obsRetiro = obsRetiro;
        this.volId = volId;
        this.doaId = doaId;
    }

    public AgendarRetirada(int agendaId, String dataRetiro, String horaRetiro, String obsRetiro, int volId, int doaId) {
        this.agendaId = agendaId;
        this.dataRetiro = dataRetiro;
        this.horaRetiro = horaRetiro;
        this.obsRetiro = obsRetiro;
        this.volId = volId;
        this.doaId = doaId;
    }

    // Getters e Setters
    public int getAgendaId() {
        return agendaId;
    }

    public void setAgendaId(int agendaId) {
        this.agendaId = agendaId;
    }

    public String getDataRetiro() {
        return dataRetiro;
    }

    public void setDataRetiro(String dataRetiro) {
        this.dataRetiro = dataRetiro;
    }

    public String getHoraRetiro() {
        return horaRetiro;
    }

    public void setHoraRetiro(String horaRetiro) {
        this.horaRetiro = horaRetiro;
    }

    public String getObsRetiro() {
        return obsRetiro;
    }

    public void setObsRetiro(String obsRetiro) {
        this.obsRetiro = obsRetiro;
    }

    public int getVolId() {
        return volId;
    }

    public void setVolId(int volId) {
        this.volId = volId;
    }

    public int getDoaId() {
        return doaId;
    }

    public void setDoaId(int doaId) {
        this.doaId = doaId;
    }

    public AgendarRetirada gravar(AgendarRetirada agendar, SingletonDB conexao) {
        return dao.gravar(agendar, conexao);
    }

    public AgendarRetirada alterar(AgendarRetirada agendar, SingletonDB conexao) {
        return dao.alterar(agendar, conexao);
    }

    public boolean apagar(AgendarRetirada agendar, SingletonDB conexao) {
        return dao.apagar(agendar, conexao);
    }

    public AgendarRetirada get(int id, SingletonDB conexao) {
        return dao.get(id, conexao);
    }

    public List<AgendarRetirada> get(String filtro, SingletonDB conexao) {
        return dao.get(filtro, conexao);
    }

    // Métodos específicos
    public List<AgendarRetirada> getPorVoluntario(int volId, SingletonDB conexao) {
        return dao.getPorVoluntario(volId, conexao);
    }

    public List<AgendarRetirada> getPorDoacao(int doaId, SingletonDB conexao) {
        return dao.getPorDoacao(doaId, conexao);
    }
}