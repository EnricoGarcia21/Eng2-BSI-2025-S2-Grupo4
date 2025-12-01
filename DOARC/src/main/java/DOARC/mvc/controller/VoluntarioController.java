package DOARC.mvc.controller;

import DOARC.mvc.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class VoluntarioController {

    @Autowired
    private Voluntario voluntarioModel;



    public Map<String, Object> getVoluntario(int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Chamada limpa, sem passar conexão
            Voluntario v = voluntarioModel.consultar(id);
            if (v == null) {
                response.put("erro", "Voluntário não encontrado");
                return response;
            }
            return voluntarioToMap(v);
        } catch (Exception e) {
            response.put("erro", "Erro ao buscar voluntário");
            return response;
        }
    }

    public Voluntario buscarVoluntarioPorId(int id) {
        return voluntarioModel.consultar(id);
    }

    public Voluntario atualizarPerfil(Voluntario voluntario) {
        return voluntarioModel.alterar(voluntario);
    }

    public Voluntario cadastrarVoluntario(Voluntario voluntario) {
        try {
            // Validação de CPF duplicado
            Voluntario existente = voluntarioModel.buscarPorCpf(voluntario.getVol_cpf());
            if (existente != null) {
                System.err.println("❌ Erro: CPF já cadastrado: " + voluntario.getVol_cpf());
                return null;
            }

            return voluntarioModel.gravar(voluntario);
        } catch (Exception e) {
            System.err.println("❌ Erro controller cadastrar: " + e.getMessage());
            return null;
        }
    }

    public boolean removerVoluntario(int id) {
        try {
            Voluntario v = voluntarioModel.consultar(id);
            return v != null && voluntarioModel.apagar(v);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Voluntario> listarTodos() {
        return voluntarioModel.consultar("");
    }

    // Helper para converter objeto completo para Map
    public Map<String, Object> voluntarioToMap(Voluntario v) {
        Map<String, Object> map = new HashMap<>();
        map.put("vol_id", v.getVol_id());
        map.put("vol_nome", v.getVol_nome());
        map.put("vol_cpf", v.getVol_cpf());
        map.put("vol_email", v.getVol_email());
        map.put("vol_telefone", v.getVol_telefone());
        map.put("vol_datanasc", v.getVol_datanasc());
        map.put("vol_sexo", v.getVol_sexo());
        map.put("vol_cep", v.getVol_cep());
        map.put("vol_rua", v.getVol_rua());
        map.put("vol_numero", v.getVol_numero());
        map.put("vol_bairro", v.getVol_bairro());
        map.put("vol_cidade", v.getVol_cidade());
        map.put("vol_uf", v.getVol_uf());
        return map;
    }
}