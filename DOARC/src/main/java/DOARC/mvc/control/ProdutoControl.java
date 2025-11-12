package DOARC.mvc.control;

import DOARC.mvc.model.Categoria;
import DOARC.mvc.model.Produto;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Control com padrão Facade
 * - Responsável por validar dados de entrada
 * - Atua como fachada para as operações de produto
 * - Control gerencia a conexão STATIC e passa para o Model
 */
@Service
public class ProdutoControl {

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

    public List<Map<String, Object>> getProdutos() {
        Connection conn = getConexao();
        List<Produto> lista = Produto.getAll(conn);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Produto p : lista) {
            result.add(toMap(p, conn));
        }
        return result;
    }

    public Map<String, Object> getProduto(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Connection conn = getConexao();
        Produto p = Produto.get(conn, id);
        if (p == null) {
            return Map.of("erro", "Produto não encontrado");
        }

        return toMap(p, conn);
    }

    public Map<String, Object> addProduto(String nome, String descricao, String informacoesAdicionais,
                                          int quantidade, int categoriaId) {
        Connection conn = getConexao();

        // Validação de entrada
        Map<String, String> validacao = validarDados(conn, nome, descricao, informacoesAdicionais, quantidade, categoriaId);
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

        Produto gravado = novo.gravar(conn);
        if (gravado == null) {
            return Map.of("erro", "Erro ao cadastrar o Produto");
        }

        return toMap(gravado, conn);
    }

    public Map<String, Object> updtProduto(int id, String nome, String descricao,
                                          String informacoesAdicionais, int quantidade, int categoriaId) {
        // Validação de ID
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Connection conn = getConexao();

        // Validação de entrada
        Map<String, String> validacao = validarDados(conn, nome, descricao, informacoesAdicionais, quantidade, categoriaId);
        if (!validacao.isEmpty()) {
            return Map.of("erro", validacao.values().iterator().next());
        }

        Produto existente = Produto.get(conn, id);
        if (existente == null) {
            return Map.of("erro", "Produto não encontrado");
        }

        existente.setProdNome(nome.trim());
        existente.setProdDescricao(descricao != null ? descricao.trim() : "");
        existente.setProdInformacoesAdicionais(informacoesAdicionais != null ? informacoesAdicionais.trim() : "");
        existente.setProdQuant(quantidade);
        existente.setCatId(categoriaId);

        Produto atualizado = existente.alterar(conn);
        if (atualizado == null) {
            return Map.of("erro", "Erro ao atualizar o Produto");
        }

        return toMap(atualizado, conn);
    }

    public Map<String, Object> deletarProduto(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Connection conn = getConexao();
        Produto p = Produto.get(conn, id);
        if (p == null) {
            return Map.of("erro", "Produto não encontrado");
        }

        boolean deletado = p.apagar(conn);
        return deletado
                ? Map.of("mensagem", "Produto removido com sucesso")
                : Map.of("erro", "Erro ao remover o Produto");
    }

    public List<Map<String, Object>> buscarProdutos(String filtro) {
        Connection conn = getConexao();
        List<Produto> lista = Produto.get(conn, filtro);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Produto p : lista) {
            result.add(toMap(p, conn));
        }
        return result;
    }

    public List<Map<String, Object>> getProdutosPorCategoria(int categoriaId) {
        Connection conn = getConexao();
        List<Produto> lista = Produto.getPorCategoria(conn, categoriaId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Produto p : lista) {
            result.add(toMap(p, conn));
        }
        return result;
    }

    private Map<String, String> validarDados(Connection conn, String nome, String descricao,
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
            Categoria cat = Categoria.get(conn, categoriaId);
            if (cat == null) {
                erros.put("categoria", "Categoria não encontrada");
            }
        }

        return erros;
    }

    private Map<String, Object> toMap(Produto p, Connection conn) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", p.getProdId());
        json.put("nome", p.getProdNome());
        json.put("descricao", p.getProdDescricao());
        json.put("informacoes_adicionais", p.getProdInformacoesAdicionais());
        json.put("quantidade", p.getProdQuant());
        json.put("categoria_id", p.getCatId());

        // Busca o nome da categoria para facilitar a exibição no frontend
        Categoria cat = Categoria.get(conn, p.getCatId());
        if (cat != null) {
            json.put("categoria_nome", cat.getCatNomeProd());
        }

        return json;
    }
}
