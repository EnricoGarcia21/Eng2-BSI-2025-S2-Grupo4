package DOARC.mvc.dao;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CampanhaDAO implements IDAO<Campanha> {

    private Connection conn;

    public CampanhaDAO() {
        conn = SingletonDB.getConexao().getConnect();
    }

    @Override
    public Campanha gravar(Campanha entidade) {
        // Verificar se o voluntário existe
        if (!voluntarioExiste(entidade.getVoluntario_vol_id())) {
            throw new RuntimeException("Voluntário não encontrado com ID: " + entidade.getVoluntario_vol_id());
        }

        String sql = "INSERT INTO campanha (cam_data_ini, cam_data_fim, voluntario_vol_id, cam_desc, cam_meta_arrecadacao, cam_valor_arrecadado) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING cam_id";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            // Usando setString para datas (assumindo que o banco aceita strings no formato DATE)
            pst.setString(1, entidade.getCam_data_ini());
            pst.setString(2, entidade.getCam_data_fim());
            pst.setInt(3, entidade.getVoluntario_vol_id());
            pst.setString(4, entidade.getCam_desc());
            pst.setDouble(5, entidade.getCam_meta_arrecadacao());
            pst.setDouble(6, entidade.getCam_valor_arrecadado());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                entidade.setCam_id(rs.getInt("cam_id"));
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao cadastrar campanha: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Campanha alterar(Campanha entidade) {
        // Verificar se a campanha existe
        if (get(entidade.getCam_id()) == null) {
            throw new RuntimeException("Campanha não encontrada com ID: " + entidade.getCam_id());
        }

        // Verificar se o voluntário existe
        if (!voluntarioExiste(entidade.getVoluntario_vol_id())) {
            throw new RuntimeException("Voluntário não encontrado com ID: " + entidade.getVoluntario_vol_id());
        }

        String sql = "UPDATE campanha SET cam_data_ini=?, cam_data_fim=?, voluntario_vol_id=?, cam_desc=?, cam_meta_arrecadacao=?, cam_valor_arrecadado=? " +
                "WHERE cam_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            // Usando setString para datas
            pst.setString(1, entidade.getCam_data_ini());
            pst.setString(2, entidade.getCam_data_fim());
            pst.setInt(3, entidade.getVoluntario_vol_id());
            pst.setString(4, entidade.getCam_desc());
            pst.setDouble(5, entidade.getCam_meta_arrecadacao());
            pst.setDouble(6, entidade.getCam_valor_arrecadado());
            pst.setInt(7, entidade.getCam_id());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao atualizar campanha: " + e.getMessage());
        }
    }

    @Override
    public boolean apagar(Campanha entidade) {
        String sql = "DELETE FROM campanha WHERE cam_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, entidade.getCam_id());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao excluir campanha: " + e.getMessage());
        }
    }

    @Override
    public Campanha get(int id) {
        String sql = "SELECT * FROM campanha WHERE cam_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapCampanha(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Campanha> get(String filtro) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapCampanha(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Campanha mapCampanha(ResultSet rs) throws SQLException {
        Campanha c = new Campanha();
        c.setCam_id(rs.getInt("cam_id"));

        // Mantendo como String - pegando diretamente como string do resultado
        c.setCam_data_ini(rs.getString("cam_data_ini"));
        c.setCam_data_fim(rs.getString("cam_data_fim"));

        c.setVoluntario_vol_id(rs.getInt("voluntario_vol_id"));
        c.setCam_desc(rs.getString("cam_desc"));
        c.setCam_meta_arrecadacao(rs.getDouble("cam_meta_arrecadacao"));
        c.setCam_valor_arrecadado(rs.getDouble("cam_valor_arrecadado"));
        return c;
    }

    // Método para verificar se o voluntário existe
    private boolean voluntarioExiste(int voluntarioId) {
        String sql = "SELECT 1 FROM voluntario WHERE vol_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, voluntarioId);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método adicional para buscar campanhas por voluntário
    public List<Campanha> getCampanhasPorVoluntario(int voluntarioId) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha WHERE voluntario_vol_id = ?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, voluntarioId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapCampanha(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}