package DOARC.mvc.control;

import DOARC.mvc.model.Compra;
import DOARC.mvc.model.CompraProduto;
import DOARC.mvc.model.Produto;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Control com padrão Facade
 * - Responsável por validar dados de entrada
 * - Gerencia transações de compra/arrecadação
 * - Atualiza estoque automaticamente
 * - Control gerencia a conexão STATIC e passa para o Model
 */
@Service
public class CompraControl {

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

    public List<Map<String, Object>> getCompras() {
        Connection conn = getConexao();
        List<Compra> lista = Compra.getAll(conn);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Compra c : lista) {
            result.add(toMap(c));
        }
        return result;
    }

    public Map<String, Object> getCompra(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Connection conn = getConexao();
        Compra c = Compra.get(conn, id);
        if (c == null) {
            return Map.of("erro", "Compra não encontrada");
        }

        Map<String, Object> result = toMap(c);

        // Adiciona os produtos da compra
        List<CompraProduto> produtos = Compra.getProdutosDaCompra(conn, id);
        List<Map<String, Object>> produtosMap = new ArrayList<>();

        for (CompraProduto cp : produtos) {
            Map<String, Object> prodMap = new HashMap<>();
            Produto p = Produto.get(conn, cp.getProdId());

            prodMap.put("produto_id", cp.getProdId());
            prodMap.put("produto_nome", p != null ? p.getProdNome() : "");
            prodMap.put("quantidade", cp.getQtde());
            prodMap.put("valor_unitario", cp.getValorUnitario());
            prodMap.put("subtotal", cp.getQtde() * cp.getValorUnitario());
            produtosMap.add(prodMap);
        }

        result.put("produtos", produtosMap);
        return result;
    }

    /**
     * Registra uma compra/arrecadação com seus produtos
     * @param dataCompra Data da compra (YYYY-MM-DD)
     * @param descricao Descrição/Observações
     * @param volId ID do voluntário responsável
     * @param valorTotal Valor total (0 para arrecadação)
     * @param fornecedor Fornecedor (vazio para arrecadação)
     * @param produtosJson Lista de produtos no formato: [{"prodId": 1, "qtde": 10, "valorUnit": 5.50}, ...]
     * @return Map com dados da compra ou erro
     */
    public Map<String, Object> registrarCompra(String dataCompra, String descricao, int volId,
                                               double valorTotal, String fornecedor,
                                               List<Map<String, Object>> produtosJson) {
        Connection conn = getConexao();

        // Validação de entrada
        Map<String, String> validacao = validarDados(conn, dataCompra, volId, fornecedor, valorTotal, produtosJson);
        if (!validacao.isEmpty()) {
            return Map.of("erro", validacao.values().iterator().next());
        }

        // Cria o objeto Compra
        Compra compra = new Compra(
            dataCompra,
            descricao != null ? descricao.trim() : "",
            volId,
            valorTotal,
            fornecedor != null ? fornecedor.trim() : ""
        );

        // Converte produtos JSON para objetos CompraProduto
        List<CompraProduto> produtos = new ArrayList<>();
        for (Map<String, Object> prodJson : produtosJson) {
            try {
                int prodId = Integer.parseInt(prodJson.get("prodId").toString());
                double qtde = Double.parseDouble(prodJson.get("qtde").toString());
                double valorUnit = prodJson.containsKey("valorUnit")
                    ? Double.parseDouble(prodJson.get("valorUnit").toString())
                    : 0.0;

                // Valida se o produto existe
                Produto p = Produto.get(conn, prodId);
                if (p == null) {
                    return Map.of("erro", "Produto ID " + prodId + " não encontrado");
                }

                produtos.add(new CompraProduto(0, prodId, qtde, valorUnit));
            } catch (Exception e) {
                return Map.of("erro", "Erro ao processar produto: " + e.getMessage());
            }
        }

        if (produtos.isEmpty()) {
            return Map.of("erro", "Adicione pelo menos um produto");
        }

        // Grava a compra completa (com transação)
        Compra gravada = compra.gravarCompraCompleta(conn, produtos);
        if (gravada == null) {
            return Map.of("erro", "Erro ao registrar a compra");
        }

        return toMap(gravada);
    }

    private Map<String, String> validarDados(Connection conn, String dataCompra, int volId, String fornecedor,
                                            double valorTotal, List<Map<String, Object>> produtos) {
        Map<String, String> erros = new HashMap<>();

        if (dataCompra == null || dataCompra.trim().isEmpty()) {
            erros.put("data", "Data da compra é obrigatória");
        } else if (!dataCompra.matches("\\d{4}-\\d{2}-\\d{2}")) {
            erros.put("data", "Data inválida (formato esperado: YYYY-MM-DD)");
        }

        if (volId <= 0) {
            erros.put("voluntario", "Voluntário responsável é obrigatório");
        }

        // Se tem fornecedor, é uma compra (não arrecadação)
        if (fornecedor != null && !fornecedor.trim().isEmpty()) {
            if (fornecedor.length() > 50) {
                erros.put("fornecedor", "Nome do fornecedor muito longo (máximo 50 caracteres)");
            }
            if (valorTotal <= 0) {
                erros.put("valor", "Valor total deve ser maior que zero para compras");
            }
        }

        if (produtos == null || produtos.isEmpty()) {
            erros.put("produtos", "Adicione pelo menos um produto");
        }

        return erros;
    }

    private Map<String, Object> toMap(Compra c) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", c.getCompId());
        json.put("data_compra", c.getCompDataCompra());
        json.put("descricao", c.getCompDesc());
        json.put("voluntario_id", c.getVolId());
        json.put("valor_total", c.getComValorTotal());
        json.put("fornecedor", c.getComFornecedor());

        // Determina se é compra ou arrecadação
        boolean isArrecadacao = c.getComFornecedor() == null || c.getComFornecedor().trim().isEmpty();
        json.put("tipo", isArrecadacao ? "arrecadacao" : "compra");

        return json;
    }
}
