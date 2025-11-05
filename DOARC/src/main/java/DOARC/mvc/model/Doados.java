package DOARC.mvc.model;

import java.math.BigDecimal;
import java.util.List;

public class Doados {

    private Integer doaId;
    private String doaDataAquisicao;
    private String doaTipoDoacao;
    private Integer volId;
    private Integer donId;
    private String obsDoado;
    private BigDecimal valorDoacao;
    private List<DoadosProduto> produtos;

    public Integer getDoaId() { return doaId; }
    public void setDoaId(Integer doaId) { this.doaId = doaId; }

    public String getDoaDataAquisicao() { return doaDataAquisicao; }
    public void setDoaDataAquisicao(String doaDataAquisicao) { this.doaDataAquisicao = doaDataAquisicao; }

    public String getDoaTipoDoacao() { return doaTipoDoacao; }
    public void setDoaTipoDoacao(String doaTipoDoacao) { this.doaTipoDoacao = doaTipoDoacao; }

    public Integer getVolId() { return volId; }
    public void setVolId(Integer volId) { this.volId = volId; }

    public Integer getDonId() { return donId; }
    public void setDonId(Integer donId) { this.donId = donId; }

    public String getObsDoado() { return obsDoado; }
    public void setObsDoado(String obsDoado) { this.obsDoado = obsDoado; }

    public BigDecimal getValorDoacao() { return valorDoacao; }
    public void setValorDoacao(BigDecimal valorDoacao) { this.valorDoacao = valorDoacao; }

    public List<DoadosProduto> getProdutos() { return produtos; }
    public void setProdutos(List<DoadosProduto> produtos) { this.produtos = produtos; }
}
