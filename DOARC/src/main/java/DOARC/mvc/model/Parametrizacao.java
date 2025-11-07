package DOARC.mvc.model;

import DOARC.mvc.dao.ParametrizacaoDAO;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.tags.Param;


import java.util.List;
@Component
public class Parametrizacao {
    @Autowired
    private ParametrizacaoDAO dao;

    private int id;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String rua;
    private String cidade;
    private String bairro;
    private int numero;
    private String uf;
    private String cep;
    private String email;
    private String site;
    private String telefone;

    public Parametrizacao(String cnpj, String razaoSocial, String nomeFantasia, String rua, String cidade, String bairro, int numero, String uf, String cep, String email, String site, String telefone) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.rua = rua;
        this.cidade = cidade;
        this.bairro = bairro;
        this.numero = numero;
        this.uf = uf;
        this.cep = cep;
        this.email = email;
        this.site = site;
        this.telefone = telefone;
    }

    public Parametrizacao(int id, String cnpj, String razaoSocial, String nomeFantasia, String rua, String cidade, String bairro, int numero, String uf, String cep, String email, String site, String telefone) {
        this.id = id;
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
        this.rua = rua;
        this.cidade = cidade;
        this.bairro = bairro;
        this.numero = numero;
        this.uf = uf;
        this.cep = cep;
        this.email = email;
        this.site = site;
        this.telefone = telefone;
    }

    public Parametrizacao() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Parametrizacao gravar(Parametrizacao param, SingletonDB conexao) {
        return dao.gravar(param, conexao);
    }

    public Parametrizacao alterar(Parametrizacao param, SingletonDB conexao) {
        return dao.alterar(param, conexao);
    }

    public Parametrizacao get(int id, SingletonDB conexao) {
        return dao.get(id, conexao);
    }

    public List<Parametrizacao> get(String filtro, SingletonDB conexao) {
        return dao.get(filtro, conexao);
    }
}
