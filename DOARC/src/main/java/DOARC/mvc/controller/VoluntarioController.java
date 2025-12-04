package DOARC.mvc.controller;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import DOARC.mvc.util.ValidationUtil;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Controller
public class VoluntarioController {

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public Map<String, Object> getVoluntario(int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Conexao conexao = getConexao();
            Voluntario v = Voluntario.get(id, conexao);
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
        return Voluntario.get(id, getConexao());
    }

    public Voluntario atualizarPerfil(Voluntario voluntario) {
        if (voluntario.getVol_cpf() != null) {
            voluntario.setVol_cpf(ValidationUtil.cleanCPF(voluntario.getVol_cpf()));
        }
        if (voluntario.getVol_telefone() != null) {
            voluntario.setVol_telefone(ValidationUtil.cleanPhone(voluntario.getVol_telefone()));
        }
        return voluntario.alterar(getConexao());
    }

    public Voluntario cadastrarVoluntario(Voluntario voluntario) {
        try {
            Conexao conexao = getConexao();

            // Normaliza
            voluntario.setVol_cpf(ValidationUtil.cleanCPF(voluntario.getVol_cpf()));
            voluntario.setVol_telefone(ValidationUtil.cleanPhone(voluntario.getVol_telefone()));
            voluntario.setVol_sexo(ValidationUtil.normalizeSexo(voluntario.getVol_sexo()));

            Voluntario existente = Voluntario.buscarPorCpf(voluntario.getVol_cpf(), conexao);
            if (existente != null) {
                System.err.println("❌ Erro: CPF já cadastrado: " + voluntario.getVol_cpf());
                return null;
            }

            return voluntario.gravar(conexao);
        } catch (Exception e) {
            System.err.println("❌ Erro controller cadastrar: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean removerVoluntario(int id) {
        try {
            Conexao conexao = getConexao();
            Voluntario v = Voluntario.get(id, conexao);
            return v != null && v.apagar(conexao);
        } catch (Exception e) {
            return false;
        }
    }

    public List<Voluntario> listarTodos() {
        return Voluntario.get("", getConexao());
    }

    public Map<String, Object> voluntarioToMap(Voluntario v) {
        Map<String, Object> map = new HashMap<>();
        if (v == null) return map;
        map.put("vol_id", v.getVol_id());
        map.put("vol_nome", v.getVol_nome());
        map.put("vol_cpf", ValidationUtil.formatCPF(v.getVol_cpf()));
        map.put("vol_email", v.getVol_email());
        map.put("vol_telefone", ValidationUtil.formatPhone(v.getVol_telefone()));
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