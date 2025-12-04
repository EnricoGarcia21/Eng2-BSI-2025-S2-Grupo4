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
import java.util.HashMap;
import java.util.Map;

@Service
public class AcessoController {

    @Autowired
    private JwtUtil jwtUtil;

    private Conexao getConexao() {
        return SingletonDB.conectar();
    }

    public String autenticarGerarToken(String email, String senha) {
        if (email == null || senha == null) return null;
        Conexao conexao = getConexao();
        Login usuario = Login.buscarPorLogin(email, conexao);

        if (usuario == null) {
            System.err.println("Login falhou: Usuário não encontrado: " + email);
            return null;
        }
        if (usuario.getStatus() != 'A') {
            System.err.println("Login falhou: Usuário inativo: " + email);
            return null;
        }

        if (PasswordEncoder.matches(senha, usuario.getSenha())) {
            String role = usuario.getNivelAcesso() != null ? usuario.getNivelAcesso() : "USER";
            return jwtUtil.gerarToken(usuario.getLogin(), role);
        }

        System.err.println("Login falhou: Senha incorreta para " + email);
        return null;
    }

    public Login buscarPorEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        return Login.buscarPorLogin(email, getConexao());
    }

    public Voluntario buscarVoluntarioPorId(int id) {
        return Voluntario.get(id, getConexao());
    }


    public Map<String, Object> verificarVoluntarioParaCadastro(String cpf) {
        String cpfLimpo = ValidationUtil.cleanCPF(cpf);
        Conexao conexao = getConexao();
        Voluntario v = Voluntario.buscarPorCpf(cpfLimpo, conexao); //

        if (v != null) {

            if (Login.get(v.getVol_id(), conexao) != null) {
                return Map.of("erro", "Este CPF já possui cadastro de acesso.");
            }

            Map<String, Object> dados = new HashMap<>();
            dados.put("encontrado", true);
            dados.put("vol_id", v.getVol_id());
            dados.put("nome", v.getVol_nome());
            dados.put("email", v.getVol_email());
            dados.put("telefone", v.getVol_telefone());
            dados.put("dataNascimento", v.getVol_datanasc());
            dados.put("sexo", v.getVol_sexo());
            dados.put("cep", v.getVol_cep());
            dados.put("rua", v.getVol_rua());
            dados.put("numero", v.getVol_numero());
            dados.put("bairro", v.getVol_bairro());
            dados.put("cidade", v.getVol_cidade());
            dados.put("uf", v.getVol_uf());

            return dados;
        }
        return Map.of("encontrado", false);
    }


    public Login registrarVoluntarioCompleto(
            String nome, String cpf, String telefone, LocalDate dataNascimento,
            String rua, String numero, String bairro, String cidade, String cep, String uf,
            String complemento, String sexo, String email, String senha, String nivelAcesso) {


        String cpfLimpo = ValidationUtil.cleanCPF(cpf);
        String telefoneLimpo = ValidationUtil.cleanPhone(telefone);
        String cepLimpo = ValidationUtil.cleanCEP(cep);

        if (!ValidationUtil.isValidName(nome)) { System.err.println("❌ Nome inválido: " + nome); return null; }
        if (!ValidationUtil.isValidPassword(senha, 6)) { System.err.println("❌ Senha inválida."); return null; }

        Conexao conexao = getConexao();


        Voluntario existente = Voluntario.buscarPorCpf(cpfLimpo, conexao); //
        int voluntarioId = 0;

        if (existente != null) {

            if (Login.get(existente.getVol_id(), conexao) != null) { //
                System.err.println("❌ Erro: Voluntário já tem login.");
                return null;
            }

            existente.setVol_nome(nome);
            existente.setVol_telefone(telefoneLimpo);
            existente.setVol_email(email);
            existente.setVol_datanasc(dataNascimento != null ? dataNascimento.toString() : null);
            existente.setVol_sexo(ValidationUtil.normalizeSexo(sexo));
            existente.setVol_rua(rua);
            existente.setVol_numero(numero);
            existente.setVol_bairro(bairro);
            existente.setVol_cidade(cidade);
            existente.setVol_cep(cepLimpo);
            existente.setVol_uf(uf);

            Voluntario atualizado = existente.alterar(conexao); //

            if (atualizado == null) {
                System.err.println("❌ Erro ao atualizar voluntário existente para registro.");
                return null;
            }

            voluntarioId = existente.getVol_id();
            // -----------------------------------------------------------
        } else {

            if (Voluntario.buscarPorEmail(email, conexao) != null) { //
                System.err.println("❌ Erro: Email já cadastrado.");
                return null;
            }

            Voluntario novo = new Voluntario();
            novo.setVol_nome(nome);
            novo.setVol_cpf(cpfLimpo);
            novo.setVol_telefone(telefoneLimpo);
            novo.setVol_email(email);
            novo.setVol_datanasc(dataNascimento != null ? dataNascimento.toString() : null);
            novo.setVol_sexo(ValidationUtil.normalizeSexo(sexo));
            novo.setVol_rua(rua);
            novo.setVol_numero(numero);
            novo.setVol_bairro(bairro);
            novo.setVol_cidade(cidade);
            novo.setVol_cep(cepLimpo);
            novo.setVol_uf(uf);

            Voluntario salvo = novo.gravar(conexao); //
            if (salvo == null || salvo.getVol_id() == 0) {
                System.err.println("❌ Erro ao gravar voluntário no banco.");
                return null;
            }
            voluntarioId = salvo.getVol_id();
        }


        if (Login.buscarPorLogin(email, conexao) != null) { //
            System.err.println("❌ Erro: Email já usado como Login.");
            return null;
        }


        Login login = new Login();
        login.setVoluntarioId(voluntarioId);
        login.setLogin(email);
        login.setSenha(PasswordEncoder.encode(senha));
        login.setNivelAcesso(nivelAcesso != null ? nivelAcesso : "USER");
        login.setStatus('A');

        return login.gravar(conexao); //
    }

    public Login registrarAdmin(String login, String senha) {
        if (login == null || senha == null) return null;
        Conexao conexao = getConexao();
        if (Login.buscarPorLogin(login, conexao) != null) return null; //

        Voluntario admin = new Voluntario();
        admin.setVol_nome("Admin " + login);
        admin.setVol_email(login);

        admin.setVol_cpf("11111111111");

        admin = admin.gravar(conexao); //
        if (admin == null) return null;

        Login l = new Login();
        l.setVoluntarioId(admin.getVol_id());
        l.setLogin(login);
        l.setSenha(PasswordEncoder.encode(senha));
        l.setNivelAcesso("ADMIN");
        l.setStatus('A');

        return l.gravar(conexao);
    }

    public boolean atualizarSenha(String email, String senhaAtual, String novaSenha) {
        if (email == null || senhaAtual == null || novaSenha == null) return false;
        Conexao conexao = getConexao();
        Login usuario = Login.buscarPorLogin(email, conexao);

        if (usuario == null || usuario.getStatus() != 'A') return false;
        if (!PasswordEncoder.matches(senhaAtual, usuario.getSenha())) return false;

        String novaSenhaHash = PasswordEncoder.encode(novaSenha);
        return Login.atualizarSenha(usuario.getVoluntarioId(), novaSenhaHash, conexao); //
    }
}