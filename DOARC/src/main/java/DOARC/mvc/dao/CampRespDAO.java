package DOARC.mvc.dao;

import DOARC.mvc.model.CampResponsavel;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Repository
public class CampRespDAO {

    public CampRespDAO() {}

    private java.sql.Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsed = format.parse(dateString);
            return new java.sql.Date(parsed.getTime());
        } catch (Exception e) {
            try {
                SimpleDateFormat formatBR = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsed = formatBR.parse(dateString);
                return new java.sql.Date(parsed.getTime());
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public List<CampResponsavel> get(String filtro) {
        List<CampResponsavel> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha_responsavel";
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            if (conn == null || conn.isClosed()) return lista;

            String queryFinal = sql;
            if (filtro != null && !filtro.isBlank()) {
                queryFinal += " WHERE " + filtro;
            }

            try (PreparedStatement stmt = conn.prepareStatement(queryFinal);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapCampResponsavel(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public List<CampResponsavel> getByCompositeKey(int camId, int voluntarioId) {
        List<CampResponsavel> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha_responsavel WHERE camp_id = ? AND vol_id = ?";
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            if (conn == null || conn.isClosed()) return lista;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, camId);
                stmt.setInt(2, voluntarioId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapCampResponsavel(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar por chave composta: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public CampResponsavel gravar(CampResponsavel cr) {
        String sql = """
            INSERT INTO campanha_responsavel 
            (camp_id, vol_id, data_inicio, data_fim, obs_texto)
            VALUES (?, ?, ?, ?, ?)
        """;
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            if (conn == null || conn.isClosed()) {
                System.err.println("Erro: Conexão fechada ou nula no gravar.");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
                java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

                if (dataInicio == null) dataInicio = new java.sql.Date(System.currentTimeMillis());
                if (dataFim == null) dataFim = new java.sql.Date(System.currentTimeMillis());

                stmt.setInt(1, cr.getCam_id());
                stmt.setInt(2, cr.getVoluntario_vol_id());
                stmt.setDate(3, dataInicio);
                stmt.setDate(4, dataFim);
                stmt.setString(5, cr.getObs_texto());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0 ? cr : null;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public CampResponsavel alterar(CampResponsavel cr) {
        String sql = """
            UPDATE campanha_responsavel SET
                data_inicio = ?,
                data_fim = ?,
                obs_texto = ?
            WHERE camp_id = ? AND vol_id = ?
        """;
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
                java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

                stmt.setDate(1, dataInicio);
                stmt.setDate(2, dataFim);
                stmt.setString(3, cr.getObs_texto());
                stmt.setInt(4, cr.getCam_id());
                stmt.setInt(5, cr.getVoluntario_vol_id());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0 ? cr : null;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean apagar(CampResponsavel cr) {
        String sql = "DELETE FROM campanha_responsavel WHERE camp_id = ? AND vol_id = ?";
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, cr.getCam_id());
                stmt.setInt(2, cr.getVoluntario_vol_id());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao apagar: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<CampResponsavel> getCampanhasPorVoluntario(int voluntarioId) {
        List<CampResponsavel> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha_responsavel WHERE vol_id = ?";
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, voluntarioId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapCampResponsavel(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar por voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private CampResponsavel mapCampResponsavel(ResultSet rs) throws SQLException {
        CampResponsavel cr = new CampResponsavel();

        cr.setCam_id(rs.getInt("camp_id"));
        cr.setVoluntario_vol_id(rs.getInt("vol_id"));

        Date dataInicio = rs.getDate("data_inicio");
        Date dataFim = rs.getDate("data_fim");

        if (dataInicio != null) {
            cr.setDATA_INICIO(new SimpleDateFormat("yyyy-MM-dd").format(dataInicio));
        }
        if (dataFim != null) {
            cr.setDATA_FIM(new SimpleDateFormat("yyyy-MM-dd").format(dataFim));
        }

        cr.setObs_texto(rs.getString("obs_texto"));
        return cr;
    }
}