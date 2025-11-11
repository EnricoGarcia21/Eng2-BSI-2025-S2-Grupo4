package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoluntarioDAO implements IDAO<Voluntario> {

    private Voluntario mapVoluntario(ResultSet rs) throws SQLException {
        Voluntario v = new Voluntario();
        v.setVolId(rs.getInt("vol_id"));
        v.setVolNome(rs.getString("vol_nome"));
        v.setVolTelefone(rs.getString("vol_telefone"));
        v.setVolEmail(rs.getString("vol_email"));
        v.setVolCidade(rs.getString("vol_cidade"));
        v.setVolBarro(rs.getString("vol_barro"));
        return v;
    }

    @Override
    public Voluntario gravar(Voluntario entidade, Conexao conn) {
        String sql = "INSERT INTO Voluntario (vol_nome, vol_telefone, vol_email, vol_cidade, vol_barro) VALUES (?, ?, ?, ?, ?) RETURNING vol_id";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getVolNome());
            pst.setString(2, entidade.getVolTelefone());
            pst.setString(3, entidade.getVolEmail());
            pst.setString(4, entidade.getVolCidade());
            pst.setString(5, entidade.getVolBarro());
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
    public Voluntario alterar(Voluntario entidade, Conexao conn) {
        String sql = "UPDATE Voluntario SET vol_nome=?, vol_telefone=?, vol_email=?, vol_cidade=?, vol_barro=? WHERE vol_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getVolNome());
            pst.setString(2, entidade.getVolTelefone());
            pst.setString(3, entidade.getVolEmail());
            pst.setString(4, entidade.getVolCidade());
            pst.setString(5, entidade.getVolBarro());
            pst.setInt(6, entidade.getVolId());
            if (pst.executeUpdate() > 0) {
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Voluntario entidade, Conexao conn) {
        String sql = "DELETE FROM Voluntario WHERE vol_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, entidade.getVolId());
            if (pst.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Voluntario get(int id, Conexao conn) {
        String sql = "SELECT * FROM voluntario WHERE vol_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
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
    public List<Voluntario> get(String filtro, Conexao conn) {
        List<Voluntario> lista = new ArrayList<>();
        String sql = "SELECT * FROM voluntario";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }
        try (Statement st = conn.getConnect().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapVoluntario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}