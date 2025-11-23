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

<<<<<<< HEAD
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
=======
    /**
     * Retorna os dados de um voluntário em formato Map
     * @param id ID do voluntário
     * @return Map com os dados do voluntário ou erro
     */
    public Map<String, Object> getVoluntario(int id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Voluntario voluntario = voluntarioModel.consultar(id, getConexao());

            if (voluntario == null) {
                response.put("erro", "Voluntário não encontrado");
                return response;
            }

            // Converter Voluntario para Map
            response.put("vol_id", voluntario.getVol_id());
            response.put("vol_nome", voluntario.getVol_nome());
            response.put("vol_cpf", voluntario.getVol_cpf());
            response.put("vol_email", voluntario.getVol_email());
            response.put("vol_telefone", voluntario.getVol_telefone());
            response.put("vol_datanasc", voluntario.getVol_datanasc());
            response.put("vol_rua", voluntario.getVol_rua());
            response.put("vol_bairro", voluntario.getVol_bairro());
            response.put("vol_cidade", voluntario.getVol_cidade());
            response.put("vol_numero", voluntario.getVol_numero());
            response.put("vol_cep", voluntario.getVol_cep());
            response.put("vol_uf", voluntario.getVol_uf());
            response.put("vol_sexo", voluntario.getVol_sexo());

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro na controller ao buscar voluntário: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            response.put("erro", "Erro ao buscar voluntário");
            return response;
        }
    }

<<<<<<< HEAD
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
=======
    /**
     * Busca um voluntário por ID e retorna o objeto
     * @param id ID do voluntário
     * @return Objeto Voluntario ou null
     */
    public Voluntario buscarVoluntarioPorId(int id) {
        try {
            return voluntarioModel.consultar(id, getConexao());
        } catch (Exception e) {
            System.err.println("❌ Erro na controller ao buscar voluntário por ID: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            return null;
        }
    }

<<<<<<< HEAD
    public boolean removerVoluntario(int id) {
        try {
            Voluntario v = voluntarioModel.consultar(id, getConexao());
            return v != null && voluntarioModel.apagar(v, getConexao());
        } catch (Exception e) {
=======
    /**
     * Atualiza o perfil de um voluntário
     * @param voluntario Objeto Voluntario com os dados atualizados
     * @return Voluntario atualizado ou null em caso de erro
     */
    public Voluntario atualizarPerfil(Voluntario voluntario) {
        try {
            return voluntarioModel.alterar(voluntario, getConexao());
        } catch (Exception e) {
            System.err.println("❌ Erro na controller ao atualizar perfil: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cadastra um novo voluntário
     * @param voluntario Objeto Voluntario com os dados
     * @return Voluntario cadastrado ou null em caso de erro
     */
    public Voluntario cadastrarVoluntario(Voluntario voluntario) {
        try {
            return voluntarioModel.gravar(voluntario, getConexao());
        } catch (Exception e) {
            System.err.println("❌ Erro na controller ao cadastrar voluntário: " + e.getMessage());
            return null;
        }
    }

    /**
     * Busca um voluntário por CPF
     * @param cpf CPF do voluntário
     * @return Objeto Voluntario ou null
     */
    public Voluntario buscarPorCpf(String cpf) {
        try {
            return voluntarioModel.buscarPorCpf(cpf, getConexao());
        } catch (Exception e) {
            System.err.println("❌ Erro na controller ao buscar por CPF: " + e.getMessage());
            return null;
        }
    }

    /**
     * Remove um voluntário
     * @param id ID do voluntário
     * @return true se removido com sucesso, false caso contrário
     */
    public boolean removerVoluntario(int id) {
        try {
            Voluntario voluntario = voluntarioModel.consultar(id, getConexao());
            if (voluntario == null) {
                return false;
            }
            return voluntarioModel.apagar(voluntario, getConexao());
        } catch (Exception e) {
            System.err.println("❌ Erro na controller ao remover voluntário: " + e.getMessage());
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            return false;
        }
    }

<<<<<<< HEAD
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
=======
    /**
     * Lista todos os voluntários
     * @return Lista de voluntários
     */
    public java.util.List<Voluntario> listarTodos() {
        try {
            return voluntarioModel.consultar("", getConexao());
        } catch (Exception e) {
            System.err.println("❌ Erro na controller ao listar voluntários: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
    }
}