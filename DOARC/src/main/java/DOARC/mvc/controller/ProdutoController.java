package DOARC.mvc.controller;

import DOARC.mvc.model.Produto;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProdutoController {

    public List<Map<String, Object>> getProdutos(Integer categoriaId, String nome) {
        String filtro = "WHERE 1=1";
        if (categoriaId != null) filtro += " AND p.categoria_cat_id = " + categoriaId;
        if (nome != null && !nome.trim().isEmpty()) filtro += " AND LOWER(p.prod_nome) LIKE LOWER('%" + nome + "%')";

        List<Produto> lista = Produto.get(filtro);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Produto p : lista) result.add(produtoToMap(p));
        return result;
    }

    public Map<String, Object> getProduto(int id) {
        Produto p = Produto.get(id);
        if (p == null) return Map.of("erro", "Produto não encontrado");
        return produtoToMap(p);
    }

    public Map<String, Object> addProduto(String nome, String descricao, String infoAdicional,
                                          int quant, int categoriaCatId) {

        Produto novo = new Produto(nome, descricao, infoAdicional, quant, categoriaCatId);
        Produto gravado = novo.gravar();

        if (gravado == null) return Map.of("erro", "Erro ao cadastrar produto");
        return getProduto(gravado.getProdId());
    }

    public Map<String, Object> updtProduto(int id, String nome, String descricao,
                                           String infoAdicional, int quant, int categoriaCatId) {

        Produto existente = Produto.get(id);
        if (existente == null) return Map.of("erro", "Produto não encontrado");

        existente.setProdNome(nome);
        existente.setProdDescricao(descricao);
        existente.setProdInformacoesAdicionais(infoAdicional);
        existente.setProdQuant(quant);
        existente.setCategoriaCatId(categoriaCatId);

        Produto atualizado = existente.alterar();
        if (atualizado == null) return Map.of("erro", "Erro ao atualizar produto");

        return getProduto(atualizado.getProdId());
    }

    public Map<String, Object> deletarProduto(int id) {
        Produto p = Produto.get(id);
        if (p == null) return Map.of("erro", "Produto não encontrado");

        boolean deletado = p.apagar();
        return deletado ? Map.of("mensagem", "Produto removido com sucesso")
                : Map.of("erro", "Erro ao remover produto");
    }

    private Map<String, Object> produtoToMap(Produto p) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", p.getProdId());
        json.put("nome", p.getProdNome());
        json.put("descricao", p.getProdDescricao());
        json.put("informacoesAdicionais", p.getProdInformacoesAdicionais());
        json.put("quant", p.getProdQuant());
        json.put("categoriaId", p.getCategoriaCatId());
        json.put("categoriaNome", p.getCategoriaNome() != null ? p.getCategoriaNome() : "Sem Categoria");
        return json;
    }
}
