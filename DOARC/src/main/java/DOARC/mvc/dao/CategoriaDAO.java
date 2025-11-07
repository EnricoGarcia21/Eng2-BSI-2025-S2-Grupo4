package DOARC.mvc.dao;

import DOARC.mvc.model.Categoria;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoriaDAO implements IDAO<Categoria> {

    private Connection getConnection() {
        return SingletonDB.getConnection();
    }

    @Override
    public Categoria gravar(Categoria entidade) {
        String sql = "INSERT INTO Categoria (CAT_NOME_PROD, CAT_ESPECIFICACAO) VALUES (?, ?) RETURNING CAT_ID";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, entidade.getCatNomeProd());
            pst.setString(2, entidade.getCatEspecificacao());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    entidade.setCatId(rs.getInt("CAT_ID"));
                    return entidade;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar categoria: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Categoria alterar(Categoria entidade) {
        String sql = "UPDATE Categoria SET CAT_NOME_PROD=?, CAT_ESPECIFICACAO=? WHERE CAT_ID=?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, entidade.getCatNomeProd());
            pst.setString(2, entidade.getCatEspecificacao());
            pst.setInt(3, entidade.getCatId());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            System.err.println("Erro ao alterar categoria: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Categoria entidade) {
        // Verifica se há produtos relacionados antes de deletar
        if (temProdutosRelacionados(entidade.getCatId())) {
            System.err.println("Não é possível deletar categoria com produtos relacionados");
            return false;
        }

        String sql = "DELETE FROM Categoria WHERE CAT_ID=?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, entidade.getCatId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar categoria: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Verifica se a categoria possui produtos relacionados
     * @param catId ID da categoria
     * @return true se há produtos relacionados, false caso contrário
     */
    public boolean temProdutosRelacionados(int catId) {
        String sql = "SELECT COUNT(*) as total FROM Produto WHERE CAT_ID=?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, catId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    return total > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar produtos relacionados: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Categoria get(int id) {
        String sql = "SELECT * FROM Categoria WHERE CAT_ID=?";
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn == null) {
                System.err.println("Conexão nula ao buscar categoria ID: " + id);
                return null;
            }

            try (PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, id);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        return mapCategoria(rs);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar categoria ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Categoria> get(String filtro) {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM Categoria WHERE CAT_NOME_PROD ILIKE ? OR CAT_ESPECIFICACAO ILIKE ?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {

            String searchPattern = "%" + (filtro != null ? filtro : "") + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapCategoria(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar categorias: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<Categoria> getAll() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM Categoria ORDER BY CAT_NOME_PROD";
        Connection conn = getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapCategoria(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas categorias: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private Categoria mapCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setCatId(rs.getInt("CAT_ID"));
        c.setCatNomeProd(rs.getString("CAT_NOME_PROD"));
        c.setCatEspecificacao(rs.getString("CAT_ESPECIFICACAO"));
        return c;
    }
}
