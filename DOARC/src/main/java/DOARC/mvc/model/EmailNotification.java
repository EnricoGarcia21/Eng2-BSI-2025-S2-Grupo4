package DOARC.mvc.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmailNotification {

    @Autowired
    private JavaMailSender mailSender;

    public boolean enviarNotificacaoCampanha(List<String> emails, String tituloCampanha, String descricao) {
        try {
            for (String email : emails) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Nova Campanha Lançada - DOARC");
                message.setText(criarMensagemCampanha(tituloCampanha, descricao));
                message.setFrom("doarc.sistema@gmail.com");

                mailSender.send(message);
                System.out.println("✅ Email enviado para: " + email);
            }
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar emails: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean enviarEmailBemVindo(String email, String nome) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Bem-vindo ao DOARC!");
            message.setText(criarMensagemBemVindo(nome));
            message.setFrom("doarc.sistema@gmail.com");

            mailSender.send(message);
            System.out.println("✅ Email de boas-vindas enviado para: " + email);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erro ao enviar email de boas-vindas: " + e.getMessage());
            return false;
        }
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
}