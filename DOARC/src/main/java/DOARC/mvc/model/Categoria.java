package DOARC.mvc.model;

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
}
