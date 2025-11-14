package DOARC.mvc.dao;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LoginDAO implements IDAO<Login> {

    public LoginDAO() {}

    @Override
    public Login gravar(Login entidade, Conexao conexao) {
        // ✅ SEM RETURNING - voluntario_vol_id já vem preenchido
        String sql = "INSERT INTO login (voluntario_vol_id, login, senha, nive_acesso, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO LoginDAO (gravar): Conexão é null");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, entidade.getVoluntarioId());
                stmt.setString(2, entidade.getLogin());
                stmt.setString(3, entidade.getSenha());
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
                    return entidade;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ ERRO LoginDAO (gravar): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Login alterar(Login entidade, Conexao conexao) {
        String sql = "UPDATE login SET login=?, senha=?, nive_acesso=?, status=? " +
                "WHERE voluntario_vol_id=?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO LoginDAO (alterar): Conexão é null");
                return null;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, entidade.getLogin());
                stmt.setString(2, entidade.getSenha());
                stmt.setString(3, entidade.getNivelAcesso());
                stmt.setString(4, String.valueOf(entidade.getStatus()));
                stmt.setInt(5, entidade.getVoluntarioId());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0 ? entidade : null;
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (alterar): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Login entidade, Conexao conexao) {
        String sql = "DELETE FROM login WHERE voluntario_vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO LoginDAO (apagar): Conexão é null");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, entidade.getVoluntarioId());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (apagar): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Login get(int id, Conexao conexao) {
        String sql = "SELECT * FROM login WHERE voluntario_vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO LoginDAO (get): Conexão é null");
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
            System.err.println("ERRO LoginDAO (get por ID): " + e.getMessage());
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
                System.err.println("ERRO LoginDAO (get filtro): Conexão é null");
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
            System.err.println("ERRO LoginDAO (listar): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca usuário por login exato (email)
     */
    public Login buscarPorLoginExato(String email, Conexao conexao) {
        String sql = "SELECT * FROM login WHERE LOWER(login) = LOWER(?)";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO LoginDAO (buscarPorLoginExato): Conexão é null");
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
            System.err.println("ERRO LoginDAO (buscarPorLoginExato): " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Busca usuário por ID do voluntário (é a mesma coisa que get(id))
     */
    public Login buscarPorVoluntarioId(int voluntarioId, Conexao conexao) {
        return get(voluntarioId, conexao);
    }

    /**
     * Atualiza apenas o status do usuário
     */
    public boolean atualizarStatus(int voluntarioId, char novoStatus, Conexao conexao) {
        String sql = "UPDATE login SET status = ? WHERE voluntario_vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO LoginDAO (atualizarStatus): Conexão é null");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, String.valueOf(novoStatus));
                stmt.setInt(2, voluntarioId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (atualizarStatus): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Atualiza apenas a senha
     */
    public boolean atualizarSenha(int voluntarioId, String novaSenhaHash, Conexao conexao) {
        String sql = "UPDATE login SET senha = ? WHERE voluntario_vol_id = ?";

        try {
            Connection conn = conexao.getConnect();
            if (conn == null) {
                System.err.println("ERRO LoginDAO (atualizarSenha): Conexão é null");
                return false;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, novaSenhaHash);
                stmt.setInt(2, voluntarioId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("ERRO LoginDAO (atualizarSenha): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

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