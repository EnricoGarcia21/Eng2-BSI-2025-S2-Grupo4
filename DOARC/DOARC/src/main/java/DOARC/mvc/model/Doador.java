package DOARC.mvc.model;

import DOARC.mvc.observer.ObservadorDonatario;

public class Doador implements ObservadorDonatario {
    private int id;
    private String nome;
    private String email;

    public Doador() {}

    public Doador(String nome) {
        this.nome = nome;
    }

    @Override
    public void update(String mensagem) {
        System.out.println("\n========================================");
        System.out.println("SISTEMA DOARC - ALERTA PARA DOADOR");
        System.out.println("DOADOR: " + this.nome.toUpperCase());
        System.out.println("MENSAGEM: " + mensagem);
        System.out.println("========================================\n");
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}