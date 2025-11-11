package DOARC.mvc.controller;

import DOARC.mvc.dao.AgendarDoadosDAO;
import DOARC.mvc.dao.VoluntarioDAO;
import DOARC.mvc.dao.DonatarioDAO;
import DOARC.mvc.dao.DoadosProdutoDAO;
import DOARC.mvc.dao.ProdutoDAO;
import DOARC.mvc.model.AgendarDoados;
import DOARC.mvc.model.Donatario;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.model.DoadosProduto;
import DOARC.mvc.model.Produto;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private final AgendarDoadosDAO agendamentoDAO = new AgendarDoadosDAO();
    private final VoluntarioDAO voluntarioDAO = new VoluntarioDAO();
    private final DonatarioDAO donatarioDAO = new DonatarioDAO();
    private final DoadosProdutoDAO doadosProdutoDAO = new DoadosProdutoDAO();
    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private final NotificacaoService notificacaoService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public AgendamentoService(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    private Conexao getConexaoDoSingleton() {
        return SingletonDB.getConexao();
    }

    private LocalDate parseToLocalDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        dateStr = dateStr.split(" ")[0].trim();

        try {
            if (dateStr.contains("/")) {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if (dateStr.contains("-") && dateStr.length() == 10) {
                return LocalDate.parse(dateStr, DATE_FORMATTER);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erro ao parsear data: " + dateStr + " | Motivo: " + e.getMessage());
            return null;
        }
    }

    private LocalTime parseToLocalTime(String timeStr) {
        if (timeStr == null || timeStr.length() < 5) {
            return LocalTime.MIDNIGHT;
        }
        try {
            String cleanTime = timeStr.substring(0, 5) + ":00";
            return LocalTime.parse(cleanTime);
        } catch (Exception e) {
            System.err.println("Erro ao parsear hora: " + timeStr);
            return LocalTime.MIDNIGHT;
        }
    }

    private String getNomeDonatario(int id, Conexao conn) {
        if (id == 0) return "N/A";
        Donatario d = donatarioDAO.get(id, conn);
        return (d != null) ? d.getDonNome(): "Donatário Desconhecido";
    }

    private String getNomeVoluntario(int id, Conexao conn) {
        if (id == 0) return "N/A";
        Voluntario v = voluntarioDAO.get(id, conn);
        return (v != null) ? v.getVolNome() : "Voluntário Desconhecido";
    }

    private List<Map<String, Object>> getProdutosDoacao(int doaId, Conexao conn) {
        List<DoadosProduto> produtosRaw = doadosProdutoDAO.getProdutosPorDoacaoId(doaId, conn);
        List<Map<String, Object>> produtosDetalhe = new ArrayList<>();

        for(DoadosProduto dp : produtosRaw) {
            Produto p = produtoDAO.get(dp.getProdId(), conn);
            if (p != null) {
                Map<String, Object> prodMap = new HashMap<>();
                prodMap.put("nome", p.getProdNome());
                prodMap.put("quantidade", dp.getDpQtde());
                prodMap.put("obs", p.getProdDescricao());
                produtosDetalhe.add(prodMap);
            }
        }
        return produtosDetalhe;
    }

    public List<Map<String, Object>> listarAgendamentosComDetalhes(String filtro) {
        Conexao conn = getConexaoDoSingleton();
        List<AgendarDoados> agendamentos = agendamentoDAO.get(filtro, conn);
        List<Map<String, Object>> resultados = new ArrayList<>();

        for (AgendarDoados ag : agendamentos) {


            String isoDate = (ag.getAgData() != null) ? ag.getAgData().format(DATE_FORMATTER) : null;
            String startTime = (ag.getAgHora() != null) ? ag.getAgHora().format(TIME_FORMATTER) : "00:00";

            if (isoDate == null) continue;


            String fullStart;
            boolean allDay = false;

            if (startTime.equals("00:00")) {
                fullStart = isoDate;
                allDay = true;
            } else {
                fullStart = isoDate + "T" + startTime + ":00";
            }


            String agObs = ag.getAgObs() != null ? ag.getAgObs() : "";
            String donatarioNome = getNomeDonatario(ag.getDonatarioId(), conn);
            String voluntarioNome = getNomeVoluntario(ag.getVoluntarioId(), conn);
            List<Map<String, Object>> produtos = getProdutosDoacao(ag.getDoaId(), conn);


            Map<String, Object> detalhe = new HashMap<>();


            detalhe.put("id", ag.getAgId());
            detalhe.put("title", "Entrega: " + donatarioNome);
            detalhe.put("start", fullStart);
            detalhe.put("allDay", allDay);


            Map<String, Object> propsMap = new HashMap<>();
            propsMap.put("agData", isoDate);
            propsMap.put("agHora", startTime);
            propsMap.put("agObs", agObs);
            propsMap.put("tipo", "entrega");
            propsMap.put("doaId", ag.getDoaId());
            propsMap.put("volId", ag.getVoluntarioId());
            propsMap.put("volNome", voluntarioNome);
            propsMap.put("donatarioDonId", ag.getDonatarioId());
            propsMap.put("produtos", produtos);


            detalhe.put("extendedProps.agData", propsMap.get("agData"));
            detalhe.put("extendedProps.agHora", propsMap.get("agHora"));
            detalhe.put("extendedProps.agObs", propsMap.get("agObs"));
            detalhe.put("extendedProps.tipo", propsMap.get("tipo"));
            detalhe.put("extendedProps.doaId", propsMap.get("doaId"));
            detalhe.put("extendedProps.volId", propsMap.get("volId"));
            detalhe.put("extendedProps.volNome", propsMap.get("volNome"));
            detalhe.put("extendedProps.donatarioDonId", propsMap.get("donatarioDonId"));
            detalhe.put("extendedProps.produtos", propsMap.get("produtos"));

            resultados.add(detalhe);
        }
        return resultados;
    }


    public Map<String, Object> getAgendamentoDetalhe(int ag_id) {
        Conexao conn = getConexaoDoSingleton();

        AgendarDoados ag = agendamentoDAO.get(ag_id, conn);

        if (ag == null) {
            return Map.of("erro", "Agendamento não encontrado com ID: " + ag_id);
        }

        String isoDate = ag.getAgData().format(DATE_FORMATTER);
        String startTime = ag.getAgHora().format(TIME_FORMATTER);

        if (isoDate == null) return Map.of("erro", "Data do agendamento inválida.");

        String fullStart = isoDate + "T" + startTime + ":00";

        Map<String, Object> detalhe = new HashMap<>();
        detalhe.put("id", ag.getAgId());
        detalhe.put("title", "Entrega: " + getNomeDonatario(ag.getDonatarioId(), conn));
        detalhe.put("start", fullStart);
        detalhe.put("agData", isoDate);
        detalhe.put("agHora", startTime);
        detalhe.put("agObs", ag.getAgObs());
        detalhe.put("volId", ag.getVoluntarioId());
        detalhe.put("donatarioDonId", ag.getDonatarioId());
        detalhe.put("produtos", getProdutosDoacao(ag.getDoaId(), conn));

        return detalhe;
    }


    public Map<String, Object> agendarDoados(String ag_data, String ag_hora, String ag_obs, int doa_id, int vol_id, int donatario_don_id) throws SQLException {
        Conexao conn = getConexaoDoSingleton();

        AgendarDoados novo = new AgendarDoados();
        novo.setAgData(parseToLocalDate(ag_data));
        novo.setAgHora(parseToLocalTime(ag_hora));
        novo.setAgObs(ag_obs);
        novo.setDoaId(doa_id);
        novo.setVoluntarioId(vol_id);
        novo.setDonatarioId(donatario_don_id);

        AgendarDoados agendado = agendamentoDAO.gravar(novo, conn);

        if (agendado == null || agendado.getAgId() == 0) {
            return Map.of("erro", "Erro ao gravar o Agendamento.");
        }

        notificacaoService.notificarDonatario(donatario_don_id, "Novo agendamento criado para " + ag_data, vol_id);

        return Map.of("sucesso", "Agendamento criado com ID: " + agendado.getAgId());
    }

    public Map<String, Object> alterarAgendamento(int ag_id, String ag_data, String ag_hora, String ag_obs, int doa_id, int vol_id, int donatario_don_id) {
        Conexao conn = getConexaoDoSingleton();

        Map<String, Object> resultado = new HashMap<>();

        try {
            AgendarDoados existente = agendamentoDAO.get(ag_id, conn);
            if (existente == null) {
                return Map.of("erro", "Agendamento não encontrado com ID: " + ag_id);
            }


            LocalDate novaData = parseToLocalDate(ag_data);
            LocalTime novaHora = parseToLocalTime(ag_hora);

            LocalDate dataFinal = (novaData == null) ? existente.getAgData() : novaData;
            LocalTime horaFinal = (novaHora == null) ? existente.getAgHora() : novaHora;


            int idVoluntario = (vol_id == 0) ? existente.getVoluntarioId() : vol_id;
            int idDoacao = (doa_id == 0) ? existente.getDoaId() : doa_id;
            int idDonatario = (donatario_don_id == 0) ? existente.getDonatarioId() : donatario_don_id;

            existente.setAgData(dataFinal);
            existente.setAgHora(horaFinal);
            existente.setAgObs(ag_obs);
            existente.setDoaId(idDoacao);
            existente.setVoluntarioId(idVoluntario);
            existente.setDonatarioId(idDonatario);

            AgendarDoados atualizado = agendamentoDAO.alterar(existente, conn);

            if (atualizado == null) {
                resultado = Map.of("erro", "Erro ao atualizar o Agendamento (DAO falhou). Verifique a sintaxe SQL do UPDATE no AgendarDoadosDAO.");
            } else {

                if (idVoluntario > 0) {
                    notificacaoService.notificarDonatario(idDonatario, "Agendamento ID " + ag_id + " alterado para " + ag_data, idVoluntario);
                }

                resultado = Map.of("sucesso", "Agendamento com ID " + ag_id + " alterado.");
            }

        } catch (SQLException e) {
            System.err.println("FATAL ERRO SQL DURANTE ALTERAÇÃO: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            resultado = Map.of("erro", "Erro de transação SQL: " + e.getMessage());

        } catch (Exception e) {

            System.err.println("ERRO INTERNO DURANTE ALTERAÇÃO: " + e.getMessage());
            resultado = Map.of("erro", "Erro interno: Falha na conversão de dados ou lógica de serviço. " + e.getMessage());
        }

        return resultado;
    }
    public Map<String, Object> reagendarRapido(int ag_id, String novaData, String novaHora) {
        Conexao conn = getConexaoDoSingleton();

        AgendarDoados existente = agendamentoDAO.get(ag_id, conn);

        if (existente == null) {
            return Map.of("erro", "Agendamento não encontrado para reagendamento.");
        }


        existente.setAgData(parseToLocalDate(novaData));
        existente.setAgHora(parseToLocalTime(novaHora));

        AgendarDoados atualizado = agendamentoDAO.alterar(existente, conn);

        if (atualizado == null) {
            return Map.of("erro", "Erro ao salvar reagendamento.");
        }

        return Map.of("sucesso", "Reagendamento OK.");
    }

    public Map<String, Object> apagarAgendamento(int id) {
        Conexao conn = getConexaoDoSingleton();
        AgendarDoados paraDeletar = agendamentoDAO.get(id, conn);

        if (paraDeletar == null) {
            return Map.of("erro", "Agendamento não encontrado para exclusão com ID: " + id);
        }

        boolean sucesso = agendamentoDAO.apagar(paraDeletar, conn);

        if (!sucesso) {
            return Map.of("erro", "Erro ao apagar o Agendamento.");
        }
        return Map.of("sucesso", "Agendamento com ID " + id + " excluído.");
    }
}