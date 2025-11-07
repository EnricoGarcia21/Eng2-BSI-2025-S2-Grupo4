package DOARC.mvc.dao;

import java.util.List;

public interface IDAO<T>{
    // DAO gerencia a Connection internamente via Singleton
    public T gravar(T entidade);

    public T alterar(T entidade);

    public boolean apagar(T entidade);

    public T get(int id);

    // Método get com filtro (substitui listar())
    public List<T> get(String filtro);
}