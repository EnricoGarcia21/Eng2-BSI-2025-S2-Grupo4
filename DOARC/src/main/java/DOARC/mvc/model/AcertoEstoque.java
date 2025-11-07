package DOARC.mvc.model;

public class AcertoEstoque {

    private int acId;
    private String acData;
    private String acMotivo;
    private String acObs;
    private String acTipo;
    private double acQuantidade;
    private int volId;
    private int prodId;

    public AcertoEstoque() {
        // Construtor vazio necessário para mapeamento e frameworks
    }

    public AcertoEstoque(String acData, String acMotivo, String acObs, String acTipo, double acQuantidade, int volId, int prodId) {
        this.acData = acData;
        this.acMotivo = acMotivo;
        this.acObs = acObs;
        this.acTipo = acTipo;
        this.acQuantidade = acQuantidade;
        this.volId = volId;
        this.prodId = prodId;
    }

    public int getAcId() {
        return acId;
    }

    public void setAcId(int acId) {
        this.acId = acId;
    }

    public String getAcData() {
        return acData;
    }

    public void setAcData(String acData) {
        this.acData = acData;
    }

    public String getAcMotivo() {
        return acMotivo;
    }

    public void setAcMotivo(String acMotivo) {
        this.acMotivo = acMotivo;
    }

    public String getAcObs() {
        return acObs;
    }

    public void setAcObs(String acObs) {
        this.acObs = acObs;
    }

    public String getAcTipo() {
        return acTipo;
    }

    public void setAcTipo(String acTipo) {
        this.acTipo = acTipo;
    }

    public double getAcQuantidade() {
        return acQuantidade;
    }

    public void setAcQuantidade(double acQuantidade) {
        this.acQuantidade = acQuantidade;
    }

    public int getVolId() {
        return volId;
    }

    public void setVolId(int volId) {
        this.volId = volId;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }
}
