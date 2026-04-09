package DOARC.mvc.controller;

import DOARC.mvc.model.Donatario;
import DOARC.mvc.util.Conexao; // Importação da classe Conexao
import DOARC.mvc.util.SingletonDB; // Importação para obter a Conexao
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DonatarioController {

    @Autowired // Controller recebe a Model
    private Donatario donatarioModel;

    // --- NOVO MÉTODO: ONDE A CONEXÃO É INSTANCIADA ---
    private Conexao getConexao() {
        // O Controller chama o método estático para obter a Conexao
        return SingletonDB.conectar();
    }
    // --------------------------------------------------

    // --- GET ALL ---
    public List<Map<String, Object>> getDonatario() {
        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        // Chama o método consultar da Model, PASSANDO a Conexão
        List<Donatario> lista = donatarioModel.consultar("", conexao);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Donatario d : lista) {
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
            result.add(json);
        }
        return result;
    }

    // --- GET BY ID ---
    public Map<String, Object> getDonatario(int id) {
        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        // Chama o método consultar da Model, PASSANDO a Conexão
        Donatario d = donatarioModel.consultar(id, conexao);

        if (d == null) return Map.of("erro", "Donatário não encontrado");

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

    // --- ADD ---
    public Map<String, Object> addDonatario(String nome, String dataNasc, String rua, String bairro,
                                            String cidade, String telefone, String cep, String uf,
                                            String email, String sexo) {
        // Controller instancia a Model
        Donatario novo = new Donatario(nome, dataNasc, rua, bairro, cidade, telefone, cep, uf, email, sexo);

        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        // Chama o método gravar da Model, PASSANDO a Conexão
        Donatario gravado = donatarioModel.gravar(novo, conexao);

        if (gravado == null) return Map.of("erro", "Erro ao cadastrar o Donatário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravado.getDonId());
        json.put("nome", gravado.getDonNome());
        json.put("data_nasc", gravado.getDonDataNasc());
        json.put("rua", gravado.getDonRua());
        json.put("bairro", gravado.getDonBairro());
        json.put("cidade", gravado.getDonCidade());
        json.put("telefone", gravado.getDonTelefone());
        json.put("cep", gravado.getDonCep());
        json.put("uf", gravado.getDonUf());
        json.put("email", gravado.getDonEmail());
        json.put("sexo", gravado.getDonSexo());
        return json;
    }

    // --- UPDATE ---
    public Map<String, Object> updtDonatario(int id, String nome, String dataNasc, String rua, String bairro,
                                             String cidade, String telefone, String cep, String uf,
                                             String email, String sexo) {

        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão

        // Chama o método consultar da Model, PASSANDO a Conexão
        Donatario existente = donatarioModel.consultar(id, conexao);
        if (existente == null) return Map.of("erro", "Donatário não encontrado");

        existente.setDonNome(nome);
        existente.setDonDataNasc(dataNasc);
        existente.setDonRua(rua);
        existente.setDonBairro(bairro);
        existente.setDonCidade(cidade);
        existente.setDonTelefone(telefone);
        existente.setDonCep(cep);
        existente.setDonUf(uf);
        existente.setDonEmail(email);
        existente.setDonSexo(sexo);

        // Chama o método alterar da Model, PASSANDO a Conexão
        Donatario atualizado = donatarioModel.alterar(existente, conexao);
        if (atualizado == null) return Map.of("erro", "Erro ao atualizar o Donatário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizado.getDonId());
        json.put("nome", atualizado.getDonNome());
        json.put("data_nasc", atualizado.getDonDataNasc());
        json.put("rua", atualizado.getDonRua());
        json.put("bairro", atualizado.getDonBairro());
        json.put("cidade", atualizado.getDonCidade());
        json.put("telefone", atualizado.getDonTelefone());
        json.put("cep", atualizado.getDonCep());
        json.put("uf", atualizado.getDonUf());
        json.put("email", atualizado.getDonEmail());
        json.put("sexo", atualizado.getDonSexo());
        return json;
    }

    // --- DELETE ---
    public Map<String, Object> deletarDonatario(int id) {
        Conexao conexao = getConexao(); // 1. INSTANCIA a Conexão
        // Chama o método consultar da Model, PASSANDO a Conexão
        Donatario d = donatarioModel.consultar(id, conexao);

        if (d == null) return Map.of("erro", "Donatário não encontrado");

        // Chama o método apagar da Model, PASSANDO a Conexão
        boolean deletado = donatarioModel.apagar(d, conexao);
        return deletado ? Map.of("mensagem", "Donatário removido com sucesso") : Map.of("erro", "Erro ao remover o Donatário");
    }
}