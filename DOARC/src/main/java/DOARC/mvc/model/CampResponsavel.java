package DOARC.mvc.model;

import DOARC.mvc.dao.CampRespDAO;
import DOARC.mvc.util.Conexao;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CampResponsavel {
    private static final CampRespDAO dao = new CampRespDAO();

    @JsonProperty("cam_id") private int cam_id;
    @JsonProperty("voluntario_vol_id") private int voluntario_vol_id;
    @JsonProperty("DATA_INICIO") private String DATA_INICIO;
    @JsonProperty("DATA_FIM") private String DATA_FIM;
    @JsonProperty("Obs_texto") private String Obs_texto;

    public CampResponsavel() {}

    public CampResponsavel gravar(Conexao conexao) { return dao.gravar(this, conexao); }
    public CampResponsavel alterar(Conexao conexao) { return dao.alterar(this, conexao); }
    public boolean apagar(Conexao conexao) { return dao.apagar(this, conexao); }
    public static List<CampResponsavel> get(String filtro, Conexao conexao) { return dao.get(filtro, conexao); }
    public static List<CampResponsavel> getPorChave(int cId, int vId, Conexao conexao) { return dao.getPorChave(cId, vId, conexao); }
    public static List<CampResponsavel> getPorVoluntario(int vId, Conexao conexao) { return dao.getCampanhasPorVoluntario(vId, conexao); }

    // Getters/Setters
    public int getCam_id() { return cam_id; }
    public void setCam_id(int cam_id) { this.cam_id = cam_id; }
    public int getVoluntario_vol_id() { return voluntario_vol_id; }
    public void setVoluntario_vol_id(int voluntario_vol_id) { this.voluntario_vol_id = voluntario_vol_id; }
    public String getDATA_INICIO() { return DATA_INICIO; }
    public void setDATA_INICIO(String DATA_INICIO) { this.DATA_INICIO = DATA_INICIO; }
    public String getDATA_FIM() { return DATA_FIM; }
    public void setDATA_FIM(String DATA_FIM) { this.DATA_FIM = DATA_FIM; }
    public String getObs_texto() { return Obs_texto; }
    public void setObs_texto(String obs_texto) { Obs_texto = obs_texto; }
}