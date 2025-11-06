package DOARC.mvc.model;

import DOARC.mvc.dao.VoluntarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Voluntario {

    private int vol_id;
    private String vol_nome;
    private String vol_datanasc;
    private String vol_rua;
    private String vol_bairro;
    private String vol_cidade;
    private String vol_telefone;
    private String vol_cep;
    private String vol_uf;
    private String vol_email;
    private String vol_sexo;
    private String vol_numero;
    private String vol_cpf;

    @Autowired // Model Instancia (recebe) o DAO
    private VoluntarioDAO dao;

    // --- CONSTRUTORES ---
    public Voluntario() {
        // Construtor vazio
    }

    public Voluntario(String vol_nome, String vol_datanasc, String vol_rua, String vol_bairro, String vol_cidade,
                      String vol_telefone, String vol_cep, String vol_uf, String vol_email, String vol_sexo,
                      String vol_numero, String vol_cpf) {
        this.vol_nome = vol_nome;
        this.vol_datanasc = vol_datanasc;
        this.vol_rua = vol_rua;
        this.vol_bairro = vol_bairro;
        this.vol_cidade = vol_cidade;
        this.vol_telefone = vol_telefone;
        this.vol_cep = vol_cep;
        this.vol_uf = vol_uf;
        this.vol_email = vol_email;
        this.vol_sexo = vol_sexo;
        this.vol_numero = vol_numero;
        this.vol_cpf = vol_cpf;
    }

    // --- MÉTODOS DE DELEGAÇÃO PARA O DAO ---
    public List<Voluntario> consultar(String filtro) {
        return dao.get(filtro);
    }

    public Voluntario consultar(int id) {
        return dao.get(id);
    }

    public Voluntario gravar() {
        return dao.gravar(this);
    }

    public Voluntario alterar() {
        return dao.alterar(this);
    }

    public boolean apagar() {
        return dao.apagar(this);
    }

    // --- GETTERS E SETTERS ---
    public int getVol_id() { return vol_id; }
    public void setVol_id(int vol_id) { this.vol_id = vol_id; }

    public String getVol_nome() { return vol_nome; }
    public void setVol_nome(String vol_nome) { this.vol_nome = vol_nome; }

    public String getVol_datanasc() { return vol_datanasc; }
    public void setVol_datanasc(String vol_datanasc) { this.vol_datanasc = vol_datanasc; }

    public String getVol_rua() { return vol_rua; }
    public void setVol_rua(String vol_rua) { this.vol_rua = vol_rua; }

    public String getVol_bairro() { return vol_bairro; }
    public void setVol_bairro(String vol_bairro) { this.vol_bairro = vol_bairro; }

    public String getVol_cidade() { return vol_cidade; }
    public void setVol_cidade(String vol_cidade) { this.vol_cidade = vol_cidade; }

    public String getVol_telefone() { return vol_telefone; }
    public void setVol_telefone(String vol_telefone) { this.vol_telefone = vol_telefone; }

    public String getVol_cep() { return vol_cep; }
    public void setVol_cep(String vol_cep) { this.vol_cep = vol_cep; }

    public String getVol_uf() { return vol_uf; }
    public void setVol_uf(String vol_uf) { this.vol_uf = vol_uf; }

    public String getVol_email() { return vol_email; }
    public void setVol_email(String vol_email) { this.vol_email = vol_email; }

    public String getVol_sexo() { return vol_sexo; }
    public void setVol_sexo(String vol_sexo) { this.vol_sexo = vol_sexo; }

    public String getVol_numero() { return vol_numero; }
    public void setVol_numero(String vol_numero) { this.vol_numero = vol_numero; }

    public String getVol_cpf() { return vol_cpf; }
    public void setVol_cpf(String vol_cpf) { this.vol_cpf = vol_cpf; }
}