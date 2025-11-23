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

<<<<<<< HEAD
    // Método auxiliar para converter String para java.sql.Date
=======
    // Método para converter String para java.sql.Date
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    private java.sql.Date parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
<<<<<<< HEAD
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
=======
            // Tenta converter do formato "dd-MM-yyyy"
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            java.util.Date parsed = format.parse(dateString);
            return new java.sql.Date(parsed.getTime());
        } catch (Exception e) {
            try {
<<<<<<< HEAD
                SimpleDateFormat formatBR = new SimpleDateFormat("dd/MM/yyyy");
                java.util.Date parsed = formatBR.parse(dateString);
                return new java.sql.Date(parsed.getTime());
            } catch (Exception e2) {
=======
                // Tenta converter do formato "yyyy-MM-dd" (formato SQL)
                return java.sql.Date.valueOf(dateString);
            } catch (Exception e2) {
                System.err.println("Erro ao converter data: " + dateString);
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                return null;
            }
        }
    }

    public List<CampResponsavel> get(String filtro, Conexao conexao) {
        List<CampResponsavel> lista = new ArrayList<>();
<<<<<<< HEAD
        String sql = "SELECT * FROM campanha_responsavel"; // Nome correto da tabela

        try {
            Connection conn = conexao.getConnect();
            if (conn == null || conn.isClosed()) return lista;

            PreparedStatement stmt;

            if (filtro != null && !filtro.isBlank()) {
=======
        String sql = "SELECT * FROM camp_responsavel";

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt;

            if (filtro != null && !filtro.isBlank()) {
                // AVISO: MANTIDO PARA COMPATIBILIDADE, MAS O CONTROLLER DEVE GARANTIR A SEGURANÇA.
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
            // NÃO fechar a conexão aqui

        } catch (SQLException e) {
            System.err.println("Erro ao listar: " + e.getMessage());
=======

        } catch (SQLException e) {
            System.err.println("Erro ao listar CampResponsavel: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }

        return lista;
    }

<<<<<<< HEAD
    // ✅ CORREÇÃO AQUI: Não fechar a conexão no try-with-resources
    public List<CampResponsavel> getByCompositeKey(int camId, int voluntarioId, Conexao conexao) {
        List<CampResponsavel> lista = new ArrayList<>();
        // Usando nomes das colunas do banco: camp_id e vol_id
        String sql = "SELECT * FROM campanha_responsavel WHERE camp_id = ? AND vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null || conn.isClosed()) return lista;

            // Fechar apenas o PreparedStatement e ResultSet
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
=======
    // ✅ NOVO MÉTODO: Consulta SEGURA por chave composta (Usado no Controller)
    public List<CampResponsavel> getByCompositeKey(int camId, int voluntarioId, Conexao conexao) {
        List<CampResponsavel> lista = new ArrayList<>();
        String sql = "SELECT * FROM camp_responsavel WHERE cam_id = ? AND voluntario_vol_id = ?";

        try (Connection conn = conexao.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, camId);
            stmt.setInt(2, voluntarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCampResponsavel(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar CampResponsavel por chave composta: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }

        return lista;
    }

<<<<<<< HEAD
    public CampResponsavel gravar(CampResponsavel cr, Conexao conexao) {
        String sql = """
            INSERT INTO campanha_responsavel 
            (camp_id, vol_id, data_inicio, data_fim, obs_texto)
=======

    public CampResponsavel gravar(CampResponsavel cr, Conexao conexao) {
        String sql = """
            INSERT INTO camp_responsavel 
            (cam_id, voluntario_vol_id, DATA_INICIO, DATA_FIM, Obs_texto)
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            VALUES (?, ?, ?, ?, ?)
        """;

        try {
            Connection conn = conexao.getConnect();
<<<<<<< HEAD
            if (conn == null || conn.isClosed()) {
                System.err.println("Erro: Conexão fechada ou nula no gravar.");
                return null;
            }

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
            java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

            if (dataInicio == null) dataInicio = new java.sql.Date(System.currentTimeMillis());
            if (dataFim == null) dataFim = new java.sql.Date(System.currentTimeMillis());
=======
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Converter strings para Date
            java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
            java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

            if (dataInicio == null || dataFim == null) {
                System.err.println("Erro: formato de data inválido");
                return null;
            }
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

            stmt.setInt(1, cr.getCam_id());
            stmt.setInt(2, cr.getVoluntario_vol_id());
            stmt.setDate(3, dataInicio);
            stmt.setDate(4, dataFim);
            stmt.setString(5, cr.getObs_texto());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();
<<<<<<< HEAD
            // NÃO fechar a conexão aqui
=======
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

            return rowsAffected > 0 ? cr : null;

        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("Erro ao gravar: " + e.getMessage());
=======
            System.err.println("Erro ao gravar CampResponsavel: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }

        return null;
    }

    public CampResponsavel alterar(CampResponsavel cr, Conexao conexao) {
        String sql = """
<<<<<<< HEAD
            UPDATE campanha_responsavel SET
                data_inicio = ?,
                data_fim = ?,
                obs_texto = ?
            WHERE camp_id = ? AND vol_id = ?
=======
            UPDATE camp_responsavel SET
                DATA_INICIO = ?,
                DATA_FIM = ?,
                Obs_texto = ?
            WHERE cam_id = ? AND voluntario_vol_id = ?
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        """;

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);

<<<<<<< HEAD
            java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
            java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

=======
            // Converter strings para Date
            java.sql.Date dataInicio = parseDate(cr.getDATA_INICIO());
            java.sql.Date dataFim = parseDate(cr.getDATA_FIM());

            if (dataInicio == null || dataFim == null) {
                System.err.println("Erro: formato de data inválido");
                return null;
            }

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            stmt.setDate(1, dataInicio);
            stmt.setDate(2, dataFim);
            stmt.setString(3, cr.getObs_texto());
            stmt.setInt(4, cr.getCam_id());
            stmt.setInt(5, cr.getVoluntario_vol_id());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0 ? cr : null;

        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("Erro ao alterar: " + e.getMessage());
=======
            System.err.println("Erro ao alterar CampResponsavel: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }

        return null;
    }

    public boolean apagar(CampResponsavel cr, Conexao conexao) {
<<<<<<< HEAD
        String sql = "DELETE FROM campanha_responsavel WHERE camp_id = ? AND vol_id = ?";
=======
        String sql = "DELETE FROM camp_responsavel WHERE cam_id = ? AND voluntario_vol_id = ?";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        try {
            Connection conn = conexao.getConnect();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cr.getCam_id());
            stmt.setInt(2, cr.getVoluntario_vol_id());

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            return rowsAffected > 0;

        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("Erro ao apagar: " + e.getMessage());
=======
            System.err.println("Erro ao apagar CampResponsavel: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }

        return false;
    }

    public List<CampResponsavel> getCampanhasPorVoluntario(int voluntarioId, Conexao conexao) {
        List<CampResponsavel> lista = new ArrayList<>();
<<<<<<< HEAD
        String sql = "SELECT * FROM campanha_responsavel WHERE vol_id = ?";
=======
        String sql = "SELECT * FROM camp_responsavel WHERE voluntario_vol_id = ?";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

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
<<<<<<< HEAD
            System.err.println("Erro ao buscar por voluntário: " + e.getMessage());
=======
            System.err.println("Erro ao buscar campanhas por voluntário: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }

        return lista;
    }

    private CampResponsavel mapCampResponsavel(ResultSet rs) throws SQLException {
        CampResponsavel cr = new CampResponsavel();

<<<<<<< HEAD
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
=======
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
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        return cr;
    }
}