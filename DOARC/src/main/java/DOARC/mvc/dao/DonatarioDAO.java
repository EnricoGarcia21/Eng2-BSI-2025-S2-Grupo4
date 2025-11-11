package DOARC.mvc.dao;

import DOARC.mvc.model.Donatario;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonatarioDAO implements IDAO<Donatario> {

    /**
     * Método auxiliar para mapear um ResultSet para um objeto Donatario.
     */
    private Donatario mapDonatario(ResultSet rs) throws SQLException {
        Donatario d = new Donatario();
        d.setDonId(rs.getInt("don_id"));
        d.setDonNome(rs.getString("don_nome"));
        d.setDonDataNasc(rs.getString("don_data_nasc"));
        d.setDonRua(rs.getString("don_rua"));
        d.setDonBairro(rs.getString("don_bairro"));
        d.setDonCidade(rs.getString("don_cidade"));
        d.setDonTelefone(rs.getString("don_telefone"));
        d.setDonCep(rs.getString("don_cep"));
        d.setDonUf(rs.getString("don_uf"));
        d.setDonEmail(rs.getString("don_email"));
        d.setDonSexo(rs.getString("don_sexo"));
        return d;
    }

    @Override
    public Donatario gravar(Donatario entidade, Conexao conn) {
        String sql = "INSERT INTO donatario (don_nome, don_data_nasc, don_rua, don_bairro, don_cidade, don_telefone, don_cep, don_uf, don_email, don_sexo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING don_id";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getDonNome());
            pst.setString(2, entidade.getDonDataNasc());
            pst.setString(3, entidade.getDonRua());
            pst.setString(4, entidade.getDonBairro());
            pst.setString(5, entidade.getDonCidade());
            pst.setString(6, entidade.getDonTelefone());
            pst.setString(7, entidade.getDonCep());
            pst.setString(8, entidade.getDonUf());
            pst.setString(9, entidade.getDonEmail());
            pst.setString(10, entidade.getDonSexo());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                entidade.setDonId(rs.getInt("don_id"));
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Donatário: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Donatario alterar(Donatario entidade, Conexao conn) {
        String sql = "UPDATE donatario SET don_nome=?, don_data_nasc=?, don_rua=?, don_bairro=?, don_cidade=?, don_telefone=?, don_cep=?, don_uf=?, don_email=?, don_sexo=? WHERE don_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getDonNome());
            pst.setString(2, entidade.getDonDataNasc());
            pst.setString(3, entidade.getDonRua());
            pst.setString(4, entidade.getDonBairro());
            pst.setString(5, entidade.getDonCidade());
            pst.setString(6, entidade.getDonTelefone());
            pst.setString(7, entidade.getDonCep());
            pst.setString(8, entidade.getDonUf());
            pst.setString(9, entidade.getDonEmail());
            pst.setString(10, entidade.getDonSexo());
            pst.setInt(11, entidade.getDonId());

            int updated = pst.executeUpdate();
            if (updated > 0) {
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar Donatário: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean apagar(Donatario entidade, Conexao conn) {
        String sql = "DELETE FROM donatario WHERE don_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, entidade.getDonId());
            if (pst.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao apagar Donatário: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Donatario get(int id, Conexao conn) {
        String sql = "SELECT * FROM donatario WHERE don_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapDonatario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Donatário por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Donatario> get(String filtro, Conexao conn) {
        List<Donatario> lista = new ArrayList<>();
        String sql = "SELECT * FROM donatario";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (Statement st = conn.getConnect().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapDonatario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar lista de Donatários: " + e.getMessage());
        }
        return lista;
    }
}