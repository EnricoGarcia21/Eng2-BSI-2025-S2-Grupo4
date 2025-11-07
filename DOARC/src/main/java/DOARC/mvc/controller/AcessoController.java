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

        @Autowired // Controller recebe a Model (não o DAO)
        private Login loginModel;

        @Autowired // Controller recebe a Model (não o DAO)
        private Voluntario voluntarioModel;

        @Autowired
        private PasswordEncoder passwordEncoder;

        public Map<String, Object> autenticar(String login, String senha) {
            Map<String, Object> response = new HashMap<>();

            try {
                // Busca login usando o método específico da model
                Login credencial = loginModel.autenticar(login, senha);

                if (credencial == null) {
                    response.put("sucesso", false);
                    response.put("mensagem", "Usuário não encontrado ou credenciais inválidas");
                    return response;
                }

                // Verifica status
                if (credencial.getStatus() != 'A') {
                    response.put("sucesso", false);
                    response.put("mensagem", "Usuário inativo");
                    return response;
                }

                // Busca dados do voluntário usando a model
                Voluntario voluntario = voluntarioModel.consultar(credencial.getVoluntarioId());

                if (voluntario == null) {
                    response.put("sucesso", false);
                    response.put("mensagem", "Dados do voluntário não encontrados");
                    return response;
                }

                // Autenticação bem-sucedida
                response.put("sucesso", true);
                response.put("mensagem", "Login realizado com sucesso");
                response.put("usuario", Map.of(
                        "voluntarioId", credencial.getVoluntarioId(),
                        "login", credencial.getLogin(),
                        "nivelAcesso", credencial.getNiveAcesso(),
                        "nome", voluntario.getVol_nome(),
                        "email", voluntario.getVol_email()
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

                // Verifica se o login já está em uso
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

                // Verifica senha atual
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

                // Chama o método apagar da Model (sem parâmetro - usa this)
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

        private Login buscarLoginPorUsuario(String login) {
            List<Login> logins = loginModel.consultar(login);
            return logins != null && !logins.isEmpty() ? logins.get(0) : null;
        }

        private Login buscarLoginPorVoluntarioId(int voluntarioId) {
            // Busca por voluntarioId específico
            List<Login> logins = loginModel.consultar("voluntario_vol_id = " + voluntarioId);
            return logins != null && !logins.isEmpty() ? logins.get(0) : null;
        }
        public Map<String, Object> registrar(String nome, String cpf, String email, String telefone,
                                             String dataNascimento, String sexo, String rua, String numero,
                                             String bairro, String cidade, String cep, String uf, String senha) {
            Map<String, Object> response = new HashMap<>();

            try {
                // Primeiro, cadastra o voluntário
                Map<String, Object> resultadoVoluntario =       voluntarioController.addVoluntario(
                        nome, dataNascimento, rua, bairro, cidade, telefone, cep, uf, email, sexo, numero, cpf
                );

                if (resultadoVoluntario.get("erro") != null) {
                    response.put("sucesso", false);
                    response.put("mensagem", "Erro ao cadastrar voluntário: " + resultadoVoluntario.get("erro"));
                    return response;
                }

                // Recupera o ID do voluntário cadastrado
                Integer voluntarioId = (Integer) resultadoVoluntario.get("id");

                // Cria as credenciais de login
                Map<String, Object> resultadoCredenciais = criarCredenciais(
                        voluntarioId, email, senha, "VOLUNTARIO"
                );

                if (!(boolean) resultadoCredenciais.get("sucesso")) {
                    response.put("sucesso", false);
                    response.put("mensagem", "Voluntário cadastrado, mas erro ao criar credenciais: " + resultadoCredenciais.get("mensagem"));
                    return response;
                }

                response.put("sucesso", true);
                response.put("mensagem", "Cadastro realizado com sucesso! Você já pode fazer login.");

            } catch (Exception e) {
                response.put("sucesso", false);
                response.put("mensagem", "Erro durante o registro: " + e.getMessage());
            }

            return response;
        }




    }