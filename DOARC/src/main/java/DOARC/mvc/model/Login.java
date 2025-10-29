package DOARC.mvc.model;

public class Login {

    private int voluntarioId;
    private String login;
    private String senha;

    private String nive_acesso;
    private char status;

    public Login() {
    }

    public Login(int voluntarioId, String login, String senha, String nive_acesso, char status) {
        this.voluntarioId = voluntarioId;
        this.login = login;
        this.senha = senha;
        this.nive_acesso = nive_acesso;
        this.status = status;
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

    public String getNive_acesso() {
        return nive_acesso;
    }

    public void setNive_acesso(String nive_acesso) {
        this.nive_acesso = nive_acesso;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }
}
