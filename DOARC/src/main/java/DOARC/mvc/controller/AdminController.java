package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
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

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    // ==========================================
    // MÉTODO AUXILIAR PARA MAPEAR OBJETO -> JSON
    // (Atualizado com TODOS os campos do banco)
    // ==========================================
    private Map<String, Object> voluntarioToMap(Voluntario v) {
        Map<String, Object> map = new HashMap<>();
        map.put("vol_id", v.getVol_id());
        map.put("vol_nome", v.getVol_nome());
        map.put("vol_cpf", v.getVol_cpf()); // Novo
        map.put("vol_email", v.getVol_email());
        map.put("vol_telefone", v.getVol_telefone());
        map.put("vol_datanasc", v.getVol_datanasc()); // Novo
        map.put("vol_sexo", v.getVol_sexo()); // Novo

        // Endereço completo
        map.put("vol_cep", v.getVol_cep()); // Novo
        map.put("vol_rua", v.getVol_rua()); // Novo (von_rua no banco)
        map.put("vol_numero", v.getVol_numero()); // Novo
        map.put("vol_bairro", v.getVol_bairro());
        map.put("vol_cidade", v.getVol_cidade());
        map.put("vol_uf", v.getVol_uf()); // Novo

        return map;
    }

    // ==========================================
    // IMPLEMENTAÇÃO DOS MÉTODOS
    // ==========================================

    public List<Map<String, Object>> listarUsuarios() {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            List<Voluntario> lista = voluntarioModel.consultar("", getConexao());

            for (Voluntario v : lista) {
                // Busca login para verificar o nível
                Login login = loginModel.buscarPorVoluntarioId(v.getVol_id(), getConexao());

                // 🔒 FILTRO DE SEGURANÇA:
                // Se for ADMIN, pule para o próximo (não adiciona na lista)
                // O LoginDAO converte "Administrador" do banco para "ADMIN" no objeto Java.
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
            Voluntario voluntario = voluntarioModel.consultar(id, getConexao());

            if (voluntario == null) {
                Map<String, Object> erro = new HashMap<>();
                erro.put("erro", "Voluntário não encontrado");
                return erro;
            }

            return voluntarioToMap(voluntario);

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao buscar voluntário: " + e.getMessage());
            return erro;
        }
    }

    public Map<String, Object> updtVoluntario(Voluntario voluntario) {
        try {
            // Verifica se o voluntário existe antes de alterar
            Voluntario existente = voluntarioModel.consultar(voluntario.getVol_id(), getConexao());
            if (existente == null) {
                Map<String, Object> erro = new HashMap<>();
                erro.put("erro", "Voluntário não encontrado para atualização");
                return erro;
            }

            // Atualiza TODOS os campos no objeto existente
            existente.setVol_nome(voluntario.getVol_nome());
            existente.setVol_cpf(voluntario.getVol_cpf());
            existente.setVol_email(voluntario.getVol_email());
            existente.setVol_telefone(voluntario.getVol_telefone());
            existente.setVol_datanasc(voluntario.getVol_datanasc());
            existente.setVol_sexo(voluntario.getVol_sexo());

            // Campos de endereço
            existente.setVol_cep(voluntario.getVol_cep());
            existente.setVol_rua(voluntario.getVol_rua());
            existente.setVol_numero(voluntario.getVol_numero());
            existente.setVol_bairro(voluntario.getVol_bairro());
            existente.setVol_cidade(voluntario.getVol_cidade());
            existente.setVol_uf(voluntario.getVol_uf());

            // Realiza a alteração no banco
            Voluntario atualizado = voluntarioModel.alterar(existente, getConexao());

            if (atualizado != null) {
                return voluntarioToMap(atualizado);
            } else {
                Map<String, Object> erro = new HashMap<>();
                erro.put("erro", "Falha ao atualizar dados no banco");
                return erro;
            }

        } catch (Exception e) {
            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Erro ao atualizar voluntário: " + e.getMessage());
            return erro;
        }
    }

    public Map<String, Object> deletarVoluntario(int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Voluntario v = voluntarioModel.consultar(id, getConexao());
            if (v == null) {
                response.put("erro", "Voluntário não encontrado");
                return response;
            }

            // Nota: Se houver Login atrelado, deleta o Login antes
            Login login = loginModel.buscarPorVoluntarioId(id, getConexao());
            if (login != null) {
                loginModel.apagar(login, getConexao());
            }

            boolean sucesso = voluntarioModel.apagar(v, getConexao());

            if (sucesso) {
                response.put("mensagem", "Voluntário removido com sucesso");
            } else {
                response.put("erro", "Não foi possível remover o voluntário");
            }

        } catch (Exception e) {
            response.put("erro", "Erro ao deletar: " + e.getMessage());
        }
        return response;
    }

    // ==========================================
    // MÉTODOS JÁ EXISTENTES (MANTIDOS)
    // ==========================================

    public List<Login> listarLogins() {
        return loginModel.consultar("", getConexao());
    }

    public Login buscarLoginPorVoluntarioId(int voluntarioId) {
        return loginModel.buscarPorVoluntarioId(voluntarioId, getConexao());
    }

    public boolean atualizarStatusLogin(int voluntarioId, char novoStatus) {
        return loginModel.atualizarStatus(voluntarioId, novoStatus, getConexao());
    }

    public Login atualizarNivelAcesso(int voluntarioId, String novoNivel) {
        Login login = loginModel.buscarPorVoluntarioId(voluntarioId, getConexao());
        if (login == null) {
            return null;
        }
        login.setNivelAcesso(novoNivel);
        return loginModel.alterar(login, getConexao());
    }

    public List<Voluntario> listarTodosVoluntarios() {
        return voluntarioModel.consultar("", getConexao());
    }

    public Voluntario buscarVoluntarioPorId(int id) {
        return voluntarioModel.consultar(id, getConexao());
    }
}