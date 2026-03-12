package DOARC.mvc.observer;

import DOARC.mvc.model.HigienizacaoRoupa;

public interface HigienizacaoObserver {
    void atualizar(HigienizacaoRoupa h, long diasRestantes, long horasRestantes);
}