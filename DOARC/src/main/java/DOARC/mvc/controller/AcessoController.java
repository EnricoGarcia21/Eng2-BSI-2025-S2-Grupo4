package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.security.JwtUtil;
import DOARC.mvc.security.PasswordEncoder;
import DOARC.mvc.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AcessoController {

    @Autowired
    private Login loginModel;

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private JwtUtil jwtUtil;

    // Removemos getConexao()

    public String autenticarGerarToken(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            System.err.println("❌ Email ou senha vazios");
            return null;
        }

        Login usuario = loginModel.buscarPorLogin(email);

        if (usuario == null) {
            System.err.println("❌ Usuário não encontrado: " + email);
            return null;
        }

        if (usuario.getStatus() != 'A') {
            System.err.println("❌ Usuário inativo: " + email);
            return null;
        }

        if (PasswordEncoder.matches(senha, usuario.getSenha())) {
            String role = usuario.getNivelAcesso() != null ? usuario.getNivelAcesso() : "USER";
            System.out.println("✅ Login bem-sucedido: " + email + " (Role: " + role + ")");
            return jwtUtil.gerarToken(usuario.getLogin(), role);
        }

        System.err.println("❌ Senha incorreta para: " + email);
        return null;
    }

    public Login buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return loginModel.buscarPorLogin(email);
    }

    public Voluntario buscarVoluntarioPorId(int voluntarioId) {
        return voluntarioModel.consultar(voluntarioId);
    }

    public Login registrarVoluntarioCompleto(
            String nome, String cpf, String telefone, LocalDate dataNascimento,
            String rua, String numero, String bairro, String cidade, String cep, String uf,
            String complemento, String sexo, String email, String senha, String nivelAcesso) {

        System.out.println("🔵 Iniciando registro: " + email);

        if (!ValidationUtil.isValidName(nome)) return null;
        if (!ValidationUtil.isValidEmail(email)) return null;
        if (!ValidationUtil.isValidPassword(senha, 6)) return null;

        telefone = ValidationUtil.cleanPhone(telefone);
        if (!ValidationUtil.isValidPhone(telefone)) return null;

        // Sem passar conexão
        if (voluntarioModel.buscarPorEmail(email) != null) {
            System.err.println("❌ Email já cadastrado em Voluntario: " + email);
            return null;
        }

        if (loginModel.buscarPorLogin(email) != null) {
            System.err.println("❌ Email já cadastrado em Login: " + email);
            return null;
        }

        Voluntario novoVoluntario = new Voluntario();
        novoVoluntario.setVol_nome(nome);
        novoVoluntario.setVol_telefone(telefone);
        novoVoluntario.setVol_email(email);
        novoVoluntario.setVol_cidade(cidade != null ? cidade : "");
        novoVoluntario.setVol_bairro(bairro != null ? bairro : "");

        Voluntario voluntarioSalvo = voluntarioModel.gravar(novoVoluntario);

        if (voluntarioSalvo == null || voluntarioSalvo.getVol_id() == 0) return null;

        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(email);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "USER");
        novoLogin.setStatus('A');

        Login loginSalvo = loginModel.gravar(novoLogin);
        if (loginSalvo != null) System.out.println("✅ Login criado para: " + email);
        return loginSalvo;
    }

    public Login registrarAdmin(String login, String senha) {
        if (login == null || login.trim().isEmpty() || senha == null || senha.length() < 6) return null;

        if (loginModel.buscarPorLogin(login) != null) return null;

        Voluntario adminVol = new Voluntario();
        adminVol.setVol_nome("Admin - " + login);
        adminVol.setVol_telefone("");
        adminVol.setVol_email(login);
        adminVol.setVol_cidade("");
        adminVol.setVol_bairro("");

        Voluntario voluntarioSalvo = voluntarioModel.gravar(adminVol);
        if (voluntarioSalvo == null) return null;

        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(login);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso("ADMIN");
        novoLogin.setStatus('A');

        return loginModel.gravar(novoLogin);
    }

    public boolean atualizarSenha(String email, String senhaAtual, String novaSenha) {
        if (email == null || senhaAtual == null || novaSenha == null) return false;

        Login usuario = loginModel.buscarPorLogin(email);
        if (usuario == null || usuario.getStatus() != 'A') return false;
        if (!PasswordEncoder.matches(senhaAtual, usuario.getSenha())) return false;
        if (novaSenha.length() < 6) return false;

        String novaSenhaHash = PasswordEncoder.encode(novaSenha);
        return loginModel.atualizarSenha(usuario.getVoluntarioId(), novaSenhaHash);
    }
}