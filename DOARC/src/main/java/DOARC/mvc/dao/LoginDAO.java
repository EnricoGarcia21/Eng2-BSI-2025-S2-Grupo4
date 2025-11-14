package DOARC.mvc.dao;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LoginDAO implements IDAO<Login>{

    public LoginDAO() {}

    @Override
    public Login gravar(Login entidade, Conexao conexao) {

        String sql = String.format(
                "INSERT INTO login (voluntario_vol_id, login, senha, nive_acesso, status) " +
                        "VALUES (%d, '%s', '%s', '%s', '%c') RETURNING login_id",
                entidade.getVoluntarioId(),
                entidade.getLogin().replace("'", "''"),
                entidade.getSenha().replace("'", "''"),
                entidade.getNiveAcesso().replace("'", "''"),
                entidade.getStatus()
        );

        try (ResultSet rs = conexao.consultar(sql)) {
            if (rs != null && rs.next()) {
                entidade.setLoginId(rs.getInt("login_id"));
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (gravar): " + conexao.getMensagemErro());
        }

        return null;
    }

    @Override
    public Login alterar(Login entidade, Conexao conexao) {

        String sql = String.format(
                "UPDATE login SET voluntario_vol_id=%d, login='%s', senha='%s', nive_acesso='%s', status='%c' " +
                        "WHERE login_id=%d",
                entidade.getVoluntarioId(),
                entidade.getLogin().replace("'", "''"),
                entidade.getSenha().replace("'", "''"),
                entidade.getNiveAcesso().replace("'", "''"),
                entidade.getStatus(),
                entidade.getLoginId()
        );

        return conexao.manipular(sql) ? entidade : null;
    }

    @Override
    public boolean apagar(Login entidade, Conexao conexao) {
        String sql = "DELETE FROM login WHERE login_id = " + entidade.getLoginId();
        return conexao.manipular(sql);
    }

    @Override
    public Login get(int id, Conexao conexao) {

        String sql = "SELECT * FROM login WHERE login_id=" + id;

        try (ResultSet rs = conexao.consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapLogin(rs);
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (get por ID): " + conexao.getMensagemErro());
        }

        return null;
    }

    @Override
    public List<Login> get(String filtro, Conexao conexao) {

        List<Login> lista = new ArrayList<>();
        filtro = filtro.replace("'", "''");

        String sql =
                "SELECT * FROM login WHERE login ILIKE '%" + filtro +
                        "%' OR nive_acesso ILIKE '%" + filtro + "%'";

        try (ResultSet rs = conexao.consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(mapLogin(rs));
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (listar): " + conexao.getMensagemErro());
        }

        return lista;
    }

    private Login mapLogin(ResultSet rs) throws SQLException {

        Login l = new Login();

        l.setLoginId(rs.getInt("login_id"));
        l.setVoluntarioId(rs.getInt("voluntario_vol_id"));
        l.setLogin(rs.getString("login"));
        l.setSenha(rs.getString("senha"));
        l.setNiveAcesso(rs.getString("nive_acesso"));

        String st = rs.getString("status");
        l.setStatus((st != null && !st.isEmpty()) ? st.charAt(0) : 'A');

        return l;
    }
}
