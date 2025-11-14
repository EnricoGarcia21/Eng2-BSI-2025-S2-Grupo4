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
            response.put("erro", "Erro ao buscar voluntário");
            return response;
        }
    }

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
            return null;
        }
    }

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
            return false;
        }
    }

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
    }
}