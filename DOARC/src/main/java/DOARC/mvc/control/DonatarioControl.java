package DOARC.mvc.control;

import DOARC.mvc.dao.DonatarioDAO;
import DOARC.mvc.model.Donatario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Control com padrão Facade
 * - Responsável por validar dados de entrada
 * - Atua como fachada para as operações de donatário
 * - DAO gerencia a conexão internamente
 */
@Service
public class DonatarioControl {

    @Autowired
    private DonatarioDAO donatarioDAO;

    public List<Map<String, Object>> getDonatarios() {
        List<Donatario> lista = donatarioDAO.getAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Donatario d : lista) {
            result.add(toMap(d));
        }
        return result;
    }

    public Map<String, Object> getDonatario(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Donatario d = donatarioDAO.get(id);
        if (d == null) {
            return Map.of("erro", "Donatário não encontrado");
        }

        return toMap(d);
    }

    public Map<String, Object> addDonatario(String nome, String dataNasc, String rua, String bairro,
                                            String cidade, String telefone, String cep, String uf,
                                            String email, String sexo) {
        // Validação de entrada
        Map<String, String> validacao = validarDados(nome, dataNasc, telefone, cep, uf, email, sexo);
        if (!validacao.isEmpty()) {
            return Map.of("erro", validacao.values().iterator().next());
        }

        Donatario novo = new Donatario(nome.trim(), dataNasc, rua != null ? rua.trim() : "",
                bairro != null ? bairro.trim() : "", cidade != null ? cidade.trim() : "",
                telefone, cep, uf, email != null ? email.trim() : "", sexo);

        Donatario gravado = donatarioDAO.gravar(novo);
        if (gravado == null) {
            return Map.of("erro", "Erro ao cadastrar o Donatário");
        }

        return toMap(gravado);
    }

    public Map<String, Object> updtDonatario(int id, String nome, String dataNasc, String rua, String bairro,
                                             String cidade, String telefone, String cep, String uf,
                                             String email, String sexo) {
        // Validação de ID
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        // Validação de entrada
        Map<String, String> validacao = validarDados(nome, dataNasc, telefone, cep, uf, email, sexo);
        if (!validacao.isEmpty()) {
            return Map.of("erro", validacao.values().iterator().next());
        }

        Donatario existente = donatarioDAO.get(id);
        if (existente == null) {
            return Map.of("erro", "Donatário não encontrado");
        }

        existente.setDonNome(nome.trim());
        existente.setDonDataNasc(dataNasc);
        existente.setDonRua(rua != null ? rua.trim() : "");
        existente.setDonBairro(bairro != null ? bairro.trim() : "");
        existente.setDonCidade(cidade != null ? cidade.trim() : "");
        existente.setDonTelefone(telefone);
        existente.setDonCep(cep);
        existente.setDonUf(uf);
        existente.setDonEmail(email != null ? email.trim() : "");
        existente.setDonSexo(sexo);

        Donatario atualizado = donatarioDAO.alterar(existente);
        if (atualizado == null) {
            return Map.of("erro", "Erro ao atualizar o Donatário");
        }

        return toMap(atualizado);
    }

    public Map<String, Object> deletarDonatario(int id) {
        if (id <= 0) {
            return Map.of("erro", "ID inválido");
        }

        Donatario d = donatarioDAO.get(id);
        if (d == null) {
            return Map.of("erro", "Donatário não encontrado");
        }

        boolean deletado = donatarioDAO.apagar(d);
        return deletado
                ? Map.of("mensagem", "Donatário removido com sucesso")
                : Map.of("erro", "Erro ao remover o Donatário");
    }

    public List<Map<String, Object>> buscarDonatarios(String filtro) {
        List<Donatario> lista = donatarioDAO.get(filtro);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Donatario d : lista) {
            result.add(toMap(d));
        }
        return result;
    }

    private Map<String, String> validarDados(String nome, String dataNasc, String telefone,
                                            String cep, String uf, String email, String sexo) {
        Map<String, String> erros = new HashMap<>();

        if (nome == null || nome.trim().isEmpty()) {
            erros.put("nome", "Nome é obrigatório");
        } else if (nome.length() > 100) {
            erros.put("nome", "Nome muito longo (máximo 100 caracteres)");
        }

        if (dataNasc == null || dataNasc.trim().isEmpty()) {
            erros.put("dataNasc", "Data de nascimento é obrigatória");
        } else if (!dataNasc.matches("\\d{4}-\\d{2}-\\d{2}")) {
            erros.put("dataNasc", "Data de nascimento inválida (formato esperado: YYYY-MM-DD)");
        }

        if (telefone != null && !telefone.isEmpty() && telefone.length() > 12) {
            erros.put("telefone", "Telefone muito longo (máximo 12 caracteres)");
        }

        if (cep != null && !cep.isEmpty() && cep.length() != 8) {
            erros.put("cep", "CEP inválido (deve ter 8 dígitos)");
        }

        if (uf != null && !uf.isEmpty() && uf.length() != 2) {
            erros.put("uf", "UF inválida (deve ter 2 caracteres)");
        }

        if (email != null && !email.isEmpty()) {
            if (email.length() > 100) {
                erros.put("email", "Email muito longo (máximo 100 caracteres)");
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                erros.put("email", "Email inválido");
            }
        }

        if (sexo != null && !sexo.isEmpty()) {
            if (!sexo.matches("Masculino|Feminino|Não Informado")) {
                erros.put("sexo", "Sexo inválido (valores aceitos: Masculino, Feminino, Não Informado)");
            }
        }

        return erros;
    }

    private Map<String, Object> toMap(Donatario d) {
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
}
