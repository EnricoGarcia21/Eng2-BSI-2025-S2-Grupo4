package DOARC.mvc.model;

public class Doador {

    private int doaId;
    private String doaNome;
    private String doaTelefone;
    private String doaEmail;
    private String doaCep;
    private String doaUf;
    private String doaCidade;
    private String doaBairro;
    private String doaRua;
    private String doaCpf;
    private String doaDataNasc;
    private String doaSexo;
    private String doaSite;


    public Doador() {}

    public Doador(String doaNome, String doaTelefone, String doaEmail, String doaCep, String doaUf, String doaCidade,
                  String doaBairro, String doaRua, String doaCpf, String doaDataNasc, String doaSexo, String doaSite) {
        this.doaNome = doaNome;
        this.doaTelefone = doaTelefone;
        this.doaEmail = doaEmail;
        this.doaCep = doaCep;
        this.doaUf = doaUf;
        this.doaCidade = doaCidade;
        this.doaBairro = doaBairro;
        this.doaRua = doaRua;
        this.doaCpf = doaCpf;
        this.doaDataNasc = doaDataNasc;
        this.doaSexo = doaSexo;
        this.doaSite = doaSite;
    }

    public int getDoaId() { return doaId; }
    public void setDoaId(int doaId) { this.doaId = doaId; }
    public String getDoaNome() { return doaNome; }
    public void setDoaNome(String doaNome) { this.doaNome = doaNome; }
    public String getDoaTelefone() { return doaTelefone; }
    public void setDoaTelefone(String doaTelefone) { this.doaTelefone = doaTelefone; }
    public String getDoaEmail() { return doaEmail; }
    public void setDoaEmail(String doaEmail) { this.doaEmail = doaEmail; }
    public String getDoaCep() { return doaCep; }
    public void setDoaCep(String doaCep) { this.doaCep = doaCep; }
    public String getDoaUf() { return doaUf; }
    public void setDoaUf(String doaUf) { this.doaUf = doaUf; }
    public String getDoaCidade() { return doaCidade; }
    public void setDoaCidade(String doaCidade) { this.doaCidade = doaCidade; }
    public String getDoaBairro() { return doaBairro; }
    public void setDoaBairro(String doaBairro) { this.doaBairro = doaBairro; }
    public String getDoaRua() { return doaRua; }
    public void setDoaRua(String doaRua) { this.doaRua = doaRua; }
    public String getDoaCpf() { return doaCpf; }
    public void setDoaCpf(String doaCpf) { this.doaCpf = doaCpf; }
    public String getDoaDataNasc() { return doaDataNasc; }
    public void setDoaDataNasc(String doaDataNasc) { this.doaDataNasc = doaDataNasc; }
    public String getDoaSexo() { return doaSexo; }
    public void setDoaSexo(String doaSexo) { this.doaSexo = doaSexo; }
    public String getDoaSite() { return doaSite; }
    public void setDoaSite(String doaSite) { this.doaSite = doaSite; }
}