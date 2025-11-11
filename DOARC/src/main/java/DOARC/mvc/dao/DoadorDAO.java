package DOARC.mvc.dao;

import DOARC.mvc.model.Doador;
import DOARC.mvc.util.Conexao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoadorDAO implements IDAO<Doador> {

    private Doador mapDoador(ResultSet rs) throws SQLException {
        Doador d = new Doador();
        d.setDoaId(rs.getInt("doa_id"));
        d.setDoaNome(rs.getString("doa_nome"));
        d.setDoaTelefone(rs.getString("doa_telefone"));
        d.setDoaEmail(rs.getString("doa_email"));
        d.setDoaCep(rs.getString("doa_cep"));
        d.setDoaUf(rs.getString("doa_uf"));
        d.setDoaCidade(rs.getString("doa_cidade"));
        d.setDoaBairro(rs.getString("doa_bairro"));
        d.setDoaRua(rs.getString("doa_rua"));
        d.setDoaCpf(rs.getString("doa_cpf"));
        d.setDoaDataNasc(rs.getString("doa_datanasc"));
        d.setDoaSexo(rs.getString("doa_sexo"));
        d.setDoaSite(rs.getString("doa_site"));
        return d;
    }

    @Override
    public Doador gravar(Doador entidade, Conexao conn) {
        String sql = "INSERT INTO doadores (doa_nome, doa_telefone, doa_email, doa_cep, doa_uf, doa_cidade, doa_bairro, doa_rua, doa_cpf, doa_datanasc, doa_sexo, doa_site) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING doa_id";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getDoaNome());
            pst.setString(2, entidade.getDoaTelefone());
            pst.setString(3, entidade.getDoaEmail());
            pst.setString(4, entidade.getDoaCep());
            pst.setString(5, entidade.getDoaUf());
            pst.setString(6, entidade.getDoaCidade());
            pst.setString(7, entidade.getDoaBairro());
            pst.setString(8, entidade.getDoaRua());
            pst.setString(9, entidade.getDoaCpf());
            pst.setString(10, entidade.getDoaDataNasc());
            pst.setString(11, entidade.getDoaSexo());
            pst.setString(12, entidade.getDoaSite());

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
    public Doador alterar(Doador entidade, Conexao conn) {
        String sql = "UPDATE doadores SET doa_nome=?, doa_telefone=?, doa_email=?, doa_cep=?, doa_uf=?, doa_cidade=?, doa_bairro=?, doa_rua=?, doa_cpf=?, doa_datanasc=?, doa_sexo=?, doa_site=? WHERE doa_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getDoaNome());
            pst.setString(2, entidade.getDoaTelefone());
            pst.setString(3, entidade.getDoaEmail());
            pst.setString(4, entidade.getDoaCep());
            pst.setString(5, entidade.getDoaUf());
            pst.setString(6, entidade.getDoaCidade());
            pst.setString(7, entidade.getDoaBairro());
            pst.setString(8, entidade.getDoaRua());
            pst.setString(9, entidade.getDoaCpf());
            pst.setString(10, entidade.getDoaDataNasc());
            pst.setString(11, entidade.getDoaSexo());
            pst.setString(12, entidade.getDoaSite());
            pst.setInt(13, entidade.getDoaId());

            int updated = pst.executeUpdate();
            if (updated > 0) {
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Doador entidade, Conexao conn) {
        String sql = "DELETE FROM doadores WHERE doa_id=?";
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
    public Doador get(int id, Conexao conn) {
        String sql = "SELECT * FROM doadores WHERE doa_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapDoador(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Doador> get(String filtro, Conexao conn) {
        List<Doador> lista = new ArrayList<>();
        String sql = "SELECT * FROM doadores";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (Statement st = conn.getConnect().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapDoador(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}