package DOARC.mvc.dao;

import DOARC.mvc.model.HigienizacaoRoupa;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
public class HigienizacaoRoupaDAO implements IDAO<HigienizacaoRoupa> {

    @Override
    public HigienizacaoRoupa gravar(HigienizacaoRoupa entidade, Conexao conexao) { // Recebe Conexao
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        String valorFormatado = df.format(entidade.getHigValorPago());

        String sql = String.format(
                "INSERT INTO higienizacao_roupas (hig_data_agendada, hig_descricao_roupa, vol_id, hig_local, hig_hora, hig_valorpago) " +
                        "VALUES ('%s', '%s', %d, '%s', '%s', %s) RETURNING hig_id",
                entidade.getHigDataAgendada().replace("'", "''"),
                entidade.getHigDescricaoRoupa().replace("'", "''"),
                entidade.getVolId(),
                entidade.getHigLocal().replace("'", "''"),
                entidade.getHigHora().replace("'", "''"),
                valorFormatado
        );

        try (ResultSet rs = conexao.consultar(sql)) { // Usa Conexao do parâmetro
            if (rs != null && rs.next()) {
                entidade.setHigId(rs.getInt("hig_id"));
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Higienização (SQL): " + conexao.getMensagemErro());
        }
        return null;
    }

    @Override
    public HigienizacaoRoupa alterar(HigienizacaoRoupa entidade, Conexao conexao) { // Recebe Conexao
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        String valorFormatado = df.format(entidade.getHigValorPago());

        String sql = String.format(
                "UPDATE higienizacao_roupas SET hig_data_agendada='%s', hig_descricao_roupa='%s', vol_id=%d, hig_local='%s', hig_hora='%s', hig_valorpago=%s " +
                        "WHERE hig_id=%d",
                entidade.getHigDataAgendada().replace("'", "''"),
                entidade.getHigDescricaoRoupa().replace("'", "''"),
                entidade.getVolId(),
                entidade.getHigLocal().replace("'", "''"),
                entidade.getHigHora().replace("'", "''"),
                valorFormatado,
                entidade.getHigId()
        );

        return conexao.manipular(sql) ? entidade : null; // Usa Conexao do parâmetro
    }

    @Override
    public boolean apagar(HigienizacaoRoupa entidade, Conexao conexao) { // Recebe Conexao
        String sql = "DELETE FROM higienizacao_roupas WHERE hig_id=" + entidade.getHigId();
        return conexao.manipular(sql); // Usa Conexao do parâmetro
    }

    @Override
    public HigienizacaoRoupa get(int id, Conexao conexao) { // Recebe Conexao
        String sql = "SELECT * FROM higienizacao_roupas WHERE hig_id=" + id;
        try (ResultSet rs = conexao.consultar(sql)) { // Usa Conexao do parâmetro
            if (rs != null && rs.next()) {
                return mapHigienizacaoRoupa(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Higienização por ID: " + conexao.getMensagemErro());
        }
        return null;
    }

    @Override
    public List<HigienizacaoRoupa> get(String filtro, Conexao conexao) { // Recebe Conexao
        List<HigienizacaoRoupa> lista = new ArrayList<>();
        String sql = "SELECT * FROM higienizacao_roupas";
        if (filtro != null && !filtro.isEmpty()) {
            sql += String.format(" WHERE hig_descricao_roupa ILIKE '%%%s%%' OR hig_local ILIKE '%%%s%%'",
                    filtro.replace("'", "''"), filtro.replace("'", "''"));
        }

        try (ResultSet rs = conexao.consultar(sql)) { // Usa Conexao do parâmetro
            while (rs != null && rs.next()) {
                lista.add(mapHigienizacaoRoupa(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Higienizações: " + conexao.getMensagemErro());
        }
        return lista;
    }

    private HigienizacaoRoupa mapHigienizacaoRoupa(ResultSet rs) throws SQLException {
        HigienizacaoRoupa h = new HigienizacaoRoupa();
        h.setHigId(rs.getInt("hig_id"));
        h.setHigDataAgendada(rs.getString("hig_data_agendada"));
        h.setHigDescricaoRoupa(rs.getString("hig_descricao_roupa"));
        h.setVolId(rs.getInt("vol_id"));
        h.setHigLocal(rs.getString("hig_local"));
        h.setHigHora(rs.getString("hig_hora"));
        h.setHigValorPago(rs.getDouble("hig_valorpago"));
        return h;
    }
}