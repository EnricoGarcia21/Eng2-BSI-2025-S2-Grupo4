package DOARC.mvc.controller;

import DOARC.mvc.model.AtualizarEstoque;
import DOARC.mvc.model.Doados;
import DOARC.mvc.model.DoadosProduto;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoadosController {


    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public Map<String, Object> efetuarDoacao(Map<String, Object> payload) {
        Conexao conexao = getConexao();
        Connection conn = conexao.getConnect(); // Precisamos da Connection crua para gerenciar a transação manualmente

        try {
            // 1. Instancia o Model (igual ao ProdutoController)
            Doados doacao = new Doados(
                    (Integer) payload.get("donatarioId"),
                    (Integer) payload.get("voluntarioId"),
                    (String) payload.get("observacoes"),
                    (String) payload.get("tipoDoacao"),
                    BigDecimal.valueOf(((Number) payload.get("valorDoacao")).doubleValue()),
                    LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );

            // Preparar a lista de produtos
            List<Map<String, Object>> produtosPayload = (List<Map<String, Object>>) payload.get("produtos");
            if (produtosPayload == null || produtosPayload.isEmpty()) {
                return Map.of("erro", "A lista de produtos não pode estar vazia.");
            }

            List<DoadosProduto> produtosList = produtosPayload.stream()
                    .map(item -> {
                        DoadosProduto dp = new DoadosProduto();
                        dp.setProdId((Integer) item.get("prodId"));
                        dp.setDpQtde(BigDecimal.valueOf(((Number) item.get("quantidade")).doubleValue()));
                        return dp;
                    })
                    .collect(Collectors.toList());

            doacao.setProdutos(produtosList);

            // Início da Transação
            conn.setAutoCommit(false);

            // 2. Chama o gravar DIRETAMENTE no objeto Model
            Doados doacaoGravada = doacao.gravar(conexao);

            if (doacaoGravada == null || doacaoGravada.getDoaId() == null) {
                conn.rollback();
                return Map.of("erro", "Falha ao gravar os dados da doação.");
            }

            // 3. Atualizar o Estoque
            for (DoadosProduto item : produtosList) {
                AtualizarEstoque estoqueModel = new AtualizarEstoque(
                        item.getProdId(),
                        item.getDpQtde().intValue()
                );

                // Assumindo que AtualizarEstoque também segue o padrão e aceita conexao
                boolean sucesso = estoqueModel.baixarEstoque(conexao);

                if (!sucesso) {
                    conn.rollback(); // Desfaz a doação se não tiver estoque
                    return Map.of("erro", "Estoque insuficiente para o produto ID: " + item.getProdId());
                }
            }

            conn.commit(); // Sucesso total
            return Map.of("mensagem", "Doação efetuada com sucesso!", "id", doacaoGravada.getDoaId());

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return Map.of("erro", "Erro ao processar doação: " + e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}