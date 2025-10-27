package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoluntarioDAO implements IDAO<Voluntario> {

    private Connection conn;

    public VoluntarioDAO() {
        conn = SingletonDB.getConexao().getConnect();
    }

    @Override
    public Voluntario gravar(Voluntario entidade) {
        String sql = "INSERT INTO voluntario (vol_nome, vol_datanasc, vol_rua, vol_bairro, vol_cidade, vol_telefone, vol_cep, vol_uf, vol_email, vol_sexo, vol_numero, vol_cpf) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING vol_id";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entidade.getVolNome());
            pst.setString(2, entidade.getVolDataNasc());
            pst.setString(3, entidade.getVolRua());
            pst.setString(4, entidade.getVolBairro());
            pst.setString(5, entidade.getVolCidade());
            pst.setString(6, entidade.getVolTelefone());
            pst.setString(7, entidade.getVolCep());
            pst.setString(8, entidade.getVolUf());
            pst.setString(9, entidade.getVolEmail());
            pst.setString(10, entidade.getVolSexo());
            pst.setString(11, entidade.getVolNumero());
            pst.setString(12, entidade.getVolCpf());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                entidade.setVolId(rs.getInt("vol_id"));
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Voluntario alterar(Voluntario entidade) {
        String sql = "UPDATE voluntario SET vol_nome=?, vol_datanasc=?, vol_rua=?, vol_bairro=?, vol_cidade=?, vol_telefone=?, vol_cep=?, vol_uf=?, vol_email=?, vol_sexo=?, vol_numero=?, vol_cpf=? WHERE vol_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entidade.getVolNome());
            pst.setString(2, entidade.getVolDataNasc());
            pst.setString(3, entidade.getVolRua());
            pst.setString(4, entidade.getVolBairro());
            pst.setString(5, entidade.getVolCidade());
            pst.setString(6, entidade.getVolTelefone());
            pst.setString(7, entidade.getVolCep());
            pst.setString(8, entidade.getVolUf());
            pst.setString(9, entidade.getVolEmail());
            pst.setString(10, entidade.getVolSexo());
            pst.setString(11, entidade.getVolNumero());
            pst.setString(12, entidade.getVolCpf());
            pst.setInt(13, entidade.getVolId());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Voluntario entidade) {
        String sql = "DELETE FROM voluntario WHERE vol_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, entidade.getVolId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Voluntario get(int id) {
        String sql = "SELECT * FROM voluntario WHERE vol_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapVoluntario(rs);
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
        if (filtro != null && !filtro.isEmpty()) sql += " WHERE " + filtro;

        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapVoluntario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Voluntario mapVoluntario(ResultSet rs) throws SQLException {
        Voluntario v = new Voluntario();
        v.setVolId(rs.getInt("vol_id"));
        v.setVolNome(rs.getString("vol_nome"));
        v.setVolDataNasc(rs.getString("vol_datanasc"));
        v.setVolRua(rs.getString("vol_rua"));
        v.setVolBairro(rs.getString("vol_bairro"));
        v.setVolCidade(rs.getString("vol_cidade"));
        v.setVolTelefone(rs.getString("vol_telefone"));
        v.setVolCep(rs.getString("vol_cep"));
        v.setVolUf(rs.getString("vol_uf"));
        v.setVolEmail(rs.getString("vol_email"));
        v.setVolSexo(rs.getString("vol_sexo"));
        v.setVolNumero(rs.getString("vol_numero"));
        v.setVolCpf(rs.getString("vol_cpf"));
        return v;
    }
}