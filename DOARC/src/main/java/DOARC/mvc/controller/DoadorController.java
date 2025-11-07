package DOARC.mvc.controller;

import DOARC.mvc.model.Doador;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DoadorController {

    @Autowired
    private Doador doadorModel;

    // Buscar doador por ID
    public Map<String, Object> getDoador(int id) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                Doador doador = doadorModel.get(id, conexao);

                if (doador != null) {
                    json.put("doaId", doador.getDoaId());
                    json.put("doaNome", doador.getDoaNome());
                    json.put("doaTelefone", doador.getDoaTelefone());
                    json.put("doaEmail", doador.getDoaEmail());
                    json.put("doaCep", doador.getDoaCep());
                    json.put("doaUf", doador.getDoaUf());
                    json.put("doaCidade", doador.getDoaCidade());
                    json.put("doaBairro", doador.getDoaBairro());
                    json.put("doaRua", doador.getDoaRua());
                    json.put("doaCpf", doador.getDoaCpf());
                    json.put("doaDataNasc", doador.getDoaDataNasc());
                    json.put("doaSexo", doador.getDoaSexo());
                    json.put("doaSite", doador.getDoaSite());
                } else {
                    json.put("erro", "Doador não encontrado");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar doador: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
            // ❌ Removido o conexao.Desconectar()
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }

        return json;
    }

    // Listar todos os doadores
    public Map<String, Object> listarDoadores() {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Doador> doadores = doadorModel.getAll(conexao);
                json.put("doadores", doadores);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao listar doadores: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
            // ❌ Removido o conexao.Desconectar()
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }

        return json;
    }

    // Buscar doadores por cidade
    public Map<String, Object> getDoadoresPorCidade(String cidade) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Doador> doadores = doadorModel.getPorCidade(cidade, conexao);
                json.put("doadores", doadores);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar doadores por cidade: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
            // ❌ Removido o conexao.Desconectar()
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }

        return json;
    }

    // Buscar doadores por bairro
    public Map<String, Object> getDoadoresPorBairro(String bairro) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Doador> doadores = doadorModel.getPorBairro(bairro, conexao);
                json.put("doadores", doadores);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar doadores por bairro: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
            // ❌ Removido o conexao.Desconectar()
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }

        return json;
    }

    // Buscar apenas o nome do doador por ID (método específico para dropdowns)
    public Map<String, Object> getNomeDoador(int id) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                Doador doador = doadorModel.get(id, conexao);

                if (doador != null) {
                    json.put("doaId", doador.getDoaId());
                    json.put("doaNome", doador.getDoaNome());
                } else {
                    json.put("erro", "Doador não encontrado");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar nome do doador: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            }
            // ❌ Removido o conexao.Desconectar()
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }

        return json;
    }
}
