package DOARC.mvc.dao;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CampanhaDAO implements IDAO<Campanha> {

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    @Override
    public Campanha gravar(Campanha entidade) {
        String sql = String.format("INSERT INTO campanha (cam_data_ini, cam_data_fim, voluntario_vol_id, cam_desc, cam_meta_arrecadacao, cam_valor_arrecadado) VALUES ('%s', '%s', %d, '%s', %.2f, %.2f) RETURNING cam_id",
                entidade.getCam_data_ini().replace("'", "''"),
                entidade.getCam_data_fim().replace("'", "''"),
                entidade.getVoluntario_vol_id(),
                entidade.getCam_desc().replace("'", "''"),
                entidade.getCam_meta_arrecadacao(),
                entidade.getCam_valor_arrecadado()
        );

        try (ResultSet rs = getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                entidade.setCam_id(rs.getInt("cam_id"));
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Campanha (SQL): " + getConexao().getMensagemErro());
        }
        return null;
    }

    @Override
    public Campanha alterar(Campanha entidade) {
        String sql = String.format("UPDATE campanha SET cam_data_ini='%s', cam_data_fim='%s', voluntario_vol_id=%d, cam_desc='%s', cam_meta_arrecadacao=%.2f, cam_valor_arrecadado=%.2f WHERE cam_id=%d",
                entidade.getCam_data_ini().replace("'", "''"),
                entidade.getCam_data_fim().replace("'", "''"),
                entidade.getVoluntario_vol_id(),
                entidade.getCam_desc().replace("'", "''"),
                entidade.getCam_meta_arrecadacao(),
                entidade.getCam_valor_arrecadado(),
                entidade.getCam_id()
        );

        return getConexao().manipular(sql) ? entidade : null;
    }

    @Override
    public boolean apagar(Campanha entidade) {
        String sql = "DELETE FROM campanha WHERE cam_id=" + entidade.getCam_id();
        return getConexao().manipular(sql);
    }

    @Override
    public Campanha get(int id) {
        String sql = "SELECT * FROM campanha WHERE cam_id=" + id;
        try (ResultSet rs = getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapCampanha(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Campanha por ID: " + getConexao().getMensagemErro());
        }
        return null;
    }

    @Override
    public List<Campanha> get(String filtro) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha";
        if (filtro != null && !filtro.isEmpty()) {
            sql += String.format(" WHERE cam_desc ILIKE '%%%s%%'",
                    filtro.replace("'", "''"));
        }

        try (ResultSet rs = getConexao().consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(mapCampanha(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Campanhas: " + getConexao().getMensagemErro());
        }
        return lista;
    }

    private Campanha mapCampanha(ResultSet rs) throws SQLException {
        Campanha c = new Campanha();
        c.setCam_id(rs.getInt("cam_id"));
        c.setCam_data_ini(rs.getString("cam_data_ini"));
        c.setCam_data_fim(rs.getString("cam_data_fim"));
        c.setVoluntario_vol_id(rs.getInt("voluntario_vol_id"));
        c.setCam_desc(rs.getString("cam_desc"));
        c.setCam_meta_arrecadacao(rs.getDouble("cam_meta_arrecadacao"));
        c.setCam_valor_arrecadado(rs.getDouble("cam_valor_arrecadado"));
        return c;
    }

    // Método adicional para buscar campanhas por voluntário
    public List<Campanha> getCampanhasPorVoluntario(int voluntarioId) {
        List<Campanha> lista = new ArrayList<>();
        String sql = "SELECT * FROM campanha WHERE voluntario_vol_id=" + voluntarioId;

        try (ResultSet rs = getConexao().consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(mapCampanha(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Campanhas por Voluntário: " + getConexao().getMensagemErro());
        }
        return lista;
    }

    // Método para verificar se o voluntário existe
    private boolean voluntarioExiste(int voluntarioId) {
        String sql = "SELECT 1 FROM voluntario WHERE vol_id=" + voluntarioId;
        try (ResultSet rs = getConexao().consultar(sql)) {
            return rs != null && rs.next();
        } catch (SQLException e) {
            System.err.println("Erro ao verificar Voluntário: " + getConexao().getMensagemErro());
            return false;
        }
    }
}