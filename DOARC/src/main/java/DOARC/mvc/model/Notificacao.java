package DOARC.mvc.model;

public class Notificacao {
    private int notId;
    private String notTexto;
    private String notData;
    private String notHora;
    private int volId;

    public Notificacao() {}

    public int getNotId() { return notId; }
    public void setNotId(int notId) { this.notId = notId; }
    public String getNotTexto() { return notTexto; }
    public void setNotTexto(String notTexto) { this.notTexto = notTexto; }
    public String getNotData() { return notData; }
    public void setNotData(String notData) { this.notData = notData; }
    public String getNotHora() { return notHora; }
    public void setNotHora(String notHora) { this.notHora = notHora; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
}