package DOARC.mvc.controller;

import DOARC.mvc.dao.DonatarioDAO;
import DOARC.mvc.model.Donatario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DonatarioController {

    private final DonatarioDAO donatarioModel = new DonatarioDAO();

    private Conexao getConexaoDoSingleton() {
        return SingletonDB.getConexao();
    }

    private Map<String, Object> mapDonatarioToJson(Donatario d) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", d.getDonId());
        json.put("nome", d.getDonNome());
        json.put("data_nasc", d.getDonDataNasc());
        json.put("rua", d.getDonRua());
        json.put("bairro", d.getDonBairro());
        json.put("cidade", d.getDonCidade());
        json.put("telefone", d.getDonTelefone());
        json.put("cep", d.getDonCep());
        json.put("uf", d.getDonUf());
        json.put("email", d.getDonEmail());
        json.put("sexo", d.getDonSexo());
        return json;
    }

    public List<Map<String, Object>> getDonatario() {
        Conexao conn = getConexaoDoSingleton();
        List<Donatario> lista = donatarioModel.get("", conn);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Donatario d : lista) {
            result.add(mapDonatarioToJson(d));
        }
        return result;
    }

    public Map<String, Object> getDonatario(int id) {
        Conexao conn = getConexaoDoSingleton();
        Donatario d = donatarioModel.get(id, conn);

        if (d == null) {
            return Map.of("erro", "Donatário não encontrado com ID: " + id);
        }
        return mapDonatarioToJson(d);
    }

    public Map<String, Object> addDonatario(String don_nome, String don_data_nasc, String don_rua, String don_bairro,
                                            String don_cidade, String don_telefone, String don_cep, String don_uf,
                                            String don_email, String don_sexo) {

        Conexao conn = getConexaoDoSingleton();
        Donatario novo = new Donatario(don_nome, don_data_nasc, don_rua, don_bairro, don_cidade,
                don_telefone, don_cep, don_uf, don_email, don_sexo);

        Donatario gravado = donatarioModel.gravar(novo, conn);

        if (gravado == null || gravado.getDonId() == 0) {
            return Map.of("erro", "Erro ao gravar o Donatário no banco de dados.");
        }
        return mapDonatarioToJson(gravado);
    }

    /**
     * UPDATE: Método renomeado para updtDonatario (para corresponder à View).
     */
    public Map<String, Object> updtDonatario(int don_id, String don_nome, String don_data_nasc, String don_rua,
                                             String don_bairro, String don_cidade, String don_telefone,
                                             String don_cep, String don_uf, String don_email, String don_sexo) {

        Conexao conn = getConexaoDoSingleton();
        Donatario existente = donatarioModel.get(don_id, conn);

        if (existente == null) {
            return Map.of("erro", "Donatário não encontrado para alteração com ID: " + don_id);
        }

        existente.setDonNome(don_nome);
        existente.setDonDataNasc(don_data_nasc);
        existente.setDonRua(don_rua);
        existente.setDonBairro(don_bairro);
        existente.setDonCidade(don_cidade);
        existente.setDonTelefone(don_telefone);
        existente.setDonCep(don_cep);
        existente.setDonUf(don_uf);
        existente.setDonEmail(don_email);
        existente.setDonSexo(don_sexo);

        Donatario atualizado = donatarioModel.alterar(existente, conn);

        if (atualizado == null) {
            return Map.of("erro", "Erro ao atualizar o Donatário no banco de dados.");
        }
        return mapDonatarioToJson(atualizado);
    }

    public Map<String, Object> deletarDonatario(int id) {
        Conexao conn = getConexaoDoSingleton();
        Donatario paraDeletar = donatarioModel.get(id, conn);

        if (paraDeletar == null) {
            return Map.of("erro", "Donatário não encontrado para exclusão com ID: " + id);
        }

        boolean sucesso = donatarioModel.apagar(paraDeletar, conn);

        if (!sucesso) {
            return Map.of("erro", "Erro ao apagar o Donatário no banco de dados.");
        }
        return Map.of("sucesso", "Donatário com ID " + id + " excluído.");
    }
}