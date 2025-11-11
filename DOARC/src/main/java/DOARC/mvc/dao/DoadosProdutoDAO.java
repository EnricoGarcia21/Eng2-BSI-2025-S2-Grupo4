package DOARC.mvc.dao;

import DOARC.mvc.model.DoadosProduto;
import DOARC.mvc.util.Conexao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class DoadosProdutoDAO {

    public DoadosProduto mapDoadosProduto(ResultSet rs) throws SQLException {
        DoadosProduto dp = new DoadosProduto();
        dp.setDoaId(rs.getInt("doa_id"));
        dp.setProdId(rs.getInt("prod_id"));
        dp.setDpQtde(rs.getBigDecimal("dp_qtde"));
        return dp;
    }

    /**
     * Busca os itens de produto de uma doação específica.
     */
    public List<DoadosProduto> getProdutosPorDoacaoId(int doaId, Conexao conn) {
        List<DoadosProduto> lista = new ArrayList<>();
        String sql = "SELECT * FROM DoadosProduto WHERE doa_id = ?";
        try (PreparedStatement pst = conn.getConnect().prepareStatement(sql)) {
            pst.setInt(1, doaId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                lista.add(mapDoadosProduto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produtos da doação: " + e.getMessage());
        }
        return lista;
    }
}