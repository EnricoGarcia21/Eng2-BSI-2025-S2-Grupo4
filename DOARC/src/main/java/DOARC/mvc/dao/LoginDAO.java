package DOARC.mvc.dao;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
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
=======
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
@Repository
public class LoginDAO implements IDAO<Login> {

    public LoginDAO() {}

    @Override
    public Login gravar(Login entidade, Conexao conexao) {
<<<<<<< HEAD
        String sql = "INSERT INTO login (vol_id, login, senha, nive_acesso, status) VALUES (?, ?, ?, ?, ?)";
=======
        // ✅ SEM RETURNING - voluntario_vol_id já vem preenchido
        String sql = "INSERT INTO login (voluntario_vol_id, login, senha, nive_acesso, status) " +
                "VALUES (?, ?, ?, ?, ?)";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.gravar: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (gravar): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, entidade.getVoluntarioId());
                stmt.setString(2, entidade.getLogin());
                stmt.setString(3, entidade.getSenha());
<<<<<<< HEAD
                stmt.setString(4, convertRoleToDb(entidade.getNivelAcesso()));
                stmt.setString(5, convertStatusToDb(entidade.getStatus()));

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    entidade.setLoginId(entidade.getVoluntarioId());
                    System.out.println("✅ Login criado: " + entidade.getLogin());
=======
                stmt.setString(4, entidade.getNivelAcesso());
                stmt.setString(5, String.valueOf(entidade.getStatus()));

                System.out.println("🔵 Inserindo login:");
                System.out.println("   voluntario_vol_id (PK): " + entidade.getVoluntarioId());
                System.out.println("   login (email): " + entidade.getLogin());
                System.out.println("   nive_acesso: " + entidade.getNivelAcesso());
                System.out.println("   status: " + entidade.getStatus());

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("✅ Login criado para voluntário ID: " + entidade.getVoluntarioId());
                    // ✅ LoginId = VoluntarioId (pois é a PK)
                    entidade.setLoginId(entidade.getVoluntarioId());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                    return entidade;
                }
            }
        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("❌ Erro ao gravar Login: " + e.getMessage());
=======
            System.err.println("❌ ERRO LoginDAO (gravar): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Login alterar(Login entidade, Conexao conexao) {
<<<<<<< HEAD
        String sql = "UPDATE login SET login=?, senha=?, nive_acesso=?, status=? WHERE vol_id=?";
=======
        String sql = "UPDATE login SET login=?, senha=?, nive_acesso=?, status=? " +
                "WHERE voluntario_vol_id=?";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.alterar: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (alterar): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, entidade.getLogin());
                stmt.setString(2, entidade.getSenha());
<<<<<<< HEAD
                stmt.setString(3, convertRoleToDb(entidade.getNivelAcesso()));
                stmt.setString(4, convertStatusToDb(entidade.getStatus()));
                stmt.setInt(5, entidade.getVoluntarioId());

                return stmt.executeUpdate() > 0 ? entidade : null;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao alterar Login: " + e.getMessage());
=======
                stmt.setString(3, entidade.getNivelAcesso());
                stmt.setString(4, String.valueOf(entidade.getStatus()));
                stmt.setInt(5, entidade.getVoluntarioId());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0 ? entidade : null;
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (alterar): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Login entidade, Conexao conexao) {
<<<<<<< HEAD
        String sql = "DELETE FROM login WHERE vol_id = ?";
=======
        String sql = "DELETE FROM login WHERE voluntario_vol_id = ?";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.apagar: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (apagar): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, entidade.getVoluntarioId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("❌ Erro ao apagar Login: " + e.getMessage());
=======
            System.err.println("ERRO LoginDAO (apagar): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Login get(int id, Conexao conexao) {
<<<<<<< HEAD
        String sql = "SELECT * FROM login WHERE vol_id = ?";
=======
        String sql = "SELECT * FROM login WHERE voluntario_vol_id = ?";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.get: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (get): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
            System.err.println("❌ Erro ao buscar Login por ID: " + e.getMessage());
=======
            System.err.println("ERRO LoginDAO (get por ID): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.get filtro: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (get filtro): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
            System.err.println("❌ Erro ao listar Logins: " + e.getMessage());
=======
            System.err.println("ERRO LoginDAO (listar): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return lista;
    }

    /**
<<<<<<< HEAD
     * Busca login por email/username exato
     */
    public Login buscarPorLogin(String email, Conexao conexao) {
=======
     * Busca usuário por login exato (email)
     */
    public Login buscarPorLoginExato(String email, Conexao conexao) {
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        String sql = "SELECT * FROM login WHERE LOWER(login) = LOWER(?)";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.buscarPorLogin: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (buscarPorLoginExato): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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
<<<<<<< HEAD
            System.err.println("❌ Erro ao buscar Login por email: " + e.getMessage());
=======
            System.err.println("ERRO LoginDAO (buscarPorLoginExato): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return null;
    }

    /**
<<<<<<< HEAD
     * Busca login por VOL_ID
=======
     * Busca usuário por ID do voluntário (é a mesma coisa que get(id))
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
     */
    public Login buscarPorVoluntarioId(int voluntarioId, Conexao conexao) {
        return get(voluntarioId, conexao);
    }

    /**
<<<<<<< HEAD
     * Atualiza apenas o status
     */
    public boolean atualizarStatus(int voluntarioId, String novoStatus, Conexao conexao) {
        String sql = "UPDATE login SET status = ? WHERE vol_id = ?";
=======
     * Atualiza apenas o status do usuário
     */
    public boolean atualizarStatus(int voluntarioId, char novoStatus, Conexao conexao) {
        String sql = "UPDATE login SET status = ? WHERE voluntario_vol_id = ?";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.atualizarStatus: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (atualizarStatus): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
<<<<<<< HEAD
                stmt.setString(1, novoStatus); // 'Ativo' ou 'Inativo'
=======
                stmt.setString(1, String.valueOf(novoStatus));
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                stmt.setInt(2, voluntarioId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("❌ Erro ao atualizar status: " + e.getMessage());
=======
            System.err.println("ERRO LoginDAO (atualizarStatus): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Atualiza apenas a senha
     */
    public boolean atualizarSenha(int voluntarioId, String novaSenhaHash, Conexao conexao) {
<<<<<<< HEAD
        String sql = "UPDATE login SET senha = ? WHERE vol_id = ?";
=======
        String sql = "UPDATE login SET senha = ? WHERE voluntario_vol_id = ?";
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
<<<<<<< HEAD
                System.err.println("❌ LoginDAO.atualizarSenha: Conexão é null");
=======
                System.err.println("ERRO LoginDAO (atualizarSenha): Conexão é null");
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, novaSenhaHash);
                stmt.setInt(2, voluntarioId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
<<<<<<< HEAD
            System.err.println("❌ Erro ao atualizar senha: " + e.getMessage());
=======
            System.err.println("ERRO LoginDAO (atualizarSenha): " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            e.printStackTrace();
        }
        return false;
    }

<<<<<<< HEAD
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
=======
    /**
     * Mapeia ResultSet para objeto Login
     */
    private Login mapLogin(ResultSet rs) throws SQLException {
        Login l = new Login();
        int volId = rs.getInt("voluntario_vol_id");
        l.setVoluntarioId(volId);
        l.setLoginId(volId);  // ✅ LoginId = VoluntarioId (pois é a mesma coisa)
        l.setLogin(rs.getString("login"));
        l.setSenha(rs.getString("senha"));
        l.setNivelAcesso(rs.getString("nive_acesso"));

        String st = rs.getString("status");
        l.setStatus((st != null && !st.isEmpty()) ? st.charAt(0) : 'A');

        return l;
    }
}
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
