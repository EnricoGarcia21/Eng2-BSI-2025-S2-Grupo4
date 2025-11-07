package DOARC.mvc.dao;

import DOARC.mvc.util.SingletonDB;
import java.util.List;

public interface IDAO<T> {
    T gravar(T entidade, SingletonDB conexao);
    T alterar(T entidade, SingletonDB conexao);
    boolean apagar(T entidade, SingletonDB conexao);
    T get(int id, SingletonDB conexao);
    List<T> get(String filtro, SingletonDB conexao);
}