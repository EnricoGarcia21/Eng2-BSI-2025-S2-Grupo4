package DOARC.mvc.control;

import DOARC.mvc.dao.CategoriaDAO;
import DOARC.mvc.dao.ProdutoDAO;
import DOARC.mvc.model.Categoria;
import DOARC.mvc.model.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Control com padrão Facade
 * - Responsável por validar dados de entrada
 * - Atua como fachada para as operações de produto
 * - DAO gerencia a conexão internamente
 */
@Service
public class ProdutoControl {

    @Autowired
    private ProdutoDAO produtoDAO;

    @Autowired
    private CategoriaDAO categoriaDAO;

    public List<Map<String, Object>> getProdutos() {
        List<Produto> lista = produtoDAO.getAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Produto p : lista) {
            result.add(toMap(p));
        }
        return result;
    }

    public Map<String, Object> getProduto(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Produto p = produtoDAO.get(id);
        if (p == null) {
            return Map.of("erro", "Produto não encontrado");
        }

        return toMap(p);
    }

    public Map<String, Object> addProduto(String nome, String descricao, String informacoesAdicionais,
                                          int quantidade, int categoriaId) {
        // Validação de entrada
        Map<String, String> validacao = validarDados(nome, descricao, informacoesAdicionais, quantidade, categoriaId);
        if (!validacao.isEmpty()) {
            return Map.of("erro", validacao.values().iterator().next());
        }

        Produto novo = new Produto(
            nome.trim(),
            descricao != null ? descricao.trim() : "",
            informacoesAdicionais != null ? informacoesAdicionais.trim() : "",
            quantidade,
            categoriaId
        );

        Produto gravado = produtoDAO.gravar(novo);
        if (gravado == null) {
            return Map.of("erro", "Erro ao cadastrar o Produto");
        }

        return toMap(gravado);
    }

    public Map<String, Object> updtProduto(int id, String nome, String descricao,
                                          String informacoesAdicionais, int quantidade, int categoriaId) {
        // Validação de ID
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        // Validação de entrada
        Map<String, String> validacao = validarDados(nome, descricao, informacoesAdicionais, quantidade, categoriaId);
        if (!validacao.isEmpty()) {
            return Map.of("erro", validacao.values().iterator().next());
        }

        Produto existente = produtoDAO.get(id);
        if (existente == null) {
            return Map.of("erro", "Produto não encontrado");
        }

        existente.setProdNome(nome.trim());
        existente.setProdDescricao(descricao != null ? descricao.trim() : "");
        existente.setProdInformacoesAdicionais(informacoesAdicionais != null ? informacoesAdicionais.trim() : "");
        existente.setProdQuant(quantidade);
        existente.setCatId(categoriaId);

        Produto atualizado = produtoDAO.alterar(existente);
        if (atualizado == null) {
            return Map.of("erro", "Erro ao atualizar o Produto");
        }

        return toMap(atualizado);
    }

    public Map<String, Object> deletarProduto(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Produto p = produtoDAO.get(id);
        if (p == null) {
            return Map.of("erro", "Produto não encontrado");
        }

        boolean deletado = produtoDAO.apagar(p);
        return deletado
                ? Map.of("mensagem", "Produto removido com sucesso")
                : Map.of("erro", "Erro ao remover o Produto");
    }

    public List<Map<String, Object>> buscarProdutos(String filtro) {
        List<Produto> lista = produtoDAO.get(filtro);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Produto p : lista) {
            result.add(toMap(p));
        }
        return result;
    }

    public List<Map<String, Object>> getProdutosPorCategoria(int categoriaId) {
        List<Produto> lista = produtoDAO.getPorCategoria(categoriaId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Produto p : lista) {
            result.add(toMap(p));
        }
        return result;
    }

    private Map<String, String> validarDados(String nome, String descricao,
                                            String informacoesAdicionais, int quantidade, int categoriaId) {
        Map<String, String> erros = new HashMap<>();

        if (nome == null || nome.trim().isEmpty()) {
            erros.put("nome", "Nome do produto é obrigatório");
        } else if (nome.length() > 100) {
            erros.put("nome", "Nome do produto muito longo (máximo 100 caracteres)");
        }

        if (descricao != null && descricao.length() > 150) {
            erros.put("descricao", "Descrição muito longa (máximo 150 caracteres)");
        }

        if (informacoesAdicionais != null && informacoesAdicionais.length() > 150) {
            erros.put("informacoesAdicionais", "Informações adicionais muito longas (máximo 150 caracteres)");
        }

        if (quantidade < 0) {
            erros.put("quantidade", "Quantidade não pode ser negativa");
        }

        if (categoriaId <= 0) {
            erros.put("categoria", "Categoria é obrigatória");
        } else {
            // Verifica se a categoria existe
            Categoria cat = categoriaDAO.get(categoriaId);
            if (cat == null) {
                erros.put("categoria", "Categoria não encontrada");
            }
        }

        return erros;
    }

    private Map<String, Object> toMap(Produto p) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", p.getProdId());
        json.put("nome", p.getProdNome());
        json.put("descricao", p.getProdDescricao());
        json.put("informacoes_adicionais", p.getProdInformacoesAdicionais());
        json.put("quantidade", p.getProdQuant());
        json.put("categoria_id", p.getCatId());

        // Busca o nome da categoria para facilitar a exibição no frontend
        Categoria cat = categoriaDAO.get(p.getCatId());
        if (cat != null) {
            json.put("categoria_nome", cat.getCatNomeProd());
        }

        return json;
    }
}
