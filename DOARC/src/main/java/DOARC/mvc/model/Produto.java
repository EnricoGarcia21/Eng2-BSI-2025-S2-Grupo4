package DOARC.mvc.model;

import java.math.BigDecimal;

public class Produto {
    private int prodId;
    private String prodNome;
    private String prodDescricao;
    private String prodInformacoesAdicionais;
    private int prodQuant;
    private int catId;
    private BigDecimal preco;

    public Produto() {}

    public int getProdId() { return prodId; }
    public void setProdId(int prodId) { this.prodId = prodId; }
    public String getProdNome() { return prodNome; }
    public void setProdNome(String prodNome) { this.prodNome = prodNome; }
    public String getProdDescricao() { return prodDescricao; }
    public void setProdDescricao(String prodDescricao) { this.prodDescricao = prodDescricao; }
    public String getProdInformacoesAdicionais() { return prodInformacoesAdicionais; }
    public void setProdInformacoesAdicionais(String prodInformacoesAdicionais) { this.prodInformacoesAdicionais = prodInformacoesAdicionais; }
    public int getProdQuant() { return prodQuant; }
    public void setProdQuant(int prodQuant) { this.prodQuant = prodQuant; }
    public int getCatId() { return catId; }
    public void setCatId(int catId) { this.catId = catId; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
}