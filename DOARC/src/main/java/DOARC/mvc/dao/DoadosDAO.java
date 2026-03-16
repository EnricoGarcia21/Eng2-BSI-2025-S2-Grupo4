package DOARC.mvc.dao;

import DOARC.mvc.model.Doados;
import DOARC.mvc.model.DoadosProduto;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class DoadosDAO {

    public Doados gravar(Doados entidade, Conexao conexao) {
        Connection conn = conexao.getConnect();

        String sqlMestre = "INSERT INTO doados (doa_data_aquisicao, doa_tipo_doacao, vol_id, don_id, obs_doado, valor_doacao) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING doa_id";

        String sqlDetalhe = "INSERT INTO doadosproduto (doa_id, prod_id, dp_qtde) VALUES (?, ?, ?)";

        // Como o Controller vai gerenciar o setAutoCommit(false) para controlar o estoque junto,
        // aqui nós apenas executamos os PreparedStatement.
        // O try-catch aqui serve para retornar null em caso de falha SQL, seguindo o padrão ProdutoDAO.

        try (PreparedStatement pstMestre = conn.prepareStatement(sqlMestre)) {
            pstMestre.setString(1, entidade.getDoaDataAquisicao());
            pstMestre.setString(2, entidade.getDoaTipoDoacao());
            pstMestre.setInt(3, entidade.getVolId());
            pstMestre.setInt(4, entidade.getDonId());
            pstMestre.setString(5, entidade.getObsDoado());
            pstMestre.setBigDecimal(6, entidade.getValorDoacao());

            ResultSet rs = pstMestre.executeQuery();
            if (rs.next()) {
                entidade.setDoaId(rs.getInt("doa_id"));
            } else {
                return null; // Falha ao gerar ID
            }

            // Gravar os itens (Produtos da doação)
            try (PreparedStatement pstDetalhe = conn.prepareStatement(sqlDetalhe)) {
                for (DoadosProduto dp : entidade.getProdutos()) {
                    pstDetalhe.setInt(1, entidade.getDoaId());
                    pstDetalhe.setInt(2, dp.getProdId());
                    pstDetalhe.setBigDecimal(3, dp.getDpQtde());
                    pstDetalhe.addBatch();
                }
                pstDetalhe.executeBatch();
            }

            return entidade; // Sucesso

        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Retorna null em caso de erro, igual ao ProdutoDAO
        }
    }
}