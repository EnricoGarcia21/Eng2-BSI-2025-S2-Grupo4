package DOARC.mvc.model;

import DOARC.mvc.dao.HigienizacaoRoupaDAO;
import DOARC.mvc.util.Conexao; // Importação necessária
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HigienizacaoRoupa {

    private int higId;
    private String higDataAgendada;
    private String higDescricaoRoupa;
    private int volId;
    private String higLocal;
    private String higHora;
    private double higValorPago;

    @Autowired
    private HigienizacaoRoupaDAO dao;

    // --- CONSTRUTORES --- (Inalterados)

    public HigienizacaoRoupa() {
    }

    public HigienizacaoRoupa(String higDataAgendada, String higDescricaoRoupa, int volId,
                             String higLocal, String higHora, double higValorPago) {
        this.higDataAgendada = higDataAgendada;
        this.higDescricaoRoupa = higDescricaoRoupa;
        this.volId = volId;
        this.higLocal = higLocal;
        this.higHora = higHora;
        this.higValorPago = higValorPago;
    }

    // --- MÉTODOS DE DELEGAÇÃO PARA O DAO (ALTERADOS) ---
    public List<HigienizacaoRoupa> consultar(String filtro, Conexao conexao) { // Recebe e repassa
        return dao.get(filtro, conexao);
    }

    public HigienizacaoRoupa consultar(int id, Conexao conexao) { // Recebe e repassa
        return dao.get(id, conexao);
    }

    public HigienizacaoRoupa gravar(HigienizacaoRoupa higienizacao, Conexao conexao) { // Recebe e repassa
        return dao.gravar(higienizacao, conexao);
    }

    public HigienizacaoRoupa alterar(HigienizacaoRoupa higienizacao, Conexao conexao) { // Recebe e repassa
        return dao.alterar(higienizacao, conexao);
    }

    public boolean apagar(HigienizacaoRoupa higienizacao, Conexao conexao) { // Recebe e repassa
        return dao.apagar(higienizacao, conexao);
    }

    // --- GETTERS E SETTERS --- (Inalterados)
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