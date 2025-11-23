package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.security.JwtUtil;
import DOARC.mvc.security.PasswordEncoder;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import DOARC.mvc.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Controller de Acesso - Login e Registro
 * Baseado em BANCO-COMPLETO.txt
 */
@Service
public class AcessoController {

    @Autowired
    private Login loginModel;

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private JwtUtil jwtUtil;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    // ==================== AUTENTICAÇÃO ====================

    /**
     * Autentica usuário e gera token JWT
     * @return Token JWT ou null se falhar
     */
    public String autenticarGerarToken(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            System.err.println("❌ Email ou senha vazios");
            return null;
        }

        Login usuario = loginModel.buscarPorLogin(email, getConexao());

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

    /**
     * Busca login por email
     */
    public Login buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return loginModel.buscarPorLogin(email, getConexao());
    }

    /**
     * Busca voluntário por ID
     */
    public Voluntario buscarVoluntarioPorId(int voluntarioId) {
        return voluntarioModel.consultar(voluntarioId, getConexao());
    }

    // ==================== REGISTRO ====================

    /**
     * Registra voluntário completo (Voluntario + Login)
     * Baseado nos campos que existem no banco
     */
    public Login registrarVoluntarioCompleto(
            String nome,
            String cpf,
            String telefone,
            LocalDate dataNascimento,
            String rua,
            String numero,
            String bairro,
            String cidade,
            String cep,
            String uf,
            String complemento,
            String sexo,
            String email,
            String senha,
            String nivelAcesso) {

        Conexao conexao = getConexao();

        System.out.println("🔵 Iniciando registro: " + email);

        // ===== VALIDAÇÕES =====

        if (!ValidationUtil.isValidName(nome)) {
            System.err.println("❌ Nome inválido");
            return null;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            System.err.println("❌ Email inválido");
            return null;
        }

        if (!ValidationUtil.isValidPassword(senha, 6)) {
            System.err.println("❌ Senha inválida (mínimo 6 caracteres)");
            return null;
        }

        telefone = ValidationUtil.cleanPhone(telefone);
        if (!ValidationUtil.isValidPhone(telefone)) {
            System.err.println("❌ Telefone inválido");
            return null;
        }

        // ===== VERIFICAR DUPLICADOS =====

        if (voluntarioModel.buscarPorEmail(email, conexao) != null) {
            System.err.println("❌ Email já cadastrado em Voluntario: " + email);
            return null;
        }

        if (loginModel.buscarPorLogin(email, conexao) != null) {
            System.err.println("❌ Email já cadastrado em Login: " + email);
            return null;
        }

        // ===== CRIAR VOLUNTÁRIO =====
        // Campos do banco: VOL_NOME, VOL_TELEFONE, VOL_EMAIL, VOL_CIDADE, VOL_BAIRRO

        Voluntario novoVoluntario = new Voluntario();
        novoVoluntario.setVol_nome(nome);
        novoVoluntario.setVol_telefone(telefone);
        novoVoluntario.setVol_email(email);
        novoVoluntario.setVol_cidade(cidade != null ? cidade : "");
        novoVoluntario.setVol_bairro(bairro != null ? bairro : "");

        Voluntario voluntarioSalvo = voluntarioModel.gravar(novoVoluntario, conexao);

        if (voluntarioSalvo == null || voluntarioSalvo.getVol_id() == 0) {
            System.err.println("❌ Falha ao criar Voluntario");
            return null;
        }

        System.out.println("✅ Voluntario criado com ID: " + voluntarioSalvo.getVol_id());

        // ===== CRIAR LOGIN =====
        // Campos do banco: VOL_ID, login, senha, nive_acesso, status

        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(email);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "USER");
        novoLogin.setStatus('A');

        Login loginSalvo = loginModel.gravar(novoLogin, conexao);

        if (loginSalvo == null) {
            System.err.println("❌ Falha ao criar Login");
            return null;
        }

        System.out.println("✅ Login criado para: " + email);
        return loginSalvo;
    }

    /**
     * Registra administrador (versão simplificada)
     */
    public Login registrarAdmin(String login, String senha) {
        Conexao conexao = getConexao();

        if (login == null || login.trim().isEmpty() || senha == null || senha.length() < 6) {
            System.err.println("❌ Login ou senha inválidos");
            return null;
        }

        if (loginModel.buscarPorLogin(login, conexao) != null) {
            System.err.println("❌ Admin já existe: " + login);
            return null;
        }

        // Criar Voluntario para o admin
        Voluntario adminVol = new Voluntario();
        adminVol.setVol_nome("Admin - " + login);
        adminVol.setVol_telefone("");
        adminVol.setVol_email(login);
        adminVol.setVol_cidade("");
        adminVol.setVol_bairro("");

        Voluntario voluntarioSalvo = voluntarioModel.gravar(adminVol, conexao);

        if (voluntarioSalvo == null || voluntarioSalvo.getVol_id() == 0) {
            System.err.println("❌ Falha ao criar Voluntario para Admin");
            return null;
        }

        // Criar Login para admin
        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(login);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso("ADMIN");
        novoLogin.setStatus('A');

        Login loginSalvo = loginModel.gravar(novoLogin, conexao);

        if (loginSalvo != null) {
            System.out.println("✅ Admin criado: " + login);
        }

        return loginSalvo;
    }

    /**
     * Atualiza senha do usuário
     */
    public boolean atualizarSenha(String email, String senhaAtual, String novaSenha) {
        if (email == null || senhaAtual == null || novaSenha == null) {
            System.err.println("❌ Parâmetros inválidos para atualizar senha");
            return false;
        }

        Login usuario = loginModel.buscarPorLogin(email, getConexao());

        if (usuario == null || usuario.getStatus() != 'A') {
            System.err.println("❌ Usuário não encontrado ou inativo");
            return false;
        }

        if (!PasswordEncoder.matches(senhaAtual, usuario.getSenha())) {
            System.err.println("❌ Senha atual incorreta");
            return false;
        }

        if (novaSenha.length() < 6) {
            System.err.println("❌ Nova senha muito curta");
            return false;
        }

        String novaSenhaHash = PasswordEncoder.encode(novaSenha);
        boolean sucesso = loginModel.atualizarSenha(usuario.getVoluntarioId(), novaSenhaHash, getConexao());

        if (sucesso) {
            System.out.println("✅ Senha atualizada para: " + email);
        }

        return sucesso;
    }
}
