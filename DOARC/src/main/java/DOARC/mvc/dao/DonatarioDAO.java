package DOARC.mvc.dao;

import DOARC.mvc.model.Donatario;
import DOARC.mvc.util.Conexao; // Adicionado para referência, mas SingletonDB é o ponto de acesso
import DOARC.mvc.util.SingletonDB; // Reintroduzido
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DonatarioDAO implements IDAO<Donatario> {

    // A DAO não precisa mais do @Autowired DataSource, nem da lógica de pool.

    public DonatarioDAO() {
        // Construtor vazio. A conexão é gerenciada pelo SingletonDB/Conexao.
    }

    // Método auxiliar para obter a instância da Conexao
    private Conexao getConexao() {
        // Chama o método estático para garantir a conexão única
        return SingletonDB.conectar();
    }

    @Override
    public Donatario gravar(Donatario entidade) {
        // Usamos String.format e a classe Conexao.manipular para refletir o novo padrão

        String sql = String.format("INSERT INTO donatario (don_nome, don_data_nasc, don_rua, don_bairro, don_cidade, don_telefone, don_cep, don_uf, don_email, don_sexo) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s') RETURNING don_id",
                entidade.getDonNome().replace("'", "''"),
                entidade.getDonDataNasc().replace("'", "''"),
                entidade.getDonRua().replace("'", "''"),
                entidade.getDonBairro().replace("'", "''"),
                entidade.getDonCidade().replace("'", "''"),
                entidade.getDonTelefone().replace("'", "''"),
                entidade.getDonCep().replace("'", "''"),
                entidade.getDonUf().replace("'", "''"),
                entidade.getDonEmail().replace("'", "''"),
                entidade.getDonSexo().replace("'", "''")
        );

        // A Conexao não possui um método direto 'manipular com returning'.
        // Devemos usar o consultar para obter o ID gerado.
        try (ResultSet rs = getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                // Se a Conexão estiver usando um Statement que não é fechado no consultar(), pode haver problema.
                // Assumindo que o ID é obtido corretamente aqui.
                entidade.setDonId(rs.getInt("don_id"));
                return entidade;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao gravar Donatário (SQL): " + getConexao().getMensagemErro());
        }
        return null;
    }

    @Override
    public Donatario alterar(Donatario entidade) {
        String sql = String.format("UPDATE donatario SET don_nome='%s', don_data_nasc='%s', don_rua='%s', don_bairro='%s', don_cidade='%s', don_telefone='%s', don_cep='%s', don_uf='%s', don_email='%s', don_sexo='%s' WHERE don_id=%d",
                entidade.getDonNome().replace("'", "''"),
                entidade.getDonDataNasc().replace("'", "''"),
                entidade.getDonRua().replace("'", "''"),
                entidade.getDonBairro().replace("'", "''"),
                entidade.getDonCidade().replace("'", "''"),
                entidade.getDonTelefone().replace("'", "''"),
                entidade.getDonCep().replace("'", "''"),
                entidade.getDonUf().replace("'", "''"),
                entidade.getDonEmail().replace("'", "''"),
                entidade.getDonSexo().replace("'", "''"),
                entidade.getDonId()
        );

        // Uso de Conexao.manipular(sql)
        return getConexao().manipular(sql) ? entidade : null;
    }

    @Override
    public boolean apagar(Donatario entidade) {
        String sql = "DELETE FROM donatario WHERE don_id=" + entidade.getDonId();
        // Uso de Conexao.manipular(sql)
        return getConexao().manipular(sql);
    }

    @Override
    public Donatario get(int id) {
        String sql = "SELECT * FROM donatario WHERE don_id=" + id;
        try (ResultSet rs = getConexao().consultar(sql)) {
            if (rs != null && rs.next()) {
                return mapDonatario(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar Donatário por ID: " + getConexao().getMensagemErro());
        }
        return null;
    }

    @Override
    public List<Donatario> get(String filtro) {
        List<Donatario> lista = new ArrayList<>();
        String sql = "SELECT * FROM donatario";
        if (filtro != null && !filtro.isEmpty()) {
            // Aplicando o filtro simples conforme o método do FornecedorDAO anterior
            sql += String.format(" WHERE don_nome ILIKE '%%%s%%' OR don_email ILIKE '%%%s%%'",
                    filtro.replace("'", "''"), filtro.replace("'", "''"));
        }

        try (ResultSet rs = getConexao().consultar(sql)) {
            while (rs != null && rs.next()) {
                lista.add(mapDonatario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Donatários: " + getConexao().getMensagemErro());
        }
        return lista;
    }

    private Donatario mapDonatario(ResultSet rs) throws SQLException {
        Donatario d = new Donatario();
        d.setDonId(rs.getInt("don_id"));
        d.setDonNome(rs.getString("don_nome"));
        d.setDonDataNasc(rs.getString("don_data_nasc"));
        d.setDonRua(rs.getString("don_rua"));
        d.setDonBairro(rs.getString("don_bairro"));
        d.setDonCidade(rs.getString("don_cidade"));
        d.setDonTelefone(rs.getString("don_telefone"));
        d.setDonCep(rs.getString("don_cep"));
        d.setDonUf(rs.getString("don_uf"));
        d.setDonEmail(rs.getString("don_email"));
        d.setDonSexo(rs.getString("don_sexo"));
        return d;
    }
}