package DOARC.mvc.model;

import DOARC.mvc.dao.DonatarioDAO;
import java.sql.Connection;
import java.util.List;

public class Donatario {

    private int donId;          // ID gerado pelo banco
    private String donNome;
    private String donDataNasc;
    private String donRua;
    private String donBairro;
    private String donCidade;
    private String donTelefone;
    private String donCep;
    private String donUf;
    private String donEmail;
    private String donSexo;

    // --- CONSTRUTORES ---
    public Donatario() {
        // Construtor vazio necessário para mapeamento e frameworks
    }

    // Construtor para cadastro (sem ID, pois o banco gera)
    public Donatario(String donNome, String donDataNasc, String donRua, String donBairro, String donCidade,
                     String donTelefone, String donCep, String donUf, String donEmail, String donSexo) {
        this.donNome = donNome;
        this.donDataNasc = donDataNasc;
        this.donRua = donRua;
        this.donBairro = donBairro;
        this.donCidade = donCidade;
        this.donTelefone = donTelefone;
        this.donCep = donCep;
        this.donUf = donUf;
        this.donEmail = donEmail;
        this.donSexo = donSexo;
    }

    // --- GETTERS E SETTERS ---
    public int getDonId() {
        return donId;
    }

    public void setDonId(int donId) {
        this.donId = donId;
    }

    public String getDonNome() {
        return donNome;
    }

    public void setDonNome(String donNome) {
        this.donNome = donNome;
    }

    public String getDonDataNasc() {
        return donDataNasc;
    }

    public void setDonDataNasc(String donDataNasc) {
        this.donDataNasc = donDataNasc;
    }

    public String getDonRua() {
        return donRua;
    }

    public void setDonRua(String donRua) {
        this.donRua = donRua;
    }

    public String getDonBairro() {
        return donBairro;
    }

    public void setDonBairro(String donBairro) {
        this.donBairro = donBairro;
    }

    public String getDonCidade() {
        return donCidade;
    }

    public void setDonCidade(String donCidade) {
        this.donCidade = donCidade;
    }

    public String getDonTelefone() {
        return donTelefone;
    }

    public void setDonTelefone(String donTelefone) {
        this.donTelefone = donTelefone;
    }

    public String getDonCep() {
        return donCep;
    }

    public void setDonCep(String donCep) {
        this.donCep = donCep;
    }

    public String getDonUf() {
        return donUf;
    }

    public void setDonUf(String donUf) {
        this.donUf = donUf;
    }

    public String getDonEmail() {
        return donEmail;
    }

    public void setDonEmail(String donEmail) {
        this.donEmail = donEmail;
    }

    public String getDonSexo() {
        return donSexo;
    }

    public void setDonSexo(String donSexo) {
        this.donSexo = donSexo;
    }

    // --- MÉTODOS DE NEGÓCIO (Model instancia DAO e passa Connection) ---

    /**
     * Grava este donatário no banco
     * @param conn Conexão recebida do Control
     * @return Donatário gravado com ID ou null se erro
     */
    public Donatario gravar(Connection conn) {
        DonatarioDAO dao = new DonatarioDAO(conn);
        return dao.gravar(this);
    }

    /**
     * Altera este donatário no banco
     * @param conn Conexão recebida do Control
     * @return Donatário alterado ou null se erro
     */
    public Donatario alterar(Connection conn) {
        DonatarioDAO dao = new DonatarioDAO(conn);
        return dao.alterar(this);
    }

    /**
     * Apaga este donatário do banco
     * @param conn Conexão recebida do Control
     * @return true se removido, false se erro
     */
    public boolean apagar(Connection conn) {
        DonatarioDAO dao = new DonatarioDAO(conn);
        return dao.apagar(this);
    }

    /**
     * Busca um donatário por ID
     * @param conn Conexão recebida do Control
     * @param id ID do donatário
     * @return Donatário encontrado ou null
     */
    public static Donatario get(Connection conn, int id) {
        DonatarioDAO dao = new DonatarioDAO(conn);
        return dao.get(id);
    }

    /**
     * Busca donatários com filtro
     * @param conn Conexão recebida do Control
     * @param filtro Texto para filtrar
     * @return Lista de donatários
     */
    public static List<Donatario> get(Connection conn, String filtro) {
        DonatarioDAO dao = new DonatarioDAO(conn);
        return dao.get(filtro);
    }

    /**
     * Busca todos os donatários
     * @param conn Conexão recebida do Control
     * @return Lista de todos os donatários
     */
    public static List<Donatario> getAll(Connection conn) {
        DonatarioDAO dao = new DonatarioDAO(conn);
        return dao.getAll();
    }
}
