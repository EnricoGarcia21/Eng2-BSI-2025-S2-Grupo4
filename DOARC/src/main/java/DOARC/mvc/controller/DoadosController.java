package DOARC.mvc.controller;

import DOARC.mvc.dao.DoadosDAO;
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

    private final DoadosDAO doadosDAO;

    public DoadosController(DoadosDAO doadosDAO) {
        this.doadosDAO = doadosDAO;
    }

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public Map<String, Object> efetuarDoacao(Map<String, Object> payload) {
        try {
            // --- 1. Monta objeto principal (DOADOS) ---
            Doados doacao = new Doados();
            doacao.setDonId((Integer) payload.get("donatarioId"));
            doacao.setVolId((Integer) payload.get("voluntarioId"));
            doacao.setObsDoado((String) payload.get("observacoes"));
            doacao.setDoaTipoDoacao((String) payload.get("tipoDoacao"));
            doacao.setValorDoacao(BigDecimal.valueOf(((Number) payload.get("valorDoacao")).doubleValue()));
            doacao.setDoaDataAquisicao(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // --- 2. Monta produtos ---
            List<Map<String, Object>> produtosPayload = (List<Map<String, Object>>) payload.get("produtos");
            if (produtosPayload == null || produtosPayload.isEmpty()) {
                return Map.of("erro", "A lista de produtos não pode estar vazia.");
            }

            List<DoadosProduto> produtosList = produtosPayload.stream()
                    .map(item -> {
                        DoadosProduto dp = new DoadosProduto();
                        dp.setProdId((Integer) item.get("prodId"));
                        dp.setDpQtde(new BigDecimal(((Number) item.get("quantidade")).intValue()));
                        return dp;
                    })
                    .collect(Collectors.toList());

            doacao.setProdutos(produtosList);

            // --- 3. Transação controlada pela Controller ---
            Conexao conexao = getConexao();
            Connection conn = conexao.getConnect();

            try {
                conn.setAutoCommit(false);

                Doados doacaoGravada = doadosDAO.gravar(doacao, conexao);

                conn.commit();

                if (doacaoGravada == null) {
                    return Map.of("erro", "Falha ao processar doação.");
                }

                return Map.of("mensagem", "Doação efetuada com sucesso!", "id", doacaoGravada.getDoaId());

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return Map.of("erro", "Erro ao processar doação: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("erro", "Erro inesperado no servidor: " + e.getMessage());
        }
    }
}
