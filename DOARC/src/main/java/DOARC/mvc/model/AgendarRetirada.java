package DOARC.mvc.model;

public class AgendarRetirada {
    private int agendaId;
    private String dataRetiro;
    private String horaRetiro;
    private String obsRetiro;
    private int volId;
    private int doaId;

    public AgendarRetirada() {}

    public int getAgendaId() { return agendaId; }
    public void setAgendaId(int agendaId) { this.agendaId = agendaId; }
    public String getDataRetiro() { return dataRetiro; }
    public void setDataRetiro(String dataRetiro) { this.dataRetiro = dataRetiro; }
    public String getHoraRetiro() { return horaRetiro; }
    public void setHoraRetiro(String horaRetiro) { this.horaRetiro = horaRetiro; }
    public String getObsRetiro() { return obsRetiro; }
    public void setObsRetiro(String obsRetiro) { this.obsRetiro = obsRetiro; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public int getDoaId() { return doaId; }
    public void setDoaId(int doaId) { this.doaId = doaId; }
}