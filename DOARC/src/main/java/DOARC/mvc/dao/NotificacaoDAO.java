package DOARC.mvc.dao;

import DOARC.mvc.model.Notificacao;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacaoDAO implements IDAO<Notificacao> {
    @Override
    public Notificacao gravar(Notificacao entidade, Conexao conn) {
        String sql = "INSERT INTO notificacao (not_texto, not_data, not_hora, vol_id) VALUES (?, ?, ?, ?) RETURNING not_id";

        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            if (entidade.getNotTexto() == null || entidade.getNotTexto().isEmpty()) {
                pst.setNull(1, java.sql.Types.VARCHAR);
            } else {
                pst.setString(1, entidade.getNotTexto());
            }



            pst.setString(2, entidade.getNotData());
            pst.setString(3, entidade.getNotHora());

            if (entidade.getVolId() == 0) {
                pst.setNull(4, java.sql.Types.INTEGER);
            } else {
                pst.setInt(4, entidade.getVolId());
            }

            int linhasAfetadas = pst.executeUpdate();

            if (linhasAfetadas > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    entidade.setNotId(rs.getInt(1));
                }
                conn.commit();
                return entidade;
            }

        } catch (SQLException e) {
            System.err.println("FATAL SQL EXCEPTION: Erro ao gravar Notificação: " + e.getMessage());
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        return null;
    }
    @Override
    public Notificacao alterar(Notificacao entidade, Conexao conn) {
        String sql = "UPDATE notificacao SET not_texto=?, not_data=?, not_hora=?, vol_id=? WHERE not_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getNotTexto());
            pst.setString(2, entidade.getNotData());
            pst.setString(3, entidade.getNotHora());
            pst.setInt(4, entidade.getVolId());
            pst.setInt(5, entidade.getNotId());
            if (pst.executeUpdate() > 0) {
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Notificacao entidade, Conexao conn) {
        String sql = "DELETE FROM notificacao WHERE not_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, entidade.getNotId());
            if (pst.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Notificacao get(int id, Conexao conn) {
        String sql = "SELECT * FROM notificacao WHERE not_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Notificacao n = new Notificacao();
                n.setNotId(rs.getInt("not_id"));
                n.setNotTexto(rs.getString("not_texto"));
                n.setNotData(rs.getString("not_data"));
                n.setNotHora(rs.getString("not_hora"));
                n.setVolId(rs.getInt("vol_id"));
                return n;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Notificacao> get(String filtro, Conexao conn) {
        return new ArrayList<>();
    }

    public boolean vincularDonatario(int donatarioId, int notificacaoId, Conexao conn) {
        String sql = "INSERT INTO notificacao_donatario (don_id, not_id) VALUES (?, ?)";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, donatarioId);
            pst.setInt(2, notificacaoId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}