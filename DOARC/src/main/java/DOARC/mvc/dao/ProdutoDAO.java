package DOARC.mvc.dao;

import DOARC.mvc.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO que recebe Connection do Model
 * NÃO usa @Repository - é instanciado manualmente pelo Model
 */
public class ProdutoDAO implements IDAO<Produto> {

    private Connection conn;

    /**
     * Construtor que recebe a Connection do Model
     * @param conn Conexão recebida do Model
     */
    public ProdutoDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Produto gravar(Produto entidade) {
        String sql = "INSERT INTO Produto (PROD_NOME, PROD_DESCRICAO, PROD_INFORMACOES_ADICIONAIS, PROD_QUANT, CAT_ID) VALUES (?, ?, ?, ?, ?) RETURNING PROD_ID";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {

            pst.setString(1, entidade.getProdNome());
            pst.setString(2, entidade.getProdDescricao());
            pst.setString(3, entidade.getProdInformacoesAdicionais());
            pst.setInt(4, entidade.getProdQuant());
            pst.setInt(5, entidade.getCatId());

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    entidade.setProdId(rs.getInt("PROD_ID"));
                    return entidade;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar produto: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Produto alterar(Produto entidade) {
        String sql = "UPDATE Produto SET PROD_NOME=?, PROD_DESCRICAO=?, PROD_INFORMACOES_ADICIONAIS=?, PROD_QUANT=?, CAT_ID=? WHERE PROD_ID=?";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {

            pst.setString(1, entidade.getProdNome());
            pst.setString(2, entidade.getProdDescricao());
            pst.setString(3, entidade.getProdInformacoesAdicionais());
            pst.setInt(4, entidade.getProdQuant());
            pst.setInt(5, entidade.getCatId());
            pst.setInt(6, entidade.getProdId());

            int updated = pst.executeUpdate();
            return (updated > 0) ? entidade : null;
        } catch (SQLException e) {
            System.err.println("Erro ao alterar produto: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean apagar(Produto entidade) {
        String sql = "DELETE FROM Produto WHERE PROD_ID=?";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {

            pst.setInt(1, entidade.getProdId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao apagar produto: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Produto get(int id) {
        String sql = "SELECT * FROM Produto WHERE PROD_ID=?";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapProduto(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Produto> get(String filtro) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Produto WHERE PROD_NOME ILIKE ? OR PROD_DESCRICAO ILIKE ?";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {

            String searchPattern = "%" + (filtro != null ? filtro : "") + "%";
            pst.setString(1, searchPattern);
            pst.setString(2, searchPattern);

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapProduto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<Produto> getAll() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Produto ORDER BY PROD_NOME";
        try (Statement st = this.conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapProduto(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos produtos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Busca produtos por categoria
     * @param catId ID da categoria
     * @return Lista de produtos da categoria
     */
    public List<Produto> getPorCategoria(int catId) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM Produto WHERE CAT_ID=? ORDER BY PROD_NOME";
        try (PreparedStatement pst = this.conn.prepareStatement(sql)) {
            pst.setInt(1, catId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapProduto(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos por categoria: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    private Produto mapProduto(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setProdId(rs.getInt("PROD_ID"));
        p.setProdNome(rs.getString("PROD_NOME"));
        p.setProdDescricao(rs.getString("PROD_DESCRICAO"));
        p.setProdInformacoesAdicionais(rs.getString("PROD_INFORMACOES_ADICIONAIS"));
        p.setProdQuant(rs.getInt("PROD_QUANT"));
        p.setCatId(rs.getInt("CAT_ID"));
        return p;
    }
}
