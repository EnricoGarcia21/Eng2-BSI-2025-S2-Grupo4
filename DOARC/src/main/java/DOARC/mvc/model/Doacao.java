package DOARC.mvc.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Doacao {

    private int doacaoId;
    private int volId;
    private LocalDate dataDoacao;
    private String obsDoacao;
    private BigDecimal valorDoado;
    private int doaId;

    public Doacao() {}

    public int getDoacaoId() { return doacaoId; }
    public void setDoacaoId(int doacaoId) { this.doacaoId = doacaoId; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public LocalDate getDataDoacao() { return dataDoacao; }
    public void setDataDoacao(LocalDate dataDoacao) { this.dataDoacao = dataDoacao; }
    public String getObsDoacao() { return obsDoacao; }
    public void setObsDoacao(String obsDoacao) { this.obsDoacao = obsDoacao; }
    public BigDecimal getValorDoado() { return valorDoado; }
    public void setValorDoado(BigDecimal valorDoado) { this.valorDoado = valorDoado; }
    public int getDoaId() { return doaId; }
    public void setDoaId(int doaId) { this.doaId = doaId; }
}