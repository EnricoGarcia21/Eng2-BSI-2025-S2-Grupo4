package DOARC.mvc.controller;

import DOARC.mvc.model.Campanha;
import DOARC.mvc.model.CampResponsavel;
import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import DOARC.mvc.util.ValidationUtil;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

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

    private long contarAdminsAtivos(Conexao conexao) {
        List<Login> todos = Login.get("", conexao);
        return todos.stream()
                .filter(l -> "ADMIN".equalsIgnoreCase(l.getNivelAcesso()) && (l.getStatus() == 'A' || l.getStatus() == 'a'))
                .count();
    }

    public List<Map<String, Object>> listarUsuarios() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Conexao conexao = getConexao();
            List<Voluntario> lista = Voluntario.get("", conexao);

            for (Voluntario v : lista) {
                Login login = Login.get(v.getVol_id(), conexao);

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

    public Map<String, Object> getVoluntario(int id) {
        try {
            Conexao conexao = getConexao();
            Voluntario voluntario = Voluntario.get(id, conexao);
            if (voluntario == null) return Map.of("erro", "Voluntário não encontrado");
            return voluntarioToMap(voluntario);
        } catch (Exception e) {
            return Map.of("erro", "Erro ao buscar voluntário: " + e.getMessage());
        }
    }

    public Map<String, Object> updtVoluntario(Voluntario voluntario) {
        try {
            Conexao conexao = getConexao();
            Voluntario existente = Voluntario.get(voluntario.getVol_id(), conexao);
            if (existente == null) return Map.of("erro", "Voluntário não encontrado");

            existente.setVol_nome(voluntario.getVol_nome());


            if (voluntario.getVol_cpf() != null) {
                existente.setVol_cpf(ValidationUtil.cleanCPF(voluntario.getVol_cpf()));
            }
            if (voluntario.getVol_telefone() != null) {
                existente.setVol_telefone(ValidationUtil.cleanPhone(voluntario.getVol_telefone()));
            }

            existente.setVol_email(voluntario.getVol_email());
            existente.setVol_datanasc(voluntario.getVol_datanasc());
            existente.setVol_sexo(voluntario.getVol_sexo());
            existente.setVol_cep(voluntario.getVol_cep());
            existente.setVol_rua(voluntario.getVol_rua());
            existente.setVol_numero(voluntario.getVol_numero());
            existente.setVol_bairro(voluntario.getVol_bairro());
            existente.setVol_cidade(voluntario.getVol_cidade());
            existente.setVol_uf(voluntario.getVol_uf());

            Voluntario atualizado = existente.alterar(conexao);

            if (atualizado != null) return voluntarioToMap(atualizado);
            return Map.of("erro", "Falha ao atualizar dados no banco");

        } catch (Exception e) {
            return Map.of("erro", "Erro ao atualizar voluntário: " + e.getMessage());
        }
    }

    public Map<String, Object> deletarVoluntario(int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Conexao conexao = getConexao();
            Voluntario v = Voluntario.get(id, conexao);
            if (v == null) {
                response.put("erro", "Voluntário não encontrado");
                return response;
            }


            Login login = Login.get(id, conexao);
            if (login != null) {
                if ("ADMIN".equalsIgnoreCase(login.getNivelAcesso())) {
                    if (contarAdminsAtivos(conexao) <= 1 && (login.getStatus() == 'A' || login.getStatus() == 'a')) {
                        response.put("erro", "Não é possível remover o único administrador ativo.");
                        return response;
                    }
                }
                login.apagar(conexao);
            }


            List<CampResponsavel> participacoes = CampResponsavel.getPorVoluntario(id, conexao);
            for (CampResponsavel cr : participacoes) {
                cr.apagar(conexao);
            }


            List<Campanha> campanhasCriadas = Campanha.getPorVoluntario(id, conexao);
            for (Campanha c : campanhasCriadas) {
                if (c.getVoluntario_vol_id() == id) {
                    List<CampResponsavel> parts = CampResponsavel.get("camp_id = " + c.getCam_id(), conexao);
                    for (CampResponsavel p : parts) p.apagar(conexao);
                    c.apagar(conexao);
                }
            }

            // 4. Remover Voluntário
            if (v.apagar(conexao)) {
                response.put("mensagem", "Voluntário removido com sucesso");
            } else {
                response.put("erro", "Erro ao remover voluntário (Vínculos persistentes)");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("erro", "Erro ao deletar: " + e.getMessage());
        }
        return response;
    }

    public List<Login> listarLogins() {
        return Login.get("", getConexao());
    }

    public Login buscarLoginPorVoluntarioId(int voluntarioId) {
        return Login.get(voluntarioId, getConexao());
    }

    public boolean atualizarStatusLogin(int voluntarioId, char novoStatus) {
        Conexao conexao = getConexao();
        Login login = Login.get(voluntarioId, conexao);
        if (login != null && "ADMIN".equalsIgnoreCase(login.getNivelAcesso())) {
            if ((novoStatus == 'I' || novoStatus == 'i') && contarAdminsAtivos(conexao) <= 1) {
                return false;
            }
        }
        return Login.atualizarStatus(voluntarioId, String.valueOf(novoStatus), conexao);
    }

    public Login atualizarNivelAcesso(int voluntarioId, String novoNivel) {
        Conexao conexao = getConexao();
        Login login = Login.get(voluntarioId, conexao);
        if (login == null) return null;
        if ("ADMIN".equalsIgnoreCase(login.getNivelAcesso()) && !"ADMIN".equalsIgnoreCase(novoNivel)) {
            if (contarAdminsAtivos(conexao) <= 1) throw new RuntimeException("Não é possível rebaixar o único administrador.");
        }
        login.setNivelAcesso(novoNivel);
        return login.alterar(conexao);
    }


    public List<Voluntario> listarTodosVoluntarios() {
        return Voluntario.get("", getConexao());
    }

    public Voluntario buscarVoluntarioPorId(int id) {
        return Voluntario.get(id, getConexao());
    }
}