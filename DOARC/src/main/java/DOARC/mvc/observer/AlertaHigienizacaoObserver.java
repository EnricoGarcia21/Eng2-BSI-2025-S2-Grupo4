package DOARC.mvc.observer;
import DOARC.mvc.model.HigienizacaoRoupa;
import DOARC.mvc.observer.HigienizacaoObserver;

public class AlertaHigienizacaoObserver implements HigienizacaoObserver {
    @Override
    public void atualizar(HigienizacaoRoupa h, long dias, long horas) {
        String ultimo = h.getHigUltimoAlerta();

        String detalhes = String.format("\"%s\" na \"%s\" (R$ %.2f)",
                h.getHigDescricaoRoupa(), h.getHigLocal(), h.getHigValorPago());

        // Só entra se o alerta de 7 dias ainda não foi enviado
        if (dias == 7 && !"SETE_DIAS".equals(ultimo)) {
            System.out.println("[NOTIFICAÇÃO 7 DIAS]: Enviando e-mail... Falta 1 semana para lavar " + detalhes);
            h.setHigUltimoAlerta("SETE_DIAS");
        }
        // Só entra se o alerta de 1 dia ainda não foi enviado
        else if (dias == 1 && !"UM_DIA".equals(ultimo)) {
            System.out.println("[NOTIFICAÇÃO 1 DIA]: Enviando e-mail... Falta 1 dia para lavar " + detalhes);
            h.setHigUltimoAlerta("UM_DIA");
        }
        // Só entra se o alerta de 1 hora ainda não foi enviado
        else if (dias == 0 && horas == 1 && !"UMA_HORA".equals(ultimo)) {
            System.out.println("[NOTIFICAÇÃO 1 HORA]: Enviando e-mail... Falta 1 hora para lavar " + detalhes);
            h.setHigUltimoAlerta("UMA_HORA");
        }
    }
}