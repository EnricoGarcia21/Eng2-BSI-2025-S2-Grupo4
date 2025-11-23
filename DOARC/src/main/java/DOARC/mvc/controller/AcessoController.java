package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.security.JwtUtil;
import DOARC.mvc.security.PasswordEncoder;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
<<<<<<< HEAD
import DOARC.mvc.util.ValidationUtil;
=======
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
<<<<<<< HEAD

/**
 * Controller de Acesso - Login e Registro
 * Baseado em BANCO-COMPLETO.txt
 */
=======
import java.time.Period;

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
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

<<<<<<< HEAD
    // ==================== AUTENTICAÇÃO ====================

    /**
     * Autentica usuário e gera token JWT
     * @return Token JWT ou null se falhar
     */
    public String autenticarGerarToken(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            System.err.println("❌ Email ou senha vazios");
=======
    // ---------------- AUTENTICAÇÃO ----------------
    public String autenticarGerarToken(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
            return null;
        }

        Login usuario = loginModel.buscarPorLogin(email, getConexao());
<<<<<<< HEAD

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

=======
        if (usuario == null || usuario.getStatus() != 'A') return null;

        if (PasswordEncoder.matches(senha, usuario.getSenha())) {
            String role = usuario.getNivelAcesso() != null ? usuario.getNivelAcesso() : "USER";
            return jwtUtil.gerarToken(usuario.getLogin(), role);
        }
        return null;
    }

    // ---------------- REGISTRO VOLUNTÁRIO ----------------
    public Login registrarVoluntarioCompleto(String nome, String cpf, String telefone,
                                             LocalDate dataNascimento, String endereco,
                                             String sexo, String email, String senha,
                                             String nivelAcesso) {

        Conexao conexao = getConexao();

        // ===== VALIDAÇÕES BÁSICAS (Melhoradas para limpar máscaras) =====
        if (nome == null || nome.trim().isEmpty()) return null;
        if (email == null || email.trim().isEmpty()) return null;
        if (senha == null || senha.length() < 6) return null;
        if (dataNascimento == null || Period.between(dataNascimento, LocalDate.now()).getYears() < 18) return null;

        // Limpar CPF e Telefone de caracteres não numéricos
        String cpfLimpo = cpf != null ? cpf.replaceAll("[^0-9]", "") : null;
        String telefoneLimpo = telefone != null ? telefone.replaceAll("[^0-9]", "") : null;

        // Re-atribuir para validação e uso posterior
        cpf = cpfLimpo;
        telefone = telefoneLimpo;

        if (cpf == null || !cpf.matches("\\d{11}")) return null;
        if (telefone == null || !telefone.matches("\\d{10,11}")) return null;

        // ===== VERIFICA DUPLICADOS =====
        if (voluntarioModel.buscarPorCpf(cpf, conexao) != null) return null;
        if (loginModel.buscarPorLogin(email, conexao) != null) return null;

        // ===== SEXO VÁLIDO =====
        sexo = (sexo != null ? sexo.trim().toUpperCase() : "O");
        if (!sexo.equals("M") && !sexo.equals("F") && !sexo.equals("O")) sexo = "O";

        // ===== CRIA VOLUNTÁRIO =====
        Voluntario novoVoluntario = new Voluntario();
        novoVoluntario.setVol_nome(nome);
        novoVoluntario.setVol_cpf(cpf); // Usar CPF limpo
        novoVoluntario.setVol_telefone(telefone); // Usar telefone limpo
        novoVoluntario.setVol_datanasc(dataNascimento.toString());
        novoVoluntario.setVol_rua(endereco != null ? endereco : "");
        novoVoluntario.setVol_bairro("");
        novoVoluntario.setVol_cidade("");
        novoVoluntario.setVol_numero("");
        novoVoluntario.setVol_cep("");
        novoVoluntario.setVol_uf("");
        novoVoluntario.setVol_email(email);
        novoVoluntario.setVol_sexo(sexo);

        Voluntario voluntarioSalvo = voluntarioModel.gravar(novoVoluntario, conexao);
        if (voluntarioSalvo == null || voluntarioSalvo.getVol_id() == 0) return null;

        // ===== CRIA LOGIN =====
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(email);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "USER");
        novoLogin.setStatus('A');

<<<<<<< HEAD
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
=======
        return loginModel.gravar(novoLogin, conexao);
    }

    // ---------------- REGISTRO ADMIN ----------------
    public Login registrarAdmin(String login, String senha) {
        Conexao conexao = getConexao();
        if (login == null || login.trim().isEmpty() || senha == null || senha.length() < 6) return null;
        if (loginModel.buscarPorLogin(login, conexao) != null) return null;

        Voluntario adminVol = new Voluntario();
        adminVol.setVol_nome("ADMIN - " + login);
        adminVol.setVol_email(login);
        adminVol.setVol_cpf("");
        adminVol.setVol_sexo("O"); // SEXO VÁLIDO
        adminVol.setVol_datanasc(LocalDate.now().minusYears(18).toString());
        adminVol.setVol_rua("");
        adminVol.setVol_bairro("");
        adminVol.setVol_cidade("");
        adminVol.setVol_numero("");
        adminVol.setVol_uf("");
        adminVol.setVol_cep("");
        adminVol.setVol_telefone("");

        Voluntario voluntarioSalvo = voluntarioModel.gravar(adminVol, conexao);
        if (voluntarioSalvo == null || voluntarioSalvo.getVol_id() == 0) return null;

>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(login);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso("ADMIN");
        novoLogin.setStatus('A');

<<<<<<< HEAD
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
=======
        return loginModel.gravar(novoLogin, conexao);
    }

    public Login buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return loginModel.buscarPorLogin(email, getConexao());
    }

    public boolean atualizarSenha(String email, String senhaAtual, String novaSenha) {
        if (email == null || senhaAtual == null || novaSenha == null) return false;

        Login usuario = loginModel.buscarPorLogin(email, getConexao());
        if (usuario == null || usuario.getStatus() != 'A') return false;
        if (!PasswordEncoder.matches(senhaAtual, usuario.getSenha())) return false;
        if (novaSenha.length() < 6) return false;

        String novaSenhaHash = PasswordEncoder.encode(novaSenha);
        return loginModel.atualizarSenha(usuario.getVoluntarioId(), novaSenhaHash, getConexao());
    }
}
>>>>>>> f920d7edf7db4e47bf74d5fa54468951ca65c13a
