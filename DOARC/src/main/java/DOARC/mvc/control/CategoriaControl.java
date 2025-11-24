package DOARC.mvc.control;

import DOARC.mvc.model.Categoria;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Control com padrão Facade
 * - Responsável por validar dados de entrada
 * - Atua como fachada para as operações de categoria
 * - Control gerencia a conexão STATIC e passa para o Model
 */
@Service
public class CategoriaControl {

    // Conexão estática gerenciada pelo Control
    private static Connection conexao = null;

    /**
     * Obtém conexão estática (cria apenas se não existir ou estiver inválida)
     */
    private Connection getConexao() {
        if (conexao == null || !isConexaoValida()) {
            conexao = SingletonDB.getConnection();
        }
        return conexao;
    }

    /**
     * Verifica se a conexão ainda é válida
     */
    private boolean isConexaoValida() {
        try {
            return conexao != null && !conexao.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Map<String, Object>> getCategorias() {
        Connection conn = getConexao();
        List<Categoria> lista = Categoria.getAll(conn);

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

        Connection conn = getConexao();
        Categoria c = Categoria.get(conn, id);
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

        Connection conn = getConexao();
        Categoria nova = new Categoria(nome.trim(), especificacao != null ? especificacao.trim() : "");
        Categoria gravada = nova.gravar(conn);

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

        Connection conn = getConexao();
        Categoria existente = Categoria.get(conn, id);
        if (existente == null) {
            return Map.of("erro", "Categoria não encontrada");
        }

        existente.setCatNomeProd(nome.trim());
        existente.setCatEspecificacao(especificacao != null ? especificacao.trim() : "");

        Categoria atualizada = existente.alterar(conn);
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

        Connection conn = getConexao();
        Categoria c = Categoria.get(conn, id);
        if (c == null) {
            return Map.of("erro", "Categoria não encontrada");
        }

        // Verifica se há produtos relacionados
        if (Categoria.temProdutosRelacionados(conn, id)) {
            return Map.of("erro", "Não é possível excluir esta categoria pois existem produtos relacionados a ela");
        }

        boolean deletado = c.apagar(conn);
        return deletado
                ? Map.of("mensagem", "Categoria removida com sucesso")
                : Map.of("erro", "Erro ao remover a Categoria");
    }

    public List<Map<String, Object>> buscarCategorias(String filtro) {
        Connection conn = getConexao();
        List<Categoria> lista = Categoria.get(conn, filtro);

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
