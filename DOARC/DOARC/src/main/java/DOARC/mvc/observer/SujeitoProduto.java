package DOARC.mvc.observer;

public interface SujeitoProduto {
    void anexar(ObservadorDonatario observador);

    void desanexar(ObservadorDonatario observador);

    void notificarObservadores();
}