package DOARC.mvc.controller;

import DOARC.mvc.model.Doacao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoacaoController {
    @Autowired
    private Doacao doacaoModel;

    public Map<String, Object> receberDoacao(int volId, String dataDoacao, String obsDoacao,
                                             BigDecimal valorDoado, int doaId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                // Validações
                if (valorDoado == null || valorDoado.compareTo(BigDecimal.ZERO) <= 0) {
                    json.put("erro", "Valor da doação deve ser maior que zero");
                    return json;
                }

                Doacao doacao = new Doacao(volId, dataDoacao, obsDoacao, valorDoado, doaId);
                Doacao resultado = doacaoModel.gravar(doacao, conexao);

                if (resultado != null) {
                    json.put("doacaoId", resultado.getDoacaoId());
                    json.put("volId", resultado.getVolId());
                    json.put("dataDoacao", resultado.getDataDoacao());
                    json.put("obsDoacao", resultado.getObsDoacao());
                    json.put("valorDoado", resultado.getValorDoado());
                    json.put("doaId", resultado.getDoaId());
                    json.put("mensagem", "Doação recebida com sucesso!");
                } else {
                    json.put("erro", "Erro ao receber doação");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao receber doação: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> listarDoacoes() {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Doacao> doacoes = doacaoModel.get(null, conexao);
                json.put("doacoes", doacoes);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao listar doações: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> buscarDoacao(int doacaoId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                Doacao doacao = doacaoModel.get(doacaoId, conexao);
                if (doacao != null) {
                    json.put("doacaoId", doacao.getDoacaoId());
                    json.put("volId", doacao.getVolId());
                    json.put("dataDoacao", doacao.getDataDoacao());
                    json.put("obsDoacao", doacao.getObsDoacao());
                    json.put("valorDoado", doacao.getValorDoado());
                    json.put("doaId", doacao.getDoaId());
                } else {
                    json.put("erro", "Doação não encontrada");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar doação: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> atualizarDoacao(int doacaoId, int volId, String dataDoacao, String obsDoacao,
                                               BigDecimal valorDoado, int doaId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                // Validações
                if (valorDoado == null || valorDoado.compareTo(BigDecimal.ZERO) <= 0) {
                    json.put("erro", "Valor da doação deve ser maior que zero");
                    return json;
                }

                Doacao doacao = new Doacao(doacaoId, volId, dataDoacao, obsDoacao, valorDoado, doaId);
                Doacao resultado = doacaoModel.alterar(doacao, conexao);

                if (resultado != null) {
                    json.put("doacaoId", resultado.getDoacaoId());
                    json.put("volId", resultado.getVolId());
                    json.put("dataDoacao", resultado.getDataDoacao());
                    json.put("obsDoacao", resultado.getObsDoacao());
                    json.put("valorDoado", resultado.getValorDoado());
                    json.put("doaId", resultado.getDoaId());
                    json.put("mensagem", "Doação atualizada com sucesso!");
                } else {
                    json.put("erro", "Erro ao atualizar doação");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao atualizar doação: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> excluirDoacao(int doacaoId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                Doacao doacao = new Doacao();
                doacao.setDoacaoId(doacaoId);
                boolean resultado = doacaoModel.apagar(doacao, conexao);

                if (resultado) {
                    json.put("mensagem", "Doação excluída com sucesso!");
                } else {
                    json.put("erro", "Erro ao excluir doação");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao excluir doação: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> listarDoacoesPorVoluntario(int volId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Doacao> doacoes = doacaoModel.getPorVoluntario(volId, conexao);
                json.put("doacoes", doacoes);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao listar doações por voluntário: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> listarDoacoesPorDoador(int doaId) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Doacao> doacoes = doacaoModel.getPorDoador(doaId, conexao);
                json.put("doacoes", doacoes);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao listar doações por doador: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    public Map<String, Object> getTotalDoacoesPeriodo(String dataInicio, String dataFim) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                BigDecimal total = doacaoModel.getTotalDoacoesPeriodo(dataInicio, dataFim, conexao);
                json.put("total", total);
                json.put("periodo", dataInicio + " a " + dataFim);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar total de doações: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }
}