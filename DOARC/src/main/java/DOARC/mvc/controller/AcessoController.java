package DOARC.mvc.controller;

import DOARC.mvc.dao.LoginDAO;
import DOARC.mvc.dao.VoluntarioDAO;
import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthController {
    
    @Autowired
    private LoginDAO loginDAO;
    
    @Autowired
    private VoluntarioDAO voluntarioDAO;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public Map<String, Object> autenticar(String login, String senha) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Busca login pelo username
            Login credencial = loginDAO.buscarPorLogin(login);
            
            if (credencial == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Usuário não encontrado");
                return response;
            }
            
            // Verifica status
            if (!"ATIVO".equals(credencial.getStatus())) {
                response.put("sucesso", false);
                response.put("mensagem", "Usuário inativo");
                return response;
            }
            
            // Verifica senha com hash
            if (!passwordEncoder.matches(senha, credencial.getSenha())) {
                response.put("sucesso", false);
                response.put("mensagem", "Senha incorreta");
                return response;
            }
            
            // Busca dados do voluntário
            Voluntario voluntario = voluntarioDAO.get(credencial.getVoluntarioId());
            
            if (voluntario == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Dados do voluntário não encontrados");
                return response;
            }
            
            // Autenticação bem-sucedida
            response.put("sucesso", true);
            response.put("mensagem", "Login realizado com sucesso");
            response.put("usuario", Map.of(
                "voluntarioId", credencial.getVoluntarioId(), // ← USA O ID DO VOLUNTÁRIO
                "login", credencial.getLogin(),
                "nivelAcesso", credencial.getNivelAcesso(),
                "nome", voluntario.getVolNome(),
                "email", voluntario.getVolEmail()
            ));
            
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro durante a autenticação: " + e.getMessage());
        }
        
        return response;
    }
    
    public Map<String, Object> criarCredenciais(int voluntarioId, String login, String senha, String nivelAcesso) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Verifica se o voluntário existe
            Voluntario voluntario = voluntarioDAO.get(voluntarioId);
            if (voluntario == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Voluntário não encontrado");
                return response;
            }
            
            // Verifica se já existe login para este voluntário
            Login existente = loginDAO.buscarPorVoluntarioId(voluntarioId);
            if (existente != null) {
                response.put("sucesso", false);
                response.put("mensagem", "Já existem credenciais para este voluntário");
                return response;
            }
            
            // Verifica se o login já está em uso
            Login loginExistente = loginDAO.buscarPorLogin(login);
            if (loginExistente != null) {
                response.put("sucesso", false);
                response.put("mensagem", "Login já está em uso");
                return response;
            }
            
            // Cria nova credencial (usa voluntarioId como identificador)
            Login novaCredencial = new Login();
            novaCredencial.setVoluntarioId(voluntarioId); // ← CHAVE PRIMÁRIA
            novaCredencial.setLogin(login);
            novaCredencial.setSenha(passwordEncoder.encode(senha));
            novaCredencial.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "VOLUNTARIO");
            novaCredencial.setStatus("ATIVO");
            
            boolean criado = loginDAO.criarLogin(novaCredencial);
            
            if (criado) {
                response.put("sucesso", true);
                response.put("mensagem", "Credenciais criadas com sucesso");
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Erro ao criar credenciais");
            }
            
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro: " + e.getMessage());
        }
        
        return response;
    }
    
    public Map<String, Object> alterarSenha(int voluntarioId, String senhaAtual, String novaSenha) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Login credencial = loginDAO.buscarPorVoluntarioId(voluntarioId);
            
            if (credencial == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Credenciais não encontradas");
                return response;
            }
            
            // Verifica senha atual
            if (!passwordEncoder.matches(senhaAtual, credencial.getSenha())) {
                response.put("sucesso", false);
                response.put("mensagem", "Senha atual incorreta");
                return response;
            }
            
            // Atualiza senha
            boolean atualizado = loginDAO.atualizarSenha(voluntarioId, passwordEncoder.encode(novaSenha));
            
            if (atualizado) {
                response.put("sucesso", true);
                response.put("mensagem", "Senha alterada com sucesso");
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Erro ao alterar senha");
            }
            
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro: " + e.getMessage());
        }
        
        return response;
    }
    
    // Método para quando excluir um voluntário
    public boolean excluirCredenciais(int voluntarioId) {
        return loginDAO.excluirLogin(voluntarioId);
    }
}