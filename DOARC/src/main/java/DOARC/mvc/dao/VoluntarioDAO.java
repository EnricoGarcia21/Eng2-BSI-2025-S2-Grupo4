package DOARC.mvc.dao;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VoluntarioDAO implements IDAO<Voluntario> {

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    @Override
    public Voluntario gravar(Voluntario entidade) {
        Connection conn = getConexao().getConnect();
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

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    entidade.setVolId(rs.getInt("vol_id"));
                    return entidade;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Voluntario alterar(Voluntario entidade) {
        Connection conn = getConexao().getConnect();
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
            System.err.println("Erro ao alterar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Voluntario entidade) {
        Connection conn = getConexao().getConnect();
        String sql = "DELETE FROM voluntario WHERE vol_id=?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, entidade.getVolId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar Voluntário: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Voluntario get(int id) {
        Connection conn = getConexao().getConnect();
        String sql = "SELECT * FROM voluntario WHERE vol_id=?";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapVoluntario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Voluntário por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Voluntario> get(String filtro) {
        List<Voluntario> lista = new ArrayList<>();
        Connection conn = getConexao().getConnect();
        String sql = "SELECT * FROM voluntario";

        if (filtro != null && !filtro.isEmpty()) {
            // Usando PreparedStatement para o filtro também
            sql += " WHERE vol_nome ILIKE ? OR vol_email ILIKE ? OR vol_cpf ILIKE ?";

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                String likePattern = "%" + filtro + "%";
                pst.setString(1, likePattern);
                pst.setString(2, likePattern);
                pst.setString(3, likePattern);

                try (ResultSet rs = pst.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapVoluntario(rs));
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro ao listar Voluntários com filtro: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Sem filtro, usa consulta simples
            try (ResultSet rs = getConexao().consultar(sql)) {
                while (rs != null && rs.next()) {
                    lista.add(mapVoluntario(rs));
                }
            } catch (SQLException e) {
                System.err.println("Erro ao listar Voluntários: " + getConexao().getMensagemErro());
            }
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