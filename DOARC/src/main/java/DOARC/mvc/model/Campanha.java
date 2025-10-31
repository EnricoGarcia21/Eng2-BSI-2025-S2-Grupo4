package DOARC.mvc.model;

public class Campanha {

    private int cam_id;
    private String cam_data_ini;
    private String cam_data_fim;
    private int voluntario_vol_id;
    private String cam_desc;
    private Double cam_meta_arrecadacao;
    private Double cam_valor_arrecadado;

    public Campanha() {
    }

    public Campanha(int cam_id, String cam_data_ini, String cam_data_fim, int voluntario_vol_id,
                    String cam_desc, Double cam_meta_arrecadacao, Double cam_valor_arrecadado) {
        this.cam_id = cam_id;
        this.cam_data_ini = cam_data_ini;
        this.cam_data_fim = cam_data_fim;
        this.voluntario_vol_id = voluntario_vol_id;
        this.cam_desc = cam_desc;
        this.cam_meta_arrecadacao = cam_meta_arrecadacao;
        this.cam_valor_arrecadado = cam_valor_arrecadado;
    }

    public int getCam_id() {
        return cam_id;
    }

    public void setCam_id(int cam_id) {
        this.cam_id = cam_id;
    }

    public String getCam_data_ini() {
        return cam_data_ini;
    }

    public void setCam_data_ini(String cam_data_ini) {
        this.cam_data_ini = cam_data_ini;
    }

    public String getCam_data_fim() {
        return cam_data_fim;
    }

    public void setCam_data_fim(String cam_data_fim) {
        this.cam_data_fim = cam_data_fim;
    }

    public int getVoluntario_vol_id() {
        return voluntario_vol_id;
    }

    public void setVoluntario_vol_id(int voluntario_vol_id) {
        this.voluntario_vol_id = voluntario_vol_id;
    }

    public String getCam_desc() {
        return cam_desc;
    }

    public void setCam_desc(String cam_desc) {
        this.cam_desc = cam_desc;
    }

    public Double getCam_meta_arrecadacao() {
        return cam_meta_arrecadacao;
    }

    public void setCam_meta_arrecadacao(Double cam_meta_arrecadacao) {
        this.cam_meta_arrecadacao = cam_meta_arrecadacao;
    }

    public Double getCam_valor_arrecadado() {
        return cam_valor_arrecadado;
    }

    public void setCam_valor_arrecadado(Double cam_valor_arrecadado) {
        this.cam_valor_arrecadado = cam_valor_arrecadado;
    }
}
