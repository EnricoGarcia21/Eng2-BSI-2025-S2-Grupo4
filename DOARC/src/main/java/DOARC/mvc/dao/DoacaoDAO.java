package DOARC.mvc.dao;

import DOARC.mvc.model.Doacao;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoacaoDAO {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Doacao mapDoacao(ResultSet rs) throws SQLException {
        Doacao d = new Doacao();
        d.setDoacaoId(rs.getInt("doacao_id"));
        d.setVolId(rs.getInt("vol_id"));
        d.setDoaId(rs.getInt("doa_id"));
        d.setObsDoacao(rs.getString("obs_doacao"));
        d.setValorDoado(rs.getBigDecimal("valor_doado"));

        String dataDoacaoStr = rs.getString("data_doacao");
        if (dataDoacaoStr != null) {
            try {
                if (dataDoacaoStr.contains("/")) {
                    String[] parts = dataDoacaoStr.split("/");
                    String isoDate = parts[2] + "-" + parts[1] + "-" + parts[0];
                    d.setDataDoacao(LocalDate.parse(isoDate, DATE_FORMATTER));
                } else if (dataDoacaoStr.contains("-")) {
                    d.setDataDoacao(LocalDate.parse(dataDoacaoStr.trim(), DATE_FORMATTER));
                }
            } catch (Exception e) {
                System.err.println("Erro de Parse de DATA_DOACAO: " + dataDoacaoStr + " | Erro: " + e.getMessage());
            }
        }

        return d;
    }

    /**
     * Busca todas as doações (TABELA 15) que podem ser agendadas.
     * Retorna apenas itens básicos para o dropdown do frontend.
     */
    public List<Doacao> getDoacoesDisponiveis(Conexao conn) {
        List<Doacao> lista = new ArrayList<>();

        String sql = "SELECT doacao_id, vol_id, data_doacao, obs_doacao, valor_doado, doa_id FROM Doacao";

        try (Statement st = conn.getConnect().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapDoacao(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar doações disponíveis: " + e.getMessage());
        }
        return lista;
    }
}