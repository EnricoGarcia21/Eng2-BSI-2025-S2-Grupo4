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
import java.time.Period;

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

    // ---------------- AUTENTICAÇÃO ----------------
    public String autenticarGerarToken(String email, String senha) {
        if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            return null;
        }

        Login usuario = loginModel.buscarPorLogin(email, getConexao());
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
        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(email);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "USER");
        novoLogin.setStatus('A');

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

        Login novoLogin = new Login();
        novoLogin.setVoluntarioId(voluntarioSalvo.getVol_id());
        novoLogin.setLogin(login);
        novoLogin.setSenha(PasswordEncoder.encode(senha));
        novoLogin.setNivelAcesso("ADMIN");
        novoLogin.setStatus('A');

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