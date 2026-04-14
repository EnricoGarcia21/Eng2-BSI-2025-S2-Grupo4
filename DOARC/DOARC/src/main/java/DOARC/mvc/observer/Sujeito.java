package DOARC.mvc.observer;

public interface Sujeito {
    void anexar(Observador observador);

    void desanexar(Observador observador);

    void notificarObservadores();
}