package DOARC.mvc.dao;

import java.util.List;

public interface IDAO<T> {

    public T gravar(T entidade);
    public T alterar(T entidade);
    public boolean apagar(T entidade);
    public T get(int id);
    public List<T> get(String filtro);
}