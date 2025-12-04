package DOARC.mvc.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailNotification {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:doarc.sistema@gmail.com}")
    private String fromEmail;


    public boolean enviarNotificacaoCampanha(List<String> emails, String tituloCampanha, String descricao) {
        if (mailSender == null) {
            System.err.println("⚠️ JavaMailSender não está configurado. Email não enviado.");
            return false;
        }

        if (emails == null || emails.isEmpty()) {
            System.err.println("⚠️ Lista de emails vazia.");
            return false;
        }

        try {
            int sucessos = 0;
            int falhas = 0;

            for (String email : emails) {
                if (email == null || email.trim().isEmpty()) {
                    continue;
                }

                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email.trim());
                    message.setSubject("Nova Campanha Lançada - DOARC");
                    message.setText(criarMensagemCampanha(tituloCampanha, descricao));
                    message.setFrom(fromEmail);

                    mailSender.send(message);
                    System.out.println("✅ Email enviado para: " + email);
                    sucessos++;
                } catch (Exception e) {
                    System.err.println("❌ Erro ao enviar email para " + email + ": " + e.getMessage());
                    falhas++;
                }
            }

            System.out.println(String.format("📊 Resultado: %d sucessos, %d falhas", sucessos, falhas));
            return sucessos > 0;

        } catch (Exception e) {
            System.err.println("❌ Erro geral ao enviar emails: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envia email de boas-vindas para novo usuário
     */
    public boolean enviarEmailBemVindo(String email, String nome) {
        if (mailSender == null) {
            System.err.println("⚠️ JavaMailSender não está configurado. Email não enviado.");
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            System.err.println("⚠️ Email inválido.");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email.trim());
            message.setSubject("Bem-vindo ao DOARC!");
            message.setText(criarMensagemBemVindo(nome));
            message.setFrom(fromEmail);

            mailSender.send(message);
            System.out.println("✅ Email de boas-vindas enviado para: " + email);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar email de boas-vindas: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean enviarEmailRecuperacaoSenha(String email, String token, String nome) {
        if (mailSender == null) {
            System.err.println("⚠️ JavaMailSender não está configurado. Email não enviado.");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Recuperação de Senha - DOARC");
            message.setText(criarMensagemRecuperacaoSenha(nome, token));
            message.setFrom(fromEmail);

            mailSender.send(message);
            System.out.println("✅ Email de recuperação enviado para: " + email);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar email de recuperação: " + e.getMessage());
            return false;
        }
    }


    public boolean isConfigured() {
        return mailSender != null;
    }



    private String criarMensagemCampanha(String titulo, String descricao) {
        return String.format(
                "Olá!\n\n" +
                        "Uma nova campanha foi lançada no sistema DOARC:\n\n" +
                        "📢 CAMPANHA: %s\n\n" +
                        "📝 DESCRIÇÃO:\n%s\n\n" +
                        "Acesse o sistema para mais detalhes e participe!\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe DOARC",
                titulo, descricao
        );
    }

    private String criarMensagemBemVindo(String nome) {
        return String.format(
                "Olá %s!\n\n" +
                        "Bem-vindo ao sistema DOARC! 🎉\n\n" +
                        "Seu cadastro foi realizado com sucesso.\n" +
                        "Agora você pode fazer login e participar das nossas campanhas.\n\n" +
                        "Obrigado por fazer parte da nossa comunidade!\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe DOARC",
                nome
        );
    }

    private String criarMensagemRecuperacaoSenha(String nome, String token) {
        return String.format(
                "Olá %s!\n\n" +
                        "Recebemos uma solicitação de recuperação de senha para sua conta.\n\n" +
                        "Seu código de recuperação é: %s\n\n" +
                        "Este código expira em 30 minutos.\n\n" +
                        "Se você não solicitou esta recuperação, ignore este email.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe DOARC",
                nome, token
        );
    }
}