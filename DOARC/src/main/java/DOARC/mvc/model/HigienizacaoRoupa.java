package DOARC.mvc.model;

import DOARC.mvc.dao.HigienizacaoRoupaDAO; // Novo DAO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HigienizacaoRoupa {

    private int higId;
    private String higDataAgendada;
    private String higDescricaoRoupa;
    private int volId; // Chave estrangeira para Voluntário/Doador
    private String higLocal;
    private String higHora;
    private double higValorPago;

    @Autowired // Model Instancia (recebe) o DAO
    private HigienizacaoRoupaDAO dao;

    // --- CONSTRUTORES ---
    public HigienizacaoRoupa() {
    }

    // Construtor para gravação (sem ID)
    public HigienizacaoRoupa(String higDataAgendada, String higDescricaoRoupa, int volId,
                             String higLocal, String higHora, double higValorPago) {
        this.higDataAgendada = higDataAgendada;
        this.higDescricaoRoupa = higDescricaoRoupa;
        this.volId = volId;
        this.higLocal = higLocal;
        this.higHora = higHora;
        this.higValorPago = higValorPago;
    }

    // --- MÉTODOS DE DELEGAÇÃO PARA O DAO ---
    public List<HigienizacaoRoupa> consultar(String filtro) {
        return dao.get(filtro);
    }

    public HigienizacaoRoupa consultar(int id) {
        return dao.get(id);
    }

    public HigienizacaoRoupa gravar(HigienizacaoRoupa higienizacao) {
        return dao.gravar(higienizacao);
    }

    public HigienizacaoRoupa alterar(HigienizacaoRoupa higienizacao) {
        return dao.alterar(higienizacao);
    }

    public boolean apagar(HigienizacaoRoupa higienizacao) {
        return dao.apagar(higienizacao);
    }

    // --- GETTERS E SETTERS ---
    public int getHigId() { return higId; }
    public void setHigId(int higId) { this.higId = higId; }
    public String getHigDataAgendada() { return higDataAgendada; }
    public void setHigDataAgendada(String higDataAgendada) { this.higDataAgendada = higDataAgendada; }
    public String getHigDescricaoRoupa() { return higDescricaoRoupa; }
    public void setHigDescricaoRoupa(String higDescricaoRoupa) { this.higDescricaoRoupa = higDescricaoRoupa; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public String getHigLocal() { return higLocal; }
    public void setHigLocal(String higLocal) { this.higLocal = higLocal; }
    public String getHigHora() { return higHora; }
    public void setHigHora(String higHora) { this.higHora = higHora; }
    public double getHigValorPago() { return higValorPago; }
    public void setHigValorPago(double higValorPago) { this.higValorPago = higValorPago; }
}