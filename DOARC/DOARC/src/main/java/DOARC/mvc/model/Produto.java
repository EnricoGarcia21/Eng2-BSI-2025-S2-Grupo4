package DOARC.mvc.model;

import DOARC.mvc.dao.ProdutoDAO;
import DOARC.mvc.observer.SujeitoProduto;
import DOARC.mvc.observer.ObservadorDonatario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import java.util.List;

public class Produto implements SujeitoProduto {
    private int prodId;
    private String prodNome;
    private String prodDescricao;
    private String prodInformacoesAdicionais;
    private int prodQuant;
    private int categoriaCatId;
    private String categoriaNome; // Atributo para o nome da categoria

    private static final ProdutoDAO dao = new ProdutoDAO();

    public Produto() {}

    public Produto(String prodNome, String prodDescricao, String prodInformacoesAdicionais,
                   int prodQuant, int categoriaCatId) {
        this.prodNome = prodNome;
        this.prodDescricao = prodDescricao;
        this.prodInformacoesAdicionais = prodInformacoesAdicionais;
        this.prodQuant = prodQuant;
        this.categoriaCatId = categoriaCatId;
    }

    @Override
    public void anexar(ObservadorDonatario observador) {
        // Implementação via banco de dados
    }

    @Override
    public void desanexar(ObservadorDonatario observador) {}

    @Override
    public void notificarObservadores() {
        Conexao conexao = SingletonDB.conectar();
        List<Doador> doadores = dao.buscarObservadoresDoProduto(this.prodId, conexao);

        for (Doador d : doadores) {
            d.update("O item '" + this.prodNome + "' atingiu o nível crítico no estoque. Precisamos de doações!");
        }
    }

    public Produto atualizarComNotificacao(Conexao conexao) {
        Produto alterado = dao.alterar(this, conexao);

        if (alterado != null) {
            if (this.prodQuant <= 5) {
                notificarObservadores();
            }
        }
        return alterado;
    }

    public Produto gravar(Conexao conexao) { return dao.gravar(this, conexao); }
    public Produto alterar(Conexao conexao) { return dao.alterar(this, conexao); }
    public boolean apagar(Conexao conexao) { return dao.apagar(this, conexao); }

    public static Produto get(int id, Conexao conexao) {
        return dao.get(id, conexao);
    }

    public static List<Produto> get(String filtro, Conexao conexao) {
        return dao.get(filtro, conexao);
    }

    // --- GETTERS E SETTERS ---

    public int getProdId() { return prodId; }
    public void setProdId(int prodId) { this.prodId = prodId; }

    public String getProdNome() { return prodNome; }
    public void setProdNome(String prodNome) { this.prodNome = prodNome; }

    public int getProdQuant() { return prodQuant; }
    public void setProdQuant(int prodQuant) { this.prodQuant = prodQuant; }

    public int getCategoriaCatId() { return categoriaCatId; }
    public void setCategoriaCatId(int categoriaCatId) { this.categoriaCatId = categoriaCatId; }

    public String getProdDescricao() { return prodDescricao; }
    public void setProdDescricao(String d) { this.prodDescricao = d; }

    public String getProdInformacoesAdicionais() { return prodInformacoesAdicionais; }
    public void setProdInformacoesAdicionais(String i) { this.prodInformacoesAdicionais = i; }

    public String getCategoriaNome() {
        return this.categoriaNome;
    }

    public void setCategoriaNome(String categoriaNome) {
        this.categoriaNome = categoriaNome;
    }
}