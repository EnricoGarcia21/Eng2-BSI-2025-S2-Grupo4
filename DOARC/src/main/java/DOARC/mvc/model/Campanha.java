package DOARC.mvc.model;

import DOARC.mvc.dao.CampanhaDAO;
import DOARC.mvc.util.Conexao;
import java.util.List;

public class Campanha {
    private static final CampanhaDAO dao = new CampanhaDAO();

    private int cam_id;
    private String cam_data_ini;
    private String cam_data_fim;
    private int voluntario_vol_id;
    private String cam_desc;
    private Double cam_meta_arrecadacao;
    private Double cam_valor_arrecadado;

    public Campanha() {}

    public Campanha gravar(Conexao conexao) { return dao.gravar(this, conexao); }
    public Campanha alterar(Conexao conexao) { return dao.alterar(this, conexao); }
    public boolean apagar(Conexao conexao) { return dao.apagar(this, conexao); }
    public static Campanha get(int id, Conexao conexao) { return dao.get(id, conexao); }
    public static List<Campanha> get(String filtro, Conexao conexao) { return dao.get(filtro, conexao); }
    public static List<Campanha> getPorVoluntario(int volId, Conexao conexao) { return dao.getCampanhasPorVoluntario(volId, conexao); }


    public int getCam_id() { return cam_id; }
    public void setCam_id(int cam_id) { this.cam_id = cam_id; }
    public String getCam_data_ini() { return cam_data_ini; }
    public void setCam_data_ini(String s) { this.cam_data_ini = s; }
    public String getCam_data_fim() { return cam_data_fim; }
    public void setCam_data_fim(String s) { this.cam_data_fim = s; }
    public int getVoluntario_vol_id() { return voluntario_vol_id; }
    public void setVoluntario_vol_id(int i) { this.voluntario_vol_id = i; }
    public String getCam_desc() { return cam_desc; }
    public void setCam_desc(String s) { this.cam_desc = s; }
    public Double getCam_meta_arrecadacao() { return cam_meta_arrecadacao; }
    public void setCam_meta_arrecadacao(Double d) { this.cam_meta_arrecadacao = d; }
    public Double getCam_valor_arrecadado() { return cam_valor_arrecadado; }
    public void setCam_valor_arrecadado(Double d) { this.cam_valor_arrecadado = d; }
}