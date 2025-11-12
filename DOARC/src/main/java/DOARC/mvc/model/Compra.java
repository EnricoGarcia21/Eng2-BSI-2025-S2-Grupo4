package DOARC.mvc.model;

import DOARC.mvc.dao.CompraDAO;
import java.sql.Connection;
import java.util.List;

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

    // --- MÉTODOS DE NEGÓCIO (Model instancia DAO e passa Connection) ---

    /**
     * Grava uma compra completa (cabeçalho + produtos) e atualiza o estoque
     * @param conn Conexão recebida do Control
     * @param produtos Lista de produtos da compra
     * @return Compra gravada com ID ou null em caso de erro
     */
    public Compra gravarCompraCompleta(Connection conn, List<CompraProduto> produtos) {
        CompraDAO dao = new CompraDAO(conn);
        return dao.gravarCompraCompleta(this, produtos);
    }

    /**
     * Busca uma compra por ID
     * @param conn Conexão recebida do Control
     * @param id ID da compra
     * @return Compra encontrada ou null
     */
    public static Compra get(Connection conn, int id) {
        CompraDAO dao = new CompraDAO(conn);
        return dao.get(id);
    }

    /**
     * Busca todas as compras
     * @param conn Conexão recebida do Control
     * @return Lista de todas as compras
     */
    public static List<Compra> getAll(Connection conn) {
        CompraDAO dao = new CompraDAO(conn);
        return dao.getAll();
    }

    /**
     * Busca produtos de uma compra específica
     * @param conn Conexão recebida do Control
     * @param compraId ID da compra
     * @return Lista de produtos da compra
     */
    public static List<CompraProduto> getProdutosDaCompra(Connection conn, int compraId) {
        CompraDAO dao = new CompraDAO(conn);
        return dao.getProdutosDaCompra(compraId);
    }
}
