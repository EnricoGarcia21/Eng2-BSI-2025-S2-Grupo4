package DOARC.mvc.model;

import DOARC.mvc.dao.AtualizarEstoqueDAO;
import DOARC.mvc.util.Conexao;

public class AtualizarEstoque {

    private int prodId;
    private int quantidade;

    private static final AtualizarEstoqueDAO dao = new AtualizarEstoqueDAO();
    public AtualizarEstoque() {}

    public AtualizarEstoque(int prodId, int quantidade) {
        this.prodId = prodId;
        this.quantidade = quantidade;
    }


    public boolean baixarEstoque(Conexao conexao) {
        return dao.subtrair(this.prodId, this.quantidade, conexao);
    }

    public boolean adicionarEstoque(Conexao conexao) {
        return dao.adicionar(this.prodId, this.quantidade, conexao);
    }

    public int getProdId() { return prodId; }
    public void setProdId(int prodId) { this.prodId = prodId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}