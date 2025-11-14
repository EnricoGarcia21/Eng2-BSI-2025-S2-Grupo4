//package DOARC.mvc.controller;
//
//import DOARC.mvc.model.Login;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class AcessoController {
//
//    @Autowired
//    private Login loginModel;
//
//    // --- GET ALL ---
//    public List<Map<String, Object>> getLogins() {
//
//        List<Login> lista = loginModel.consultar("");
//
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        for (Login l : lista) {
//            Map<String, Object> json = new HashMap<>();
//            json.put("loginId", l.getLoginId());
//            json.put("voluntarioId", l.getVoluntarioId());
//            json.put("login", l.getLogin());
//            json.put("senha", l.getSenha());
//            json.put("niveAcesso", l.getNiveAcesso());
//            json.put("status", l.getStatus());
//            result.add(json);
//        }
//
//        return result;
//    }
//
//
//    public Map<String, Object> getLogin(int id) {
//
//        Login l = loginModel.consultar(id);
//
//        if (l == null) return Map.of("erro", "Login não encontrado");
//
//        Map<String, Object> json = new HashMap<>();
//        json.put("loginId", l.getLoginId());
//        json.put("voluntarioId", l.getVoluntarioId());
//        json.put("login", l.getLogin());
//        json.put("senha", l.getSenha());
//        json.put("niveAcesso", l.getNiveAcesso());
//        json.put("status", l.getStatus());
//        return json;
//    }
//
//    // --- ADD ---
//    public Map<String, Object> addLogin(int voluntarioId, String login, String senha,
//                                        String niveAcesso, char status) {
//
//        Login novo = new Login();
//        novo.setVoluntarioId(voluntarioId);
//        novo.setLogin(login);
//        novo.setSenha(senha);
//        novo.setNiveAcesso(niveAcesso);
//        novo.setStatus(status);
//
//        Login gravado = novo.gravar();
//
//        if (gravado == null) return Map.of("erro", "Erro ao registrar login");
//
//        Map<String, Object> json = new HashMap<>();
//        json.put("loginId", gravado.getLoginId());
//        json.put("voluntarioId", gravado.getVoluntarioId());
//        json.put("login", gravado.getLogin());
//        json.put("senha", gravado.getSenha());
//        json.put("niveAcesso", gravado.getNiveAcesso());
//        json.put("status", gravado.getStatus());
//        return json;
//    }
//
//    // --- UPDATE ---
//    public Map<String, Object> updtLogin(int loginId, int voluntarioId, String login,
//                                         String senha, String niveAcesso, char status) {
//
//        Login existente = loginModel.consultar(loginId);
//
//        if (existente == null) return Map.of("erro", "Login não encontrado");
//
//        existente.setVoluntarioId(voluntarioId);
//        existente.setLogin(login);
//        existente.setSenha(senha);
//        existente.setNiveAcesso(niveAcesso);
//        existente.setStatus(status);
//
//        Login atualizado = existente.alterar();
//
//        if (atualizado == null) return Map.of("erro", "Erro ao atualizar login");
//
//        Map<String, Object> json = new HashMap<>();
//        json.put("loginId", atualizado.getLoginId());
//        json.put("voluntarioId", atualizado.getVoluntarioId());
//        json.put("login", atualizado.getLogin());
//        json.put("senha", atualizado.getSenha());
//        json.put("niveAcesso", atualizado.getNiveAcesso());
//        json.put("status", atualizado.getStatus());
//        return json;
//    }
//
//    // --- DELETE ---
//    public Map<String, Object> deletarLogin(int id) {
//
//        Login existente = loginModel.consultar(id);
//
//        if (existente == null) return Map.of("erro", "Login não encontrado");
//
//        boolean deletado = existente.apagar();
//
//        return deletado
//                ? Map.of("mensagem", "Login removido com sucesso")
//                : Map.of("erro", "Erro ao remover o login");
//    }
//
//    // --- BUSCAR POR LOGIN (EXATO) ---
//    public Map<String, Object> buscarPorLogin(String login) {
//
//        Login l = loginModel.buscarPorLogin(login);
//
//        if (l == null) return Map.of("erro", "Login não encontrado");
//
//        Map<String, Object> json = new HashMap<>();
//        json.put("loginId", l.getLoginId());
//        json.put("voluntarioId", l.getVoluntarioId());
//        json.put("login", l.getLogin());
//        json.put("senha", l.getSenha());
//        json.put("niveAcesso", l.getNiveAcesso());
//        json.put("status", l.getStatus());
//        return json;
//    }
//
//    // --- BUSCAR POR VOLUNTARIO ---
//    public Map<String, Object> buscarPorVoluntarioId(int voluntarioId) {
//
//        Login l = loginModel.buscarPorVoluntarioId(voluntarioId);
//
//        if (l == null) return Map.of("erro", "Nenhum login encontrado para esse voluntário");
//
//        Map<String, Object> json = new HashMap<>();
//        json.put("loginId", l.getLoginId());
//        json.put("voluntarioId", l.getVoluntarioId());
//        json.put("login", l.getLogin());
//        json.put("senha", l.getSenha());
//        json.put("niveAcesso", l.getNiveAcesso());
//        json.put("status", l.getStatus());
//        return json;
//    }
//}
