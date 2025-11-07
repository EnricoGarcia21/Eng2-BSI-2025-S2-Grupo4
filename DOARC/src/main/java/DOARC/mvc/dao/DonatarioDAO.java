package DOARC.mvc.dao;

import DOARC.mvc.model.Donatario;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DonatarioDAO implements IDAO<Donatario> {

    private Connection getConnection() {
        return SingletonDB.getConnection();
    }

    @Override
    public Donatario gravar(Donatario entidade) {
        String sql = "INSERT INTO Donatario (DON_NOME, DON_DATA_NASC, DON_RUA, DON_BAIRRO, DON_CIDADE, DON_TELEFONE, DON_CEP, DON_UF, DON_EMAIL, DON_SEXO) VALUES (?, ?::DATE, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING DON_ID";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

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

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    entidade.setDonId(rs.getInt("DON_ID"));
                    return entidade;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar donatário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Donatario alterar(Donatario entidade) {
        String sql = "UPDATE Donatario SET DON_NOME=?, DON_DATA_NASC=?::DATE, DON_RUA=?, DON_BAIRRO=?, DON_CIDADE=?, DON_TELEFONE=?, DON_CEP=?, DON_UF=?, DON_EMAIL=?, DON_SEXO=? WHERE DON_ID=?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

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
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            System.err.println("Erro ao alterar donatário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Donatario entidade) {
        String sql = "DELETE FROM Donatario WHERE DON_ID=?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, entidade.getDonId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar donatário: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Donatario get(int id) {
        String sql = "SELECT * FROM Donatario WHERE DON_ID=?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapDonatario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar donatário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Donatario> get(String filtro) {
        List<Donatario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Donatario WHERE DON_NOME ILIKE ? OR DON_EMAIL ILIKE ? OR DON_CIDADE ILIKE ?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            String searchPattern = "%" + (filtro != null ? filtro : "") + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);
            pst.setString(3, searchPattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapDonatario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar donatários: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<Donatario> getAll() {
        List<Donatario> lista = new ArrayList<>();
        String sql = "SELECT * FROM Donatario ORDER BY DON_NOME";
        Connection conn = getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapDonatario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos donatários: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private Donatario mapDonatario(ResultSet rs) throws SQLException {
        Donatario d = new Donatario();
        d.setDonId(rs.getInt("DON_ID"));
        d.setDonNome(rs.getString("DON_NOME"));

        // Handle date conversion from database
        Date dataNasc = rs.getDate("DON_DATA_NASC");
        if (dataNasc != null) {
            d.setDonDataNasc(dataNasc.toString()); // YYYY-MM-DD format
        }

        d.setDonRua(rs.getString("DON_RUA"));
        d.setDonBairro(rs.getString("DON_BAIRRO"));
        d.setDonCidade(rs.getString("DON_CIDADE"));
        d.setDonTelefone(rs.getString("DON_TELEFONE"));
        d.setDonCep(rs.getString("DON_CEP"));
        d.setDonUf(rs.getString("DON_UF"));
        d.setDonEmail(rs.getString("DON_EMAIL"));
        d.setDonSexo(rs.getString("DON_SEXO"));
        return d;
    }
}
