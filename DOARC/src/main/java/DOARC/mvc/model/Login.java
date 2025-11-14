package DOARC.mvc.model;

import DOARC.mvc.dao.LoginDAO;
import DOARC.mvc.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Login {

    private int loginId;
    private int voluntarioId;
    private String login;
    private String senha;
    private String niveAcesso;
    private char status;


    @Autowired
    private LoginDAO dao;


    public Login() {}


    public Login(LoginDAO dao) {
        this.dao = dao;
    }


    public Login(int loginId, int voluntarioId, String login, String senha,
                 String niveAcesso, char status, LoginDAO dao) {
        this.loginId = loginId;
        this.voluntarioId = voluntarioId;
        this.login = login;
        this.senha = senha;
        this.niveAcesso = niveAcesso;
        this.status = status;
        this.dao = dao;
    }


    public Login consultar(int id, Conexao conexao) {
        return dao.get(id,conexao);
    }

    public java.util.List<Login> consultar(String filtro,Conexao conexao) {
        return dao.get(filtro,conexao);
    }

    public Login gravar(Login login,Conexao conexao) {
        return dao.gravar(login,conexao);
    }

    public Login alterar(Login login,Conexao conexao) {
        return dao.alterar(login,conexao);
    }

    public boolean apagar(Login login,Conexao conexao) {
        return dao.apagar(login,conexao);
    }

//    public Login buscarPorLogin(String login) {
//        return dao.buscarPorLoginExato(login);
//    }
//
//    public Login buscarPorVoluntarioId(int voluntarioId) {
//        return dao.buscarPorVoluntarioId(voluntarioId);
//    }



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

    public LoginDAO getDao() { return dao; }

    public void setDao(LoginDAO dao) {
        this.dao = dao;
    }
}
