package DOARC.mvc.dao;

import DOARC.mvc.model.AcertoEstoque;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AcertoEstoqueDAO {

    private Connection getConnection() {
        return SingletonDB.getConnection();
    }

    /**
     * Grava um acerto de estoque e atualiza o estoque do produto automaticamente
     * Tipos: Entrada (soma), Saída (subtrai)
     * @param acerto AcertoEstoque a ser gravado
     * @param estoqueAtual Estoque atual do produto para validação
     * @return AcertoEstoque gravado com ID ou null em caso de erro
     */
    public AcertoEstoque gravarAcertoComAtualizacao(AcertoEstoque acerto, int estoqueAtual) {
        Connection conn = getConnection();

        try {
            // Desabilita auto-commit para transação
            conn.setAutoCommit(false);

            // 1. Insere o acerto de estoque
            String sqlAcerto = "INSERT INTO Acerto_Estoque (AC_DATA, AC_MOTIVO, AC_OBS, AC_TIPO, AC_QUANTIDADE, VOL_ID, PROD_ID) " +
                              "VALUES (?::DATE, ?, ?, ?, ?, ?, ?) RETURNING AC_ID";

            int acertoId;
            try (PreparedStatement pst = conn.prepareStatement(sqlAcerto)) {
                pst.setString(1, acerto.getAcData());
                pst.setString(2, acerto.getAcMotivo());
                pst.setString(3, acerto.getAcObs());
                pst.setString(4, acerto.getAcTipo());
                pst.setDouble(5, acerto.getAcQuantidade());
                pst.setInt(6, acerto.getVolId());
                pst.setInt(7, acerto.getProdId());

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        acertoId = rs.getInt("AC_ID");
                        acerto.setAcId(acertoId);
                    } else {
                        throw new SQLException("Falha ao obter ID do acerto");
                    }
                }
            }

            // 2. Atualiza o estoque do produto
            String sqlAtualizaEstoque;
            if ("Entrada".equals(acerto.getAcTipo())) {
                // Adiciona ao estoque
                sqlAtualizaEstoque = "UPDATE Produto SET PROD_QUANT = PROD_QUANT + ? WHERE PROD_ID = ?";
            } else {
                // Subtrai do estoque (Saída)
                sqlAtualizaEstoque = "UPDATE Produto SET PROD_QUANT = PROD_QUANT - ? WHERE PROD_ID = ?";
            }

            try (PreparedStatement pstEstoque = conn.prepareStatement(sqlAtualizaEstoque)) {
                pstEstoque.setDouble(1, acerto.getAcQuantidade());
                pstEstoque.setInt(2, acerto.getProdId());
                pstEstoque.executeUpdate();
            }

            // Commit da transação
            conn.commit();
            conn.setAutoCommit(true);

            return acerto;

        } catch (SQLException e) {
            System.err.println("Erro ao gravar acerto de estoque: " + e.getMessage());
            e.printStackTrace();
            try {
                // Rollback em caso de erro
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Erro ao fazer rollback: " + ex.getMessage());
            }
            return null;
        }
    }

    public AcertoEstoque get(int id) {
        String sql = "SELECT * FROM Acerto_Estoque WHERE AC_ID=?";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapAcerto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar acerto ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<AcertoEstoque> getAll() {
        List<AcertoEstoque> lista = new ArrayList<>();
        String sql = "SELECT * FROM Acerto_Estoque ORDER BY AC_DATA DESC";
        Connection conn = getConnection();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapAcerto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar acertos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<AcertoEstoque> getPorProduto(int prodId) {
        List<AcertoEstoque> lista = new ArrayList<>();
        String sql = "SELECT * FROM Acerto_Estoque WHERE PROD_ID=? ORDER BY AC_DATA DESC";
        Connection conn = getConnection();
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, prodId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapAcerto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar acertos por produto: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private AcertoEstoque mapAcerto(ResultSet rs) throws SQLException {
        AcertoEstoque a = new AcertoEstoque();
        a.setAcId(rs.getInt("AC_ID"));

        // Handle date conversion from database
        Date data = rs.getDate("AC_DATA");
        if (data != null) {
            a.setAcData(data.toString()); // YYYY-MM-DD format
        }

        a.setAcMotivo(rs.getString("AC_MOTIVO"));
        a.setAcObs(rs.getString("AC_OBS"));
        a.setAcTipo(rs.getString("AC_TIPO"));
        a.setAcQuantidade(rs.getDouble("AC_QUANTIDADE"));
        a.setVolId(rs.getInt("VOL_ID"));
        a.setProdId(rs.getInt("PROD_ID"));
        return a;
    }
}
