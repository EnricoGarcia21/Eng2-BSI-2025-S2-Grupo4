package DOARC.mvc.dao;

import DOARC.mvc.util.Conexao;
import java.util.List;

public interface IDAO<T> {

    T gravar(T entidade, Conexao conexao);
    T alterar(T entidade, Conexao conexao);
    boolean apagar(T entidade, Conexao conexao);
    T get(int id, Conexao conexao);
    List<T> get(String filtro, Conexao conexao);
}