package DOARC.mvc.model;

import DOARC.mvc.dao.DoadorDAO;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Doador {
    @Autowired
    private DoadorDAO dao;

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

    // Construtores
    public Doador() {}

    public Doador(int doaId, String doaNome, String doaTelefone, String doaEmail,
                  String doaCep, String doaUf, String doaCidade, String doaBairro,
                  String doaRua, String doaCpf, String doaDataNasc, String doaSexo,
                  String doaSite) {
        this.doaId = doaId;
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

    // Getters e Setters
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

    // Métodos que delegam para o DAO
    public Doador gravar(Doador doador, SingletonDB conexao) {
        return dao.gravar(doador, conexao);
    }

    public Doador alterar(Doador doador, SingletonDB conexao) {
        return dao.alterar(doador, conexao);
    }

    public boolean apagar(Doador doador, SingletonDB conexao) {
        return dao.apagar(doador, conexao);
    }

    public Doador get(int id, SingletonDB conexao) {
        return dao.get(id, conexao);
    }

    public List<Doador> get(String filtro, SingletonDB conexao) {
        return dao.get(filtro, conexao);
    }

    // Métodos específicos
    public List<Doador> getAll(SingletonDB conexao) {
        return dao.getAll(conexao);
    }

    public List<Doador> getPorCidade(String cidade, SingletonDB conexao) {
        return dao.getPorCidade(cidade, conexao);
    }

    public List<Doador> getPorBairro(String bairro, SingletonDB conexao) {
        return dao.getPorBairro(bairro, conexao);
    }
}