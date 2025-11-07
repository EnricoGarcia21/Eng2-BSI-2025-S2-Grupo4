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
            pst.setString(1, entidade.getVol_nome());
            pst.setString(2, entidade.getVol_datanasc());
            pst.setString(3, entidade.getVol_rua());
            pst.setString(4, entidade.getVol_bairro());
            pst.setString(5, entidade.getVol_cidade());
            pst.setString(6, entidade.getVol_telefone());
            pst.setString(7, entidade.getVol_cep());
            pst.setString(8, entidade.getVol_uf());
            pst.setString(9, entidade.getVol_email());
            pst.setString(10, entidade.getVol_sexo());
            pst.setString(11, entidade.getVol_numero());
            pst.setString(12, entidade.getVol_cpf());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    entidade.setVol_id(rs.getInt("vol_id"));
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
            pst.setString(1, entidade.getVol_nome());
            pst.setString(2, entidade.getVol_datanasc());
            pst.setString(3, entidade.getVol_rua());
            pst.setString(4, entidade.getVol_bairro());
            pst.setString(5, entidade.getVol_cidade());
            pst.setString(6, entidade.getVol_telefone());
            pst.setString(7, entidade.getVol_cep());
            pst.setString(8, entidade.getVol_uf());
            pst.setString(9, entidade.getVol_email());
            pst.setString(10, entidade.getVol_sexo());
            pst.setString(11, entidade.getVol_numero());
            pst.setString(12, entidade.getVol_cpf());
            pst.setInt(13, entidade.getVol_id());

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
            pst.setInt(1, entidade.getVol_id());
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
        v.setVol_id(rs.getInt("vol_id"));
        v.setVol_nome(rs.getString("vol_nome"));
        v.setVol_datanasc(rs.getString("vol_datanasc"));
        v.setVol_rua(rs.getString("vol_rua"));
        v.setVol_bairro(rs.getString("vol_bairro"));
        v.setVol_cidade(rs.getString("vol_cidade"));
        v.setVol_telefone(rs.getString("vol_telefone"));
        v.setVol_cep(rs.getString("vol_cep"));
        v.setVol_uf(rs.getString("vol_uf"));
        v.setVol_email(rs.getString("vol_email"));
        v.setVol_sexo(rs.getString("vol_sexo"));
        v.setVol_numero(rs.getString("vol_numero"));
        v.setVol_cpf(rs.getString("vol_cpf"));
        return v;
    }
}