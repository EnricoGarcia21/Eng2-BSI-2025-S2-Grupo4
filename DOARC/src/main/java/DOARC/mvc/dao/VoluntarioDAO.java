package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
import java.sql.*;
=======
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoluntarioDAO implements IDAO<Voluntario> {

<<<<<<< HEAD
    public VoluntarioDAO() {}

    @Override
    public Voluntario gravar(Voluntario v, Conexao conexao) {
        // Query atualizada com todos os campos
        String sql = """
            INSERT INTO voluntario
            (vol_nome, vol_bairro, vol_numero, vol_rua, vol_telefone, vol_cidade, 
             vol_cep, vol_uf, vol_email, vol_cpf, vol_datanasc, vol_sexo)
=======
    public VoluntarioDAO() {
    }

    // =====================================================
    // ✅ GRAVAR (USA PreparedStatement)
    // =====================================================
    @Override
    public Voluntario gravar(Voluntario v, Conexao conexao) {
        String sql = """
            INSERT INTO voluntario 
            (vol_nome, vol_datanasc, vol_rua, vol_bairro, vol_cidade, vol_telefone, vol_cep, vol_uf,
             vol_email, vol_sexo, vol_numero, vol_cpf)
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING vol_id
        """;

<<<<<<< HEAD
        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, v.getVol_nome());
                stmt.setString(2, v.getVol_bairro());
                stmt.setString(3, v.getVol_numero());
                stmt.setString(4, v.getVol_rua()); // Mapeia vol_rua para von_rua
                stmt.setString(5, v.getVol_telefone());
                stmt.setString(6, v.getVol_cidade());
                stmt.setString(7, v.getVol_cep());
                stmt.setString(8, v.getVol_uf());
                stmt.setString(9, v.getVol_email());
                stmt.setString(10, v.getVol_cpf());
                stmt.setString(11, v.getVol_datanasc());
                stmt.setString(12, v.getVol_sexo());

                if (stmt.executeUpdate() > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            v.setVol_id(rs.getInt(1));
                            return v;
                        }
=======
        try (Connection conn = conexao.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, v.getVol_nome());
            stmt.setString(2, v.getVol_datanasc());
            stmt.setString(3, v.getVol_rua());
            stmt.setString(4, v.getVol_bairro());
            stmt.setString(5, v.getVol_cidade());
            stmt.setString(6, v.getVol_telefone());
            stmt.setString(7, v.getVol_cep());
            stmt.setString(8, v.getVol_uf());
            stmt.setString(9, v.getVol_email());
            stmt.setString(10, v.getVol_sexo());
            stmt.setString(11, v.getVol_numero());
            stmt.setString(12, v.getVol_cpf());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        v.setVol_id(rs.getInt(1));
                        return v;
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

<<<<<<< HEAD
=======
    // =====================================================
    // ✅ ALTERAR (USA PreparedStatement)
    // =====================================================
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    @Override
    public Voluntario alterar(Voluntario v, Conexao conexao) {
        String sql = """
            UPDATE voluntario SET
<<<<<<< HEAD
            vol_nome=?, vol_bairro=?, vol_numero=?, vol_rua=?, vol_telefone=?, 
            vol_cidade=?, vol_cep=?, vol_uf=?, vol_email=?, vol_cpf=?, 
            vol_datanasc=?, vol_sexo=?
            WHERE vol_id=?
        """;

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, v.getVol_nome());
                stmt.setString(2, v.getVol_bairro());
                stmt.setString(3, v.getVol_numero());
                stmt.setString(4, v.getVol_rua());
                stmt.setString(5, v.getVol_telefone());
                stmt.setString(6, v.getVol_cidade());
                stmt.setString(7, v.getVol_cep());
                stmt.setString(8, v.getVol_uf());
                stmt.setString(9, v.getVol_email());
                stmt.setString(10, v.getVol_cpf());
                stmt.setString(11, v.getVol_datanasc());
                stmt.setString(12, v.getVol_sexo());
                stmt.setInt(13, v.getVol_id());

                return stmt.executeUpdate() > 0 ? v : null;
            }
=======
            vol_nome=?, vol_datanasc=?, vol_rua=?, vol_bairro=?, vol_cidade=?,
            vol_telefone=?, vol_cep=?, vol_uf=?, vol_email=?, vol_sexo=?,
            vol_numero=?, vol_cpf=?
            WHERE vol_id=?
        """;

        try (Connection conn = conexao.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, v.getVol_nome());
            stmt.setString(2, v.getVol_datanasc());
            stmt.setString(3, v.getVol_rua());
            stmt.setString(4, v.getVol_bairro());
            stmt.setString(5, v.getVol_cidade());
            stmt.setString(6, v.getVol_telefone());
            stmt.setString(7, v.getVol_cep());
            stmt.setString(8, v.getVol_uf());
            stmt.setString(9, v.getVol_email());
            stmt.setString(10, v.getVol_sexo());
            stmt.setString(11, v.getVol_numero());
            stmt.setString(12, v.getVol_cpf());
            stmt.setInt(13, v.getVol_id());

            return stmt.executeUpdate() > 0 ? v : null;

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        } catch (SQLException e) {
            System.err.println("Erro ao alterar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

<<<<<<< HEAD
    @Override
    public boolean apagar(Voluntario v, Conexao conexao) {
        String sql = "DELETE FROM voluntario WHERE vol_id=?";
        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, v.getVol_id());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
=======
    // =====================================================
    // ✅ APAGAR (USA PreparedStatement)
    // =====================================================
    @Override
    public boolean apagar(Voluntario v, Conexao conexao) {
        String sql = "DELETE FROM voluntario WHERE vol_id=?";
        try (Connection conn = conexao.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, v.getVol_id());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar Voluntário: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return false;
    }

<<<<<<< HEAD
    @Override
    public Voluntario get(int id, Conexao conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_id=?";
        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return mapVoluntario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

=======
    // =====================================================
    // ✅ GET POR ID (USA PreparedStatement)
    // =====================================================
    @Override
    public Voluntario get(int id, Conexao conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_id=?";

        try (Connection conn = conexao.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapVoluntario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    // =====================================================
    // ✅ LISTAR / BUSCAR FILTRADO (USA PreparedStatement)
    // =====================================================
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    @Override
    public List<Voluntario> get(String filtro, Conexao conexao) {
        List<Voluntario> lista = new ArrayList<>();
        String sql = "SELECT * FROM voluntario";
<<<<<<< HEAD
        if (filtro != null && !filtro.isBlank()) {
            sql += " WHERE vol_nome ILIKE ? OR vol_cpf ILIKE ? OR vol_email ILIKE ?";
        }

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return lista;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (filtro != null && !filtro.isBlank()) {
                    String f = "%" + filtro + "%";
                    stmt.setString(1, f);
                    stmt.setString(2, f);
                    stmt.setString(3, f);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) lista.add(mapVoluntario(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Voluntario buscarPorEmail(String email, Conexao conexao) {
        return buscarPorCampo("vol_email", email, conexao);
    }

    // ✅ NOVO MÉTODO: Validar CPF duplicado
    public Voluntario buscarPorCpf(String cpf, Conexao conexao) {
        return buscarPorCampo("vol_cpf", cpf, conexao);
    }

    private Voluntario buscarPorCampo(String campo, String valor, Conexao conexao) {
        String sql = "SELECT * FROM voluntario WHERE " + campo + " = ?";
        try {
            Connection conn = conexao.getConnect();
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, valor);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return mapVoluntario(rs);
                }
            }
        } catch (SQLException e) {
=======
        boolean hasFilter = filtro != null && !filtro.isBlank();

        if (hasFilter) {
            sql += " WHERE vol_nome ILIKE ? OR vol_email ILIKE ? OR vol_cpf ILIKE ?";
        }

        try (Connection conn = conexao.getConnect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (hasFilter) {
                String filtroLike = "%" + filtro.replace("'", "") + "%";
                stmt.setString(1, filtroLike);
                stmt.setString(2, filtroLike);
                stmt.setString(3, filtroLike);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapVoluntario(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar Voluntários: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }


    // =====================================================
    // ✅ BUSCAR POR CPF (FIXED ClassCastException)
    // =====================================================
    public Voluntario buscarPorCpf(String cpf, Conexao conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_cpf = ?";

        try {
            // ✅ CORREÇÃO: Usar a Connection diretamente, resolvendo o ClassCastException
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO buscarPorCpf: Conexao.getConnect() retornou null");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, cpf);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapVoluntario(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO buscarPorCpf: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return null;
    }

<<<<<<< HEAD
    private Voluntario mapVoluntario(ResultSet rs) throws SQLException {
        Voluntario v = new Voluntario();
        v.setVol_id(rs.getInt("vol_id"));
        v.setVol_nome(rs.getString("vol_nome"));
        v.setVol_bairro(rs.getString("vol_bairro"));
        v.setVol_numero(rs.getString("vol_numero"));
        v.setVol_rua(rs.getString("vol_rua")); // Atenção ao nome da coluna no DB
        v.setVol_telefone(rs.getString("vol_telefone"));
        v.setVol_cidade(rs.getString("vol_cidade"));
        v.setVol_cep(rs.getString("vol_cep"));
        v.setVol_uf(rs.getString("vol_uf"));
        v.setVol_email(rs.getString("vol_email"));
        v.setVol_cpf(rs.getString("vol_cpf"));
        v.setVol_datanasc(rs.getString("vol_datanasc"));
        v.setVol_sexo(rs.getString("vol_sexo"));
=======
    // =====================================================
    // ✅ MAPEAMENTO
    // =====================================================
    private Voluntario mapVoluntario(ResultSet rs) throws SQLException {
        Voluntario v = new Voluntario();

        v.setVol_id(rs.getInt("vol_id"));
        v.setVol_nome(rs.getString("vol_nome"));
        v.setVol_datanasc(rs.getString("vol_datanasc"));
        v.setVol_rua(rs.getString("vol_rua"));
        v.setVol_bairro(rs.getString("vol_bairro"));
        v.setVol_cidade(rs.getString("vol_cidade"));
        v.setVol_telefone(rs.getString("vol_telefone"));
        v.setVol_cep(rs.getString("vol_cep"));
        v.setVol_uf(rs.getString("vol_uf"));
        v.setVol_email(rs.getString("vol_email"));
        v.setVol_sexo(rs.getString("vol_sexo"));
        v.setVol_numero(rs.getString("vol_numero"));
        v.setVol_cpf(rs.getString("vol_cpf"));

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        return v;
    }
}