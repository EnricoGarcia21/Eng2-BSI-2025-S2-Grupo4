package DOARC.mvc.model;

import DOARC.mvc.dao.VoluntarioDAO;
import DOARC.mvc.util.Conexao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    private VoluntarioDAO dao;

    public Voluntario() {}

    // ✅ Construtor completo para cadastro
    public Voluntario(String nome, String bairro, String numero, String rua,
                      String telefone, String cidade, String cep, String uf,
                      String email, String cpf, String dataNasc, String sexo)
    {
        this.vol_nome = nome;
        this.vol_bairro = bairro;
        this.vol_numero = numero;
        this.vol_rua = rua;
        this.vol_telefone = telefone;
        this.vol_cidade = cidade;
        this.vol_cep = cep;
        this.vol_uf = uf;
        this.vol_email = email;
        this.vol_cpf = cpf;
        this.vol_datanasc = dataNasc;
        this.vol_sexo = sexo;
    }

    // ✅ Métodos da Model
    public Voluntario buscarPorCpf(String cpf, Conexao conexao) {
        return dao.buscarPorCpf(cpf, conexao);
    }
    public Voluntario gravar(Voluntario v, Conexao conexao) {
        return dao.gravar(v, conexao);
    }

    public Voluntario alterar(Voluntario v, Conexao conexao) {
        return dao.alterar(v, conexao);
    }

    public boolean apagar(Voluntario v, Conexao conexao) {
        return dao.apagar(v, conexao);
    }

    public Voluntario consultar(int id, Conexao conexao) {
        return dao.get(id, conexao);
    }

    public java.util.List<Voluntario> consultar(String filtro, Conexao conexao) {
        return dao.get(filtro, conexao);
    }




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
