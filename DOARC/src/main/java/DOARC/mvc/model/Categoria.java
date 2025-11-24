package DOARC.mvc.model;

import DOARC.mvc.dao.CategoriaDAO;
import java.sql.Connection;
import java.util.List;

public class Categoria {

    private int catId;
    private String catNomeProd;
    private String catEspecificacao;

    public Categoria() {
        // Construtor vazio necessário para mapeamento e frameworks
    }

    public Categoria(String catNomeProd, String catEspecificacao) {
        this.catNomeProd = catNomeProd;
        this.catEspecificacao = catEspecificacao;
    }

    public Categoria(String catNomeProd) {
        this.catNomeProd = catNomeProd;
        this.catEspecificacao = "";
    }

    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatNomeProd() {
        return catNomeProd;
    }

    public void setCatNomeProd(String catNomeProd) {
        this.catNomeProd = catNomeProd;
    }

    public String getCatEspecificacao() {
        return catEspecificacao;
    }

    public void setCatEspecificacao(String catEspecificacao) {
        this.catEspecificacao = catEspecificacao;
    }

    // --- MÉTODOS DE NEGÓCIO (Model instancia DAO e passa Connection) ---

    /**
     * Grava esta categoria no banco
     * @param conn Conexão recebida do Control
     * @return Categoria gravada com ID ou null se erro
     */
    public Categoria gravar(Connection conn) {
        CategoriaDAO dao = new CategoriaDAO(conn);
        return dao.gravar(this);
    }

    /**
     * Altera esta categoria no banco
     * @param conn Conexão recebida do Control
     * @return Categoria alterada ou null se erro
     */
    public Categoria alterar(Connection conn) {
        CategoriaDAO dao = new CategoriaDAO(conn);
        return dao.alterar(this);
    }

    /**
     * Apaga esta categoria do banco
     * @param conn Conexão recebida do Control
     * @return true se removida, false se erro
     */
    public boolean apagar(Connection conn) {
        CategoriaDAO dao = new CategoriaDAO(conn);
        return dao.apagar(this);
    }

    /**
     * Busca uma categoria por ID
     * @param conn Conexão recebida do Control
     * @param id ID da categoria
     * @return Categoria encontrada ou null
     */
    public static Categoria get(Connection conn, int id) {
        CategoriaDAO dao = new CategoriaDAO(conn);
        return dao.get(id);
    }

    /**
     * Busca categorias com filtro
     * @param conn Conexão recebida do Control
     * @param filtro Texto para filtrar
     * @return Lista de categorias
     */
    public static List<Categoria> get(Connection conn, String filtro) {
        CategoriaDAO dao = new CategoriaDAO(conn);
        return dao.get(filtro);
    }

    /**
     * Busca todas as categorias
     * @param conn Conexão recebida do Control
     * @return Lista de todas as categorias
     */
    public static List<Categoria> getAll(Connection conn) {
        CategoriaDAO dao = new CategoriaDAO(conn);
        return dao.getAll();
    }

    /**
     * Verifica se a categoria possui produtos relacionados
     * @param conn Conexão recebida do Control
     * @param catId ID da categoria
     * @return true se há produtos relacionados, false caso contrário
     */
    public static boolean temProdutosRelacionados(Connection conn, int catId) {
        CategoriaDAO dao = new CategoriaDAO(conn);
        return dao.temProdutosRelacionados(catId);
    }
}
