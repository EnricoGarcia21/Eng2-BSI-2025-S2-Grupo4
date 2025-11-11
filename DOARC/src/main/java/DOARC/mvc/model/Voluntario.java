package DOARC.mvc.model;

public class Voluntario {
    private int volId;
    private String volNome;
    private String volTelefone;
    private String volEmail;
    private String volCidade;
    private String volBarro;

    public Voluntario() {}

    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public String getVolNome() { return volNome; }
    public void setVolNome(String volNome) { this.volNome = volNome; }
    public String getVolTelefone() { return volTelefone; }
    public void setVolTelefone(String volTelefone) { this.volTelefone = volTelefone; }
    public String getVolEmail() { return volEmail; }
    public void setVolEmail(String volEmail) { this.volEmail = volEmail; }
    public String getVolCidade() { return volCidade; }
    public void setVolCidade(String volCidade) { this.volCidade = volCidade; }
    public String getVolBarro() { return volBarro; }
    public void setVolBarro(String volBarro) { this.volBarro = volBarro; }
}