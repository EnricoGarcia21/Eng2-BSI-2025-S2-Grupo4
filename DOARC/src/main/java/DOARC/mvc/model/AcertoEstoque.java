package DOARC.mvc.model;

import DOARC.mvc.dao.AcertoEstoqueDAO;
import java.sql.Connection;
import java.util.List;

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

    // --- MÉTODOS DE NEGÓCIO (Model instancia DAO e passa Connection) ---

    /**
     * Grava um acerto de estoque e atualiza o estoque do produto automaticamente
     * @param conn Conexão recebida do Control
     * @param estoqueAtual Estoque atual do produto para validação
     * @return AcertoEstoque gravado com ID ou null em caso de erro
     */
    public AcertoEstoque gravarAcertoComAtualizacao(Connection conn, int estoqueAtual) {
        AcertoEstoqueDAO dao = new AcertoEstoqueDAO(conn);
        return dao.gravarAcertoComAtualizacao(this, estoqueAtual);
    }

    /**
     * Busca um acerto por ID
     * @param conn Conexão recebida do Control
     * @param id ID do acerto
     * @return AcertoEstoque encontrado ou null
     */
    public static AcertoEstoque get(Connection conn, int id) {
        AcertoEstoqueDAO dao = new AcertoEstoqueDAO(conn);
        return dao.get(id);
    }

    /**
     * Busca todos os acertos
     * @param conn Conexão recebida do Control
     * @return Lista de todos os acertos
     */
    public static List<AcertoEstoque> getAll(Connection conn) {
        AcertoEstoqueDAO dao = new AcertoEstoqueDAO(conn);
        return dao.getAll();
    }

    /**
     * Busca acertos por produto
     * @param conn Conexão recebida do Control
     * @param prodId ID do produto
     * @return Lista de acertos do produto
     */
    public static List<AcertoEstoque> getPorProduto(Connection conn, int prodId) {
        AcertoEstoqueDAO dao = new AcertoEstoqueDAO(conn);
        return dao.getPorProduto(prodId);
    }
}
