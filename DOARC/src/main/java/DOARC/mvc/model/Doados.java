package DOARC.mvc.model;

import java.math.BigDecimal;

public class Doados {
    private int doaId;
    private String doaDataAquisicao;
    private String doaTipoDoacao;
    private int volId;
    private int donId;
    private String obsDoado;
    private BigDecimal valorDoacao;

    public Doados() {}

    public int getDoaId() { return doaId; }
    public void setDoaId(int doaId) { this.doaId = doaId; }
    public String getDoaDataAquisicao() { return doaDataAquisicao; }
    public void setDoaDataAquisicao(String doaDataAquisicao) { this.doaDataAquisicao = doaDataAquisicao; }
    public String getDoaTipoDoacao() { return doaTipoDoacao; }
    public void setDoaTipoDoacao(String doaTipoDoacao) { this.doaTipoDoacao = doaTipoDoacao; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public int getDonId() { return donId; }
    public void setDonId(int donId) { this.donId = donId; }
    public String getObsDoado() { return obsDoado; }
    public void setObsDoado(String obsDoado) { this.obsDoado = obsDoado; }
    public BigDecimal getValorDoacao() { return valorDoacao; }
    public void setValorDoacao(BigDecimal valorDoacao) { this.valorDoacao = valorDoacao; }
}