package DOARC.mvc.dao;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class CampanhaDAO implements IDAO<Campanha> {

    public CampanhaDAO() {}

    @Override
    public Campanha gravar(Campanha c, Conexao conexao) {
        String sql = """
            INSERT INTO campanha 
            (cam_data_ini, cam_data_fim, voluntario_vol_id, cam_desc, 
             cam_meta_arrecadacao, cam_valor_arrecadado)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try {
            // Obtém a conexão JDBC
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, c.getCam_data_ini());
            stmt.setString(2, c.getCam_data_fim());
            stmt.setInt(3, c.getVoluntario_vol_id());
            stmt.setString(4, c.getCam_desc());
            stmt.setDouble(5, c.getCam_meta_arrecadacao());
            stmt.setDouble(6, c.getCam_valor_arrecadado());

            // Usa executeUpdate() para INSERT
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Obtém o ID gerado
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    c.setCam_id(rs.getInt(1));
                    rs.close();
                    stmt.close();
                    return c;
                }
                rs.close();
            }

            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao gravar Campanha: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Campanha alterar(Campanha c, Conexao conexao) {
        String sql = """
            UPDATE campanha SET
                cam_data_ini = ?,
                cam_data_fim = ?,
                voluntario_vol_id = ?,
                cam_desc = ?,
                cam_meta_arrecadacao = ?,
                cam_valor_arrecadado = ?
            WHERE cam_id = ?
        """;

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, c.getCam_data_ini());
            stmt.setString(2, c.getCam_data_fim());
            stmt.setInt(3, c.getVoluntario_vol_id());
            stmt.setString(4, c.getCam_desc());
            stmt.setDouble(5, c.getCam_meta_arrecadacao());
            stmt.setDouble(6, c.getCam_valor_arrecadado());
            stmt.setInt(7, c.getCam_id());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0 ? c : null;

        } catch (SQLException e) {
            System.err.println("Erro ao alterar Campanha: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean apagar(Campanha c, Conexao conexao) {
        String sql = "DELETE FROM campanha WHERE cam_id = ?";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, c.getCam_id());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao apagar Campanha: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Campanha get(int id, Conexao conexao) {
        String sql = "SELECT * FROM campanha WHERE cam_id = ?";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Campanha c = mapCampanha(rs);
                rs.close();
                stmt.close();
                return c;
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao buscar Campanha: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Campanha> get(String filtro, Conexao conexao) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt;

            if (filtro != null && !filtro.isBlank()) {
                sql += " WHERE cam_desc ILIKE ? OR CAST(cam_data_ini AS TEXT) ILIKE ? OR CAST(cam_data_fim AS TEXT) ILIKE ?";
                stmt = conn.prepareStatement(sql);
                String filtroLike = "%" + filtro + "%";
                stmt.setString(1, filtroLike);
                stmt.setString(2, filtroLike);
                stmt.setString(3, filtroLike);
            } else {
                stmt = conn.prepareStatement(sql);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapCampanha(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao listar Campanhas: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    private Campanha mapCampanha(ResultSet rs) throws SQLException {
        Campanha c = new Campanha();

        c.setCam_id(rs.getInt("cam_id"));
        c.setCam_data_ini(rs.getString("cam_data_ini"));
        c.setCam_data_fim(rs.getString("cam_data_fim"));
        c.setVoluntario_vol_id(rs.getInt("voluntario_vol_id"));
        c.setCam_desc(rs.getString("cam_desc"));
        c.setCam_meta_arrecadacao(rs.getDouble("cam_meta_arrecadacao"));
        c.setCam_valor_arrecadado(rs.getDouble("cam_valor_arrecadado"));

        return c;
    }

    public List<Campanha> getCampanhasPorVoluntario(int voluntarioId, Conexao conexao) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha WHERE voluntario_vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, voluntarioId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapCampanha(rs));
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao buscar campanhas por voluntário: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }
}