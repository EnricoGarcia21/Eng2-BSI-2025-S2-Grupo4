package DOARC.mvc.dao;

import DOARC.mvc.model.AgendarDoados;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AgendarDoadosDAO implements IDAO<AgendarDoados>{

    private static final DateTimeFormatter DATE_FORMATTER_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Tenta parsear a String do DB (BR ou ISO) para LocalDate.
     */
    private LocalDate parseDateFromDb(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return null;
        dateStr = dateStr.trim().split(" ")[0];
        try {
            if (dateStr.contains("/")) {
                return LocalDate.parse(dateStr, DATE_FORMATTER_BR);
            } else if (dateStr.contains("-")) {
                return LocalDate.parse(dateStr, DATE_FORMATTER_ISO);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Falha ao parsear data do DB: " + dateStr + " | Erro: " + e.getMessage());
            return null;
        }
    }

    private AgendarDoados mapAgendamento(ResultSet rs) throws SQLException {
        AgendarDoados ag = new AgendarDoados();
        ag.setAgId(rs.getInt("ag_id"));


        ag.setAgData(parseDateFromDb(rs.getString("ag_data")));

        String horaStr = rs.getString("ag_hora");
        if (horaStr != null && horaStr.length() >= 5) {

            ag.setAgHora(LocalTime.parse(horaStr.substring(0, 5)));
        } else {
            ag.setAgHora(LocalTime.MIDNIGHT);
        }

        ag.setAgObs(rs.getString("ag_obs"));
        ag.setDoaId(rs.getInt("doa_id"));
        ag.setVoluntarioId(rs.getInt("vol_id"));
        ag.setDonatarioId(rs.getInt("donatario_don_id"));
        return ag;
    }


    @Override
    public AgendarDoados gravar(AgendarDoados entidade, Conexao conn) {
        String sql = "INSERT INTO agendar_doados (ag_data, ag_hora, ag_obs, doa_id, vol_id, donatario_don_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING ag_id";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {

            pst.setString(1, entidade.getAgData().format(DATE_FORMATTER_ISO));
            pst.setString(2, entidade.getAgHora().format(DateTimeFormatter.ofPattern("HH:mm")));


            pst.setString(3, entidade.getAgObs());
            pst.setInt(4, entidade.getDoaId());
            pst.setInt(5, entidade.getVoluntarioId());
            pst.setInt(6, entidade.getDonatarioId());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                entidade.setAgId(rs.getInt("ag_id"));
                conn.commit();
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Agendamento: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return null;
    }


    @Override
    public AgendarDoados alterar(AgendarDoados entidade, Conexao conn) {
        String sql = "UPDATE agendar_doados SET ag_data=?, ag_hora=?, ag_obs=?, doa_id=?, vol_id=?, donatario_don_id=? WHERE ag_id=?";

        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {


            String dataISO = entidade.getAgData() != null
                    ? entidade.getAgData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : null;
            String horaLimpa = entidade.getAgHora() != null
                    ? entidade.getAgHora().format(DateTimeFormatter.ofPattern("HH:mm"))
                    : null;


            if (dataISO == null) {
                pst.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pst.setString(1, dataISO);
            }

            if (horaLimpa == null) {
                pst.setNull(2, java.sql.Types.VARCHAR);
            } else {
                pst.setString(2, horaLimpa);
            }

            pst.setString(3, entidade.getAgObs());


            if (entidade.getDoaId() == 0) {
                pst.setNull(4, java.sql.Types.INTEGER);
            } else {
                pst.setInt(4, entidade.getDoaId());
            }

            if (entidade.getVoluntarioId() == 0) {
                pst.setNull(5, java.sql.Types.INTEGER);
            } else {
                pst.setInt(5, entidade.getVoluntarioId());
            }

            if (entidade.getDonatarioId() == 0) {
                pst.setNull(6, java.sql.Types.INTEGER);
            } else {
                pst.setInt(6, entidade.getDonatarioId());
            }


            pst.setInt(7, entidade.getAgId());

            int linhasAfetadas = pst.executeUpdate();

            if (linhasAfetadas > 0) {
                conn.commit();
                return entidade;
            }

            conn.rollback();

        } catch (SQLException e) {
            System.err.println("FATAL SQL EXCEPTION (UPDATE): " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return null;
    }


    @Override
    public boolean apagar(AgendarDoados entidade, Conexao conn) {
        String sql = "DELETE FROM agendar_doados WHERE ag_id=?";

        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, entidade.getAgId());

            int linhasAfetadas = pst.executeUpdate();

            if (linhasAfetadas > 0) {
                conn.commit();
                return true;
            }

            conn.rollback();

        } catch (SQLException e) {
            System.err.println("Erro ao apagar Agendamento: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return false;
    }

    @Override
    public AgendarDoados get(int id, Conexao conn) {
        String sql = "SELECT * FROM agendar_doados WHERE ag_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapAgendamento(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Agendamento por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<AgendarDoados> get(String filtro, Conexao conn) {
        List<AgendarDoados> lista = new ArrayList<>();
        String sql = "SELECT * FROM agendar_doados";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (Statement st = conn.getConnect().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapAgendamento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar lista de Agendamentos: " + e.getMessage());
        }
        return lista;
    }
}