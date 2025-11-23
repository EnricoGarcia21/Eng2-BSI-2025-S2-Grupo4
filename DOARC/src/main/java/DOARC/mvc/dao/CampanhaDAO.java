package DOARC.mvc.dao;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
=======
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

@Repository
public class CampanhaDAO implements IDAO<Campanha> {

    public CampanhaDAO() {}

<<<<<<< HEAD
    // Método auxiliar para converter data
    private java.sql.Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        try {
            return java.sql.Date.valueOf(dateString);
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

=======
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    @Override
    public Campanha gravar(Campanha c, Conexao conexao) {
        String sql = """
            INSERT INTO campanha 
<<<<<<< HEAD
            (cam_data_ini, cam_data_fim, vol_id, cam_desc, 
=======
            (cam_data_ini, cam_data_fim, voluntario_vol_id, cam_desc, 
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
             cam_meta_arrecadacao, cam_valor_arrecadado)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try {
<<<<<<< HEAD
            Connection conn = conexao.getConnect();
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                java.sql.Date dtInicio = parseDate(c.getCam_data_ini());
                java.sql.Date dtFim = parseDate(c.getCam_data_fim());
                long now = System.currentTimeMillis();
                if (dtInicio == null) dtInicio = new java.sql.Date(now);
                if (dtFim == null) dtFim = new java.sql.Date(now);

                stmt.setDate(1, dtInicio);
                stmt.setDate(2, dtFim);
                stmt.setInt(3, c.getVoluntario_vol_id());
                stmt.setString(4, c.getCam_desc());
                stmt.setDouble(5, c.getCam_meta_arrecadacao());
                stmt.setDouble(6, c.getCam_valor_arrecadado());

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            c.setCam_id(rs.getInt(1));
                            return c;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao gravar Campanha: " + e.getMessage());
            e.printStackTrace();
        }
=======
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

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        return null;
    }

    @Override
    public Campanha alterar(Campanha c, Conexao conexao) {
        String sql = """
            UPDATE campanha SET
                cam_data_ini = ?,
                cam_data_fim = ?,
<<<<<<< HEAD
                vol_id = ?,
=======
                voluntario_vol_id = ?,
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                cam_desc = ?,
                cam_meta_arrecadacao = ?,
                cam_valor_arrecadado = ?
            WHERE cam_id = ?
        """;

        try {
            Connection conn = conexao.getConnect();
<<<<<<< HEAD
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                java.sql.Date dtInicio = parseDate(c.getCam_data_ini());
                java.sql.Date dtFim = parseDate(c.getCam_data_fim());

                stmt.setDate(1, dtInicio);
                stmt.setDate(2, dtFim);
                stmt.setInt(3, c.getVoluntario_vol_id());
                stmt.setString(4, c.getCam_desc());
                stmt.setDouble(5, c.getCam_meta_arrecadacao());
                stmt.setDouble(6, c.getCam_valor_arrecadado());
                stmt.setInt(7, c.getCam_id());

                return stmt.executeUpdate() > 0 ? c : null;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao alterar Campanha: " + e.getMessage());
            e.printStackTrace();
        }
=======
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

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        return null;
    }

    @Override
    public boolean apagar(Campanha c, Conexao conexao) {
<<<<<<< HEAD
        // Apagar dependências antes de apagar a campanha
        String sqlFilhos = "DELETE FROM campanha_responsavel WHERE camp_id = ?";
        String sqlPai = "DELETE FROM campanha WHERE cam_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return false;

            try (PreparedStatement stmt = conn.prepareStatement(sqlFilhos)) {
                stmt.setInt(1, c.getCam_id());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlPai)) {
                stmt.setInt(1, c.getCam_id());
                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao apagar Campanha: " + e.getMessage());
            e.printStackTrace();
        }
=======
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

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        return false;
    }

    @Override
    public Campanha get(int id, Conexao conexao) {
        String sql = "SELECT * FROM campanha WHERE cam_id = ?";
<<<<<<< HEAD
        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return mapCampanha(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
=======

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

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        return null;
    }

    @Override
    public List<Campanha> get(String filtro, Conexao conexao) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha";

<<<<<<< HEAD
        if (filtro != null && !filtro.isBlank()) {
            sql += " WHERE cam_desc ILIKE ?";
        }

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return lista;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (filtro != null && !filtro.isBlank()) {
                    stmt.setString(1, "%" + filtro + "%");
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) lista.add(mapCampanha(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar Campanhas: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // ✅ CORREÇÃO PRINCIPAL AQUI
    public List<Campanha> getCampanhasPorVoluntario(int voluntarioId, Conexao conexao) {
        List<Campanha> lista = new ArrayList<>();

        // SQL Atualizado: Busca campanhas onde o usuário é DONO (campanha.vol_id)
        // OU onde o usuário é MEMBRO DA EQUIPE (campanha_responsavel.vol_id)
        String sql = """
            SELECT DISTINCT c.* FROM campanha c
            LEFT JOIN campanha_responsavel cr ON c.cam_id = cr.camp_id
            WHERE c.vol_id = ? OR cr.vol_id = ?
        """;

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return lista;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, voluntarioId); // Para c.vol_id
                stmt.setInt(2, voluntarioId); // Para cr.vol_id

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) lista.add(mapCampanha(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar campanhas por voluntário: " + e.getMessage());
            e.printStackTrace();
        }
=======
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

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        return lista;
    }

    private Campanha mapCampanha(ResultSet rs) throws SQLException {
        Campanha c = new Campanha();
<<<<<<< HEAD
        c.setCam_id(rs.getInt("cam_id"));

        Date dtIni = rs.getDate("cam_data_ini");
        Date dtFim = rs.getDate("cam_data_fim");

        if (dtIni != null) c.setCam_data_ini(dtIni.toString());
        if (dtFim != null) c.setCam_data_fim(dtFim.toString());

        try {
            c.setVoluntario_vol_id(rs.getInt("vol_id"));
        } catch (SQLException e) {
            try { c.setVoluntario_vol_id(rs.getInt("voluntario_vol_id")); } catch (Exception ignored) {}
        }

        c.setCam_desc(rs.getString("cam_desc"));
        c.setCam_meta_arrecadacao(rs.getDouble("cam_meta_arrecadacao"));
        c.setCam_valor_arrecadado(rs.getDouble("cam_valor_arrecadado"));
        return c;
    }
=======

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
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
}