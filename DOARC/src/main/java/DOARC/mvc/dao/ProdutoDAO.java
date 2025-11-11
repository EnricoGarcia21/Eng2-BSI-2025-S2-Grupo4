package DOARC.mvc.dao;

import DOARC.mvc.model.Produto;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    private Produto mapProduto(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setProdId(rs.getInt("prod_id"));
        p.setProdNome(rs.getString("prod_nome"));
        p.setProdDescricao(rs.getString("prod_descricao"));
        p.setProdQuant(rs.getInt("prod_quant"));
        p.setCatId(rs.getInt("cat_id"));
        return p;
    }

    public Produto get(int id, Conexao conn) {
        String sql = "SELECT * FROM produto WHERE prod_id=?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return mapProduto(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Produto por ID: " + e.getMessage());
        }
        return null;
    }
}