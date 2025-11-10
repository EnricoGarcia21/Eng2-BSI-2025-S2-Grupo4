package DOARC.mvc.model;

import DOARC.mvc.dao.VerificarNecessidadeDAO;
import DOARC.mvc.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificarNecessidade {

    private int verId;
    private String verData;
    private String verObs;
    private String verResultado;
    private int volId; // Chave estrangeira para Voluntário
    private int doaId; // Chave estrangeira para Doador

    @Autowired // Model Instancia (recebe) o DAO
    private VerificarNecessidadeDAO dao;

    // --- CONSTRUTORES ---
    public VerificarNecessidade() {
    }

    // Construtor para gravação (sem ID)
    public VerificarNecessidade(String verData, String verObs, String verResultado, int volId, int doaId) {
        this.verData = verData;
        this.verObs = verObs;
        this.verResultado = verResultado;
        this.volId = volId;
        this.doaId = doaId;
    }

    // --- MÉTODOS DE DELEGAÇÃO PARA O DAO ---
    public List<VerificarNecessidade> consultar(String filtro, Conexao conexao) {
        return dao.get(filtro, conexao);
    }

    public VerificarNecessidade consultar(int id, Conexao conexao) {
        return dao.get(id, conexao);
    }

    public VerificarNecessidade gravar(VerificarNecessidade entidade, Conexao conexao) {
        return dao.gravar(entidade, conexao);
    }

    public VerificarNecessidade alterar(VerificarNecessidade entidade, Conexao conexao) {
        return dao.alterar(entidade, conexao);
    }

    public boolean apagar(VerificarNecessidade entidade, Conexao conexao) {
        return dao.apagar(entidade, conexao);
    }

    // --- GETTERS E SETTERS ---
    public int getVerId() { return verId; }
    public void setVerId(int verId) { this.verId = verId; }
    public String getVerData() { return verData; }
    public void setVerData(String verData) { this.verData = verData; }
    public String getVerObs() { return verObs; }
    public void setVerObs(String verObs) { this.verObs = verObs; }
    public String getVerResultado() { return verResultado; }
    public void setVerResultado(String verResultado) { this.verResultado = verResultado; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public int getDoaId() { return doaId; }
    public void setDoaId(int doaId) { this.doaId = doaId; }
}