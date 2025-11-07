package DOARC.mvc.model;

import DOARC.mvc.dao.VoluntarioDAO;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Voluntario {
    @Autowired
    private VoluntarioDAO dao;

    private int volId;
    private String volNome;
    private String volTelefone;
    private String volEmail;
    private String volCidade;
    private String volBairro;

    // Construtores
    public Voluntario() {}

    public Voluntario(int volId, String volNome, String volTelefone, String volEmail, String volCidade, String volBairro) {
        this.volId = volId;
        this.volNome = volNome;
        this.volTelefone = volTelefone;
        this.volEmail = volEmail;
        this.volCidade = volCidade;
        this.volBairro = volBairro;
    }

    // Getters e Setters
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

    public String getVolTelefone() {
        return volTelefone;
    }

    public void setVolTelefone(String volTelefone) {
        this.volTelefone = volTelefone;
    }

    public String getVolEmail() {
        return volEmail;
    }

    public void setVolEmail(String volEmail) {
        this.volEmail = volEmail;
    }

    public String getVolCidade() {
        return volCidade;
    }

    public void setVolCidade(String volCidade) {
        this.volCidade = volCidade;
    }

    public String getVolBairro() {
        return volBairro;
    }

    public void setVolBairro(String volBairro) {
        this.volBairro = volBairro;
    }

    // Métodos que chamam o DAO - APENAS GETs
    public Voluntario get(int id, SingletonDB conexao) {
        return dao.get(id, conexao);
    }

    public List<Voluntario> get(String filtro, SingletonDB conexao) {
        return dao.get(filtro, conexao);
    }

    public List<Voluntario> getAll(SingletonDB conexao) {
        return dao.getAll(conexao);
    }

    public List<Voluntario> getPorCidade(String cidade, SingletonDB conexao) {
        return dao.getPorCidade(cidade, conexao);
    }
}