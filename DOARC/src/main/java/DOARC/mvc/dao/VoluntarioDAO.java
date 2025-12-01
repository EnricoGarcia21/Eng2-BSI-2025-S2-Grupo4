package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoluntarioDAO implements IDAO<Voluntario> {

    public VoluntarioDAO() {}

    @Override
    public Voluntario gravar(Voluntario v) {
        // Query atualizada com todos os campos
        String sql = """
            INSERT INTO voluntario
            (vol_nome, vol_bairro, vol_numero, vol_rua, vol_telefone, vol_cidade, 
             vol_cep, vol_uf, vol_email, vol_cpf, vol_datanasc, vol_sexo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING vol_id
        """;

        Conexao conexao = new Conexao();

        // Usamos try-with-resources no Connection também para garantir o fechamento
        try (Connection conn = conexao.getConnect()) {
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

                if (stmt.executeUpdate() > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            v.setVol_id(rs.getInt(1));
                            return v;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Voluntario alterar(Voluntario v) {
        String sql = """
            UPDATE voluntario SET
            vol_nome=?, vol_bairro=?, vol_numero=?, vol_rua=?, vol_telefone=?, 
            vol_cidade=?, vol_cep=?, vol_uf=?, vol_email=?, vol_cpf=?, 
            vol_datanasc=?, vol_sexo=?
            WHERE vol_id=?
        """;

        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
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
        } catch (SQLException e) {
            System.err.println("Erro ao alterar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Voluntario v) {
        String sql = "DELETE FROM voluntario WHERE vol_id=?";
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            if (conn == null) return false;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, v.getVol_id());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Voluntario get(int id) {
        String sql = "SELECT * FROM voluntario WHERE vol_id=?";
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
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

    @Override
    public List<Voluntario> get(String filtro) {
        List<Voluntario> lista = new ArrayList<>();
        String sql = "SELECT * FROM voluntario";
        if (filtro != null && !filtro.isBlank()) {
            sql += " WHERE vol_nome ILIKE ? OR vol_cpf ILIKE ? OR vol_email ILIKE ?";
        }

        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
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

    public Voluntario buscarPorEmail(String email) {
        return buscarPorCampo("vol_email", email);
    }

    public Voluntario buscarPorCpf(String cpf) {
        return buscarPorCampo("vol_cpf", cpf);
    }

    // Método auxiliar privado agora gerencia sua própria conexão
    private Voluntario buscarPorCampo(String campo, String valor) {
        String sql = "SELECT * FROM voluntario WHERE " + campo + " = ?";
        Conexao conexao = new Conexao();

        try (Connection conn = conexao.getConnect()) {
            if (conn == null) return null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, valor);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) return mapVoluntario(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Voluntario mapVoluntario(ResultSet rs) throws SQLException {
        Voluntario v = new Voluntario();
        v.setVol_id(rs.getInt("vol_id"));
        v.setVol_nome(rs.getString("vol_nome"));
        v.setVol_bairro(rs.getString("vol_bairro"));
        v.setVol_numero(rs.getString("vol_numero"));
        v.setVol_rua(rs.getString("vol_rua"));
        v.setVol_telefone(rs.getString("vol_telefone"));
        v.setVol_cidade(rs.getString("vol_cidade"));
        v.setVol_cep(rs.getString("vol_cep"));
        v.setVol_uf(rs.getString("vol_uf"));
        v.setVol_email(rs.getString("vol_email"));
        v.setVol_cpf(rs.getString("vol_cpf"));
        v.setVol_datanasc(rs.getString("vol_datanasc"));
        v.setVol_sexo(rs.getString("vol_sexo"));
        return v;
    }
}