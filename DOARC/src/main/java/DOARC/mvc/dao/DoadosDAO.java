package DOARC.mvc.dao;

import DOARC.mvc.model.Doados;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoadosDAO implements IDAO<Doados> {

    private Doados mapDoados(ResultSet rs) throws SQLException {
        Doados d = new Doados();
        d.setDoaId(rs.getInt("doa_id"));
        d.setDoaDataAquisicao(rs.getString("doa_data_aquisicao"));
        d.setDoaTipoDoacao(rs.getString("doa_tipo_doacao"));
        d.setVolId(rs.getInt("vol_id"));
        d.setDonId(rs.getInt("don_id"));
        d.setObsDoado(rs.getString("obs_doado"));
        d.setValorDoacao(rs.getBigDecimal("valor_doacao"));
        return d;
    }

    @Override
    public Doados gravar(Doados entidade, Conexao conn) {
        String sql = "INSERT INTO doados (doa_data_aquisicao, doa_tipo_doacao, vol_id, don_id, obs_doado, valor_doacao) VALUES (?, ?, ?, ?, ?, ?) RETURNING doa_id";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getDoaDataAquisicao());
            pst.setString(2, entidade.getDoaTipoDoacao());
            pst.setInt(3, entidade.getVolId());
            pst.setInt(4, entidade.getDonId());
            pst.setString(5, entidade.getObsDoado());
            pst.setBigDecimal(6, entidade.getValorDoacao());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                entidade.setDoaId(rs.getInt("doa_id"));
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Doados alterar(Doados entidade, Conexao conn) {
        String sql = "UPDATE doados SET doa_data_aquisicao=?, doa_tipo_doacao=?, vol_id=?, don_id=?, obs_doado=?, valor_doacao=? WHERE doa_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getDoaDataAquisicao());
            pst.setString(2, entidade.getDoaTipoDoacao());
            pst.setInt(3, entidade.getVolId());
            pst.setInt(4, entidade.getDonId());
            pst.setString(5, entidade.getObsDoado());
            pst.setBigDecimal(6, entidade.getValorDoacao());
            pst.setInt(7, entidade.getDoaId());
            if (pst.executeUpdate() > 0) {
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Doados entidade, Conexao conn) {
        String sql = "DELETE FROM doados WHERE doa_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, entidade.getDoaId());
            if (pst.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Doados get(int id, Conexao conn) {
        String sql = "SELECT * FROM doados WHERE doa_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapDoados(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Doados> get(String filtro, Conexao conn) {
        List<Doados> lista = new ArrayList<>();
        String sql = "SELECT * FROM doados";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }
        try (Statement st = conn.getConnect().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapDoados(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}