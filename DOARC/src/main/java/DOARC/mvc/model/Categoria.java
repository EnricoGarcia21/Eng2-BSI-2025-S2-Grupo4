package DOARC.mvc.model;

public class Categoria {

    private int catId;
    private String catNome;

    // --- CONSTRUTORES ---
    public Categoria() {
        // Construtor vazio necessário para mapeamento e frameworks
    }

    // Construtor para cadastro (sem ID, pois o banco gera)
    public Categoria(String catNome) {
        this.catNome = catNome;
    }

    // --- GETTERS E SETTERS ---
    public int getCatId() {
        return catId;
    }

    public void setCatId(int catId) {
        this.catId = catId;
    }

    public String getCatNome() {
        return catNome;
    }

    public void setCatNome(String catNome) {
        this.catNome = catNome;
    }
}
