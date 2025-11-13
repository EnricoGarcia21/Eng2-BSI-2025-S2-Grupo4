package DOARC.mvc.dao;

import DOARC.mvc.model.AgendarRetirada;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AgendarRetiradaDAO implements IDAO<AgendarRetirada> {

    @Override
    public AgendarRetirada gravar(AgendarRetirada entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao gravar");
            return null;
        }

        String sql = "INSERT INTO agendar_retirada (data_retiro, hora_retiro, obs_retiro, vol_id, doa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, entidade.getDataRetiro());
            pst.setString(2, entidade.getHoraRetiro());
            pst.setString(3, entidade.getObsRetiro());
            pst.setInt(4, entidade.getVolId());
            pst.setInt(5, entidade.getDoaId());

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entidade.setAgendaId(generatedKeys.getInt(1));
                        return entidade;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao gravar agendamento: " + e.getMessage());
        }
        return null;
    }

    @Override
    public AgendarRetirada alterar(AgendarRetirada entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao alterar");
            return null;
        }

        String sql = "UPDATE agendar_retirada SET data_retiro=?, hora_retiro=?, obs_retiro=?, vol_id=?, doa_id=? WHERE agenda_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setString(1, entidade.getDataRetiro());
            pst.setString(2, entidade.getHoraRetiro());
            pst.setString(3, entidade.getObsRetiro());
            pst.setInt(4, entidade.getVolId());
            pst.setInt(5, entidade.getDoaId());
            pst.setInt(6, entidade.getAgendaId());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao alterar agendamento: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean apagar(AgendarRetirada entidade, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao apagar");
            return false;
        }

        String sql = "DELETE FROM agendar_retirada WHERE agenda_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, entidade.getAgendaId());
            int deleted = pst.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao apagar agendamento: " + e.getMessage());
        }
        return false;
    }

    @Override
    public AgendarRetirada get(int id, SingletonDB conexao) {
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar por ID");
            return null;
        }

        String sql = "SELECT * FROM agendar_retirada WHERE agenda_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapAgendarRetirada(rs);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar agendamento: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<AgendarRetirada> get(String filtro, SingletonDB conexao) {
        List<AgendarRetirada> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao listar");
            return lista;
        }

        String sql = "SELECT * FROM agendar_retirada";
        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        try (Statement st = conexao.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapAgendarRetirada(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao listar agendamentos: " + e.getMessage());
        }
        return lista;
    }

    public List<AgendarRetirada> getPorVoluntario(int volId, SingletonDB conexao) {
        List<AgendarRetirada> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar por voluntário");
            return lista;
        }

        String sql = "SELECT * FROM agendar_retirada WHERE vol_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, volId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapAgendarRetirada(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar agendamentos por voluntário: " + e.getMessage());
        }
        return lista;
    }

    public List<AgendarRetirada> getPorDoacao(int doaId, SingletonDB conexao) {
        List<AgendarRetirada> lista = new ArrayList<>();
        if (!conexao.isConectado()) {
            System.out.println("DEBUG: Conexão não está ativa ao buscar por doação");
            return lista;
        }

        String sql = "SELECT * FROM agendar_retirada WHERE doa_id=?";

        try (PreparedStatement pst = conexao.getConnection().prepareStatement(sql)) {
            pst.setInt(1, doaId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapAgendarRetirada(rs));
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Erro ao buscar agendamentos por doação: " + e.getMessage());
        }
        return lista;
    }

    private AgendarRetirada mapAgendarRetirada(ResultSet rs) throws SQLException {
        AgendarRetirada agendar = new AgendarRetirada();
        agendar.setAgendaId(rs.getInt("agenda_id"));
        agendar.setDataRetiro(rs.getString("data_retiro"));
        agendar.setHoraRetiro(rs.getString("hora_retiro"));
        agendar.setObsRetiro(rs.getString("obs_retiro"));
        agendar.setVolId(rs.getInt("vol_id"));
        agendar.setDoaId(rs.getInt("doa_id"));
        return agendar;
    }
}