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
    private String niveAcesso;
    private char status;

    @Autowired // Model Instancia (recebe) o DAO
    private LoginDAO dao;

    // --- CONSTRUTORES ---
    public Login() {
        // Construtor vazio
    }

    public Login(int voluntarioId, String login, String senha, String niveAcesso, char status) {
        this.voluntarioId = voluntarioId;
        this.login = login;
        this.senha = senha;
        this.niveAcesso = niveAcesso;
        this.status = status;
    }

    // --- MÉTODOS DE DELEGAÇÃO PARA O DAO ---
    public List<Login> consultar(String filtro) {
        return dao.get(filtro);
    }

    public Login consultar(int id) {
        return dao.get(id);
    }

    public Login gravar() {
        return dao.gravar(this);
    }

    public Login alterar() {
        return dao.alterar(this);
    }

    public boolean apagar() {
        return dao.apagar(this);
    }

    // 🚨 NOVO MÉTODO: Busca credencial por nome de usuário (login) exato.
    public Login buscarPorLogin(String login) {
        return dao.buscarPorLoginExato(login);
    }

    // --- GETTERS E SETTERS ---
    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }

    public int getVoluntarioId() { return voluntarioId; }
    public void setVoluntarioId(int voluntarioId) { this.voluntarioId = voluntarioId; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNiveAcesso() { return niveAcesso; }
    public void setNiveAcesso(String niveAcesso) { this.niveAcesso = niveAcesso; }

    public char getStatus() { return status; }
    public void setStatus(char status) { this.status = status; }
}