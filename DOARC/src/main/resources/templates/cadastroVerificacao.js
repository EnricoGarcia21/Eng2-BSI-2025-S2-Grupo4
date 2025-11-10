document.addEventListener("DOMContentLoaded", () => {
    const verificacaoForm = document.getElementById("verificacaoForm");
    const submitErrorMessage = document.getElementById("submitErrorMessage");
    const submitSuccessMessage = document.getElementById("submitSuccessMessage");

    const API_URL = 'http://localhost:8080/apis/verificacao';

    const CAMPOS_MAP = {
        data: {
            id: 'ver_data',
            name: 'data', // Nome esperado pelo Spring Controller
            required: true,
            validate: validarDataHojeOuPassada,
            exemplo: 'Data deve ser hoje ou passada.'
        },
        resultado: {
            id: 'ver_resultado',
            name: 'resultado', // Nome esperado pelo Spring Controller
            required: true,
            validate: validarSelecao,
            exemplo: 'Selecione o nível de necessidade.'
        },
        volId: {
            id: 'vol_id',
            name: 'vol_id', // Nome esperado pelo Spring Controller
            required: true,
            validate: validarIdNumerico,
            exemplo: 'ID numérico (Ex: 1).'
        },
        doaId: {
            id: 'doa_id',
            name: 'doa_id', // Nome esperado pelo Spring Controller
            required: true,
            validate: validarIdNumerico,
            exemplo: 'ID numérico do Donatário (Família).'
        },
        observacao: {
            id: 'ver_obs',
            name: 'observacao', // Nome esperado pelo Spring Controller
            required: false, // Observação não é estritamente obrigatória, mas deve ser validada se preenchida
            validate: validarObservacao,
            exemplo: 'Máximo de 500 caracteres.'
        }
    };

    // ---------------- VALIDADORES ----------------
    function validarSelecao(valor) {
        return valor !== '' && valor !== null && valor !== undefined;
    }

    function validarIdNumerico(valor) { 
        return !isNaN(parseInt(valor)) && parseInt(valor) > 0; 
    }
    
    function validarObservacao(valor) {
        // Valida se o campo tem no máximo 500 chars (limite do DB)
        return valor.length <= 500;
    }

    function validarDataHojeOuPassada(valor) {
        if (!valor) return false;
        const dataInput = new Date(valor + 'T00:00:00'); 
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        // A data da verificação deve ser hoje ou no passado (não pode ser futura)
        return !isNaN(dataInput.getTime()) && dataInput.getTime() <= hoje.getTime();
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
        if (exemploDiv) {
            exemploDiv.remove();
        }
    }
    
    // ---------------- INICIALIZAÇÃO E EVENTOS ----------------
    function adicionarValidacaoTempoReal(campo) {
        if (campo.element) {
            const eventType = (campo.element.tagName === 'SELECT' || campo.element.type === 'date') ? 'change' : 'input';

            campo.element.addEventListener(eventType, () => {
                const valor = campo.element.value.trim();
                const campoValido = campo.validate(valor);

                campo.element.classList.remove('error', 'success');
                
                // Trata campo como erro se for obrigatório E inválido/vazio
                const shouldValidate = campo.required || valor.length > 0;

                if (shouldValidate && !campoValido) {
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

    function inicializarCampos() {
        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            campo.element = document.getElementById(campo.id);
            
            // Adiciona validação em tempo real
            adicionarValidacaoTempoReal(campo);

            // Adiciona asterisco para campos obrigatórios
            if (campo.required) {
                const label = document.querySelector(`label[for=${campo.id}]`);
                if (label) label.innerHTML = `${label.innerHTML} <span style="color: red;">*</span>`;
            }
        }
    }

    // ---------------- SUBMIT ----------------
    verificacaoForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        hideMessage(submitErrorMessage);
        hideMessage(submitSuccessMessage);
        let formValido = true;
        const params = new URLSearchParams();

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            let valor = campo.element.value.trim();
            const campoElement = campo.element;

            // Valida o campo
            const campoValido = campo.validate(valor);
            
            const shouldValidate = campo.required || valor.length > 0;

            if (shouldValidate && !campoValido) {
                formValido = false;
                campoElement.classList.add('error');
                campoElement.classList.remove('success');
                mostrarExemplo(campoElement, campo.exemplo);
            } else if (campoValido) {
                 campoElement.classList.add('success');
                 campoElement.classList.remove('error');
                 esconderExemplo(campoElement);
            }

            // O nome do parâmetro deve ser o NAME do campo
            params.append(campo.name, valor); 
        }

        if (!formValido) {
            exibirMensagem('erro', 'Preencha todos os campos obrigatórios corretamente.');
            return;
        }

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: params.toString()
            });

            const result = await response.json().catch(() => ({}));
            
            if (!response.ok) throw new Error(result.erro || result.mensagem || `Erro HTTP: ${response.status}`);

            exibirMensagem('sucesso', result.mensagem || 'Verificação cadastrada com sucesso!');
            verificacaoForm.reset();
            // Limpa o feedback visual após reset
            document.querySelectorAll('.form-control').forEach(el => el.classList.remove('success', 'error'));
            document.querySelectorAll('.exemplo-message').forEach(el => el.remove());
            
        } catch (error) {
            console.error("Erro ao enviar Verificação:", error);
            exibirMensagem('erro', `Erro ao cadastrar Verificação: ${error.message || "Erro desconhecido."}`);
        }
    });

    inicializarCampos();
});