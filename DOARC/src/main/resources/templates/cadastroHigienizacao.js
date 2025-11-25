document.addEventListener("DOMContentLoaded", () => {
    const higienizacaoForm = document.getElementById("higienizacaoForm");
    const submitErrorMessage = document.getElementById("submitErrorMessage");
    const submitSuccessMessage = document.getElementById("submitSuccessMessage");

    // ---------------- AUXILIARES DE MOEDA ----------------
    
    // Formata visualmente: 1500 -> R$ 15,00
    function aplicarMascaraMoeda(valor) {
        if (!valor) return "";
        let valorStr = valor.toString().replace(/\D/g, ""); // Remove tudo que não for dígito
        if (valorStr === "") return "";
        
        const numero = parseFloat(valorStr) / 100;
        
        return numero.toLocaleString('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        });
    }

    // Limpa formatação para validação e envio: R$ 1.500,00 -> 1500.00
    function limparValorMoeda(valorFormatado) {
        if (!valorFormatado) return "";
        // Remove tudo que não for número ou vírgula, depois troca vírgula por ponto
        return valorFormatado.replace(/[^\d,]/g, '').replace(',', '.');
    }

    // ---------------- CONFIGURAÇÃO DOS CAMPOS ----------------
    const CAMPOS_MAP = {
        dataAgendada: {
            id: 'hig_data_agendada',
            name: 'data_agendada',
            required: true,
            validate: validarDataFuturaOuHoje,
            exemplo: 'Data deve ser hoje ou futura.'
        },
        hora: {
            id: 'hig_hora',
            name: 'hora',
            required: true,
            validate: validarHora,
            exemplo: 'Formato: HH:MM'
        },
        local: {
            id: 'hig_local',
            name: 'local',
            required: true,
            validate: validarCampoTexto,
            exemplo: 'Nome do Local (Ex: Lavanderia A)'
        },
        valorPago: {
            id: 'hig_valorpago',
            name: 'valor_pago',
            required: true,
            validate: validarValorMoeda, // Validador especial
            isMoeda: true, // Flag para ativar máscara
            exemplo: 'Valor deve ser positivo.'
        },
        descricaoRoupa: {
            id: 'hig_descricao_roupa',
            name: 'descricao_roupa',
            required: true,
            validate: validarCampoTexto,
            exemplo: 'Descrição do Lote de Roupas.'
        },
        volId: {
            id: 'vol_id',
            name: 'vol_id',
            required: true,
            validate: validarIdNumerico,
            exemplo: 'Número ID do Voluntário (Ex: 1)'
        }
    };

    // ---------------- VALIDADORES ----------------
    function validarCampoTexto(valor) { return valor.trim().length >= 3; }
    function validarHora(valor) { return /^\d{2}:\d{2}$/.test(valor); }
    function validarValorNumerico(valor) { return !isNaN(parseFloat(valor)) && isFinite(valor) && parseFloat(valor) >= 0; }
    function validarIdNumerico(valor) { return validarValorNumerico(valor) && parseInt(valor) > 0; }
    
    // Validador que limpa a máscara antes de checar se é número
    function validarValorMoeda(valorComMascara) {
        const valorLimpo = limparValorMoeda(valorComMascara);
        return validarValorNumerico(valorLimpo);
    }

    function validarDataFuturaOuHoje(valor) {
        if (!valor) return false;
        const dataInput = new Date(valor + 'T00:00:00'); 
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0); 
        return !(isNaN(dataInput.getTime()) || dataInput.getTime() < hoje.getTime());
    }

    // ---------------- FUNÇÕES DE FEEDBACK ----------------
    function showMessage(element, message, type) {
        if (!element) return;
        element.textContent = message;
        element.style.display = 'block';
        element.classList.remove('error-message', 'success-message');
        element.classList.add(`${type}-message`);
    }

    function hideMessage(element) {
        if (!element) return;
        element.style.display = 'none';
        element.textContent = '';
    }

    function exibirMensagem(tipo, mensagem) {
        hideMessage(submitErrorMessage);
        hideMessage(submitSuccessMessage);
        if (tipo === 'erro') showMessage(submitErrorMessage, mensagem, 'error');
        else showMessage(submitSuccessMessage, mensagem, 'success');
    }

    function mostrarExemplo(element, exemplo) {
        esconderExemplo(element);
        const exemploDiv = document.createElement('div');
        exemploDiv.classList.add('exemplo-message');
        exemploDiv.style.color = 'red';
        exemploDiv.textContent = `Exemplo: ${exemplo}`;
        element.parentElement.insertBefore(exemploDiv, element.nextSibling); 
    }

    function esconderExemplo(element) {
        const exemploDiv = element.parentElement.querySelector('.exemplo-message');
        if (exemploDiv) exemploDiv.remove();
    }

    // ---------------- INICIALIZAÇÃO E EVENTOS ----------------
    function inicializarCampos() {
        const urlParams = new URLSearchParams(window.location.search);
        const dataUrl = urlParams.get('data_agendada');

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            campo.element = document.getElementById(campo.id);
            
            // Pré-preenche data da URL
            if (campo.id === 'hig_data_agendada' && dataUrl) {
                campo.element.value = dataUrl;
                const isValid = campo.validate(dataUrl);
                if (isValid) {
                    campo.element.classList.add('success');
                } else {
                    campo.element.classList.add('error');
                    mostrarExemplo(campo.element, `A data selecionada ${dataUrl} é passada. ${campo.exemplo}`);
                }
            }
            
            // Adiciona asterisco
            if (campo.required) {
                const label = document.querySelector(`label[for=${campo.id}]`);
                if (label) label.innerHTML = `${label.innerHTML} <span style="color: red;">*</span>`;
            }
            
            if (campo.element) {
                // Evento de INPUT
                campo.element.addEventListener('input', (e) => {
                    // SE FOR MOEDA: Aplica máscara visual
                    if (campo.isMoeda) {
                        e.target.value = aplicarMascaraMoeda(e.target.value);
                    }

                    const valor = campo.element.value.trim();
                    const campoValido = campo.validate(valor);

                    campo.element.classList.remove('error', 'success');
                    if (valor.length > 0 && !campoValido) {
                        campo.element.classList.add('error');
                        mostrarExemplo(campo.element, campo.exemplo);
                    } else if (campoValido) {
                        campo.element.classList.add('success');
                        esconderExemplo(campo.element);
                    } else {
                        esconderExemplo(campo.element);
                    }
                });
            }
        }
    }

    // ---------------- SUBMIT ----------------
    higienizacaoForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        hideMessage(submitErrorMessage);
        hideMessage(submitSuccessMessage);
        
        let formValido = true;
        const params = new URLSearchParams();

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            let valor = campo.element.value.trim();
            const campoElement = campo.element;

            const campoValido = campo.validate(valor);
            
            if (!campoValido && campo.required) {
                formValido = false;
                campoElement.classList.add('error');
                campoElement.classList.remove('success');
                mostrarExemplo(campoElement, campo.exemplo);
            } else if (campoValido) {
                campoElement.classList.add('success');
                campoElement.classList.remove('error');
                esconderExemplo(campoElement);
            }

            // --- LÓGICA DE ENVIO DO VALOR ---
            let valorParaEnvio = valor;
            if (campo.isMoeda) {
                // Limpa o R$ e converte para ponto flutuante string (1500.50)
                valorParaEnvio = limparValorMoeda(valor);
            }

            params.append(campo.name, valorParaEnvio); 
        }

        if (!formValido) {
            exibirMensagem('erro', 'Preencha todos os campos obrigatórios corretamente.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/apis/higienizacao', {
                method: 'POST',
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: params.toString()
            });

            const result = await response.json().catch(() => ({}));
            
            if (!response.ok) throw new Error(result.erro || result.mensagem || `Erro HTTP: ${response.status}`);

            exibirMensagem('sucesso', result.mensagem || 'Registro cadastrado com sucesso!');
            higienizacaoForm.reset();
            
            document.querySelectorAll('.form-control').forEach(el => el.classList.remove('success', 'error'));
            document.querySelectorAll('.exemplo-message').forEach(el => el.remove());
            
        } catch (error) {
            console.error("Erro ao enviar:", error);
            exibirMensagem('erro', `Erro ao cadastrar: ${error.message || "Erro desconhecido."}`);
        }
    });

    inicializarCampos();
});