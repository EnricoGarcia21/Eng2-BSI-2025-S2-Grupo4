package DOARC.mvc.dao;

import DOARC.mvc.model.Compra;
import DOARC.mvc.model.CompraProduto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que recebe Connection do Model
 * NÃO usa @Repository - é instanciado manualmente pelo Model
 */
public class CompraDAO {

    private Connection conn;

    /**
     * Construtor que recebe a Connection do Model
     * @param conn Conexão recebida do Model
     */
    public CompraDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Grava uma compra completa (cabeçalho + produtos) e atualiza o estoque
     * @param compra Compra a ser gravada
     * @param produtos Lista de produtos da compra
     * @return Compra gravada com ID ou null em caso de erro
     */
    public Compra gravarCompraCompleta(Compra compra, List<CompraProduto> produtos) {
        try {
            // Desabilita auto-commit para transação
            this.conn.setAutoCommit(false);

            // 1. Insere o cabeçalho da compra
            String sqlCompra = "INSERT INTO Compra (COMP_DATA_COMPRA, COMP_DESC, VOL_ID, COM_VALOR_TOTAL, COM_FORNECEDOR) " +
                              "VALUES (?::DATE, ?, ?, ?, ?) RETURNING COMP_ID";

            int compraId;
            try (PreparedStatement pst = this.conn.prepareStatement(sqlCompra)) {
                pst.setString(1, compra.getCompDataCompra());
                pst.setString(2, compra.getCompDesc());
                pst.setInt(3, compra.getVolId());
                pst.setDouble(4, compra.getComValorTotal());
                pst.setString(5, compra.getComFornecedor());

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        compraId = rs.getInt("COMP_ID");
                        compra.setCompId(compraId);
                    } else {
                        throw new SQLException("Falha ao obter ID da compra");
                    }
                }
            }

            // 2. Insere os produtos da compra e atualiza o estoque
            String sqlCompraProduto = "INSERT INTO Compra_Produto (COMP_ID, PROD_ID, QTDE, VALOR_UNITARIO) VALUES (?, ?, ?, ?)";
            String sqlAtualizaEstoque = "UPDATE Produto SET PROD_QUANT = PROD_QUANT + ? WHERE PROD_ID = ?";

            try (PreparedStatement pstProduto = this.conn.prepareStatement(sqlCompraProduto);
                 PreparedStatement pstEstoque = this.conn.prepareStatement(sqlAtualizaEstoque)) {

                for (CompraProduto cp : produtos) {
                    // Insere na tabela Compra_Produto
                    pstProduto.setInt(1, compraId);
                    pstProduto.setInt(2, cp.getProdId());
                    pstProduto.setDouble(3, cp.getQtde());
                    pstProduto.setDouble(4, cp.getValorUnitario());
                    pstProduto.executeUpdate();

                    // Atualiza o estoque do produto
                    pstEstoque.setDouble(1, cp.getQtde());
                    pstEstoque.setInt(2, cp.getProdId());
                    pstEstoque.executeUpdate();
                }
            }

            // Commit da transação
            this.conn.commit();
            this.conn.setAutoCommit(true);

            return compra;

        } catch (SQLException e) {
            System.err.println("Erro ao gravar compra: " + e.getMessage());
            e.printStackTrace();
            try {
                // Rollback em caso de erro
                this.conn.rollback();
                this.conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Erro ao fazer rollback: " + ex.getMessage());
            }
            return null;
        }
    }

    public Compra get(int id) {
        String sql = "SELECT * FROM Compra WHERE COMP_ID=?";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapCompra(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar compra ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Compra> getAll() {
        List<Compra> lista = new ArrayList<>();
        String sql = "SELECT * FROM Compra ORDER BY COMP_DATA_COMPRA DESC";
        try (Statement st = this.conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapCompra(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar compras: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca produtos de uma compra específica
     * @param compraId ID da compra
     * @return Lista de produtos da compra
     */
    public List<CompraProduto> getProdutosDaCompra(int compraId) {
        List<CompraProduto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Compra_Produto WHERE COMP_ID=?";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            pst.setInt(1, compraId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    CompraProduto cp = new CompraProduto();
                    cp.setCompId(rs.getInt("COMP_ID"));
                    cp.setProdId(rs.getInt("PROD_ID"));
                    cp.setQtde(rs.getDouble("QTDE"));
                    cp.setValorUnitario(rs.getDouble("VALOR_UNITARIO"));
                    lista.add(cp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produtos da compra: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private Compra mapCompra(ResultSet rs) throws SQLException {
        Compra c = new Compra();
        c.setCompId(rs.getInt("COMP_ID"));

        // Handle date conversion from database
        Date dataCompra = rs.getDate("COMP_DATA_COMPRA");
        if (dataCompra != null) {
            c.setCompDataCompra(dataCompra.toString()); // YYYY-MM-DD format
        }

        c.setCompDesc(rs.getString("COMP_DESC"));
        c.setVolId(rs.getInt("VOL_ID"));
        c.setComValorTotal(rs.getDouble("COM_VALOR_TOTAL"));
        c.setComFornecedor(rs.getString("COM_FORNECEDOR"));
        return c;
    }
}
