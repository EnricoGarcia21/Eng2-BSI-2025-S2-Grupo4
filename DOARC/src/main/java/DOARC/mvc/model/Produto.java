package DOARC.mvc.model;

import DOARC.mvc.dao.ProdutoDAO;
import java.sql.Connection;
import java.util.List;

public class Produto {

    private int prodId;
    private String prodNome;
    private String prodDescricao;
    private String prodInformacoesAdicionais;
    private int prodQuant;
    private int catId;

    public Produto() {
        // Construtor vazio necessário para mapeamento e frameworks
    }

    public Produto(String prodNome, String prodDescricao, String prodInformacoesAdicionais, int prodQuant, int catId) {
        this.prodNome = prodNome;
        this.prodDescricao = prodDescricao;
        this.prodInformacoesAdicionais = prodInformacoesAdicionais;
        this.prodQuant = prodQuant;
        this.catId = catId;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public String getProdNome() {
        return prodNome;
    }

    public void setProdNome(String prodNome) {
        this.prodNome = prodNome;
    }

    public String getProdDescricao() {
        return prodDescricao;
    }

    public void setProdDescricao(String prodDescricao) {
        this.prodDescricao = prodDescricao;
    }

    public String getProdInformacoesAdicionais() {
        return prodInformacoesAdicionais;
    }

    public void setProdInformacoesAdicionais(String prodInformacoesAdicionais) {
        this.prodInformacoesAdicionais = prodInformacoesAdicionais;
    }

    public int getProdQuant() {
        return prodQuant;
    }

    public void setProdQuant(int prodQuant) {
        this.prodQuant = prodQuant;
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    // --- MÉTODOS DE NEGÓCIO (Model instancia DAO e passa Connection) ---

    /**
     * Grava este produto no banco
     * @param conn Conexão recebida do Control
     * @return Produto gravado com ID ou null se erro
     */
    public Produto gravar(Connection conn) {
        ProdutoDAO dao = new ProdutoDAO(conn);
        return dao.gravar(this);
    }

    /**
     * Altera este produto no banco
     * @param conn Conexão recebida do Control
     * @return Produto alterado ou null se erro
     */
    public Produto alterar(Connection conn) {
        ProdutoDAO dao = new ProdutoDAO(conn);
        return dao.alterar(this);
    }

    /**
     * Apaga este produto do banco
     * @param conn Conexão recebida do Control
     * @return true se removido, false se erro
     */
    public boolean apagar(Connection conn) {
        ProdutoDAO dao = new ProdutoDAO(conn);
        return dao.apagar(this);
    }

    /**
     * Busca um produto por ID
     * @param conn Conexão recebida do Control
     * @param id ID do produto
     * @return Produto encontrado ou null
     */
    public static Produto get(Connection conn, int id) {
        ProdutoDAO dao = new ProdutoDAO(conn);
        return dao.get(id);
    }

    /**
     * Busca produtos com filtro
     * @param conn Conexão recebida do Control
     * @param filtro Texto para filtrar
     * @return Lista de produtos
     */
    public static List<Produto> get(Connection conn, String filtro) {
        ProdutoDAO dao = new ProdutoDAO(conn);
        return dao.get(filtro);
    }

    /**
     * Busca todos os produtos
     * @param conn Conexão recebida do Control
     * @return Lista de todos os produtos
     */
    public static List<Produto> getAll(Connection conn) {
        ProdutoDAO dao = new ProdutoDAO(conn);
        return dao.getAll();
    }

    /**
     * Busca produtos por categoria
     * @param conn Conexão recebida do Control
     * @param catId ID da categoria
     * @return Lista de produtos da categoria
     */
    public static List<Produto> getPorCategoria(Connection conn, int catId) {
        ProdutoDAO dao = new ProdutoDAO(conn);
        return dao.getPorCategoria(catId);
    }
}
