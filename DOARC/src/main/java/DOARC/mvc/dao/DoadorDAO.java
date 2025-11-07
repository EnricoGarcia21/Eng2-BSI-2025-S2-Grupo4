package DOARC.mvc.dao;

import DOARC.mvc.model.Doador;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DoadorDAO implements IDAO<Doador> {

    @Override
    public Doador gravar(Doador entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao gravar doador");
            return null;
        }

        String sql = "INSERT INTO doadores (doa_nome, doa_telefone, doa_email, doa_cep, doa_uf, " +
                "doa_cidade, doa_bairro, doa_rua, doa_cpf, doa_datanasc, doa_sexo, doa_site) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entidade.setDoaId(generatedKeys.getInt(1));
                        return entidade;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao gravar doador: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Doador alterar(Doador entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao alterar doador");
            return null;
        }

        String sql = "UPDATE doadores SET doa_nome=?, doa_telefone=?, doa_email=?, doa_cep=?, " +
                "doa_uf=?, doa_cidade=?, doa_bairro=?, doa_rua=?, doa_cpf=?, doa_datanasc=?, " +
                "doa_sexo=?, doa_site=? WHERE doa_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
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
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao alterar doador: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean apagar(Doador entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao apagar doador");
            return false;
        }

        String sql = "DELETE FROM doadores WHERE doa_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, entidade.getDoaId());
            int deleted = pst.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao apagar doador: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Doador get(int id, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar doador por ID");
            return null;
        }

        String sql = "SELECT * FROM doadores WHERE doa_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapDoador(rs);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar doador: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Doador> get(String filtro, SingletonDB conexao) {
        List<Doador> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao listar doadores");
            return lista;
        }

        String sql = "SELECT * FROM doadores";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (Statement st = conexao.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapDoador(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao listar doadores: " + e.getMessage());
        }
        return lista;
    }

    // Método específico para buscar todos os doadores (alias para get com filtro null)
    public List<Doador> getAll(SingletonDB conexao) {
        return get(null, conexao);
    }

    // Método específico para buscar doadores por cidade
    public List<Doador> getPorCidade(String cidade, SingletonDB conexao) {
        List<Doador> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar doadores por cidade");
            return lista;
        }

        String sql = "SELECT * FROM doadores WHERE doa_cidade = ?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setString(1, cidade);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapDoador(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar doadores por cidade: " + e.getMessage());
        }
        return lista;
    }

    // Método específico para buscar doadores por bairro
    public List<Doador> getPorBairro(String bairro, SingletonDB conexao) {
        List<Doador> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar doadores por bairro");
            return lista;
        }

        String sql = "SELECT * FROM doadores WHERE doa_bairro = ?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setString(1, bairro);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapDoador(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar doadores por bairro: " + e.getMessage());
        }
        return lista;
    }

    private Doador mapDoador(ResultSet rs) throws SQLException {
        Doador doador = new Doador();
        doador.setDoaId(rs.getInt("doa_id"));
        doador.setDoaNome(rs.getString("doa_nome"));
        doador.setDoaTelefone(rs.getString("doa_telefone"));
        doador.setDoaEmail(rs.getString("doa_email"));
        doador.setDoaCep(rs.getString("doa_cep"));
        doador.setDoaUf(rs.getString("doa_uf"));
        doador.setDoaCidade(rs.getString("doa_cidade"));
        doador.setDoaBairro(rs.getString("doa_bairro"));
        doador.setDoaRua(rs.getString("doa_rua"));
        doador.setDoaCpf(rs.getString("doa_cpf"));
        doador.setDoaDataNasc(rs.getString("doa_datanasc"));
        doador.setDoaSexo(rs.getString("doa_sexo"));
        doador.setDoaSite(rs.getString("doa_site"));
        return doador;
    }
}