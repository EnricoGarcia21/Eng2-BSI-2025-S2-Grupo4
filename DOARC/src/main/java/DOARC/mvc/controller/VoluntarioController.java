package DOARC.mvc.controller;

import DOARC.mvc.dao.VoluntarioDAO;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VoluntarioController {

    private final VoluntarioDAO voluntarioModel = new VoluntarioDAO();

    private Conexao getConexaoDoSingleton() {
        return SingletonDB.getConexao();
    }

    public List<Map<String, Object>> getVoluntarios() {
        Conexao conn = getConexaoDoSingleton();

        // Chamada Corrigida
        List<Voluntario> lista = voluntarioModel.get("", conn);

        List<Map<String, Object>> result = new ArrayList<>();

        if (lista != null) {
            for (Voluntario v : lista) {
                Map<String, Object> json = new HashMap<>();
                json.put("id", v.getVolId());
                json.put("nome", v.getVolNome());
                result.add(json);
            }
        }
        return result;
    }
}