package DOARC.mvc.dao;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class LoginDAO {

    private Connection conn;

    public LoginDAO() {
        conn = SingletonDB.getConexao().getConnect();
    }

    public Login buscarPorLogin(String login) {
        String sql = "SELECT * FROM login WHERE login = ? AND status = 'A'"; // 'A' para ATIVO
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, login);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapLogin(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Login buscarPorVoluntarioId(int voluntarioId) {
        String sql = "SELECT * FROM login WHERE voluntario_vol_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, voluntarioId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapLogin(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean criarLogin(Login login) {
        String sql = "INSERT INTO login (voluntario_vol_id, login, senha, nive_acesso, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, login.getVoluntarioId());
            pst.setString(2, login.getLogin());
            pst.setString(3, login.getSenha());
            pst.setString(4, login.getNive_acesso()); // ✅ CORRIGIDO: getNive_acesso()
            pst.setString(5, String.valueOf(login.getStatus())); // ✅ CORRIGIDO: char para String

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Pode ser violação de chave única (já existe login para este voluntário)
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("unique constraint")) {
                System.out.println("Já existe login para o voluntário ID: " + login.getVoluntarioId());
            }
        }
        return false;
    }

    public boolean atualizarSenha(int voluntarioId, String novaSenha) {
        String sql = "UPDATE login SET senha = ? WHERE voluntario_vol_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, novaSenha);
            pst.setInt(2, voluntarioId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean alterarStatus(int voluntarioId, char status) { // ✅ CORRIGIDO: char como parâmetro
        String sql = "UPDATE login SET status = ? WHERE voluntario_vol_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, String.valueOf(status)); // ✅ CORRIGIDO: char para String
            pst.setInt(2, voluntarioId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean excluirLogin(int voluntarioId) {
        String sql = "DELETE FROM login WHERE voluntario_vol_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, voluntarioId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Login mapLogin(ResultSet rs) throws SQLException {
        Login login = new Login();
        login.setVoluntarioId(rs.getInt("voluntario_vol_id"));
        login.setLogin(rs.getString("login"));
        login.setSenha(rs.getString("senha"));
        login.setNive_acesso(rs.getString("nive_acesso")); // ✅ CORRIGIDO: setNive_acesso()

        // ✅ CORRIGIDO: Converte String do banco para char
        String statusStr = rs.getString("status");
        if (statusStr != null && !statusStr.isEmpty()) {
            login.setStatus(statusStr.charAt(0));
        }

        return login;
    }
}