package DOARC.mvc.dao;

import DOARC.mvc.model.VerificarNecessidade;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VerificarNecessidadeDAO implements IDAO<VerificarNecessidade> {

    @Override
    public VerificarNecessidade gravar(VerificarNecessidade entidade, Conexao conexao) {
        String sql = String.format(
                "INSERT INTO verificar_necessidade (ver_data, ver_obs, ver_resultado, vol_id, doa_id) " +
                        "VALUES ('%s', '%s', '%s', %d, %d) RETURNING ver_id",
                entidade.getVerData().replace("'", "''"),
                entidade.getVerObs().replace("'", "''"),
                entidade.getVerResultado().replace("'", "''"),
                entidade.getVolId(),
                entidade.getDoaId()
        );

        try (ResultSet rs = conexao.consultar(sql)) {
            if (rs != null && rs.next()) {
                entidade.setVerId(rs.getInt("ver_id"));
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Verificação (SQL): " + conexao.getMensagemErro());
        }
        return null;
    }

    @Override
    public VerificarNecessidade alterar(VerificarNecessidade entidade, Conexao conexao) {
        String sql = String.format(
                "UPDATE verificar_necessidade SET ver_data='%s', ver_obs='%s', ver_resultado='%s', vol_id=%d, doa_id=%d " +
                        "WHERE ver_id=%d",
                entidade.getVerData().replace("'", "''"),
                entidade.getVerObs().replace("'", "''"),
                entidade.getVerResultado().replace("'", "''"),
                entidade.getVolId(),
                entidade.getDoaId(),
                entidade.getVerId()
        );

        return conexao.manipular(sql) ? entidade : null;
    }

    @Override
    public boolean apagar(VerificarNecessidade entidade, Conexao conexao) {
        String sql = "DELETE FROM verificar_necessidade WHERE ver_id=" + entidade.getVerId();
        return conexao.manipular(sql);
    }

    @Override
    public VerificarNecessidade get(int id, Conexao conexao) {
        String sql = "SELECT * FROM verificar_necessidade WHERE ver_id=" + id;
        try (ResultSet rs = conexao.consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapVerificarNecessidade(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Verificação por ID: " + conexao.getMensagemErro());
        }
        return null;
    }

    @Override
    public List<VerificarNecessidade> get(String filtro, Conexao conexao) {
        List<VerificarNecessidade> lista = new ArrayList<>();
        String sql = "SELECT * FROM verificar_necessidade";
        if (filtro != null && !filtro.isEmpty()) {
            // Filtro pode ser aplicado a ver_obs ou ver_resultado
            sql += String.format(" WHERE ver_obs ILIKE '%%%s%%' OR ver_resultado ILIKE '%%%s%%'",
                    filtro.replace("'", "''"), filtro.replace("'", "''"));
        }

        try (ResultSet rs = conexao.consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(mapVerificarNecessidade(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Verificações: " + conexao.getMensagemErro());
        }
        return lista;
    }

    private VerificarNecessidade mapVerificarNecessidade(ResultSet rs) throws SQLException {
        VerificarNecessidade v = new VerificarNecessidade();
        v.setVerId(rs.getInt("ver_id"));
        v.setVerData(rs.getString("ver_data"));
        v.setVerObs(rs.getString("ver_obs"));
        v.setVerResultado(rs.getString("ver_resultado"));
        v.setVolId(rs.getInt("vol_id"));
        v.setDoaId(rs.getInt("doa_id"));
        return v;
    }
}