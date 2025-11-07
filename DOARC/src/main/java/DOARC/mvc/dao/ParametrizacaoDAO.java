package DOARC.mvc.dao;

import DOARC.mvc.model.Parametrizacao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ParametrizacaoDAO implements IDAO<Parametrizacao> {

    @Override
    public Parametrizacao gravar(Parametrizacao entidade, SingletonDB conexao) {
        // Força o ID para 1 sempre
        String sql = "INSERT INTO parametrizacao (id, p_cnpj, p_razaosocial, p_nomefantasia, p_rua, p_cidade, p_bairro, numero, p_uf, p_cep, p_email, p_site, p_telefone) " +
                "VALUES (1, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setString(1, entidade.getCnpj());
            pst.setString(2, entidade.getRazaoSocial());
            pst.setString(3, entidade.getNomeFantasia());
            pst.setString(4, entidade.getRua());
            pst.setString(5, entidade.getCidade());
            pst.setString(6, entidade.getBairro());
            pst.setInt(7, entidade.getNumero());
            pst.setString(8, entidade.getUf());
            pst.setString(9, entidade.getCep());
            pst.setString(10, entidade.getEmail());
            pst.setString(11, entidade.getSite());
            pst.setString(12, entidade.getTelefone());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                entidade.setId(1); // Define o ID como 1
                return entidade;
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao gravar parametrização: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Parametrizacao alterar(Parametrizacao entidade, SingletonDB conexao) {
        String sql = "UPDATE parametrizacao SET p_cnpj=?, p_razaosocial=?, p_nomefantasia=?, p_rua=?, p_cidade=?, p_bairro=?, numero=?, p_uf=?, p_cep=?, p_email=?, p_site=?, p_telefone=? WHERE id=1"; // Sempre ID 1

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setString(1, entidade.getCnpj());
            pst.setString(2, entidade.getRazaoSocial());
            pst.setString(3, entidade.getNomeFantasia());
            pst.setString(4, entidade.getRua());
            pst.setString(5, entidade.getCidade());
            pst.setString(6, entidade.getBairro());
            pst.setInt(7, entidade.getNumero());
            pst.setString(8, entidade.getUf());
            pst.setString(9, entidade.getCep());
            pst.setString(10, entidade.getEmail());
            pst.setString(11, entidade.getSite());
            pst.setString(12, entidade.getTelefone());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao alterar parametrização: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean apagar(Parametrizacao entidade, SingletonDB conexao) {
        return false;
    }

    @Override
    public Parametrizacao get(int id, SingletonDB conexao) {
        String sql = "SELECT * FROM parametrizacao WHERE id=1"; // Sempre busca ID 1

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapParametrizacao(rs);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar parametrização: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Parametrizacao> get(String filtro, SingletonDB conexao) {
        List<Parametrizacao> lista = new ArrayList<>();
        String sql = "SELECT * FROM parametrizacao";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (Statement st = conexao.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapParametrizacao(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao listar parametrizações: " + e.getMessage());
        }
        return lista;
    }

    private Parametrizacao mapParametrizacao(ResultSet rs) throws SQLException {
        Parametrizacao p = new Parametrizacao();
        p.setId(rs.getInt("id"));
        p.setCnpj(rs.getString("p_cnpj"));
        p.setRazaoSocial(rs.getString("p_razaosocial"));
        p.setNomeFantasia(rs.getString("p_nomefantasia"));
        p.setRua(rs.getString("p_rua"));
        p.setCidade(rs.getString("p_cidade"));
        p.setBairro(rs.getString("p_bairro"));
        p.setNumero(rs.getInt("numero"));
        p.setUf(rs.getString("p_uf"));
        p.setCep(rs.getString("p_cep"));
        p.setEmail(rs.getString("p_email"));
        p.setSite(rs.getString("p_site"));
        p.setTelefone(rs.getString("p_telefone"));
        return p;
    }
}