package DOARC.mvc.model;

import java.math.BigDecimal;

public class DoadosProduto {
    private int doaId;
    private int prodId;
    private BigDecimal dpQtde;

    public DoadosProduto() {}

    public int getDoaId() { return doaId; }
    public void setDoaId(int doaId) { this.doaId = doaId; }
    public int getProdId() { return prodId; }
    public void setProdId(int prodId) { this.prodId = prodId; }
    public BigDecimal getDpQtde() { return dpQtde; }
    public void setDpQtde(BigDecimal dpQtde) { this.dpQtde = dpQtde; }
}