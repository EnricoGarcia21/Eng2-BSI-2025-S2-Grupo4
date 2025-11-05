package DOARC.mvc.dao;

import DOARC.mvc.model.Produto;
import DOARC.mvc.util.SingletonDB;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoDAO implements IDAO<Produto> {

    private final Conexao conexao;

    public ProdutoDAO() {
        this.conexao = SingletonDB.conectar();
    }

    @Override
    public Produto gravar(Produto entidade) {
        String sql = "INSERT INTO produto (prod_nome, prod_descricao, prod_informacoes_adicionais, prod_quant, categoria_cat_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING prod_id";

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getProdNome());
            pst.setString(2, entidade.getProdDescricao());
            pst.setString(3, entidade.getProdInformacoesAdicionais());
            pst.setInt(4, entidade.getProdQuant());
            pst.setInt(5, entidade.getCategoriaCatId());

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                entidade.setProdId(rs.getInt("prod_id"));
                return entidade;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Produto alterar(Produto entidade) {
        String sql = "UPDATE produto SET prod_nome=?, prod_descricao=?, prod_informacoes_adicionais=?, prod_quant=?, categoria_cat_id=? " +
                "WHERE prod_id=?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setString(1, entidade.getProdNome());
            pst.setString(2, entidade.getProdDescricao());
            pst.setString(3, entidade.getProdInformacoesAdicionais());
            pst.setInt(4, entidade.getProdQuant());
            pst.setInt(5, entidade.getCategoriaCatId());
            pst.setInt(6, entidade.getProdId());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Produto entidade) {
        String sql = "DELETE FROM produto WHERE prod_id=?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, entidade.getProdId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Produto get(int id) {
        String sql = "SELECT p.*, c.cat_nome FROM produto p " +
                "LEFT JOIN categoria c ON p.categoria_cat_id = c.cat_id " +
                "WHERE p.prod_id=?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return mapProduto(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Produto> get(String filtro) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.cat_nome FROM produto p " +
                "LEFT JOIN categoria c ON p.categoria_cat_id = c.cat_id";

        if (filtro != null && !filtro.isEmpty())
            sql += " " + filtro;

        sql += " ORDER BY p.prod_nome";

        try (Statement st = conexao.getConnect().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapProduto(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Produto mapProduto(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setProdId(rs.getInt("prod_id"));
        p.setProdNome(rs.getString("prod_nome"));
        p.setProdDescricao(rs.getString("prod_descricao"));
        p.setProdInformacoesAdicionais(rs.getString("prod_informacoes_adicionais"));
        p.setProdQuant(rs.getInt("prod_quant"));
        p.setCategoriaCatId(rs.getInt("categoria_cat_id"));
        p.setCategoriaNome(rs.getString("cat_nome"));
        return p;
    }
}
