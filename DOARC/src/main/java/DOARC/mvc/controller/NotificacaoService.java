package DOARC.mvc.controller;

import DOARC.mvc.dao.DonatarioDAO;
import DOARC.mvc.dao.NotificacaoDAO;
import DOARC.mvc.model.Donatario;
import DOARC.mvc.model.Notificacao;
import DOARC.mvc.util.Conexao;
import DOARC.mvc.util.SingletonDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificacaoService {

    private final NotificacaoDAO notificacaoDAO = new NotificacaoDAO();
    private final DonatarioDAO donatarioDAO = new DonatarioDAO();

    @Autowired
    private JavaMailSender mailSender;

    private static final String SMTP_USERNAME = "emp.doarc@gmail.com";

    public NotificacaoService() {
    }

    private Conexao getConexaoDoSingleton() {
        return SingletonDB.getConexao();
    }

    private void enviarEmailDeConfirmacao(String toEmail, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SMTP_USERNAME);
            message.setTo(toEmail);
            message.setSubject("CONFIRMAÇÃO DE AGENDAMENTO DE DOAÇÃO - DOARC");
            message.setText(
                    "Prezado(a) Donatário(a),\n\n" +
                            "Sua doação foi agendada/modificada com sucesso.\n\n" +
                            "Detalhes: " + content + "\n\n" +
                            "Atenciosamente,\nEquipe DOARC"
            );
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERRO SMTP CRÍTICO: Falha ao enviar e-mail para " + toEmail + ". Erro: " + e.getMessage());
        }
    }


    public void notificarDonatario(int donatarioId, String texto, int volId) throws SQLException {
        Conexao conn = getConexaoDoSingleton();

        if (volId == 0) {
            System.err.println("ALERTA: Notificação bloqueada. Voluntário ID é 0 (NULL).");
            return;
        }

        Donatario donatario = donatarioDAO.get(donatarioId, conn);
        String emailDestino = (donatario != null) ? donatario.getDonEmail() : null;

        if (emailDestino == null || emailDestino.isEmpty()) {
            System.err.println("ALERTA: E-mail do Donatário ID " + donatarioId + " não encontrado. Notificação de envio cancelada.");
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        Notificacao novaNotificacao = new Notificacao();
        novaNotificacao.setNotTexto(texto);
        novaNotificacao.setNotData(agora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        novaNotificacao.setNotHora(agora.format(DateTimeFormatter.ofPattern("HH:mm")));
        novaNotificacao.setVolId(volId);

        Notificacao gravada = notificacaoDAO.gravar(novaNotificacao, conn);

        if (gravada != null) {

            notificacaoDAO.vincularDonatario(donatarioId, gravada.getNotId(), conn);


            enviarEmailDeConfirmacao(emailDestino, texto);

        } else {

            System.err.println("Erro ao persistir notificação (Gravamento no DAO falhou).");
        }
    }
}