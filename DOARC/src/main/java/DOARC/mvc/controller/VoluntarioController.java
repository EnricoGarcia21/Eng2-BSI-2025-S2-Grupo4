package DOARC.mvc.controller;

import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoluntarioController {
    @Autowired
    private Voluntario voluntarioModel;

    // Buscar voluntário por ID
    public Map<String, Object> getVoluntario(int id) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                Voluntario voluntario = voluntarioModel.get(id, conexao);

                if (voluntario != null) {
                    json.put("volId", voluntario.getVolId());
                    json.put("volNome", voluntario.getVolNome());
                    json.put("volTelefone", voluntario.getVolTelefone());
                    json.put("volEmail", voluntario.getVolEmail());
                    json.put("volCidade", voluntario.getVolCidade());
                    json.put("volBairro", voluntario.getVolBairro());
                } else {
                    json.put("erro", "Voluntário não encontrado");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar voluntário: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    // Listar todos os voluntários
    public Map<String, Object> listarVoluntarios() {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Voluntario> voluntarios = voluntarioModel.getAll(conexao);
                json.put("voluntarios", voluntarios);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao listar voluntários: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    // Buscar voluntários por cidade
    public Map<String, Object> getVoluntariosPorCidade(String cidade) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                List<Voluntario> voluntarios = voluntarioModel.getPorCidade(cidade, conexao);
                json.put("voluntarios", voluntarios);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar voluntários por cidade: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    // Buscar apenas o nome do voluntário por ID (método específico para dropdowns)
    public Map<String, Object> getNomeVoluntario(int id) {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                Voluntario voluntario = voluntarioModel.get(id, conexao);

                if (voluntario != null) {
                    json.put("volId", voluntario.getVolId());
                    json.put("volNome", voluntario.getVolNome());
                } else {
                    json.put("erro", "Voluntário não encontrado");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar nome do voluntário: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }

    // Método adicional para buscar voluntários disponíveis (útil para agendamentos)
    public Map<String, Object> getVoluntariosDisponiveis() {
        SingletonDB conexao = SingletonDB.getInstancia();
        Map<String, Object> json = new HashMap<>();

        if (conexao.conectar()) {
            try {
                // Assumindo que há um campo 'disponivel' na tabela ou filtro específico
                List<Voluntario> voluntarios = voluntarioModel.get("disponivel = true", conexao);
                json.put("voluntarios", voluntarios);
            } catch (Exception e) {
                System.out.println("DEBUG: Erro no controller ao buscar voluntários disponíveis: " + e.getMessage());
                json.put("erro", "Erro interno: " + e.getMessage());
            } finally {
                //conexao.Desconectar();
            }
        } else {
            json.put("erro", "Erro ao conectar com o BD");
        }
        return json;
    }
}