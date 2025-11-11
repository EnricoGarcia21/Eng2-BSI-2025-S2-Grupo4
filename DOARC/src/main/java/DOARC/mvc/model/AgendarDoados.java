package DOARC.mvc.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendarDoados {
    private int agId;
    private LocalDate agData;
    private LocalTime agHora;
    private String agObs;
    private int doaId;
    private int voluntarioId;
    private int donatarioId;

    public AgendarDoados() {}

    public LocalDate getAgData() { return agData; }
    public void setAgData(LocalDate agData) { this.agData = agData; }

    public LocalTime getAgHora() { return agHora; }
    public void setAgHora(LocalTime agHora) { this.agHora = agHora; }

    public int getAgId() { return agId; }
    public void setAgId(int agId) { this.agId = agId; }
    public String getAgObs() { return agObs; }
    public void setAgObs(String agObs) { this.agObs = agObs; }
    public int getDoaId() { return doaId; }
    public void setDoaId(int doaId) { this.doaId = doaId; }
    public int getVoluntarioId() { return voluntarioId; }
    public void setVoluntarioId(int voluntarioId) { this.voluntarioId = voluntarioId; }
    public int getDonatarioId() { return donatarioId; }
    public void setDonatarioId(int donatarioId) { this.donatarioId = donatarioId; }
}