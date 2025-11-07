package DOARC.mvc.model;

public class Compra {

    private int compId;
    private String compDataCompra;
    private String compDesc;
    private int volId;
    private double comValorTotal;
    private String comFornecedor;

    public Compra() {
        // Construtor vazio necessário para mapeamento e frameworks
    }

    public Compra(String compDataCompra, String compDesc, int volId, double comValorTotal, String comFornecedor) {
        this.compDataCompra = compDataCompra;
        this.compDesc = compDesc;
        this.volId = volId;
        this.comValorTotal = comValorTotal;
        this.comFornecedor = comFornecedor;
    }

    public int getCompId() {
        return compId;
    }

    public void setCompId(int compId) {
        this.compId = compId;
    }

    public String getCompDataCompra() {
        return compDataCompra;
    }

    public void setCompDataCompra(String compDataCompra) {
        this.compDataCompra = compDataCompra;
    }

    public String getCompDesc() {
        return compDesc;
    }

    public void setCompDesc(String compDesc) {
        this.compDesc = compDesc;
    }

    public int getVolId() {
        return volId;
    }

    public void setVolId(int volId) {
        this.volId = volId;
    }

    public double getComValorTotal() {
        return comValorTotal;
    }

    public void setComValorTotal(double comValorTotal) {
        this.comValorTotal = comValorTotal;
    }

    public String getComFornecedor() {
        return comFornecedor;
    }

    public void setComFornecedor(String comFornecedor) {
        this.comFornecedor = comFornecedor;
    }
}
