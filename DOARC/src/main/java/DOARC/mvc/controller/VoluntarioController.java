package DOARC.mvc.controller;

import DOARC.mvc.dao.VoluntarioDAO;
import DOARC.mvc.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoluntarioController {

    @Autowired
    private VoluntarioDAO volModel;

    // Retorna todos os voluntários cadastrados
    public List<Map<String, Object>> getVoluntarios() {
        List<Voluntario> lista = volModel.get("");
        List<Map<String, Object>> list = new ArrayList<>();
        for (Voluntario vol : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", vol.getVolId());
            json.put("nome", vol.getVolNome());
            json.put("bairro", vol.getVolBairro());
            json.put("numero", vol.getVolNumero());
            json.put("rua", vol.getVolRua());
            json.put("telefone", vol.getVolTelefone());
            json.put("cidade", vol.getVolCidade());
            json.put("cep", vol.getVolCep());
            json.put("uf", vol.getVolUf());
            json.put("email", vol.getVolEmail());
            json.put("cpf", vol.getVolCpf());
            json.put("dataNasc", vol.getVolDataNasc());
            json.put("sexo", vol.getVolSexo());
            list.add(json);
        }
        return list;
    }

    // Retorna apenas um voluntário por id
    public Map<String, Object> getVoluntario(int id) {
        Voluntario vol = volModel.get(id);
        if (vol == null)
            return Map.of("erro", "Voluntário não encontrado");
        else {
            Map<String, Object> json = new HashMap<>();
            json.put("id", vol.getVolId());
            json.put("nome", vol.getVolNome());
            json.put("bairro", vol.getVolBairro());
            json.put("numero", vol.getVolNumero());
            json.put("rua", vol.getVolRua());
            json.put("telefone", vol.getVolTelefone());
            json.put("cidade", vol.getVolCidade());
            json.put("cep", vol.getVolCep());
            json.put("uf", vol.getVolUf());
            json.put("email", vol.getVolEmail());
            json.put("cpf", vol.getVolCpf());
            json.put("dataNasc", vol.getVolDataNasc());
            json.put("sexo", vol.getVolSexo());
            return json;
        }
    }

    // Adiciona um novo voluntário
    public Map<String, Object> addVoluntario(String nome, String bairro, String numero, String rua, String telefone,
                                             String cidade, String cep, String uf, String email,
                                             String cpf, String dataNasc, String sexo) {

        Voluntario novo = new Voluntario(0,nome, bairro, numero, rua, telefone, cidade, cep, uf, email, cpf, dataNasc, sexo);
        Voluntario gravado = volModel.gravar(novo);

        if (gravado == null)
            return Map.of("erro", "Erro ao cadastrar o voluntário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravado.getVolId());
        json.put("nome", gravado.getVolNome());
        json.put("bairro", gravado.getVolBairro());
        json.put("numero", gravado.getVolNumero());
        json.put("rua", gravado.getVolRua());
        json.put("telefone", gravado.getVolTelefone());
        json.put("cidade", gravado.getVolCidade());
        json.put("cep", gravado.getVolCep());
        json.put("uf", gravado.getVolUf());
        json.put("email", gravado.getVolEmail());
        json.put("cpf", gravado.getVolCpf());
        json.put("dataNasc", gravado.getVolDataNasc());
        json.put("sexo", gravado.getVolSexo());
        return json;
    }

    // Atualiza um voluntário existente
    public Map<String, Object> updtVoluntario(int id, String nome, String bairro, String numero, String rua, String telefone,
                                              String cidade, String cep, String uf, String email,
                                              String cpf, String dataNasc, String sexo) {

        Voluntario existente = volModel.get(id);
        if (existente == null)
            return Map.of("erro", "Voluntário não encontrado");

        existente.setVolNome(nome);
        existente.setVolBairro(bairro);
        existente.setVolNumero(numero);
        existente.setVolRua(rua);
        existente.setVolTelefone(telefone);
        existente.setVolCidade(cidade);
        existente.setVolCep(cep);
        existente.setVolUf(uf);
        existente.setVolEmail(email);
        existente.setVolCpf(cpf);
        existente.setVolDataNasc(dataNasc);
        existente.setVolSexo(sexo);

        Voluntario atualizado = volModel.alterar(existente);
        if (atualizado == null)
            return Map.of("erro", "Erro ao atualizar o voluntário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizado.getVolId());
        json.put("nome", atualizado.getVolNome());
        json.put("bairro", atualizado.getVolBairro());
        json.put("numero", atualizado.getVolNumero());
        json.put("rua", atualizado.getVolRua());
        json.put("telefone", atualizado.getVolTelefone());
        json.put("cidade", atualizado.getVolCidade());
        json.put("cep", atualizado.getVolCep());
        json.put("uf", atualizado.getVolUf());
        json.put("email", atualizado.getVolEmail());
        json.put("cpf", atualizado.getVolCpf());
        json.put("dataNasc", atualizado.getVolDataNasc());
        json.put("sexo", atualizado.getVolSexo());
        return json;
    }

    // Deleta um voluntário
    public Map<String, Object> deletarVoluntario(int id) {
        Voluntario v = volModel.get(id);
        if (v == null)
            return Map.of("erro", "Voluntário não encontrado");

        boolean deletado = volModel.apagar(v);
        return deletado
                ? Map.of("mensagem", "Voluntário removido com sucesso")
                : Map.of("erro", "Erro ao remover o voluntário");
    }
}
