package DOARC.mvc.dao;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LoginDAO implements IDAO<Login> {

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    @Override
    public Login gravar(Login entidade) {
        // ⚠️ CORREÇÃO DE SEGURANÇA: Usando PreparedStatement para prevenir SQL Injection
        Connection conn = getConexao().getConnect();
        // 🚨 CORRIGIDO: O RETURNING DEVE SER 'login_id'
        String sql = "INSERT INTO login (voluntario_vol_id, login, senha, nive_acesso, status) VALUES (?, ?, ?, ?, ?) RETURNING login_id";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            // Define os parâmetros
            pst.setInt(1, entidade.getVoluntarioId());
            pst.setString(2, entidade.getLogin());
            pst.setString(3, entidade.getSenha());
            pst.setString(4, entidade.getNiveAcesso());
            pst.setString(5, String.valueOf(entidade.getStatus()));

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    // 🚨 CORRIGIDO: Lendo o 'login_id' gerado
                    entidade.setLoginId(rs.getInt("login_id"));
                    return entidade;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Login (SQL EXCEPTION): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Login alterar(Login entidade) {
        String sql = String.format("UPDATE login SET voluntario_vol_id=%d, login='%s', senha='%s', nive_acesso='%s', status='%s' WHERE login_id=%d",
                entidade.getVoluntarioId(),
                entidade.getLogin().replace("'", "''"),
                entidade.getSenha().replace("'", "''"),
                entidade.getNiveAcesso().replace("'", "''"),
                String.valueOf(entidade.getStatus()).replace("'", "''"),
                entidade.getLoginId()
        );

        return getConexao().manipular(sql) ? entidade : null;
    }

    @Override
    public boolean apagar(Login entidade) {
        String sql = "DELETE FROM login WHERE login_id=" + entidade.getLoginId();
        return getConexao().manipular(sql);
    }

    @Override
    public Login get(int id) {
        String sql = "SELECT * FROM login WHERE login_id=" + id;
        try (ResultSet rs = getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapLogin(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Login por ID: " + getConexao().getMensagemErro());
        }
        return null;
    }

    @Override
    public List<Login> get(String filtro) {
        List<Login> lista = new ArrayList<>();
        String sql = "SELECT * FROM login";
        if (filtro != null && !filtro.isEmpty()) {
            sql += String.format(" WHERE login ILIKE '%%%s%%' OR nive_acesso ILIKE '%%%s%%'",
                    filtro.replace("'", "''"), filtro.replace("'", "''"));
        }

        try (ResultSet rs = getConexao().consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(mapLogin(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Logins: " + getConexao().getMensagemErro());
        }
        return lista;
    }

    // 🚨 NOVO MÉTODO: Busca de Login por nome de usuário EXATO.
    public Login buscarPorLoginExato(String login) {
        String sql = String.format("SELECT * FROM login WHERE login = '%s'",
                login.replace("'", "''"));

        try (ResultSet rs = getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapLogin(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Login por nome de usuário: " + getConexao().getMensagemErro());
        }
        return null;
    }

    public Login buscarPorVoluntarioId(int voluntarioId) {
        String sql = "SELECT * FROM login WHERE voluntario_vol_id=" + voluntarioId;
        try (ResultSet rs = getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapLogin(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Login por Voluntario ID: " + getConexao().getMensagemErro());
        }
        return null;
    }

    public boolean atualizarSenha(int loginId, String novaSenha) {
        String sql = String.format("UPDATE login SET senha='%s' WHERE login_id=%d",
                novaSenha.replace("'", "''"), loginId);
        return getConexao().manipular(sql);
    }

    public boolean alterarStatus(int loginId, char status) {
        String sql = String.format("UPDATE login SET status='%s' WHERE login_id=%d",
                String.valueOf(status).replace("'", "''"), loginId);
        return getConexao().manipular(sql);
    }

    private Login mapLogin(ResultSet rs) throws SQLException {
        Login login = new Login();
        login.setLoginId(rs.getInt("login_id"));
        login.setVoluntarioId(rs.getInt("voluntario_vol_id"));
        login.setLogin(rs.getString("login"));
        login.setSenha(rs.getString("senha"));
        login.setNiveAcesso(rs.getString("nive_acesso"));

        String statusStr = rs.getString("status");
        if (statusStr != null && !statusStr.isEmpty()) {
            login.setStatus(statusStr.charAt(0));
        }

        return login;
    }
}