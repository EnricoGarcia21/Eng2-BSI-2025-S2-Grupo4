package DOARC.mvc.dao;

import DOARC.mvc.model.Donatario;
import DOARC.mvc.util.Conexao; // Importação da Conexao
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
// MUDANÇA 1: Removido o 'implements IDAO<Donatario>'
public class DonatarioDAO {

    public DonatarioDAO() {
        // Construtor vazio (correto)
    }

    // MUDANÇA 2: Todos os métodos foram reescritos para usar PreparedStatement (?)
    // igual ao ProdutoDAO, em vez de String.format()

    public Donatario gravar(Donatario entidade, Conexao conexao) {
        String sql = "INSERT INTO donatario (don_nome, don_data_nasc, don_rua, don_bairro, don_cidade, don_telefone, don_cep, don_uf, don_email, don_sexo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING don_id";

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
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
            e.printStackTrace();
        }
        return null;
    }

    public Donatario alterar(Donatario entidade, Conexao conexao) {
        String sql = "UPDATE donatario SET don_nome=?, don_data_nasc=?, don_rua=?, don_bairro=?, don_cidade=?, don_telefone=?, don_cep=?, don_uf=?, don_email=?, don_sexo=? " +
                "WHERE don_id=?";

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
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
            e.printStackTrace();
        }
        return null;
    }

    public boolean apagar(Donatario entidade, Conexao conexao) {
        String sql = "DELETE FROM donatario WHERE don_id=?";

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, entidade.getDonId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Donatario get(int id, Conexao conexao) {
        String sql = "SELECT * FROM donatario WHERE don_id=?";

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapDonatario(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Donatario> get(String filtro, Conexao conexao) {
        List<Donatario> lista = new ArrayList<>();
        // MUDANÇA 3: A consulta de filtro também usa PreparedStatement
        String sql = "SELECT * FROM donatario";

        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE don_nome ILIKE ? OR don_email ILIKE ?";
        }

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {

            if (filtro != null && !filtro.isEmpty()) {
                pst.setString(1, "%" + filtro + "%");
                pst.setString(2, "%" + filtro + "%");
            }

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapDonatario(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // O método mapDonatario estava correto e foi mantido
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
}