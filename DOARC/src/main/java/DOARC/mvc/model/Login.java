package DOARC.mvc.model;

public class Usuario {

    private String email;
    private String senha;
    private int voluntarioId;
    private String tipoUsuario;
    private boolean ativo;
    
    // Construtores
    public Usuario() {}
    
    public Usuario(String email, String senha, int voluntarioId, String tipoUsuario, boolean ativo) {
        
        this.email = email;
        this.senha = senha;
        this.voluntarioId = voluntarioId;
        this.tipoUsuario = tipoUsuario;
        this.ativo = ativo;
    }
    
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public int getVoluntarioId() { return voluntarioId; }
    public void setVoluntarioId(int voluntarioId) { this.voluntarioId = voluntarioId; }
    
    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}