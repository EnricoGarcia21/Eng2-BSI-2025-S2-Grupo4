package DOARC.mvc.dao;

import DOARC.mvc.model.Categoria;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoriaDAO implements IDAO<Categoria> {

    private Connection conn;

    public CategoriaDAO() {
        conn = SingletonDB.getConexao().getConnect();
    }

    @Override
    public Categoria gravar(Categoria entidade) {
        String sql = "INSERT INTO categoria (cat_nome) VALUES (?) RETURNING cat_id";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entidade.getCatNome());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                entidade.setCatId(rs.getInt("cat_id"));
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Categoria alterar(Categoria entidade) {
        String sql = "UPDATE categoria SET cat_nome=? WHERE cat_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entidade.getCatNome());
            pst.setInt(2, entidade.getCatId());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Categoria entidade) {
        String sql = "DELETE FROM categoria WHERE cat_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, entidade.getCatId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Categoria get(int id) {
        String sql = "SELECT * FROM categoria WHERE cat_id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapCategoria(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Categoria> get(String filtro) {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM categoria";
        if (filtro != null && !filtro.isEmpty()) sql += " WHERE " + filtro;

        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                lista.add(mapCategoria(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Categoria mapCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setCatId(rs.getInt("cat_id"));
        c.setCatNome(rs.getString("cat_nome"));
        return c;
    }
}
