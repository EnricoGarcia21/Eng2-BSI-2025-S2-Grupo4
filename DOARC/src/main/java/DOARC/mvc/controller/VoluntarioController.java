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
            json.put("id", v.getVol_id());
            json.put("nome", v.getVol_nome());
            json.put("data_nasc", v.getVol_datanasc());
            json.put("rua", v.getVol_rua());
            json.put("bairro", v.getVol_bairro());
            json.put("cidade", v.getVol_cidade());
            json.put("telefone", v.getVol_telefone());
            json.put("cep", v.getVol_cep());
            json.put("uf", v.getVol_uf());
            json.put("email", v.getVol_email());
            json.put("sexo", v.getVol_sexo());
            json.put("numero", v.getVol_numero());
            json.put("cpf", v.getVol_cpf());
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
        json.put("id", v.getVol_id());
        json.put("nome", v.getVol_nome());
        json.put("data_nasc", v.getVol_datanasc());
        json.put("rua", v.getVol_rua());
        json.put("bairro", v.getVol_bairro());
        json.put("cidade", v.getVol_cidade());
        json.put("telefone", v.getVol_telefone());
        json.put("cep", v.getVol_cep());
        json.put("uf", v.getVol_uf());
        json.put("email", v.getVol_email());
        json.put("sexo", v.getVol_sexo());
        json.put("numero", v.getVol_numero());
        json.put("cpf", v.getVol_cpf());
        return json;
    }

    public Map<String, Object> addVoluntario(String nome, String dataNasc, String rua, String bairro,
                                             String cidade, String telefone, String cep, String uf,
                                             String email, String sexo, String numero, String cpf) {
        // Configura os dados na model injetada
        voluntarioModel.setVol_nome(nome);
        voluntarioModel.setVol_datanasc(dataNasc);
        voluntarioModel.setVol_rua(rua);
        voluntarioModel.setVol_bairro(bairro);
        voluntarioModel.setVol_cidade(cidade);
        voluntarioModel.setVol_telefone(telefone);
        voluntarioModel.setVol_cep(cep);
        voluntarioModel.setVol_uf(uf);
        voluntarioModel.setVol_email(email);
        voluntarioModel.setVol_sexo(sexo);
        voluntarioModel.setVol_numero(numero);
        voluntarioModel.setVol_cpf(cpf);

        // Chama o método gravar da Model (sem parâmetro - usa this)
        Voluntario gravado = voluntarioModel.gravar();
        if (gravado == null) return Map.of("erro", "Erro ao cadastrar o Voluntário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", gravado.getVol_id());
        json.put("nome", gravado.getVol_nome());
        json.put("data_nasc", gravado.getVol_datanasc());
        json.put("rua", gravado.getVol_rua());
        json.put("bairro", gravado.getVol_bairro());
        json.put("cidade", gravado.getVol_cidade());
        json.put("telefone", gravado.getVol_telefone());
        json.put("cep", gravado.getVol_cep());
        json.put("uf", gravado.getVol_uf());
        json.put("email", gravado.getVol_email());
        json.put("sexo", gravado.getVol_sexo());
        json.put("numero", gravado.getVol_numero());
        json.put("cpf", gravado.getVol_cpf());
        return json;
    }

    public Map<String, Object> updtVoluntario(int id, String nome, String dataNasc, String rua, String bairro,
                                              String cidade, String telefone, String cep, String uf,
                                              String email, String sexo, String numero, String cpf) {
        // Chama o método consultar da Model
        Voluntario existente = voluntarioModel.consultar(id);
        if (existente == null) return Map.of("erro", "Voluntário não encontrado");

        // Configura os dados no objeto existente
        existente.setVol_nome(nome);
        existente.setVol_datanasc(dataNasc);
        existente.setVol_rua(rua);
        existente.setVol_bairro(bairro);
        existente.setVol_cidade(cidade);
        existente.setVol_telefone(telefone);
        existente.setVol_cep(cep);
        existente.setVol_uf(uf);
        existente.setVol_email(email);
        existente.setVol_sexo(sexo);
        existente.setVol_numero(numero);
        existente.setVol_cpf(cpf);

        // Chama o método alterar da Model (sem parâmetro - usa this)
        Voluntario atualizado = existente.alterar();
        if (atualizado == null) return Map.of("erro", "Erro ao atualizar o Voluntário");

        Map<String, Object> json = new HashMap<>();
        json.put("id", atualizado.getVol_id());
        json.put("nome", atualizado.getVol_nome());
        json.put("data_nasc", atualizado.getVol_datanasc());
        json.put("rua", atualizado.getVol_rua());
        json.put("bairro", atualizado.getVol_bairro());
        json.put("cidade", atualizado.getVol_cidade());
        json.put("telefone", atualizado.getVol_telefone());
        json.put("cep", atualizado.getVol_cep());
        json.put("uf", atualizado.getVol_uf());
        json.put("email", atualizado.getVol_email());
        json.put("sexo", atualizado.getVol_sexo());
        json.put("numero", atualizado.getVol_numero());
        json.put("cpf", atualizado.getVol_cpf());
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