package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private Login loginModel;


    private Map<String, Object> voluntarioToMap(Voluntario v) {
        Map<String, Object> map = new HashMap<>();
        map.put("vol_id", v.getVol_id());
        map.put("vol_nome", v.getVol_nome());
        map.put("vol_cpf", v.getVol_cpf());
        map.put("vol_email", v.getVol_email());
        map.put("vol_telefone", v.getVol_telefone());
        map.put("vol_datanasc", v.getVol_datanasc());
        map.put("vol_sexo", v.getVol_sexo());
        map.put("vol_cep", v.getVol_cep());
        map.put("vol_rua", v.getVol_rua());
        map.put("vol_numero", v.getVol_numero());
        map.put("vol_bairro", v.getVol_bairro());
        map.put("vol_cidade", v.getVol_cidade());
        map.put("vol_uf", v.getVol_uf());
        return map;
    }


    private long contarAdminsAtivos() {
        List<Login> todos = loginModel.consultar("");
        return todos.stream()
                .filter(l -> "ADMIN".equalsIgnoreCase(l.getNivelAcesso()) && (l.getStatus() == 'A' || l.getStatus() == 'a'))
                .count();
    }


    public List<Map<String, Object>> listarUsuarios() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Voluntario> lista = voluntarioModel.consultar("");

            for (Voluntario v : lista) {
                Login login = loginModel.buscarPorVoluntarioId(v.getVol_id());

                // Se for ADMIN, pula (não mostra na lista comum de voluntários)
                if (login != null && "ADMIN".equalsIgnoreCase(login.getNivelAcesso())) {
                    continue;
                }

                Map<String, Object> map = voluntarioToMap(v);
                if (login != null) {
                    map.put("nivelAcesso", login.getNivelAcesso());
                    map.put("status", login.getStatus());
                } else {
                    map.put("nivelAcesso", "N/A");
                    map.put("status", "N/A");
                }
                result.add(map);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return result;
    }

    // Busca um voluntário específico
    public Map<String, Object> getVoluntario(int id) {
        try {
            Voluntario voluntario = voluntarioModel.consultar(id);
            if (voluntario == null) return Map.of("erro", "Voluntário não encontrado");
            return voluntarioToMap(voluntario);
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar voluntário: " + e.getMessage());
        }
    }

    // Atualiza dados do voluntário
    public Map<String, Object> updtVoluntario(Voluntario voluntario) {
        try {
            Voluntario existente = voluntarioModel.consultar(voluntario.getVol_id());
            if (existente == null) return Map.of("erro", "Voluntário não encontrado");

            existente.setVol_nome(voluntario.getVol_nome());
            existente.setVol_cpf(voluntario.getVol_cpf());
            existente.setVol_email(voluntario.getVol_email());
            existente.setVol_telefone(voluntario.getVol_telefone());
            existente.setVol_datanasc(voluntario.getVol_datanasc());
            existente.setVol_sexo(voluntario.getVol_sexo());
            existente.setVol_cep(voluntario.getVol_cep());
            existente.setVol_rua(voluntario.getVol_rua());
            existente.setVol_numero(voluntario.getVol_numero());
            existente.setVol_bairro(voluntario.getVol_bairro());
            existente.setVol_cidade(voluntario.getVol_cidade());
            existente.setVol_uf(voluntario.getVol_uf());

            Voluntario atualizado = voluntarioModel.alterar(existente);

            if (atualizado != null) return voluntarioToMap(atualizado);
            return Map.of("erro", "Falha ao atualizar dados no banco");

        } catch (Exception e) {
            return Map.of("erro", "Erro ao atualizar voluntário: " + e.getMessage());
        }
    }

    // Deleta voluntário (com proteção para não apagar o último admin)
    public Map<String, Object> deletarVoluntario(int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Voluntario v = voluntarioModel.consultar(id);
            if (v == null) {
                response.put("erro", "Voluntário não encontrado");
                return response;
            }

            Login login = loginModel.buscarPorVoluntarioId(id);
            if (login != null) {

                if ("ADMIN".equalsIgnoreCase(login.getNivelAcesso())) {
                    if (contarAdminsAtivos() <= 1) {
                        response.put("erro", "Não é possível remover o único administrador do sistema.");
                        return response;
                    }
                }
                loginModel.apagar(login);
            }

            boolean sucesso = voluntarioModel.apagar(v);
            if (sucesso) response.put("mensagem", "Voluntário removido com sucesso");
            else response.put("erro", "Não foi possível remover o voluntário");

        } catch (Exception e) {
            response.put("erro", "Erro ao deletar: " + e.getMessage());
        }
        return response;
    }


    public List<Login> listarLogins() {
        return loginModel.consultar("");
    }

    public Login buscarLoginPorVoluntarioId(int voluntarioId) {
        return loginModel.buscarPorVoluntarioId(voluntarioId);
    }

    // Atualiza status (Ativar/Desativar) com proteção
    public boolean atualizarStatusLogin(int voluntarioId, char novoStatus) {
        Login login = loginModel.buscarPorVoluntarioId(voluntarioId);


        if (login != null && "ADMIN".equalsIgnoreCase(login.getNivelAcesso())) {

            if ((novoStatus == 'I' || novoStatus == 'i') && contarAdminsAtivos() <= 1) {
                System.err.println("Tentativa de desativar o último admin bloqueada.");
                return false;
            }
        }

        return loginModel.atualizarStatus(voluntarioId, novoStatus);
    }


    public Login atualizarNivelAcesso(int voluntarioId, String novoNivel) {
        Login login = loginModel.buscarPorVoluntarioId(voluntarioId);
        if (login == null) return null;


        if ("ADMIN".equalsIgnoreCase(login.getNivelAcesso()) && !"ADMIN".equalsIgnoreCase(novoNivel)) {
            if (contarAdminsAtivos() <= 1) {
                throw new RuntimeException("Não é possível rebaixar o único administrador.");
            }
        }

        login.setNivelAcesso(novoNivel);
        return loginModel.alterar(login);
    }

    public List<Voluntario> listarTodosVoluntarios() {
        return voluntarioModel.consultar("");
    }

    public Voluntario buscarVoluntarioPorId(int id) {
        return voluntarioModel.consultar(id);
    }
}