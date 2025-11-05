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

    public Doados gravar(Doados entidade, Conexao conexao) throws SQLException {
        Connection conn = conexao.getConnect();

        String sqlMestre = "INSERT INTO doados (doa_data_aquisicao, doa_tipo_doacao, vol_id, don_id, obs_doado, valor_doacao) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING doa_id";

        String sqlDetalhe = "INSERT INTO doadosproduto (doa_id, prod_id, dp_qtde) VALUES (?, ?, ?)";

        // --- Inserir o mestre ---
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
                throw new SQLException("Falha ao criar registro de doação: nenhum ID retornado.");
            }
        }

        // --- Inserir os detalhes ---
        try (PreparedStatement pstDetalhe = conn.prepareStatement(sqlDetalhe)) {
            for (DoadosProduto dp : entidade.getProdutos()) {
                pstDetalhe.setInt(1, entidade.getDoaId());
                pstDetalhe.setInt(2, dp.getProdId());
                pstDetalhe.setBigDecimal(3, dp.getDpQtde());
                pstDetalhe.addBatch();
            }
            pstDetalhe.executeBatch();
        }

        return entidade;
    }
}
