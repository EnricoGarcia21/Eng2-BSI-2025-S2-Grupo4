package DOARC.mvc.model;

import DOARC.mvc.dao.CampRespDAO;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampResponsavel {

    @JsonProperty("cam_id")
    private int cam_id;

    @JsonProperty("voluntario_vol_id")
    private int voluntario_vol_id;

    @JsonProperty("DATA_INICIO")
    private String DATA_INICIO;

    @JsonProperty("DATA_FIM")
    private String DATA_FIM;

    @JsonProperty("Obs_texto")
    private String Obs_texto;

    public CampResponsavel() {}

    @Autowired
    private CampRespDAO dao;

    // Métodos de delegação (sem Conexao)
    public List<CampResponsavel> consultar(String filtro) {
        return dao.get(filtro);
    }

    public List<CampResponsavel> consultarPorChaveComposta(int camId, int voluntarioId) {
        return dao.getByCompositeKey(camId, voluntarioId);
    }

    public CampResponsavel gravar(CampResponsavel c) {
        return dao.gravar(c);
    }

    public CampResponsavel alterar(CampResponsavel c) {
        return dao.alterar(c);
    }

    public boolean apagar(CampResponsavel c) {
        return dao.apagar(c);
    }

    public List<CampResponsavel> getCampanhasPorVoluntario(int voluntarioId) {
        return dao.getCampanhasPorVoluntario(voluntarioId);
    }

    public CampResponsavel(int cam_id, int voluntario_vol_id, String DATA_INICIO, String DATA_FIM, String obs_texto) {
        this.cam_id = cam_id;
        this.voluntario_vol_id = voluntario_vol_id;
        this.DATA_INICIO = DATA_INICIO;
        this.DATA_FIM = DATA_FIM;
        Obs_texto = obs_texto;
    }

    // Getters e Setters
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

    @Override
    public String toString() {
        return "CampResponsavel{" +
                "cam_id=" + cam_id +
                ", voluntario_vol_id=" + voluntario_vol_id +
                ", DATA_INICIO='" + DATA_INICIO + '\'' +
                ", DATA_FIM='" + DATA_FIM + '\'' +
                ", Obs_texto='" + Obs_texto + '\'' +
                '}';
    }
}