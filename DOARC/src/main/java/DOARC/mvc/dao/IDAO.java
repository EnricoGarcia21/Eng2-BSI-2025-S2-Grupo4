package DOARC.mvc.dao;

import DOARC.mvc.util.Conexao;
import java.util.List;

public interface IDAO<T>{

    public T gravar(T entidade, Conexao conn);

    public T alterar(T entidade, Conexao conn);

    public boolean apagar(T entidade, Conexao conn);

    public T get(int id, Conexao conn);

    public List<T> get(String filtro, Conexao conn);
}