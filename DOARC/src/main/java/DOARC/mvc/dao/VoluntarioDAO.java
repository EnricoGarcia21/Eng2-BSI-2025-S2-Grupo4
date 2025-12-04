package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoluntarioDAO {

    public Voluntario gravar(Voluntario v, Conexao conexao) {
        // SQL com RETURNING para o PostgreSQL
        String sql = "INSERT INTO voluntario (vol_nome, vol_cpf, vol_email, vol_telefone, vol_datanasc, vol_sexo, vol_rua, vol_numero, vol_bairro, vol_cidade, vol_uf, vol_cep) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING vol_id";

        // CORREÇÃO: Não usamos RETURN_GENERATED_KEYS aqui para evitar conflito com o RETURNING
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setString(1, v.getVol_nome());
            stmt.setString(2, v.getVol_cpf());
            stmt.setString(3, v.getVol_email());
            stmt.setString(4, v.getVol_telefone());

            // Tratamento para data nula
            if (v.getVol_datanasc() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(v.getVol_datanasc()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, v.getVol_sexo());
            stmt.setString(7, v.getVol_rua());
            stmt.setString(8, v.getVol_numero());
            stmt.setString(9, v.getVol_bairro());
            stmt.setString(10, v.getVol_cidade());
            stmt.setString(11, v.getVol_uf());
            stmt.setString(12, v.getVol_cep());

            // Executa como Query porque retorna um ResultSet (o ID)
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                v.setVol_id(rs.getInt(1));
            }
            return v;
        } catch (SQLException e) {
            System.err.println("❌ Erro ao gravar voluntário: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Voluntario alterar(Voluntario v, Conexao conexao) {
        String sql = "UPDATE voluntario SET vol_nome=?, vol_cpf=?, vol_email=?, vol_telefone=?, vol_datanasc=?, vol_sexo=?, vol_rua=?, vol_numero=?, vol_bairro=?, vol_cidade=?, vol_uf=?, vol_cep=? WHERE vol_id=?";

        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setString(1, v.getVol_nome());
            stmt.setString(2, v.getVol_cpf());
            stmt.setString(3, v.getVol_email());
            stmt.setString(4, v.getVol_telefone());

            if (v.getVol_datanasc() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(v.getVol_datanasc()));
            } else {
                stmt.setNull(5, Types.DATE);
            }

            stmt.setString(6, v.getVol_sexo());
            stmt.setString(7, v.getVol_rua());
            stmt.setString(8, v.getVol_numero());
            stmt.setString(9, v.getVol_bairro());
            stmt.setString(10, v.getVol_cidade());
            stmt.setString(11, v.getVol_uf());
            stmt.setString(12, v.getVol_cep());
            stmt.setInt(13, v.getVol_id());

            return stmt.executeUpdate() > 0 ? v : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean apagar(Voluntario v, Conexao conexao) {
        String sql = "DELETE FROM voluntario WHERE vol_id=?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, v.getVol_id());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Voluntario get(int id, Conexao conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_id=?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapVoluntario(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Voluntario> get(String filtro, Conexao conexao) {
        List<Voluntario> lista = new ArrayList<>();
        String sql = "SELECT * FROM voluntario";
        if (filtro != null && !filtro.isEmpty()) sql += " WHERE " + filtro;

        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapVoluntario(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Voluntario buscarPorCampo(String campo, String valor, Conexao conexao) {
        // Validação de segurança básica para o nome do campo
        if (!campo.matches("^[a-zA-Z0-9_]+$")) return null;

        String sql = "SELECT * FROM voluntario WHERE " + campo + " = ?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setString(1, valor);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapVoluntario(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Voluntario mapVoluntario(ResultSet rs) throws SQLException {
        Voluntario v = new Voluntario();
        v.setVol_id(rs.getInt("vol_id"));
        v.setVol_nome(rs.getString("vol_nome"));
        v.setVol_cpf(rs.getString("vol_cpf"));
        v.setVol_email(rs.getString("vol_email"));
        v.setVol_telefone(rs.getString("vol_telefone"));

        Date data = rs.getDate("vol_datanasc");
        if (data != null) v.setVol_datanasc(data.toString());

        v.setVol_sexo(rs.getString("vol_sexo"));
        v.setVol_rua(rs.getString("vol_rua"));
        v.setVol_numero(rs.getString("vol_numero"));
        v.setVol_bairro(rs.getString("vol_bairro"));
        v.setVol_cidade(rs.getString("vol_cidade"));
        v.setVol_uf(rs.getString("vol_uf"));
        v.setVol_cep(rs.getString("vol_cep"));
        return v;
    }
}