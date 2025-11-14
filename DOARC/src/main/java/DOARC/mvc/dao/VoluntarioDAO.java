package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoluntarioDAO implements IDAO<Voluntario> {

    public VoluntarioDAO() {
    }

    // =====================================================
    // ✅ GRAVAR
    // =====================================================
    @Override
    public Voluntario gravar(Voluntario v, Conexao conexao) {

        String sql = String.format("""
            INSERT INTO voluntario 
            (vol_nome, vol_datanasc, vol_rua, vol_bairro, vol_cidade, vol_telefone, vol_cep, vol_uf,
             vol_email, vol_sexo, vol_numero, vol_cpf)
            VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')
            RETURNING vol_id
        """,
                v.getVol_nome().replace("'", "''"),
                v.getVol_datanasc().replace("'", "''"),
                v.getVol_rua().replace("'", "''"),
                v.getVol_bairro().replace("'", "''"),
                v.getVol_cidade().replace("'", "''"),
                v.getVol_telefone().replace("'", "''"),
                v.getVol_cep().replace("'", "''"),
                v.getVol_uf().replace("'", "''"),
                v.getVol_email().replace("'", "''"),
                v.getVol_sexo().replace("'", "''"),
                v.getVol_numero().replace("'", "''"),
                v.getVol_cpf().replace("'", "''")
        );

        try (ResultSet rs = conexao.consultar(sql)) {
            if (rs != null && rs.next()) {
                v.setVol_id(rs.getInt("vol_id"));
                return v;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Voluntário: " + conexao.getMensagemErro());
        }

        return null;
    }

    // =====================================================
    // ✅ ALTERAR
    // =====================================================
    @Override
    public Voluntario alterar(Voluntario v, Conexao conexao) {

        String sql = String.format("""
            UPDATE voluntario SET
            vol_nome='%s', vol_datanasc='%s', vol_rua='%s', vol_bairro='%s', vol_cidade='%s',
            vol_telefone='%s', vol_cep='%s', vol_uf='%s', vol_email='%s', vol_sexo='%s',
            vol_numero='%s', vol_cpf='%s'
            WHERE vol_id=%d
        """,
                v.getVol_nome().replace("'", "''"),
                v.getVol_datanasc().replace("'", "''"),
                v.getVol_rua().replace("'", "''"),
                v.getVol_bairro().replace("'", "''"),
                v.getVol_cidade().replace("'", "''"),
                v.getVol_telefone().replace("'", "''"),
                v.getVol_cep().replace("'", "''"),
                v.getVol_uf().replace("'", "''"),
                v.getVol_email().replace("'", "''"),
                v.getVol_sexo().replace("'", "''"),
                v.getVol_numero().replace("'", "''"),
                v.getVol_cpf().replace("'", "''"),
                v.getVol_id()
        );

        return conexao.manipular(sql) ? v : null;
    }

    // =====================================================
    // ✅ APAGAR
    // =====================================================
    @Override
    public boolean apagar(Voluntario v, Conexao conexao) {
        String sql = "DELETE FROM voluntario WHERE vol_id=" + v.getVol_id();
        return conexao.manipular(sql);
    }

    // =====================================================
    // ✅ GET POR ID
    // =====================================================
    @Override
    public Voluntario get(int id, Conexao conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_id=" + id;

        try (ResultSet rs = conexao.consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapVoluntario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Voluntário: " + conexao.getMensagemErro());
        }

        return null;
    }

    // =====================================================
    // ✅ LISTAR / BUSCAR FILTRADO
    // =====================================================
    @Override
    public List<Voluntario> get(String filtro, Conexao conexao) {
        List<Voluntario> lista = new ArrayList<>();

        String sql = "SELECT * FROM voluntario";

        if (filtro != null && !filtro.isBlank()) {
            filtro = filtro.replace("'", "''");
            sql += String.format("""
                WHERE vol_nome ILIKE '%%%s%%'
                OR vol_email ILIKE '%%%s%%'
                OR vol_cpf ILIKE '%%%s%%'
            """, filtro, filtro, filtro);
        }

        try (ResultSet rs = conexao.consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(mapVoluntario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Voluntários: " + conexao.getMensagemErro());
        }

        return lista;
    }

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

        return v;
    }

    public Voluntario buscarPorCpf(String cpf, Conexao conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_cpf = ?";

        try {
            Conexao conn = (Conexao) conexao.getConnect();
            if (conn == null) return null;

            try (PreparedStatement stmt = ((java.sql.Connection) conn).prepareStatement(sql)) {
                stmt.setString(1, cpf);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapVoluntario(rs); // Use seu método de mapeamento
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ERRO buscarPorCpf: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
