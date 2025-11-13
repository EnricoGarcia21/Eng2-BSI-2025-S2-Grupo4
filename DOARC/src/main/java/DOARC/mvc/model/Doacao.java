package DOARC.mvc.model;

import DOARC.mvc.dao.DoacaoDAO;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class Doacao {
    @Autowired
    private DoacaoDAO dao;

    private int doacaoId;
    private int volId;
    private String dataDoacao;
    private String obsDoacao;
    private BigDecimal valorDoado;
    private int doaId;

    // Construtores
    public Doacao() {}

    public Doacao(int volId, String dataDoacao, String obsDoacao, BigDecimal valorDoado, int doaId) {
        this.volId = volId;
        this.dataDoacao = dataDoacao;
        this.obsDoacao = obsDoacao;
        this.valorDoado = valorDoado;
        this.doaId = doaId;
    }

    public Doacao(int doacaoId, int volId, String dataDoacao, String obsDoacao, BigDecimal valorDoado, int doaId) {
        this.doacaoId = doacaoId;
        this.volId = volId;
        this.dataDoacao = dataDoacao;
        this.obsDoacao = obsDoacao;
        this.valorDoado = valorDoado;
        this.doaId = doaId;
    }

    // Getters e Setters
    public int getDoacaoId() {
        return doacaoId;
    }

    public void setDoacaoId(int doacaoId) {
        this.doacaoId = doacaoId;
    }

    public int getVolId() {
        return volId;
    }

    public void setVolId(int volId) {
        this.volId = volId;
    }

    public String getDataDoacao() {
        return dataDoacao;
    }

    public void setDataDoacao(String dataDoacao) {
        this.dataDoacao = dataDoacao;
    }

    public String getObsDoacao() {
        return obsDoacao;
    }

    public void setObsDoacao(String obsDoacao) {
        this.obsDoacao = obsDoacao;
    }

    public BigDecimal getValorDoado() {
        return valorDoado;
    }

    public void setValorDoado(BigDecimal valorDoado) {
        this.valorDoado = valorDoado;
    }

    public int getDoaId() {
        return doaId;
    }

    public void setDoaId(int doaId) {
        this.doaId = doaId;
    }

    public Doacao gravar(Doacao doacao, SingletonDB conexao) {
        return dao.gravar(doacao, conexao);
    }

    public Doacao alterar(Doacao doacao, SingletonDB conexao) {
        return dao.alterar(doacao, conexao);
    }

    public boolean apagar(Doacao doacao, SingletonDB conexao) {
        return dao.apagar(doacao, conexao);
    }

    public Doacao get(int id, SingletonDB conexao) {
        return dao.get(id, conexao);
    }

    public List<Doacao> get(String filtro, SingletonDB conexao) {
        return dao.get(filtro, conexao);
    }

    // Métodos específicos
    public List<Doacao> getPorVoluntario(int volId, SingletonDB conexao) {
        return dao.getPorVoluntario(volId, conexao);
    }

    public List<Doacao> getPorDoador(int doaId, SingletonDB conexao) {
        return dao.getPorDoador(doaId, conexao);
    }

    public BigDecimal getTotalDoacoesPeriodo(String dataInicio, String dataFim, SingletonDB conexao) {
        return dao.getTotalDoacoesPeriodo(dataInicio, dataFim, conexao);
    }
}