package DOARC.mvc.dao;

import DOARC.mvc.model.DoadosProduto;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class DoadosProdutoDAO {

    public void inserirProdutos(Integer doaId, List<DoadosProduto> produtos, Connection conn) throws SQLException {
        String sql = "INSERT INTO doadosproduto (doa_id, prod_id, dp_qtde) VALUES (?, ?, ?)";

        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            for (DoadosProduto dp : produtos) {
                pst.setInt(1, doaId);
                pst.setInt(2, dp.getProdId());
                pst.setBigDecimal(3, dp.getDpQtde());
                pst.addBatch();
            }
            pst.executeBatch();
        }
    }
}
