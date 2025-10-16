package DOARC.mvc.controller;

import DOARC.mvc.dao.CategoriaDAO;
import DOARC.mvc.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoriaController {

    @Autowired
    private CategoriaDAO categoriaModel;

    public List<Map<String, Object>> getCategorias() {
        List<Categoria> lista = categoriaModel.get("");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Categoria c : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", c.getCatId());
            json.put("nome", c.getCatNome());
            result.add(json);
        }
        return result;
    }

    public Map<String, Object> getCategoria(int id) {
        Categoria c = categoriaModel.get(id);
        if (c == null) return Map.of("erro", "Categoria não encontrada");

        Map<String, Object> json = new HashMap<>();
        json.put("id", c.getCatId());
        json.put("nome", c.getCatNome());
        return json;
    }

    public Map<String, Object> addCategoria(String nome) {
        Categoria nova = new Categoria(nome);
        Categoria gravada = categoriaModel.gravar(nova);
        if (gravada == null) return Map.of("erro", "Erro ao cadastrar a Categoria");

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravada.getCatId());
        json.put("nome", gravada.getCatNome());
        return json;
    }

    public Map<String, Object> updtCategoria(int id, String nome) {
        Categoria existente = categoriaModel.get(id);
        if (existente == null) return Map.of("erro", "Categoria não encontrada");

        existente.setCatNome(nome);

        Categoria atualizada = categoriaModel.alterar(existente);
        if (atualizada == null) return Map.of("erro", "Erro ao atualizar a Categoria");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizada.getCatId());
        json.put("nome", atualizada.getCatNome());
        return json;
    }

    public Map<String, Object> deletarCategoria(int id) {
        Categoria c = categoriaModel.get(id);
        if (c == null) return Map.of("erro", "Categoria não encontrada");

        boolean deletado = categoriaModel.apagar(c);
        return deletado ? Map.of("mensagem", "Categoria removida com sucesso") : Map.of("erro", "Erro ao remover a Categoria");
    }
}
