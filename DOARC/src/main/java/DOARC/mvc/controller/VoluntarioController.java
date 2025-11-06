package DOARC.mvc.controller;

import DOARC.mvc.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoluntarioController {

    @Autowired // Controller recebe a Model (não o DAO)
    private Voluntario voluntarioModel;

    // Método para listar todos os voluntários
    public List<Map<String, Object>> getVoluntarios() {
        // Chama o método consultar da Model
        List<Voluntario> lista = voluntarioModel.consultar("");
        List<Map<String, Object>> result = new ArrayList<>();
        for (Voluntario v : lista) {
            Map<String, Object> json = new HashMap<>();
            json.put("id", v.getVolId());
            json.put("nome", v.getVolNome());
            json.put("data_nasc", v.getVolDataNasc());
            json.put("rua", v.getVolRua());
            json.put("bairro", v.getVolBairro());
            json.put("cidade", v.getVolCidade());
            json.put("telefone", v.getVolTelefone());
            json.put("cep", v.getVolCep());
            json.put("uf", v.getVolUf());
            json.put("email", v.getVolEmail());
            json.put("sexo", v.getVolSexo());
            json.put("numero", v.getVolNumero());
            json.put("cpf", v.getVolCpf());
            result.add(json);
        }
        return result;
    }

    // Método para buscar um voluntário por ID (nome diferente para evitar conflito)
    public Map<String, Object> getVoluntarioById(int id) {
        // Chama o método consultar da Model
        Voluntario v = voluntarioModel.consultar(id);
        if (v == null) return Map.of("erro", "Voluntário não encontrado");

        Map<String, Object> json = new HashMap<>();
        json.put("id", v.getVolId());
        json.put("nome", v.getVolNome());
        json.put("data_nasc", v.getVolDataNasc());
        json.put("rua", v.getVolRua());
        json.put("bairro", v.getVolBairro());
        json.put("cidade", v.getVolCidade());
        json.put("telefone", v.getVolTelefone());
        json.put("cep", v.getVolCep());
        json.put("uf", v.getVolUf());
        json.put("email", v.getVolEmail());
        json.put("sexo", v.getVolSexo());
        json.put("numero", v.getVolNumero());
        json.put("cpf", v.getVolCpf());
        return json;
    }

    public Map<String, Object> addVoluntario(String nome, String dataNasc, String rua, String bairro,
                                             String cidade, String telefone, String cep, String uf,
                                             String email, String sexo, String numero, String cpf) {
        // Configura os dados na model injetada
        voluntarioModel.setVolNome(nome);
        voluntarioModel.setVolDataNasc(dataNasc);
        voluntarioModel.setVolRua(rua);
        voluntarioModel.setVolBairro(bairro);
        voluntarioModel.setVolCidade(cidade);
        voluntarioModel.setVolTelefone(telefone);
        voluntarioModel.setVolCep(cep);
        voluntarioModel.setVolUf(uf);
        voluntarioModel.setVolEmail(email);
        voluntarioModel.setVolSexo(sexo);
        voluntarioModel.setVolNumero(numero);
        voluntarioModel.setVolCpf(cpf);

        // Chama o método gravar da Model (sem parâmetro - usa this)
        Voluntario gravado = voluntarioModel.gravar();
        if (gravado == null) return Map.of("erro", "Erro ao cadastrar o Voluntário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravado.getVolId());
        json.put("nome", gravado.getVolNome());
        json.put("data_nasc", gravado.getVolDataNasc());
        json.put("rua", gravado.getVolRua());
        json.put("bairro", gravado.getVolBairro());
        json.put("cidade", gravado.getVolCidade());
        json.put("telefone", gravado.getVolTelefone());
        json.put("cep", gravado.getVolCep());
        json.put("uf", gravado.getVolUf());
        json.put("email", gravado.getVolEmail());
        json.put("sexo", gravado.getVolSexo());
        json.put("numero", gravado.getVolNumero());
        json.put("cpf", gravado.getVolCpf());
        return json;
    }

    public Map<String, Object> updtVoluntario(int id, String nome, String dataNasc, String rua, String bairro,
                                              String cidade, String telefone, String cep, String uf,
                                              String email, String sexo, String numero, String cpf) {
        // Chama o método consultar da Model
        Voluntario existente = voluntarioModel.consultar(id);
        if (existente == null) return Map.of("erro", "Voluntário não encontrado");

        // Configura os dados no objeto existente
        existente.setVolNome(nome);
        existente.setVolDataNasc(dataNasc);
        existente.setVolRua(rua);
        existente.setVolBairro(bairro);
        existente.setVolCidade(cidade);
        existente.setVolTelefone(telefone);
        existente.setVolCep(cep);
        existente.setVolUf(uf);
        existente.setVolEmail(email);
        existente.setVolSexo(sexo);
        existente.setVolNumero(numero);
        existente.setVolCpf(cpf);

        // Chama o método alterar da Model (sem parâmetro - usa this)
        Voluntario atualizado = existente.alterar();
        if (atualizado == null) return Map.of("erro", "Erro ao atualizar o Voluntário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizado.getVolId());
        json.put("nome", atualizado.getVolNome());
        json.put("data_nasc", atualizado.getVolDataNasc());
        json.put("rua", atualizado.getVolRua());
        json.put("bairro", atualizado.getVolBairro());
        json.put("cidade", atualizado.getVolCidade());
        json.put("telefone", atualizado.getVolTelefone());
        json.put("cep", atualizado.getVolCep());
        json.put("uf", atualizado.getVolUf());
        json.put("email", atualizado.getVolEmail());
        json.put("sexo", atualizado.getVolSexo());
        json.put("numero", atualizado.getVolNumero());
        json.put("cpf", atualizado.getVolCpf());
        return json;
    }

    public Map<String, Object> deletarVoluntario(int id) {
        // Chama o método consultar da Model
        Voluntario v = voluntarioModel.consultar(id);
        if (v == null) return Map.of("erro", "Voluntário não encontrado");

        // Chama o método apagar da Model (sem parâmetro - usa this)
        boolean deletado = v.apagar();
        return deletado ? Map.of("mensagem", "Voluntário removido com sucesso") : Map.of("erro", "Erro ao remover o Voluntário");
    }
}