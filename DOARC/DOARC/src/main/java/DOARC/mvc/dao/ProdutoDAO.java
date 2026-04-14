package DOARC.mvc.dao;

import DOARC.mvc.model.Doador;
import DOARC.mvc.model.Produto;
import DOARC.mvc.observer.Observador;
import DOARC.mvc.util.Conexao;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ProdutoDAO {

    public Produto gravar(Produto produto, Conexao conexao) {
        String sql = "INSERT INTO produto (prod_nome, prod_descricao, prod_informacoes_adicionais, prod_quant, cat_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING prod_id";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setString(1, produto.getProdNome());
            pst.setString(2, produto.getProdDescricao());
            pst.setString(3, produto.getProdInformacoesAdicionais());
            pst.setInt(4, produto.getProdQuant());
            pst.setInt(5, produto.getCategoriaCatId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) produto.setProdId(rs.getInt("prod_id"));
            return produto;
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public Produto alterar(Produto produto, Conexao conexao) {
        String sql = "UPDATE produto SET prod_nome=?, prod_descricao=?, prod_informacoes_adicionais=?, prod_quant=?, cat_id=? WHERE prod_id=?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setString(1, produto.getProdNome());
            pst.setString(2, produto.getProdDescricao());
            pst.setString(3, produto.getProdInformacoesAdicionais());
            pst.setInt(4, produto.getProdQuant());
            pst.setInt(5, produto.getCategoriaCatId());
            pst.setInt(6, produto.getProdId());
            pst.executeUpdate();
            return produto;
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public Produto get(int id, Conexao conexao) {
        String sql = "SELECT p.*, c.cat_nome_prod AS categoria_nome FROM produto p " +
                "LEFT JOIN categoria c ON p.cat_id = c.cat_id WHERE p.prod_id=?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Produto p = new Produto();
                p.setProdId(rs.getInt("prod_id"));
                p.setProdNome(rs.getString("prod_nome"));
                p.setProdDescricao(rs.getString("prod_descricao"));
                p.setProdInformacoesAdicionais(rs.getString("prod_informacoes_adicionais"));
                p.setProdQuant(rs.getInt("prod_quant"));
                p.setCategoriaCatId(rs.getInt("cat_id"));
                p.setCategoriaNome(rs.getString("categoria_nome"));
                return p;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Produto> get(String filtro, Conexao conexao) {
        String sql = "SELECT p.*, c.cat_nome_prod AS categoria_nome FROM produto p " +
                "LEFT JOIN categoria c ON p.cat_id = c.cat_id";

        if (filtro != null && !filtro.isEmpty()) {
            sql += " WHERE " + filtro;
        }

        List<Produto> lista = new ArrayList<>();
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Produto p = new Produto();
                p.setProdId(rs.getInt("prod_id"));
                p.setProdNome(rs.getString("prod_nome"));
                p.setProdDescricao(rs.getString("prod_descricao"));
                p.setProdInformacoesAdicionais(rs.getString("prod_informacoes_adicionais"));
                p.setProdQuant(rs.getInt("prod_quant"));
                p.setCategoriaCatId(rs.getInt("cat_id"));
                p.setCategoriaNome(rs.getString("categoria_nome"));
                lista.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public boolean apagar(Produto produto, Conexao conexao) {
        String sql = "DELETE FROM produto WHERE prod_id=?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, produto.getProdId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean vincularObservador(int prodId, int doaId, Conexao conexao) {
        String sql = "INSERT INTO Produto_Observador (PROD_ID, DOA_ID) VALUES (?, ?)";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, prodId);
            pst.setInt(2, doaId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean desvincularObservador(int prodId, int doaId, Conexao conexao) {
        String sql = "DELETE FROM Produto_Observador WHERE PROD_ID = ? AND DOA_ID = ?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, prodId);
            pst.setInt(2, doaId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Observador> buscarObservadoresDoProduto(int prodId, Conexao conexao) {
        List<Observador> lista = new ArrayList<>();
        String sql = "SELECT d.DOA_ID, d.DOA_NOME, d.DOA_EMAIL FROM Doadores d " +
                "JOIN Produto_Observador po ON d.DOA_ID = po.DOA_ID " +
                "WHERE po.PROD_ID = ?";
        try (PreparedStatement pst = conexao.getConnect().prepareStatement(sql)) {
            pst.setInt(1, prodId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Doador d = new Doador();
                d.setId(rs.getInt("DOA_ID"));
                d.setNome(rs.getString("DOA_NOME"));
                d.setEmail(rs.getString("DOA_EMAIL"));
                lista.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}