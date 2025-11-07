package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoluntarioDAO implements IDAO<Voluntario> {

    @Override
    public Voluntario gravar(Voluntario entidade, SingletonDB conexao) {
        // Não implementado - apenas GET
        return null;
    }

    @Override
    public Voluntario alterar(Voluntario entidade, SingletonDB conexao) {
        // Não implementado - apenas GET
        return null;
    }

    @Override
    public boolean apagar(Voluntario entidade, SingletonDB conexao) {
        // Não implementado - apenas GET
        return false;
    }

    @Override
    public Voluntario get(int id, SingletonDB conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_id = ?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                return mapVoluntario(rs);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar voluntário: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Voluntario> get(String filtro, SingletonDB conexao) {
        List<Voluntario> lista = new ArrayList<>();
        String sql = "SELECT * FROM voluntario";

        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (Statement st = conexao.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapVoluntario(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao listar voluntários: " + e.getMessage());
        }
        return lista;
    }

    // Método específico para buscar todos os voluntários (útil para dropdowns)
    public List<Voluntario> getAll(SingletonDB conexao) {
        return get(null, conexao);
    }

    // Método específico para buscar voluntários por cidade
    public List<Voluntario> getPorCidade(String cidade, SingletonDB conexao) {
        String sql = "SELECT * FROM voluntario WHERE vol_cidade = ?";
        List<Voluntario> lista = new ArrayList<>();

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setString(1, cidade);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                lista.add(mapVoluntario(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar voluntários por cidade: " + e.getMessage());
        }
        return lista;
    }

    private Voluntario mapVoluntario(ResultSet rs) throws SQLException {
        Voluntario voluntario = new Voluntario();
        voluntario.setVolId(rs.getInt("vol_id"));
        voluntario.setVolNome(rs.getString("vol_nome"));
        voluntario.setVolTelefone(rs.getString("vol_telefone"));
        voluntario.setVolEmail(rs.getString("vol_email"));
        voluntario.setVolCidade(rs.getString("vol_cidade"));
        voluntario.setVolBairro(rs.getString("vol_bairro"));
        return voluntario;
    }
}