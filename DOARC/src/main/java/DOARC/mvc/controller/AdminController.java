package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private Login loginModel;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    // ===== MÉTODOS PARA VOLUNTÁRIOS =====

    public List<Map<String, Object>> listarUsuarios() {
        // Sua implementação existente
        return null; // placeholder
    }

    public Map<String, Object> getVoluntario(int id) {
        // Sua implementação existente
        return null; // placeholder
    }

    public Map<String, Object> updtVoluntario(Voluntario voluntario) {
        // Sua implementação existente
        return null; // placeholder
    }

    public Map<String, Object> deletarVoluntario(int id) {
        // Sua implementação existente
        return null; // placeholder
    }

    // ===== NOVOS MÉTODOS PARA LOGINS =====

    /**
     * Lista todos os logins do sistema
     */
    public List<Login> listarLogins() {
        return loginModel.consultar("", getConexao());
    }

    /**
     * Busca um login específico por ID do voluntário
     */
    public Login buscarLoginPorVoluntarioId(int voluntarioId) {
        return loginModel.buscarPorVoluntarioId(voluntarioId, getConexao());
    }

    /**
     * Atualiza o status de um login
     */
    public boolean atualizarStatusLogin(int voluntarioId, char novoStatus) {
        return loginModel.atualizarStatus(voluntarioId, novoStatus, getConexao());
    }

    /**
     * Atualiza o nível de acesso de um login
     */
    public Login atualizarNivelAcesso(int voluntarioId, String novoNivel) {
        Login login = loginModel.buscarPorVoluntarioId(voluntarioId, getConexao());
        if (login == null) {
            return null;
        }

        login.setNivelAcesso(novoNivel);
        return loginModel.alterar(login, getConexao());
    }

    // ===== NOVOS MÉTODOS PARA VOLUNTÁRIOS =====

    /**
     * Lista todos os voluntários
     */
    public List<Voluntario> listarTodosVoluntarios() {
        return voluntarioModel.consultar("", getConexao());
    }

    /**
     * Busca um voluntário por ID
     */
    public Voluntario buscarVoluntarioPorId(int id) {
        return voluntarioModel.consultar(id, getConexao());
    }
}