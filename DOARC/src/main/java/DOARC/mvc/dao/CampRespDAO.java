package DOARC.mvc.dao;

import DOARC.mvc.model.CampResponsavel;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CampRespDAO {

    public CampRespDAO() {}

    // Método para converter String para java.sql.Date
    private java.sql.Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            // Tenta converter do formato "dd-MM-yyyy"
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            java.util.Date parsed = format.parse(dateString);
            return new java.sql.Date(parsed.getTime());
        } catch (Exception e) {
            try {
                // Tenta converter do formato "yyyy-MM-dd" (formato SQL)
                return java.sql.Date.valueOf(dateString);
            } catch (Exception e2) {
                System.err.println("Erro ao converter data: " + dateString);
                return null;
            }
        }
    }

    public List<CampResponsavel> get(String filtro, Conexao conexao) {
        List<CampResponsavel> lista = new ArrayList<>();
        String sql = "SELECT * FROM camp_responsavel";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt;

            if (filtro != null && !filtro.isBlank()) {
                sql += " WHERE " + filtro;
                stmt = conn.prepareStatement(sql);
            } else {
                stmt = conn.prepareStatement(sql);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapCampResponsavel(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao listar CampResponsavel: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public CampResponsavel gravar(CampResponsavel cr, Conexao conexao) {
        String sql = """
            INSERT INTO camp_responsavel 
            (cam_id, voluntario_vol_id, DATA_INICIO, DATA_FIM, Obs_texto)
            VALUES (?, ?, ?, ?, ?)
        """;

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Converter strings para Date
            java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
            java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

            if (dataInicio == null || dataFim == null) {
                System.err.println("Erro: formato de data inválido");
                return null;
            }

            stmt.setInt(1, cr.getCam_id());
            stmt.setInt(2, cr.getVoluntario_vol_id());
            stmt.setDate(3, dataInicio);  // CORREÇÃO: usar setDate
            stmt.setDate(4, dataFim);     // CORREÇÃO: usar setDate
            stmt.setString(5, cr.getObs_texto());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0 ? cr : null;

        } catch (SQLException e) {
            System.err.println("Erro ao gravar CampResponsavel: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public CampResponsavel alterar(CampResponsavel cr, Conexao conexao) {
        String sql = """
            UPDATE camp_responsavel SET
                DATA_INICIO = ?,
                DATA_FIM = ?,
                Obs_texto = ?
            WHERE cam_id = ? AND voluntario_vol_id = ?
        """;

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Converter strings para Date
            java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
            java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

            if (dataInicio == null || dataFim == null) {
                System.err.println("Erro: formato de data inválido");
                return null;
            }

            stmt.setDate(1, dataInicio);  // CORREÇÃO: usar setDate
            stmt.setDate(2, dataFim);     // CORREÇÃO: usar setDate
            stmt.setString(3, cr.getObs_texto());
            stmt.setInt(4, cr.getCam_id());
            stmt.setInt(5, cr.getVoluntario_vol_id());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0 ? cr : null;

        } catch (SQLException e) {
            System.err.println("Erro ao alterar CampResponsavel: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean apagar(CampResponsavel cr, Conexao conexao) {
        String sql = "DELETE FROM camp_responsavel WHERE cam_id = ? AND voluntario_vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cr.getCam_id());
            stmt.setInt(2, cr.getVoluntario_vol_id());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao apagar CampResponsavel: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public List<CampResponsavel> getCampanhasPorVoluntario(int voluntarioId, Conexao conexao) {
        List<CampResponsavel> lista = new ArrayList<>();
        String sql = "SELECT * FROM camp_responsavel WHERE voluntario_vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, voluntarioId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapCampResponsavel(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao buscar campanhas por voluntário: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    private CampResponsavel mapCampResponsavel(ResultSet rs) throws SQLException {
        CampResponsavel cr = new CampResponsavel();

        cr.setCam_id(rs.getInt("cam_id"));
        cr.setVoluntario_vol_id(rs.getInt("voluntario_vol_id"));

        // Converter Date para String no formato dd-MM-yyyy
        Date dataInicio = rs.getDate("DATA_INICIO");
        Date dataFim = rs.getDate("DATA_FIM");

        if (dataInicio != null) {
            cr.setDATA_INICIO(new SimpleDateFormat("dd-MM-yyyy").format(dataInicio));
        }
        if (dataFim != null) {
            cr.setDATA_FIM(new SimpleDateFormat("dd-MM-yyyy").format(dataFim));
        }

        cr.setObs_texto(rs.getString("Obs_texto"));

        return cr;
    }
}