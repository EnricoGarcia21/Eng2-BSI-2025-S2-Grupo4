package DOARC.mvc.controller;

import DOARC.mvc.model.Login;
import DOARC.mvc.model.Voluntario;
import DOARC.mvc.security.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AcessoController {

    @Autowired
    private Login loginModel;

    @Autowired
    private Voluntario voluntarioModel;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VoluntarioController voluntarioController;

    // 🚨 LÓGICA DE AUTENTICAÇÃO CORRIGIDA E SEGURA 🚨
    public Map<String, Object> autenticar(String login, String senha) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Busca a credencial APENAS pelo login (email)
            Login credencial = loginModel.buscarPorLogin(login);

            if (credencial == null) {
                response.put("sucesso", false);
                // Mensagem genérica para não dar dica sobre qual campo está errado (boa prática de segurança)
                response.put("mensagem", "Usuário não encontrado ou credenciais inválidas");
                return response;
            }

            // 2. Compara a senha (plain text) com o hash (armazenado)
            if (!passwordEncoder.matches(senha, credencial.getSenha())) {
                response.put("sucesso", false);
                response.put("mensagem", "Usuário não encontrado ou credenciais inválidas");
                return response;
            }

            // 3. Verifica status
            if (credencial.getStatus() != 'A') {
                response.put("sucesso", false);
                response.put("mensagem", "Usuário inativo. Contate o suporte.");
                return response;
            }

            // 4. Busca dados do voluntário
            Voluntario voluntario = voluntarioModel.consultar(credencial.getVoluntarioId());

            if (voluntario == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Dados do voluntário não encontrados");
                return response;
            }

            // 5. Autenticação bem-sucedida
            response.put("sucesso", true);
            response.put("mensagem", "Login realizado com sucesso");

            // 💡 O NÍVEL DE ACESSO (ADMIN ou VOLUNTARIO) é retornado aqui para o frontend
            String nivel = credencial.getNiveAcesso();

            response.put("usuario", Map.of(
                    "voluntarioId", credencial.getVoluntarioId(),
                    "login", credencial.getLogin(),
                    "nivelAcesso", nivel, // 🚨 CAMPO CHAVE PARA LÓGICA DE ADMIN NO FRONTEND
                    "nome", voluntario.getVol_nome(),
                    "email", voluntario.getVol_email()
            ));

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro durante a autenticação: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    // ... (Outros métodos como criarCredenciais, alterarSenha, etc., continuam) ...

    public Map<String, Object> criarCredenciais(int voluntarioId, String login, String senha, String nivelAcesso) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Verifica se o voluntário existe usando a model
            Voluntario voluntario = voluntarioModel.consultar(voluntarioId);
            if (voluntario == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Voluntário não encontrado");
                return response;
            }

            // Verifica se já existe login para este voluntário
            Login existente = buscarLoginPorVoluntarioId(voluntarioId);
            if (existente != null) {
                response.put("sucesso", false);
                response.put("mensagem", "Já existem credenciais para este voluntário");
                return response;
            }

            // 🚨 CORRIGIDO: Usa o novo método de busca exata para checar login duplicado.
            Login loginExistente = buscarLoginPorUsuario(login);
            if (loginExistente != null) {
                response.put("sucesso", false);
                response.put("mensagem", "Login já está em uso");
                return response;
            }

            // Configura os dados na model injetada
            loginModel.setVoluntarioId(voluntarioId);
            loginModel.setLogin(login);
            loginModel.setSenha(passwordEncoder.encode(senha));
            loginModel.setNiveAcesso(nivelAcesso != null ? nivelAcesso : "VOLUNTARIO");
            loginModel.setStatus('A');

            // Chama o método gravar da Model (sem parâmetro - usa this)
            Login novaCredencial = loginModel.gravar();

            if (novaCredencial != null) {
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
            // Busca credenciais usando a model
            Login credencial = buscarLoginPorVoluntarioId(voluntarioId);

            if (credencial == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Credenciais não encontradas");
                return response;
            }

            // Verifica senha atual (Segurança)
            if (!passwordEncoder.matches(senhaAtual, credencial.getSenha())) {
                response.put("sucesso", false);
                response.put("mensagem", "Senha atual incorreta");
                return response;
            }

            // Atualiza senha no objeto
            credencial.setSenha(passwordEncoder.encode(novaSenha));

            // Chama o método alterar da Model (sem parâmetro - usa this)
            Login atualizado = credencial.alterar();

            if (atualizado != null) {
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

    public Map<String, Object> deletarCredenciais(int voluntarioId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Busca credenciais usando a model
            Login credencial = buscarLoginPorVoluntarioId(voluntarioId);

            if (credencial == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Credenciais não encontradas");
                return response;
            }


            boolean deletado = credencial.apagar();

            if (deletado) {
                response.put("sucesso", true);
                response.put("mensagem", "Credenciais removidas com sucesso");
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Erro ao remover credenciais");
            }

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> alterarStatus(int voluntarioId, char status) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Busca credenciais usando a model
            Login credencial = buscarLoginPorVoluntarioId(voluntarioId);

            if (credencial == null) {
                response.put("sucesso", false);
                response.put("mensagem", "Credenciais não encontradas");
                return response;
            }

            // Atualiza status no objeto
            credencial.setStatus(status);

            // Chama o método alterar da Model (sem parâmetro - usa this)
            Login atualizado = credencial.alterar();

            if (atualizado != null) {
                response.put("sucesso", true);
                response.put("mensagem", "Status alterado com sucesso");
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Erro ao alterar status");
            }

        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro: " + e.getMessage());
        }

        return response;
    }

    // --- MÉTODOS AUXILIARES ---

    // 🚨 CORRIGIDO: Usa o novo método de busca exata na Model
    private Login buscarLoginPorUsuario(String login) {
        return loginModel.buscarPorLogin(login);
    }

    private Login buscarLoginPorVoluntarioId(int voluntarioId) {
        // Assume que a consulta com filtro funciona para buscar por ID exato
        List<Login> logins = loginModel.consultar("voluntario_vol_id = " + voluntarioId);
        return logins != null && !logins.isEmpty() ? logins.get(0) : null;
    }

    public Map<String, Object> registrarVoluntario(Map<String, Object> dadosRegistro) {
        Map<String, Object> response = new HashMap<>();
        Voluntario novoVoluntario = null;

        try {
            // ... (Extração de dados omitida por brevidade) ...
            String nome = (String) dadosRegistro.get("nome");
            String cpf = (String) dadosRegistro.get("cpf");
            String email = (String) dadosRegistro.get("email");
            String telefone = (String) dadosRegistro.get("telefone");
            String dataNascimento = (String) dadosRegistro.get("dataNascimento");
            String sexo = (String) dadosRegistro.get("sexo");
            String rua = (String) dadosRegistro.get("rua");
            String numeroStr = dadosRegistro.get("numero") instanceof Integer
                    ? String.valueOf((Integer) dadosRegistro.get("numero"))
                    : (String) dadosRegistro.get("numero");
            String bairro = (String) dadosRegistro.get("bairro");
            String cidade = (String) dadosRegistro.get("cidade");
            String cep = (String) dadosRegistro.get("cep");
            String uf = (String) dadosRegistro.get("uf");
            String senha = (String) dadosRegistro.get("senha");
            String login = (String) dadosRegistro.get("login");
            String nivelAcesso = "VOLUNTARIO";

            // --- 1. REGISTRAR O VOLUNTÁRIO ---
            voluntarioModel.setVol_nome(nome);
            voluntarioModel.setVol_cpf(cpf);
            voluntarioModel.setVol_email(email);
            voluntarioModel.setVol_telefone(telefone);
            voluntarioModel.setVol_datanasc(dataNascimento);
            voluntarioModel.setVol_sexo(sexo);
            voluntarioModel.setVol_rua(rua);
            voluntarioModel.setVol_numero(numeroStr);
            voluntarioModel.setVol_bairro(bairro);
            voluntarioModel.setVol_cidade(cidade);
            voluntarioModel.setVol_cep(cep);
            voluntarioModel.setVol_uf(uf);


            novoVoluntario = voluntarioModel.gravar();

            if (novoVoluntario == null || novoVoluntario.getVol_id() == 0) {
                response.put("sucesso", false);
                response.put("mensagem", "Erro ao cadastrar o voluntário. Verifique logs do banco de dados.");
                return response;
            }

            int voluntarioId = novoVoluntario.getVol_id();

            // --- 2. CRIAR AS CREDENCIAIS DE ACESSO ---

            Map<String, Object> resultadoCredenciais = criarCredenciais(
                    voluntarioId,
                    login,
                    senha,
                    nivelAcesso
            );

            if (!(boolean) resultadoCredenciais.get("sucesso")) {
                // 🚨 CORREÇÃO CRÍTICA: APAGAR O VOLUNTÁRIO E RETORNAR A MENSAGEM DETALHADA
                System.err.println("Falha na criação de credenciais para Voluntário ID " + voluntarioId + ": " + resultadoCredenciais.get("mensagem"));

                try {
                    // Tenta desfazer a criação do Voluntário
                    if (voluntarioModel.apagar()) {
                        System.out.println("Voluntário ID " + voluntarioId + " excluído com sucesso após falha na credencial.");
                    } else {
                        System.err.println("AVISO: Falha ao apagar o Voluntário ID " + voluntarioId + " após erro na credencial. Registro órfão criado.");
                    }
                } catch (Exception deleteE) {
                    System.err.println("ERRO GRAVE: Exceção ao tentar apagar o Voluntário ID " + voluntarioId + ": " + deleteE.getMessage());
                }

                response.put("sucesso", false);
                response.put("mensagem", "Falha ao criar credenciais. Motivo: " + resultadoCredenciais.get("mensagem"));
                return response;
            }

            response.put("sucesso", true);
            response.put("mensagem", "Voluntário registrado e credenciais criadas com sucesso!");

        } catch (Exception e) {
            System.err.println("Exceção durante o registro completo: " + e.getMessage());
            e.printStackTrace();

            response.put("sucesso", false);
            response.put("mensagem", "Erro interno durante o registro: " + e.getMessage());
        }

        return response;
    }
}