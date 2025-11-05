package DOARC.mvc.model;

import DOARC.mvc.dao.DonatarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Donatario {

    private int donId;
    private String donNome;
    private String donDataNasc;
    private String donRua;
    private String donBairro;
    private String donCidade;
    private String donTelefone;
    private String donCep;
    private String donUf;
    private String donEmail;
    private String donSexo;

    @Autowired // Model Instancia (recebe) o DAO
    private DonatarioDAO dao;

    // --- CONSTRUTORES ---
    public Donatario() {
        // Construtor vazio
    }

    public Donatario(String donNome, String donDataNasc, String donRua, String donBairro, String donCidade,
                     String donTelefone, String donCep, String donUf, String donEmail, String donSexo) {
        this.donNome = donNome;
        this.donDataNasc = donDataNasc;
        this.donRua = donRua;
        this.donBairro = donBairro;
        this.donCidade = donCidade;
        this.donTelefone = donTelefone;
        this.donCep = donCep;
        this.donUf = donUf;
        this.donEmail = donEmail;
        this.donSexo = donSexo;
    }

    // --- MÉTODOS DE DELEGAÇÃO PARA O DAO ---
    public List<Donatario> consultar(String filtro) {
        return dao.get(filtro);
    }

    public Donatario consultar(int id) {
        return dao.get(id);
    }

    public Donatario gravar(Donatario donatario) {
        return dao.gravar(donatario);
    }

    public Donatario alterar(Donatario donatario) {
        return dao.alterar(donatario);
    }

    public boolean apagar(Donatario donatario) {
        return dao.apagar(donatario);
    }

    // --- GETTERS E SETTERS ---
    public int getDonId() { return donId; }
    public void setDonId(int donId) { this.donId = donId; }
    public String getDonNome() { return donNome; }
    public void setDonNome(String donNome) { this.donNome = donNome; }
    public String getDonDataNasc() { return donDataNasc; }
    public void setDonDataNasc(String donDataNasc) { this.donDataNasc = donDataNasc; }
    public String getDonRua() { return donRua; }
    public void setDonRua(String donRua) { this.donRua = donRua; }
    public String getDonBairro() { return donBairro; }
    public void setDonBairro(String donBairro) { this.donBairro = donBairro; }
    public String getDonCidade() { return donCidade; }
    public void setDonCidade(String donCidade) { this.donCidade = donCidade; }
    public String getDonTelefone() { return donTelefone; }
    public void setDonTelefone(String donTelefone) { this.donTelefone = donTelefone; }
    public String getDonCep() { return donCep; }
    public void setDonCep(String donCep) { this.donCep = donCep; }
    public String getDonUf() { return donUf; }
    public void setDonUf(String donUf) { this.donUf = donUf; }
    public String getDonEmail() { return donEmail; }
    public void setDonEmail(String donEmail) { this.donEmail = donEmail; }
    public String getDonSexo() { return donSexo; }
    public void setDonSexo(String donSexo) { this.donSexo = donSexo; }
}