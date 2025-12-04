package DOARC.mvc.dao;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CampanhaDAO {

    public Campanha gravar(Campanha c, Conexao conexao) {
        String sql = "INSERT INTO campanha (cam_data_ini, cam_data_fim, vol_id, cam_desc, cam_meta_arrecadacao, cam_valor_arrecadado) VALUES (?, ?, ?, ?, ?, ?) RETURNING cam_id";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, parseDate(c.getCam_data_ini()));
            stmt.setDate(2, parseDate(c.getCam_data_fim()));
            stmt.setInt(3, c.getVoluntario_vol_id());
            stmt.setString(4, c.getCam_desc());
            stmt.setDouble(5, c.getCam_meta_arrecadacao());
            stmt.setDouble(6, c.getCam_valor_arrecadado());

            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) c.setCam_id(rs.getInt(1));
                return c;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Campanha alterar(Campanha c, Conexao conexao) {
        String sql = "UPDATE campanha SET cam_data_ini=?, cam_data_fim=?, cam_desc=?, cam_meta_arrecadacao=?, cam_valor_arrecadado=? WHERE cam_id=?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setDate(1, parseDate(c.getCam_data_ini()));
            stmt.setDate(2, parseDate(c.getCam_data_fim()));
            stmt.setString(3, c.getCam_desc());
            stmt.setDouble(4, c.getCam_meta_arrecadacao());
            stmt.setDouble(5, c.getCam_valor_arrecadado());
            stmt.setInt(6, c.getCam_id());
            return stmt.executeUpdate() > 0 ? c : null;
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean apagar(Campanha c, Conexao conexao) {
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement("DELETE FROM campanha WHERE cam_id=?")) {
            stmt.setInt(1, c.getCam_id());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Campanha get(int id, Conexao conexao) {
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement("SELECT * FROM campanha WHERE cam_id=?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapCampanha(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Campanha> get(String filtro, Conexao conexao) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha";
        if (filtro != null && !filtro.isEmpty()) sql += " WHERE " + filtro;
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapCampanha(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<Campanha> getCampanhasPorVoluntario(int voluntarioId, Conexao conexao) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT c.* FROM campanha c LEFT JOIN campanha_responsavel cr ON c.cam_id = cr.camp_id WHERE c.vol_id = ? OR cr.vol_id = ?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, voluntarioId);
            stmt.setInt(2, voluntarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapCampanha(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private java.sql.Date parseDate(String dateString) {
        try { return java.sql.Date.valueOf(dateString); } catch (Exception e) { return null; }
    }

    private Campanha mapCampanha(ResultSet rs) throws SQLException {
        Campanha c = new Campanha();
        c.setCam_id(rs.getInt("cam_id"));
        c.setCam_data_ini(String.valueOf(rs.getDate("cam_data_ini")));
        c.setCam_data_fim(String.valueOf(rs.getDate("cam_data_fim")));
        c.setVoluntario_vol_id(rs.getInt("vol_id"));
        c.setCam_desc(rs.getString("cam_desc"));
        c.setCam_meta_arrecadacao(rs.getDouble("cam_meta_arrecadacao"));
        c.setCam_valor_arrecadado(rs.getDouble("cam_valor_arrecadado"));
        return c;
    }
}