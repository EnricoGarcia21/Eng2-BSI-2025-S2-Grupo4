package DOARC.mvc.control;

import DOARC.mvc.dao.AcertoEstoqueDAO;
import DOARC.mvc.dao.ProdutoDAO;
import DOARC.mvc.model.AcertoEstoque;
import DOARC.mvc.model.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Control com padrão Facade
 * - Responsável por validar dados de entrada
 * - Gerencia acertos de estoque com atualização automática
 * - Valida se o estoque ficará negativo
 */
@Service
public class AcertoEstoqueControl {

    @Autowired
    private AcertoEstoqueDAO acertoEstoqueDAO;

    @Autowired
    private ProdutoDAO produtoDAO;

    // Motivos válidos conforme banco de dados
    private static final List<String> MOTIVOS_VALIDOS = Arrays.asList(
        "Perda", "Ajuste", "Roubo", "Doação", "Vencimento", "Outro"
    );

    // Tipos válidos conforme banco de dados
    private static final List<String> TIPOS_VALIDOS = Arrays.asList(
        "Entrada", "Saída"
    );

    public List<Map<String, Object>> getAcertos() {
        List<AcertoEstoque> lista = acertoEstoqueDAO.getAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (AcertoEstoque a : lista) {
            result.add(toMap(a));
        }
        return result;
    }

    public Map<String, Object> getAcerto(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        AcertoEstoque a = acertoEstoqueDAO.get(id);
        if (a == null) {
            return Map.of("erro", "Acerto não encontrado");
        }

        return toMap(a);
    }

    public List<Map<String, Object>> getAcertosPorProduto(int prodId) {
        List<AcertoEstoque> lista = acertoEstoqueDAO.getPorProduto(prodId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (AcertoEstoque a : lista) {
            result.add(toMap(a));
        }
        return result;
    }

    /**
     * Registra um acerto de estoque
     * @param data Data do acerto (YYYY-MM-DD)
     * @param motivo Motivo do acerto (Perda, Ajuste, Roubo, Doação, Vencimento, Outro)
     * @param observacao Observações detalhadas
     * @param tipo Tipo de acerto (Entrada ou Saída)
     * @param quantidade Quantidade do acerto
     * @param volId ID do voluntário responsável
     * @param prodId ID do produto
     * @return Map com dados do acerto ou erro
     */
    public Map<String, Object> registrarAcerto(String data, String motivo, String observacao,
                                               String tipo, double quantidade, int volId, int prodId) {
        // Validação de entrada
        Map<String, String> validacao = validarDados(data, motivo, observacao, tipo, quantidade, volId, prodId);
        if (!validacao.isEmpty()) {
            return Map.of("erro", validacao.values().iterator().next());
        }

        // Busca o produto para obter o estoque atual
        Produto produto = produtoDAO.get(prodId);
        if (produto == null) {
            return Map.of("erro", "Produto não encontrado");
        }

        int estoqueAtual = produto.getProdQuant();

        // Valida se o estoque ficará negativo em caso de Saída
        if ("Saída".equals(tipo)) {
            if (estoqueAtual - quantidade < 0) {
                return Map.of("erro", String.format(
                    "Estoque insuficiente. Estoque atual: %d, tentando subtrair: %.2f",
                    estoqueAtual, quantidade
                ));
            }
        }

        // Cria o objeto AcertoEstoque
        AcertoEstoque acerto = new AcertoEstoque(
            data,
            motivo,
            observacao != null ? observacao.trim() : "",
            tipo,
            quantidade,
            volId,
            prodId
        );

        // Grava o acerto com atualização de estoque
        AcertoEstoque gravado = acertoEstoqueDAO.gravarAcertoComAtualizacao(acerto, estoqueAtual);
        if (gravado == null) {
            return Map.of("erro", "Erro ao registrar o acerto de estoque");
        }

        Map<String, Object> result = toMap(gravado);
        // Adiciona o estoque atualizado
        Produto produtoAtualizado = produtoDAO.get(prodId);
        if (produtoAtualizado != null) {
            result.put("estoque_novo", produtoAtualizado.getProdQuant());
        }

        return result;
    }

    private Map<String, String> validarDados(String data, String motivo, String observacao,
                                            String tipo, double quantidade, int volId, int prodId) {
        Map<String, String> erros = new HashMap<>();

        if (data == null || data.trim().isEmpty()) {
            erros.put("data", "Data do acerto é obrigatória");
        } else if (!data.matches("\\d{4}-\\d{2}-\\d{2}")) {
            erros.put("data", "Data inválida (formato esperado: YYYY-MM-DD)");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            erros.put("motivo", "Motivo é obrigatório");
        } else if (!MOTIVOS_VALIDOS.contains(motivo)) {
            erros.put("motivo", "Motivo inválido. Valores permitidos: " + String.join(", ", MOTIVOS_VALIDOS));
        }

        if (observacao == null || observacao.trim().isEmpty()) {
            erros.put("observacao", "Observações são obrigatórias");
        } else if (observacao.trim().length() < 10) {
            erros.put("observacao", "Observações devem ter pelo menos 10 caracteres");
        } else if (observacao.length() > 500) {
            erros.put("observacao", "Observações muito longas (máximo 500 caracteres)");
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            erros.put("tipo", "Tipo de acerto é obrigatório");
        } else if (!TIPOS_VALIDOS.contains(tipo)) {
            erros.put("tipo", "Tipo inválido. Valores permitidos: " + String.join(", ", TIPOS_VALIDOS));
        }

        if (quantidade <= 0) {
            erros.put("quantidade", "Quantidade deve ser maior que zero");
        }

        if (volId <= 0) {
            erros.put("voluntario", "Voluntário responsável é obrigatório");
        }

        if (prodId <= 0) {
            erros.put("produto", "Produto é obrigatório");
        }

        return erros;
    }

    private Map<String, Object> toMap(AcertoEstoque a) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", a.getAcId());
        json.put("data", a.getAcData());
        json.put("motivo", a.getAcMotivo());
        json.put("observacao", a.getAcObs());
        json.put("tipo", a.getAcTipo());
        json.put("quantidade", a.getAcQuantidade());
        json.put("voluntario_id", a.getVolId());
        json.put("produto_id", a.getProdId());

        // Adiciona o nome do produto para facilitar a exibição no frontend
        Produto p = produtoDAO.get(a.getProdId());
        if (p != null) {
            json.put("produto_nome", p.getProdNome());
        }

        return json;
    }
}
