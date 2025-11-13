package DOARC.mvc.dao;

import DOARC.mvc.model.Doacao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DoacaoDAO implements IDAO<Doacao> {

    @Override
    public Doacao gravar(Doacao entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao gravar doação");
            return null;
        }

        String sql = "INSERT INTO doacao (vol_id, data_doacao, obs_doacao, valor_doado, doa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, entidade.getVolId());
            pst.setString(2, entidade.getDataDoacao());
            pst.setString(3, entidade.getObsDoacao());
            pst.setBigDecimal(4, entidade.getValorDoado());
            pst.setInt(5, entidade.getDoaId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entidade.setDoacaoId(generatedKeys.getInt(1));
                        return entidade;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao gravar doação: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Doacao alterar(Doacao entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao alterar doação");
            return null;
        }

        String sql = "UPDATE doacao SET vol_id=?, data_doacao=?, obs_doacao=?, valor_doado=?, doa_id=? WHERE doacao_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, entidade.getVolId());
            pst.setString(2, entidade.getDataDoacao());
            pst.setString(3, entidade.getObsDoacao());
            pst.setBigDecimal(4, entidade.getValorDoado());
            pst.setInt(5, entidade.getDoaId());
            pst.setInt(6, entidade.getDoacaoId());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao alterar doação: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean apagar(Doacao entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao apagar doação");
            return false;
        }

        String sql = "DELETE FROM doacao WHERE doacao_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, entidade.getDoacaoId());
            int deleted = pst.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao apagar doação: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Doacao get(int id, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar doação por ID");
            return null;
        }

        String sql = "SELECT * FROM doacao WHERE doacao_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapDoacao(rs);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar doação: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Doacao> get(String filtro, SingletonDB conexao) {
        List<Doacao> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao listar doações");
            return lista;
        }

        String sql = "SELECT * FROM doacao";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }
        sql += " ORDER BY doacao_id DESC";

        try (Statement st = conexao.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapDoacao(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao listar doações: " + e.getMessage());
        }
        return lista;
    }

    public List<Doacao> getPorVoluntario(int volId, SingletonDB conexao) {
        List<Doacao> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar doações por voluntário");
            return lista;
        }

        String sql = "SELECT * FROM doacao WHERE vol_id=? ORDER BY doacao_id DESC";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, volId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapDoacao(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar doações por voluntário: " + e.getMessage());
        }
        return lista;
    }

    public List<Doacao> getPorDoador(int doaId, SingletonDB conexao) {
        List<Doacao> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar doações por doador");
            return lista;
        }

        String sql = "SELECT * FROM doacao WHERE doa_id=? ORDER BY doacao_id DESC";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, doaId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapDoacao(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar doações por doador: " + e.getMessage());
        }
        return lista;
    }

    public BigDecimal getTotalDoacoesPeriodo(String dataInicio, String dataFim, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar total de doações");
            return BigDecimal.ZERO;
        }

        String sql = "SELECT COALESCE(SUM(valor_doado), 0) as total FROM doacao WHERE data_doacao BETWEEN ? AND ?";
        BigDecimal total = BigDecimal.ZERO;

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setString(1, dataInicio);
            pst.setString(2, dataFim);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                total = rs.getBigDecimal("total");
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar total de doações: " + e.getMessage());
        }
        return total;
    }

    private Doacao mapDoacao(ResultSet rs) throws SQLException {
        Doacao doacao = new Doacao();
        doacao.setDoacaoId(rs.getInt("doacao_id"));
        doacao.setVolId(rs.getInt("vol_id"));
        doacao.setDataDoacao(rs.getString("data_doacao"));
        doacao.setObsDoacao(rs.getString("obs_doacao"));
        doacao.setValorDoado(rs.getBigDecimal("valor_doado"));
        doacao.setDoaId(rs.getInt("doa_id"));
        return doacao;
    }
}