package DOARC.mvc.model;

import DOARC.mvc.dao.LoginDAO;
import DOARC.mvc.util.Conexao;
import java.util.List;

public class Login {
    private static final LoginDAO dao = new LoginDAO();

    private int loginId;
    private int voluntarioId;
    private String login;
    private String senha;
    private String nivelAcesso;
    private char status;

    public Login() {}

    public Login gravar(Conexao conexao) { return dao.gravar(this, conexao); }
    public Login alterar(Conexao conexao) { return dao.alterar(this, conexao); }
    public boolean apagar(Conexao conexao) { return dao.apagar(this, conexao); }
    public static Login get(int id, Conexao conexao) { return dao.get(id, conexao); }
    public static List<Login> get(String filtro, Conexao conexao) { return dao.get(filtro, conexao); }

    public static Login buscarPorLogin(String email, Conexao conexao) { return dao.buscarPorLogin(email, conexao); }
    public static boolean atualizarStatus(int id, String status, Conexao conexao) { return dao.atualizarStatus(id, status, conexao); }
    public static boolean atualizarSenha(int id, String senha, Conexao conexao) { return dao.atualizarSenha(id, senha, conexao); }

    // Getters/Setters
    public int getLoginId() { return loginId; }
    public void setLoginId(int loginId) { this.loginId = loginId; }
    public int getVoluntarioId() { return voluntarioId; }
    public void setVoluntarioId(int voluntarioId) { this.voluntarioId = voluntarioId; }
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getNivelAcesso() { return nivelAcesso; }
    public void setNivelAcesso(String nivelAcesso) { this.nivelAcesso = nivelAcesso; }
    public char getStatus() { return status; }
    public void setStatus(char status) { this.status = status; }
}