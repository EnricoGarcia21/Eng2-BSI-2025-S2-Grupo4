package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AdminController {

    @Autowired
    private Login loginModel;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    // ============================
    //  LISTAR TODOS OS USUÁRIOS
    // ============================
    public Map<String, Object> listarUsuarios() {
        try {
            List<Login> usuarios = loginModel.consultar("", getConexao());

            List<Map<String, Object>> lista = new ArrayList<>();

            for (Login u : usuarios) {
                lista.add(mapUsuario(u)); // sem senha
            }

            return Map.of(
                    "usuarios", lista,
                    "total", lista.size()
            );

        } catch (Exception e) {
            return Map.of("erro", "Erro ao listar usuários: " + e.getMessage());
        }
    }

    // ============================
    // CONSULTAR POR ID
    // ============================
    public Map<String, Object> buscarUsuario(int id) {
        Login usuario = loginModel.consultar(id, getConexao());

        return (usuario == null)
                ? Map.of("erro", "Usuário não encontrado")
                : mapUsuario(usuario);
    }

    // ============================
    // ALTERAR STATUS (A / I)
    // ============================
    public Map<String, Object> alterarStatus(int id, String status, String emailAdmin) {

        if (status == null || status.length() != 1)
            return Map.of("erro", "Status inválido");

        char novoStatus = status.charAt(0);

        Login usuario = loginModel.consultar(id, getConexao());

        if (usuario == null)
            return Map.of("erro", "Usuário não encontrado");

        if (usuario.getLogin().equalsIgnoreCase(emailAdmin))
            return Map.of("erro", "Você não pode alterar seu próprio status");

        usuario.setStatus(novoStatus);

        Login atualizado = loginModel.alterar(usuario, getConexao());

        return atualizado == null
                ? Map.of("erro", "Erro ao atualizar status")
                : Map.of("mensagem", "Status atualizado com sucesso", "novoStatus", novoStatus);
    }

    // ============================
    // EXCLUIR USUÁRIO
    // ============================
    public Map<String, Object> deletarUsuario(int id, String emailAdmin) {

        Conexao conexao = getConexao();

        Login usuario = loginModel.consultar(id, conexao);

        if (usuario == null)
            return Map.of("erro", "Usuário não encontrado");

        if (usuario.getLogin().equalsIgnoreCase(emailAdmin))
            return Map.of("erro", "Você não pode deletar sua própria conta");

        boolean deletado = loginModel.apagar(usuario, conexao);

        return deletado
                ? Map.of("mensagem", "Usuário removido com sucesso")
                : Map.of("erro", "Erro ao remover usuário");
    }

    // ============================
    // DASHBOARD ADMIN
    // ============================
    public Map<String, Object> dashboard() {

        List<Login> usuarios = loginModel.consultar("", getConexao());

        long total = usuarios.size();
        long ativos = usuarios.stream().filter(u -> u.getStatus() == 'A').count();
        long admins = usuarios.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getNivelAcesso())).count();

        return Map.of(
                "totalUsuarios", total,
                "usuariosAtivos", ativos,
                "usuariosInativos", total - ativos,
                "admins", admins,
                "usuariosComuns", total - admins
        );
    }

    // ============================
    // MAP PARA O JSON FINAL
    // ============================
    private Map<String, Object> mapUsuario(Login u) {
        Map<String, Object> json = new HashMap<>();

        json.put("loginId", u.getLoginId());
        json.put("voluntarioId", u.getVoluntarioId());
        json.put("email", u.getLogin());
        json.put("nivelAcesso", u.getNivelAcesso());
        json.put("status", u.getStatus());

        return json;
    }
}
