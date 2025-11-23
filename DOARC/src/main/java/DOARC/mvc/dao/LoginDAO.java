package DOARC.mvc.dao;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Login - Baseado em BANCO-COMPLETO.txt
 *
 * Tabela Login:
 * - VOL_ID INTEGER PRIMARY KEY
 * - login VARCHAR(50) UNIQUE NOT NULL
 * - senha VARCHAR(255) NOT NULL
 * - nive_acesso VARCHAR(20) CHECK ('Administrador', 'Logistica', 'Operacional')
 * - status VARCHAR(10) CHECK ('Ativo', 'Inativo')
 */
@Repository
public class LoginDAO implements IDAO<Login> {

    public LoginDAO() {}

    @Override
    public Login gravar(Login entidade, Conexao conexao) {
        String sql = "INSERT INTO login (vol_id, login, senha, nive_acesso, status) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.gravar: Conexão é null");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, entidade.getVoluntarioId());
                stmt.setString(2, entidade.getLogin());
                stmt.setString(3, entidade.getSenha());
                stmt.setString(4, convertRoleToDb(entidade.getNivelAcesso()));
                stmt.setString(5, convertStatusToDb(entidade.getStatus()));

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    entidade.setLoginId(entidade.getVoluntarioId());
                    System.out.println("✅ Login criado: " + entidade.getLogin());
                    return entidade;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao gravar Login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Login alterar(Login entidade, Conexao conexao) {
        String sql = "UPDATE login SET login=?, senha=?, nive_acesso=?, status=? WHERE vol_id=?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.alterar: Conexão é null");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, entidade.getLogin());
                stmt.setString(2, entidade.getSenha());
                stmt.setString(3, convertRoleToDb(entidade.getNivelAcesso()));
                stmt.setString(4, convertStatusToDb(entidade.getStatus()));
                stmt.setInt(5, entidade.getVoluntarioId());

                return stmt.executeUpdate() > 0 ? entidade : null;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao alterar Login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Login entidade, Conexao conexao) {
        String sql = "DELETE FROM login WHERE vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.apagar: Conexão é null");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, entidade.getVoluntarioId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao apagar Login: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Login get(int id, Conexao conexao) {
        String sql = "SELECT * FROM login WHERE vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.get: Conexão é null");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapLogin(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar Login por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Login> get(String filtro, Conexao conexao) {
        List<Login> lista = new ArrayList<>();
        String sql = "SELECT * FROM login WHERE login ILIKE ? OR nive_acesso ILIKE ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.get filtro: Conexão é null");
                return lista;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String filtroLike = "%" + filtro + "%";
                stmt.setString(1, filtroLike);
                stmt.setString(2, filtroLike);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapLogin(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar Logins: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca login por email/username exato
     */
    public Login buscarPorLogin(String email, Conexao conexao) {
        String sql = "SELECT * FROM login WHERE LOWER(login) = LOWER(?)";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.buscarPorLogin: Conexão é null");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapLogin(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar Login por email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca login por VOL_ID
     */
    public Login buscarPorVoluntarioId(int voluntarioId, Conexao conexao) {
        return get(voluntarioId, conexao);
    }

    /**
     * Atualiza apenas o status
     */
    public boolean atualizarStatus(int voluntarioId, String novoStatus, Conexao conexao) {
        String sql = "UPDATE login SET status = ? WHERE vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.atualizarStatus: Conexão é null");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, novoStatus); // 'Ativo' ou 'Inativo'
                stmt.setInt(2, voluntarioId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Atualiza apenas a senha
     */
    public boolean atualizarSenha(int voluntarioId, String novaSenhaHash, Conexao conexao) {
        String sql = "UPDATE login SET senha = ? WHERE vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("❌ LoginDAO.atualizarSenha: Conexão é null");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, novaSenhaHash);
                stmt.setInt(2, voluntarioId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar senha: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ==================== HELPER METHODS ====================

    /**
     * Mapeia ResultSet para Login
     */
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

    /**
     * Converte role do código para o banco
     * ADMIN → Administrador
     * MANAGER → Logistica
     * USER → Operacional
     */
    private String convertRoleToDb(String role) {
        if (role == null) return "Operacional";

        switch (role.toUpperCase()) {
            case "ADMIN":
            case "ADMINISTRADOR":
                return "Administrador";
            case "MANAGER":
            case "LOGISTICA":
                return "Logistica";
            case "USER":
            case "OPERACIONAL":
            default:
                return "Operacional";
        }
    }

    /**
     * Converte role do banco para o código
     * Administrador → ADMIN
     * Logistica → MANAGER
     * Operacional → USER
     */
    private String convertRoleFromDb(String roleDB) {
        if (roleDB == null) return "USER";

        switch (roleDB) {
            case "Administrador":
                return "ADMIN";
            case "Logistica":
                return "MANAGER";
            case "Operacional":
                return "USER";
            default:
                return "USER";
        }
    }

    /**
     * Converte status do código para o banco
     * 'A' → Ativo
     * 'I' → Inativo
     */
    private String convertStatusToDb(char status) {
        return (status == 'A' || status == 'a') ? "Ativo" : "Inativo";
    }

    /**
     * Converte status do banco para o código
     * Ativo → 'A'
     * Inativo → 'I'
     */
    private char convertStatusFromDb(String statusDB) {
        return (statusDB != null && statusDB.equalsIgnoreCase("Ativo")) ? 'A' : 'I';
    }
}
