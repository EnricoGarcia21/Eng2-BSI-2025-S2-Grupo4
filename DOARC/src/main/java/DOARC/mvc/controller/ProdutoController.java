package DOARC.mvc.controller;

import DOARC.mvc.model.Produto;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProdutoController {

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public Map<String, Object> addProduto(String nome, String descricao, String informacoesAdicionais,
                                          int quantidade, int categoriaId) {
        try {
            Conexao conexao = getConexao();

            Produto produto = new Produto(nome, descricao, informacoesAdicionais, quantidade, categoriaId);
            Produto gravado = produto.gravar(conexao);

            if (gravado == null || gravado.getProdId() == 0) {
                return Map.of("erro", "Falha ao gravar produto.");
            }

            return Map.of(
                    "mensagem", "Produto cadastrado com sucesso!",
                    "id", gravado.getProdId()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao salvar produto: " + e.getMessage());
        }
    }

    public Map<String, Object> updtProduto(int id, String nome, String descricao, String informacoesAdicionais,
                                           int quantidade, int categoriaId) {
        try {
            Conexao conexao = getConexao();

            Produto produto = new Produto(nome, descricao, informacoesAdicionais, quantidade, categoriaId);
            produto.setProdId(id);

            Produto alterado = produto.alterar(conexao);

            if (alterado == null) {
                return Map.of("erro", "Falha ao atualizar produto.");
            }

            return Map.of("mensagem", "Produto atualizado com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getProdutos(Integer categoriaId, String nome) {
        Conexao conexao = getConexao();

        String filtro = "";
        if (categoriaId != null) {
            filtro += "cat_id = " + categoriaId;
        }
        if (nome != null && !nome.isEmpty()) {
            if (!filtro.isEmpty()) filtro += " AND ";
            filtro += "LOWER(prod_nome) LIKE LOWER('%" + nome + "%')";
        }

        return Produto.get(filtro, conexao).stream()
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getProdId());
                    map.put("nome", p.getProdNome());
                    map.put("descricao", p.getProdDescricao());
                    map.put("informacoesAdicionais", p.getProdInformacoesAdicionais());
                    map.put("quantidade", p.getProdQuant());
                    map.put("categoriaId", p.getCategoriaCatId());
                    map.put("categoriaNome", p.getCategoriaNome());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getProduto(int id) {
        Conexao conexao = getConexao();
        Produto p = Produto.get(id, conexao);

        if (p == null) {
            return Map.of("erro", "Produto não encontrado.");
        }

        return Map.of(
                "id", p.getProdId(),
                "nome", p.getProdNome(),
                "descricao", p.getProdDescricao(),
                "informacoesAdicionais", p.getProdInformacoesAdicionais(),
                "quantidade", p.getProdQuant(),
                "categoriaId", p.getCategoriaCatId(),
                "categoriaNome", p.getCategoriaNome()
        );
    }

    public Map<String, Object> deletarProduto(int id) {
        Conexao conexao = getConexao();
        Produto p = new Produto();
        p.setProdId(id);

        boolean apagado = p.apagar(conexao);

        if (!apagado) {
            return Map.of("erro", "Falha ao excluir produto.");
        }

        return Map.of("mensagem", "Produto removido com sucesso!");
    }
}
