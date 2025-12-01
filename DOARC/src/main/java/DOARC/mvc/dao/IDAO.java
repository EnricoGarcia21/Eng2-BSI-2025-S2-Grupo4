package DOARC.mvc.dao;

import java.util.List;

public interface IDAO<T> {

    T gravar(T entidade);
    T alterar(T entidade);
    boolean apagar(T entidade);
    T get(int id);
    List<T> get(String filtro);
}