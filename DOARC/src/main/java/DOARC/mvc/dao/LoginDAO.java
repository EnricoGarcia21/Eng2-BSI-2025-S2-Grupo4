package DOARC.mvc.dao;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {

    // ==================================================================
    // CONVERSORES (Cruciais para evitar o erro de Constraint do Banco)
    // ==================================================================

    private String convertRoleToDb(String role) {
        if (role == null) return "Operacional";
        switch (role.toUpperCase()) {
            case "ADMIN": case "ADMINISTRADOR": return "Administrador";
            case "MANAGER": case "LOGISTICA": return "Logistica";
            default: return "Operacional";
        }
    }

    private String convertRoleFromDb(String roleDB) {
        if (roleDB == null) return "USER";
        switch (roleDB) {
            case "Administrador": return "ADMIN";
            case "Logistica": return "MANAGER";
            default: return "USER";
        }
    }

    private String convertStatusToDb(char status) {
        // CORREÇÃO: O banco espera a palavra completa "Ativo"/"Inativo"
        return (status == 'A' || status == 'a') ? "Ativo" : "Inativo";
    }

    private char convertStatusFromDb(String statusDB) {
        return (statusDB != null && statusDB.equalsIgnoreCase("Ativo")) ? 'A' : 'I';
    }

    // ==================================================================
    // MÉTODOS CRUD (Completos)
    // ==================================================================

    public Login gravar(Login entidade, Conexao conexao) {
        String sql = "INSERT INTO login (vol_id, login, senha, nive_acesso, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, entidade.getVoluntarioId());
            stmt.setString(2, entidade.getLogin());
            stmt.setString(3, entidade.getSenha());
            stmt.setString(4, convertRoleToDb(entidade.getNivelAcesso()));
            stmt.setString(5, convertStatusToDb(entidade.getStatus())); // Usa conversor

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                entidade.setLoginId(entidade.getVoluntarioId());
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Login alterar(Login entidade, Conexao conexao) {
        String sql = "UPDATE login SET login=?, senha=?, nive_acesso=?, status=? WHERE vol_id=?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setString(1, entidade.getLogin());
            stmt.setString(2, entidade.getSenha());
            stmt.setString(3, convertRoleToDb(entidade.getNivelAcesso()));
            stmt.setString(4, convertStatusToDb(entidade.getStatus())); // Usa conversor
            stmt.setInt(5, entidade.getVoluntarioId());

            return stmt.executeUpdate() > 0 ? entidade : null;
        } catch (SQLException e) {
            System.err.println("Erro ao alterar login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean apagar(Login entidade, Conexao conexao) {
        String sql = "DELETE FROM login WHERE vol_id = ?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, entidade.getVoluntarioId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar login: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Login get(int id, Conexao conexao) {
        String sql = "SELECT * FROM login WHERE vol_id = ?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapLogin(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Login> get(String filtro, Conexao conexao) {
        List<Login> lista = new ArrayList<>();
        String sql = "SELECT * FROM login WHERE login ILIKE ? OR nive_acesso ILIKE ?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            String filtroLike = "%" + filtro + "%";
            stmt.setString(1, filtroLike);
            stmt.setString(2, filtroLike);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lista.add(mapLogin(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Login buscarPorLogin(String email, Conexao conexao) {
        String sql = "SELECT * FROM login WHERE LOWER(login) = LOWER(?)";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapLogin(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================================================================
    // MÉTODOS ESPECÍFICOS (Atualização de Status e Senha)
    // ==================================================================

    public boolean atualizarStatus(int voluntarioId, String novoStatus, Conexao conexao) {
        String sql = "UPDATE login SET status = ? WHERE vol_id = ?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            // Pega o primeiro caractere ('A' ou 'I') se for string longa
            char statusChar = (novoStatus != null && !novoStatus.isEmpty()) ? novoStatus.charAt(0) : 'I';

            // Converte para a palavra completa esperada pelo banco ("Ativo"/"Inativo")
            stmt.setString(1, convertStatusToDb(statusChar));

            stmt.setInt(2, voluntarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro SQL ao atualizar status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean atualizarSenha(int voluntarioId, String novaSenhaHash, Conexao conexao) {
        String sql = "UPDATE login SET senha = ? WHERE vol_id = ?";
        try (PreparedStatement stmt = conexao.getConnect().prepareStatement(sql)) {
            stmt.setString(1, novaSenhaHash);
            stmt.setInt(2, voluntarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==================================================================
    // HELPER DE MAPEAMENTO
    // ==================================================================

    private Login mapLogin(ResultSet rs) throws SQLException {
        Login l = new Login();
        int volId = rs.getInt("vol_id");
        l.setVoluntarioId(volId);
        l.setLoginId(volId);
        l.setLogin(rs.getString("login"));
        l.setSenha(rs.getString("senha"));
        l.setNivelAcesso(convertRoleFromDb(rs.getString("nive_acesso")));
        l.setStatus(convertStatusFromDb(rs.getString("status")));
        return l;
    }
}