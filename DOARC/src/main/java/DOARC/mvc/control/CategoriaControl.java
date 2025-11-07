package DOARC.mvc.control;

import DOARC.mvc.dao.CategoriaDAO;
import DOARC.mvc.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoriaControl {

    @Autowired
    private CategoriaDAO categoriaDAO;

    public List<Map<String, Object>> getCategorias() {
        List<Categoria> lista = categoriaDAO.getAll();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Categoria c : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", c.getCatId());
            json.put("nome", c.getCatNomeProd());
            json.put("especificacao", c.getCatEspecificacao());
            result.add(json);
        }
        return result;
    }

    public Map<String, Object> getCategoria(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Categoria c = categoriaDAO.get(id);
        if (c == null) {
            return Map.of("erro", "Categoria não encontrada");
        }

        Map<String, Object> json = new HashMap<>();
        json.put("id", c.getCatId());
        json.put("nome", c.getCatNomeProd());
        json.put("especificacao", c.getCatEspecificacao());
        return json;
    }

    public Map<String, Object> addCategoria(String nome, String especificacao) {
        // Validação de entrada
        if (nome == null || nome.trim().isEmpty()) {
            return Map.of("erro", "Nome da categoria é obrigatório");
        }

        if (nome.length() > 150) {
            return Map.of("erro", "Nome da categoria muito longo (máximo 150 caracteres)");
        }

        if (especificacao != null && especificacao.length() > 130) {
            return Map.of("erro", "Especificação muito longa (máximo 130 caracteres)");
        }

        Categoria nova = new Categoria(nome.trim(), especificacao != null ? especificacao.trim() : "");
        Categoria gravada = categoriaDAO.gravar(nova);

        if (gravada == null) {
            return Map.of("erro", "Erro ao cadastrar a Categoria");
        }

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravada.getCatId());
        json.put("nome", gravada.getCatNomeProd());
        json.put("especificacao", gravada.getCatEspecificacao());
        return json;
    }

    public Map<String, Object> updtCategoria(int id, String nome, String especificacao) {
        // Validação de entrada
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        if (nome == null || nome.trim().isEmpty()) {
            return Map.of("erro", "Nome da categoria é obrigatório");
        }

        if (nome.length() > 150) {
            return Map.of("erro", "Nome da categoria muito longo (máximo 150 caracteres)");
        }

        if (especificacao != null && especificacao.length() > 130) {
            return Map.of("erro", "Especificação muito longa (máximo 130 caracteres)");
        }

        Categoria existente = categoriaDAO.get(id);
        if (existente == null) {
            return Map.of("erro", "Categoria não encontrada");
        }

        existente.setCatNomeProd(nome.trim());
        existente.setCatEspecificacao(especificacao != null ? especificacao.trim() : "");

        Categoria atualizada = categoriaDAO.alterar(existente);
        if (atualizada == null) {
            return Map.of("erro", "Erro ao atualizar a Categoria");
        }

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizada.getCatId());
        json.put("nome", atualizada.getCatNomeProd());
        json.put("especificacao", atualizada.getCatEspecificacao());
        return json;
    }

    public Map<String, Object> deletarCategoria(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Categoria c = categoriaDAO.get(id);
        if (c == null) {
            return Map.of("erro", "Categoria não encontrada");
        }

        // Verifica se há produtos relacionados
        if (categoriaDAO.temProdutosRelacionados(id)) {
            return Map.of("erro", "Não é possível excluir esta categoria pois existem produtos relacionados a ela");
        }

        boolean deletado = categoriaDAO.apagar(c);
        return deletado
                ? Map.of("mensagem", "Categoria removida com sucesso")
                : Map.of("erro", "Erro ao remover a Categoria");
    }

    public List<Map<String, Object>> buscarCategorias(String filtro) {
        List<Categoria> lista = categoriaDAO.get(filtro);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Categoria c : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", c.getCatId());
            json.put("nome", c.getCatNomeProd());
            json.put("especificacao", c.getCatEspecificacao());
            result.add(json);
        }
        return result;
    }
}
