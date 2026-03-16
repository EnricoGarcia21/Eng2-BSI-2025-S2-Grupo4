package DOARC.mvc.dao;

import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class AtualizarEstoqueDAO {

    public boolean subtrair(int produtoId, int quantidade, Conexao conexao) {
        String sql = "UPDATE produto SET prod_quant = prod_quant - ? WHERE prod_id = ? AND prod_quant >= ?";

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, quantidade);
            pst.setInt(2, produtoId);
            pst.setInt(3, quantidade);

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean adicionar(int produtoId, int quantidade, Conexao conexao) {
        String sql = "UPDATE produto SET prod_quant = prod_quant + ? WHERE prod_id = ?";

        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, quantidade);
            pst.setInt(2, produtoId);

            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}