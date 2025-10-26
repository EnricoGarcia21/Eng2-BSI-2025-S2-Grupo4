package DOARC.mvc.model;

public class Voluntario {

    private int volId;
    private String volNome;
    private String volBairro;
    private String volNumero;
    private String volRua;
    private String volTelefone;
    private String volCidade;
    private String volCep;
    private String volUf;
    private String volEmail;
    private String volCpf;
    private String volDataNasc;
    private String volSexo;


    public Voluntario() {

    }

    public Voluntario(int volId, String volNome, String volBairro, String volNumero, String volRua, String volTelefone, String volCidade, String volCep, String volUf, String volEmail, String volCpf, String volDataNasc, String volSexo) {
        this.volId = volId;
        this.volNome = volNome;
        this.volBairro = volBairro;
        this.volNumero = volNumero;
        this.volRua = volRua;
        this.volTelefone = volTelefone;
        this.volCidade = volCidade;
        this.volCep = volCep;
        this.volUf = volUf;
        this.volEmail = volEmail;
        this.volCpf = volCpf;
        this.volDataNasc = volDataNasc;
        this.volSexo = volSexo;
    }

    public int getVolId() {
        return volId;
    }

    public void setVolId(int volId) {
        this.volId = volId;
    }

    public String getVolNome() {
        return volNome;
    }

    public void setVolNome(String volNome) {
        this.volNome = volNome;
    }

    public String getVolBairro() {
        return volBairro;
    }

    public void setVolBairro(String volBairro) {
        this.volBairro = volBairro;
    }

    public String getVolNumero() {
        return volNumero;
    }

    public void setVolNumero(String volNumero) {
        this.volNumero = volNumero;
    }

    public String getVolRua() {
        return volRua;
    }

    public void setVolRua(String volRua) {
        this.volRua = volRua;
    }

    public String getVolTelefone() {
        return volTelefone;
    }

    public void setVolTelefone(String volTelefone) {
        this.volTelefone = volTelefone;
    }

    public String getVolCidade() {
        return volCidade;
    }

    public void setVolCidade(String volCidade) {
        this.volCidade = volCidade;
    }

    public String getVolCep() {
        return volCep;
    }

    public void setVolCep(String volCep) {
        this.volCep = volCep;
    }

    public String getVolUf() {
        return volUf;
    }

    public void setVolUf(String volUf) {
        this.volUf = volUf;
    }

    public String getVolEmail() {
        return volEmail;
    }

    public void setVolEmail(String volEmail) {
        this.volEmail = volEmail;
    }

    public String getVolCpf() {
        return volCpf;
    }

    public void setVolCpf(String volCpf) {
        this.volCpf = volCpf;
    }

    public String getVolDataNasc() {
        return volDataNasc;
    }

    public void setVolDataNasc(String volDataNasc) {
        this.volDataNasc = volDataNasc;
    }

    public String getVolSexo() {
        return volSexo;
    }

    public void setVolSexo(String volSexo) {
        this.volSexo = volSexo;
    }
}
