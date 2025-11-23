package DOARC.mvc.controller;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class VoluntarioController {

    @Autowired
    private Voluntario voluntarioModel;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public Map<String, Object> getVoluntario(int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Voluntario v = voluntarioModel.consultar(id, getConexao());
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
        return voluntarioModel.consultar(id, getConexao());
    }

    public Voluntario atualizarPerfil(Voluntario voluntario) {
        return voluntarioModel.alterar(voluntario, getConexao());
    }

    public Voluntario cadastrarVoluntario(Voluntario voluntario) {
        try {
            Conexao conexao = getConexao();

            // ✅ VALIDAÇÃO: Verificar CPF duplicado
            Voluntario existente = voluntarioModel.buscarPorCpf(voluntario.getVol_cpf(), conexao);
            if (existente != null) {
                System.err.println("❌ Erro: CPF já cadastrado: " + voluntario.getVol_cpf());
                return null; // Retorna null para indicar falha (View trata como erro genérico)
            }

            return voluntarioModel.gravar(voluntario, conexao);
        } catch (Exception e) {
            System.err.println("❌ Erro controller cadastrar: " + e.getMessage());
            return null;
        }
    }

    public boolean removerVoluntario(int id) {
        try {
            Voluntario v = voluntarioModel.consultar(id, getConexao());
            return v != null && voluntarioModel.apagar(v, getConexao());
        } catch (Exception e) {
            return false;
        }
    }

    public java.util.List<Voluntario> listarTodos() {
        return voluntarioModel.consultar("", getConexao());
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