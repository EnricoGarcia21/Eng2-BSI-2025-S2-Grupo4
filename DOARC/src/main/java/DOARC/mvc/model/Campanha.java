package DOARC.mvc.model;

import DOARC.mvc.dao.CampanhaDAO;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Campanha {

    private int cam_id;
    private String cam_data_ini;
    private String cam_data_fim;
    private int voluntario_vol_id;
    private String cam_desc;
    private Double cam_meta_arrecadacao;
    private Double cam_valor_arrecadado;

    // ✅ CORREÇÃO: Injeção de Dependência
    @Autowired
    private CampanhaDAO dao;

    // --- CONSTRUTORES ---
    public Campanha() {}

    public Campanha(String cam_data_ini, String cam_data_fim, int voluntario_vol_id,
                    String cam_desc, Double cam_meta_arrecadacao, Double cam_valor_arrecadado) {

        this.cam_data_ini = cam_data_ini;
        this.cam_data_fim = cam_data_fim;
        this.voluntario_vol_id = voluntario_vol_id;
        this.cam_desc = cam_desc;
        this.cam_meta_arrecadacao = cam_meta_arrecadacao;
        this.cam_valor_arrecadado = cam_valor_arrecadado;
    }

    // =============================
    // ✅ MÉTODOS DE DELEGAÇÃO (Todos exigem Conexao)
    // =============================
    private Conexao getConexao() {
        return SingletonDB.conectar();
    }
    public List<Campanha> consultar(String filtro, Conexao conexao) {
        return dao.get(filtro, conexao);
    }

    public Campanha consultar(int id, Conexao conexao) {
        return dao.get(id, conexao);
    }

    public Campanha gravar(Campanha c, Conexao conexao) {
        return dao.gravar(c, conexao);
    }

    public Campanha alterar(Campanha c, Conexao conexao) {
        return dao.alterar(c, conexao);
    }

    public boolean apagar(Campanha c, Conexao conexao) {
        return dao.apagar(c, conexao);
    }

    public List<Campanha> getCampanhasPorVoluntario(int voluntarioId, Conexao conexao) {
        return dao.getCampanhasPorVoluntario(voluntarioId, conexao);
    }


    // =============================
    // ✅ GETTERS / SETTERS
    // =============================

    public int getCam_id() { return cam_id; }
    public void setCam_id(int cam_id) { this.cam_id = cam_id; }

    public String getCam_data_ini() { return cam_data_ini; }
    public void setCam_data_ini(String cam_data_ini) { this.cam_data_ini = cam_data_ini; }

    public String getCam_data_fim() { return cam_data_fim; }
    public void setCam_data_fim(String cam_data_fim) { this.cam_data_fim = cam_data_fim; }

    public int getVoluntario_vol_id() { return voluntario_vol_id; }
    public void setVoluntario_vol_id(int voluntario_vol_id) { this.voluntario_vol_id = voluntario_vol_id; }

    public String getCam_desc() { return cam_desc; }
    public void setCam_desc(String cam_desc) { this.cam_desc = cam_desc; }

    public Double getCam_meta_arrecadacao() { return cam_meta_arrecadacao; }
    public void setCam_meta_arrecadacao(Double cam_meta_arrecadacao) { this.cam_meta_arrecadacao = cam_meta_arrecadacao; }

    public Double getCam_valor_arrecadado() { return cam_valor_arrecadado; }
    public void setCam_valor_arrecadado(Double cam_valor_arrecadado) { this.cam_valor_arrecadado = cam_valor_arrecadado; }
}