package DOARC.mvc.model;

public class CompraProduto {

    private int compId;
    private int prodId;
    private double qtde;
    private double valorUnitario;

    public CompraProduto() {
        // Construtor vazio necessário para mapeamento e frameworks
    }

    public CompraProduto(int compId, int prodId, double qtde, double valorUnitario) {
        this.compId = compId;
        this.prodId = prodId;
        this.qtde = qtde;
        this.valorUnitario = valorUnitario;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public double getQtde() {
        return qtde;
    }

    public void setQtde(double qtde) {
        this.qtde = qtde;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(double valorUnitario) {
        this.valorUnitario = valorUnitario;
    }
}
