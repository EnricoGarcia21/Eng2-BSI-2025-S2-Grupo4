package DOARC.mvc.controller;

import DOARC.mvc.dao.DoadorDAO;
import DOARC.mvc.model.Doador;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoadorController {

    private DoadorDAO doadorDAO = new DoadorDAO();

    private Conexao getConexaoDoSingleton() {
        return SingletonDB.getConexao();
    }

    private Map<String, Object> mapDoadorToJson(Doador d) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", d.getDoaId());
        json.put("nome", d.getDoaNome());
        json.put("telefone", d.getDoaTelefone());
        json.put("email", d.getDoaEmail());
        json.put("cep", d.getDoaCep());
        json.put("uf", d.getDoaUf());
        json.put("cidade", d.getDoaCidade());
        json.put("bairro", d.getDoaBairro());
        json.put("rua", d.getDoaRua());
        json.put("cpf", d.getDoaCpf());
        json.put("data_nasc", d.getDoaDataNasc());
        json.put("sexo", d.getDoaSexo());
        json.put("site", d.getDoaSite());
        return json;
    }

    public List<Map<String, Object>> getDoador() {
        Conexao conn = getConexaoDoSingleton();
        List<Doador> lista = doadorDAO.get("", conn);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Doador d : lista) {
            result.add(mapDoadorToJson(d));
        }
        return result;
    }

    public Map<String, Object> getDoador(int id) {
        Conexao conn = getConexaoDoSingleton();
        Doador d = doadorDAO.get(id, conn);

        if (d == null) {
            return Map.of("erro", "Doador não encontrado");
        }
        return mapDoadorToJson(d);
    }

    public Map<String, Object> addDoador(String nome, String telefone, String email, String cep, String uf, String cidade, String bairro, String rua, String cpf, String dataNasc, String sexo, String site) {
        Conexao conn = getConexaoDoSingleton();
        Doador novo = new Doador(nome, telefone, email, cep, uf, cidade, bairro, rua, cpf, dataNasc, sexo, site);
        Doador gravado = doadorDAO.gravar(novo, conn);

        if (gravado == null) {
            return Map.of("erro", "Erro ao cadastrar o Doador");
        }
        return mapDoadorToJson(gravado);
    }

    public Map<String, Object> updtDoador(int id, String nome, String telefone, String email, String cep, String uf, String cidade, String bairro, String rua, String cpf, String dataNasc, String sexo, String site) {
        Conexao conn = getConexaoDoSingleton();
        Doador existente = doadorDAO.get(id, conn);

        if (existente == null) {
            return Map.of("erro", "Doador não encontrado para atualização");
        }

        existente.setDoaNome(nome);
        existente.setDoaTelefone(telefone);
        existente.setDoaEmail(email);
        existente.setDoaCep(cep);
        existente.setDoaUf(uf);
        existente.setDoaCidade(cidade);
        existente.setDoaBairro(bairro);
        existente.setDoaRua(rua);
        existente.setDoaCpf(cpf);
        existente.setDoaDataNasc(dataNasc);
        existente.setDoaSexo(sexo);
        existente.setDoaSite(site);

        Doador atualizado = doadorDAO.alterar(existente, conn);

        if (atualizado == null) {
            return Map.of("erro", "Erro ao atualizar o Doador");
        }
        return mapDoadorToJson(atualizado);
    }

    public Map<String, Object> deletarDoador(int id) {
        Conexao conn = getConexaoDoSingleton();
        Doador d = doadorDAO.get(id, conn);

        if (d == null) {
            return Map.of("erro", "Doador não encontrado para exclusão");
        }

        boolean deletado = doadorDAO.apagar(d, conn);

        if (deletado) {
            return Map.of("mensagem", "Doador removido com sucesso");
        }
        return Map.of("erro", "Erro ao remover o Doador");
    }
}