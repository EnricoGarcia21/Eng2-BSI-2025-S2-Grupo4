package DOARC.mvc.dao;

import DOARC.mvc.model.CampResponsavel;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CampRespDAO {

    public CampResponsavel gravar(CampResponsavel obj, Conexao conexao) {
        String sql = "INSERT INTO campanha_responsavel (camp_id, vol_id, data_inicio, data_fim, obs_texto) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, obj.getCam_id());
            stmt.setInt(2, obj.getVoluntario_vol_id());
            stmt.setDate(3, java.sql.Date.valueOf(obj.getDATA_INICIO()));
            stmt.setDate(4, java.sql.Date.valueOf(obj.getDATA_FIM()));
            stmt.setString(5, obj.getObs_texto());
            stmt.executeUpdate();
            return obj;
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public CampResponsavel alterar(CampResponsavel obj, Conexao conexao) {
        String sql = "UPDATE campanha_responsavel SET data_inicio=?, data_fim=?, obs_texto=? WHERE camp_id=? AND vol_id=?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(obj.getDATA_INICIO()));
            stmt.setDate(2, java.sql.Date.valueOf(obj.getDATA_FIM()));
            stmt.setString(3, obj.getObs_texto());
            stmt.setInt(4, obj.getCam_id());
            stmt.setInt(5, obj.getVoluntario_vol_id());
            return stmt.executeUpdate() > 0 ? obj : null;
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean apagar(CampResponsavel obj, Conexao conexao) {
        String sql = "DELETE FROM campanha_responsavel WHERE camp_id=? AND vol_id=?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, obj.getCam_id());
            stmt.setInt(2, obj.getVoluntario_vol_id());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<CampResponsavel> getPorChave(int camId, int volId, Conexao conexao) {
        String sql = "SELECT * FROM campanha_responsavel WHERE camp_id=? AND vol_id=?";
        List<CampResponsavel> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, camId);
            stmt.setInt(2, volId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<CampResponsavel> get(String filtro, Conexao conexao) {
        List<CampResponsavel> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha_responsavel";
        if(filtro != null && !filtro.isEmpty()) sql += " WHERE " + filtro;
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public List<CampResponsavel> getCampanhasPorVoluntario(int voluntarioId, Conexao conexao) {
        String sql = "SELECT * FROM campanha_responsavel WHERE vol_id=?";
        List<CampResponsavel> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, voluntarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    private CampResponsavel map(ResultSet rs) throws SQLException {
        CampResponsavel c = new CampResponsavel();
        c.setCam_id(rs.getInt("camp_id"));
        c.setVoluntario_vol_id(rs.getInt("vol_id"));
        c.setDATA_INICIO(String.valueOf(rs.getDate("data_inicio")));
        c.setDATA_FIM(String.valueOf(rs.getDate("data_fim")));
        c.setObs_texto(rs.getString("obs_texto"));
        return c;
    }
}