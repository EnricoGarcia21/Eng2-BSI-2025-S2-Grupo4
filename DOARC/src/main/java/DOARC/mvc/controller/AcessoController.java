package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.security.JwtUtil;
import DOARC.mvc.security.PasswordEncoder;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
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

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }


    public String autenticarGerarToken(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("Email não pode ser vazio");
            return null;
        }

        if (senha == null || senha.trim().isEmpty()) {
            System.out.println("Senha não pode ser vazia");
            return null;
        }

        Login usuario = loginModel.buscarPorLogin(email, getConexao());

        if (usuario == null) {
            System.out.println("Usuário não encontrado: " + email);
            return null;
        }

        if (usuario.getStatus() != 'A') {
            System.out.println("Usuário inativo: " + email);
            return null;
        }

        if (PasswordEncoder.matches(senha, usuario.getSenha())) {
            String role = usuario.getNivelAcesso() != null ? usuario.getNivelAcesso() : "USER";
            return jwtUtil.gerarToken(usuario.getLogin(), role);
        } else {
            System.out.println("Senha incorreta para: " + email);
            return null;
        }
    }


    public Login registrarVoluntarioCompleto(String nome, String cpf, String telefone,
                                             LocalDate dataNascimento, String endereco,
                                             String email, String senha, String nivelAcesso) {

        Conexao conexao = getConexao();

        System.out.println("=====================================");
        System.out.println("🔵 INICIANDO REGISTRO COMPLETO");
        System.out.println("=====================================");

        // ========== VALIDAÇÕES ==========
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("❌ Nome é obrigatório");
            return null;
        }

        if (cpf == null || cpf.trim().isEmpty()) {
            System.out.println("❌ CPF é obrigatório");
            return null;
        }

        if (email == null || email.trim().isEmpty()) {
            System.out.println("❌ Email é obrigatório");
            return null;
        }

        if (senha == null || senha.length() < 6) {
            System.out.println("❌ Senha deve ter no mínimo 6 caracteres");
            return null;
        }

        // ========== VERIFICA DUPLICADOS ==========
        Voluntario voluntarioExistente = voluntarioModel.buscarPorCpf(cpf, conexao);
        if (voluntarioExistente != null) {
            System.out.println("❌ CPF já cadastrado: " + cpf);
            return null;
        }

        Login loginExistente = loginModel.buscarPorLogin(email, conexao);
        if (loginExistente != null) {
            System.out.println("❌ Email já cadastrado: " + email);
            return null;
        }

        // ========== PASSO 1: CRIA O VOLUNTÁRIO ==========
        System.out.println("🔵 PASSO 1: Criando voluntário...");
        Voluntario novoVoluntario = new Voluntario();
        novoVoluntario.setVol_nome(nome);
        novoVoluntario.setVol_cpf(cpf);
        novoVoluntario.setVol_telefone(telefone);
        novoVoluntario.setVol_datanasc(dataNascimento.toString()); // LocalDate -> String

        // Quebra endereço em partes (simplificado)
        if (endereco != null && !endereco.trim().isEmpty()) {
            novoVoluntario.setVol_rua(endereco);
            novoVoluntario.setVol_bairro("");
            novoVoluntario.setVol_cidade("");
            novoVoluntario.setVol_numero("");
            novoVoluntario.setVol_cep("");
            novoVoluntario.setVol_uf("");
        }

        novoVoluntario.setVol_email(email);
        novoVoluntario.setVol_sexo(""); // Não coletamos no form

        Voluntario voluntarioSalvo = voluntarioModel.gravar(novoVoluntario, conexao);

        if (voluntarioSalvo == null || voluntarioSalvo.getVol_id() == 0) {
            System.out.println("❌ ERRO: Falha ao criar voluntário");
            return null;
        }

        System.out.println("✅ Voluntário criado! ID: " + voluntarioSalvo.getVol_id());

        // ========== PASSO 2: CRIA O LOGIN ==========
        System.out.println("🔵 PASSO 2: Criando login...");

        String senhaHash = PasswordEncoder.encode(senha);

        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());  // ✅ USA O ID DO VOLUNTÁRIO
        novoLogin.setLogin(email);
        novoLogin.setSenha(senhaHash);
        novoLogin.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "USER");
        novoLogin.setStatus('A');

        Login loginSalvo = loginModel.gravar(novoLogin, conexao);

        if (loginSalvo != null) {
            System.out.println("✅ Login criado com sucesso!");
            System.out.println("   Email: " + loginSalvo.getLogin());
            System.out.println("   Voluntário ID: " + loginSalvo.getVoluntarioId());
            System.out.println("   Nível: " + loginSalvo.getNivelAcesso());
            System.out.println("=====================================");
        } else {
            System.out.println("❌ ERRO: Falha ao criar login");
            System.out.println("⚠️  ATENÇÃO: Voluntário foi criado mas login falhou!");
            System.out.println("=====================================");
        }

        return loginSalvo;
    }


    public Login registrarAdmin(String login, String senha) {

        Conexao conexao = getConexao();

        System.out.println("=====================================");
        System.out.println("🔵 INICIANDO REGISTRO DE ADMIN");
        System.out.println("=====================================");

        // --- Validações ---
        if (login == null || login.trim().isEmpty()) {
            System.out.println("❌ Login é obrigatório");
            return null;
        }

        if (senha == null || senha.length() < 6) {
            System.out.println("❌ Senha deve ter no mínimo 6 caracteres");
            return null;
        }

        // Checa se já existe
        Login existente = loginModel.buscarPorLogin(login, conexao);
        if (existente != null) {
            System.out.println("❌ Login já cadastrado: " + login);
            return null;
        }

        // --- Criar voluntário automaticamente ---
        Voluntario adminVol = new Voluntario();
        adminVol.setVol_nome("ADMIN - " + login);
        adminVol.setVol_email(login);
        adminVol.setVol_cpf(""); // opcional
        adminVol.setVol_sexo("");
        adminVol.setVol_datanasc("");
        adminVol.setVol_rua("");
        adminVol.setVol_bairro("");
        adminVol.setVol_cidade("");
        adminVol.setVol_numero("");
        adminVol.setVol_uf("");
        adminVol.setVol_cep("");
        adminVol.setVol_telefone("");

        Voluntario voluntarioSalvo = voluntarioModel.gravar(adminVol, conexao);

        if (voluntarioSalvo == null || voluntarioSalvo.getVol_id() == 0) {
            System.out.println("❌ ERRO ao criar voluntário para admin");
            return null;
        }

        System.out.println("✅ Voluntário admin criado! ID: " + voluntarioSalvo.getVol_id());

        // --- Criar login ADMIN ---
        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(login);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso("ADMIN");
        novoLogin.setStatus('A');

        Login loginSalvo = loginModel.gravar(novoLogin, conexao);

        if (loginSalvo == null) {
            System.out.println("❌ ERRO ao criar login admin");
            return null;
        }

        System.out.println("✅ Login ADMIN criado com sucesso!");
        System.out.println("=====================================");

        return loginSalvo;
    }

    @Deprecated
    public Login registrarUsuario(int voluntarioId, String email, String senha, String nivelAcesso) {
        System.out.println("⚠️  AVISO: Método registrarUsuario() está deprecated!");
        System.out.println("⚠️  Use registrarVoluntarioCompleto() ao invés disso!");

        if (voluntarioId == 0) {
            System.out.println("❌ ERRO: voluntarioId não pode ser 0!");
            System.out.println("❌ Você precisa criar o voluntário ANTES de criar o login!");
            return null;
        }

        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            System.out.println("Email e senha são obrigatórios");
            return null;
        }

        if (senha.length() < 6) {
            System.out.println("Senha deve ter no mínimo 6 caracteres");
            return null;
        }

        Login existente = loginModel.buscarPorLogin(email, getConexao());
        if (existente != null) {
            System.out.println("Email já cadastrado: " + email);
            return null;
        }

        String senhaHash = PasswordEncoder.encode(senha);

        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioId);
        novoLogin.setLogin(email);
        novoLogin.setSenha(senhaHash);
        novoLogin.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "USER");
        novoLogin.setStatus('A');

        Login resultado = loginModel.gravar(novoLogin, getConexao());

        if (resultado != null) {
            System.out.println("Usuário registrado: " + resultado.getLogin());
        } else {
            System.out.println("Erro ao registrar usuário");
        }

        return resultado;
    }


    public Login buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return loginModel.buscarPorLogin(email, getConexao());
    }


    public boolean atualizarSenha(String email, String senhaAtual, String novaSenha) {
        if (email == null || email.trim().isEmpty() || senhaAtual == null || senhaAtual.trim().isEmpty() || novaSenha == null || novaSenha.trim().isEmpty()) {
            return false;
        }

        Login usuario = loginModel.buscarPorLogin(email, getConexao());
        if (usuario == null || usuario.getStatus() != 'A') {
            System.out.println("Usuário não encontrado ou inativo: " + email);
            return false;
        }

        if (!PasswordEncoder.matches(senhaAtual, usuario.getSenha())) {
            System.out.println("Senha atual incorreta para: " + email);
            return false;
        }

        if (novaSenha.length() < 6) {
            System.out.println("Nova senha deve ter no mínimo 6 caracteres");
            return false;
        }

        String novaSenhaHash = PasswordEncoder.encode(novaSenha);

        boolean sucesso = loginModel.atualizarSenha(usuario.getVoluntarioId(), novaSenhaHash, getConexao());

        if (sucesso) {
            System.out.println("Senha atualizada com sucesso para: " + email);
        } else {
            System.out.println("Erro ao atualizar senha para: " + email);
        }

        return sucesso;
    }


}