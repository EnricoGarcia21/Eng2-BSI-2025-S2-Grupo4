package DOARC.mvc.model;

import DOARC.mvc.dao.LoginDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Login {

    private int loginId;
    private int voluntarioId;
    private String login;
    private String senha;
    private String nivelAcesso;
    private char status;

    @Autowired
    private LoginDAO dao;

    public Login() {}

    public Login(int loginId, int voluntarioId, String login, String senha,
                 String nivelAcesso, char status) {
        this.loginId = loginId;
        this.voluntarioId = voluntarioId;
        this.login = login;
        this.senha = senha;
        this.nivelAcesso = nivelAcesso;
        this.status = status;
    }

    // Métodos de operações CRUD (sem Conexao)
    public Login consultar(int id) {
        return dao.get(id);
    }

    public List<Login> consultar(String filtro) {
        return dao.get(filtro);
    }

    public Login gravar(Login login) {
        return dao.gravar(login);
    }

    public Login alterar(Login login) {
        return dao.alterar(login);
    }

    public boolean apagar(Login login) {
        return dao.apagar(login);
    }

    // Métodos de busca específicos
    public Login buscarPorLogin(String login) {
        return dao.buscarPorLogin(login);
    }

    public Login buscarPorVoluntarioId(int voluntarioId) {
        return dao.buscarPorVoluntarioId(voluntarioId);
    }

    public boolean atualizarStatus(int loginId, char novoStatus) {
        String statusStr = (novoStatus == 'A' || novoStatus == 'a') ? "Ativo" : "Inativo";
        return dao.atualizarStatus(loginId, statusStr);
    }

    public boolean atualizarSenha(int loginId, String novaSenhaHash) {
        return dao.atualizarSenha(loginId, novaSenhaHash);
    }

    // Métodos auxiliares
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.nivelAcesso);
    }

    public boolean isAtivo() {
        return this.status == 'A';
    }

    // Getters e Setters
    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public int getVoluntarioId() {
        return voluntarioId;
    }

    public void setVoluntarioId(int voluntarioId) {
        this.voluntarioId = voluntarioId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public LoginDAO getDao() {
        return dao;
    }

    public void setDao(LoginDAO dao) {
        this.dao = dao;
    }

    @Override
    public String toString() {
        return "Login{" +
                "loginId=" + loginId +
                ", voluntarioId=" + voluntarioId +
                ", login='" + login + '\'' +
                ", nivelAcesso='" + nivelAcesso + '\'' +
                ", status=" + status +
                '}';
    }
}