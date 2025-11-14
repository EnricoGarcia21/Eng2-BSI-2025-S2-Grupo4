document.addEventListener("DOMContentLoaded", () => {
    const verificacaoForm = document.getElementById("verificacaoForm");
    const submitErrorMessage = document.getElementById("submitErrorMessage");
    const submitSuccessMessage = document.getElementById("submitSuccessMessage");

    const API_URL = 'http://localhost:8080/apis/verificacao';
    // NOVA URL PARA BUSCAR DONATÁRIOS
    const API_DONATARIO_URL = 'http://localhost:8080/apis/donatario';
    
    // Opcional: Variável para armazenar a lista para futuras interações (como pesquisa)
    let listaDonatarios = []; 

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
        // ALTERADO: Campo agora é um SELECT (doa_select)
        doaId: {
            id: 'doa_select', 
            name: 'doa_id', // O nome do parâmetro para o backend continua 'doa_id'
            required: true,
            validate: validarIdNumerico, // Agora valida se o valor selecionado (ID) é numérico
            exemplo: 'Selecione um Donatário válido.'
        },
        observacao: {
            id: 'ver_obs',
            name: 'observacao', // Nome esperado pelo Spring Controller
            required: false, 
            validate: validarObservacao,
            exemplo: 'Máximo de 500 caracteres.'
        }
    };

    // ---------------- VALIDADORES ----------------
    function validarSelecao(valor) {
        // Validação se o valor não é a opção padrão vazia
        return valor !== '' && valor !== null && valor !== undefined;
    }

    function validarIdNumerico(valor) { 
        // Verifica se o valor é um número inteiro positivo
        return !isNaN(parseInt(valor)) && parseInt(valor) > 0; 
    }
    
    function validarObservacao(valor) {
        return valor.length <= 500;
    }

    function validarDataHojeOuPassada(valor) {
        if (!valor) return false;
        const dataInput = new Date(valor + 'T00:00:00'); 
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        return !isNaN(dataInput.getTime()) && dataInput.getTime() <= hoje.getTime();
    }

    // ---------------- FUNÇÕES DE FEEDBACK (Sem Alteração) ----------------
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
    
    // ---------------- NOVO: CARREGAR DONATÁRIOS ----------------
    async function carregarDonatarios() {
        const doaSelect = document.getElementById(CAMPOS_MAP.doaId.id);
        doaSelect.innerHTML = '<option value="">Carregando Donatários...</option>';
        doaSelect.disabled = true;

        try {
            const response = await fetch(API_DONATARIO_URL);
            if (!response.ok) throw new Error('Erro ao buscar lista de donatários.');
            
            const data = await response.json();
            
            if (data.mensagem && !Array.isArray(data)) {
                 listaDonatarios = []; // Nenhuma lista retornada
            } else {
                 listaDonatarios = data;
            }

            doaSelect.innerHTML = '<option value="">Selecione o Donatário...</option>';
            
            if (listaDonatarios.length > 0) {
                listaDonatarios.forEach(donatario => {
                    const option = document.createElement('option');
                    // O valor enviado é o ID (doa_id)
                    option.value = donatario.id; 
                    // O texto visível é o Nome + ID
                    option.textContent = `${donatario.nome} (ID: ${donatario.id})`;
                    doaSelect.appendChild(option);
                });
                doaSelect.disabled = false;
            } else {
                 doaSelect.innerHTML = '<option value="">Nenhum Donatário cadastrado.</option>';
                 doaSelect.disabled = true;
            }

        } catch (error) {
            console.error('Erro ao carregar Donatários:', error);
            doaSelect.innerHTML = '<option value="">Erro ao carregar lista.</option>';
            doaSelect.disabled = true;
            exibirMensagem('erro', 'Não foi possível carregar a lista de Donatários. Tente novamente.');
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
        // NOVO: Carregar a lista de donatários antes de inicializar o form
        carregarDonatarios(); 
        
        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            campo.element = document.getElementById(campo.id);
            
            adicionarValidacaoTempoReal(campo);

            if (campo.required) {
                const label = document.querySelector(`label[for=${campo.id}]`);
                if (label) label.innerHTML = `${label.innerHTML} <span style="color: red;">*</span>`;
            }
        }
    }

    // ---------------- SUBMIT (Sem Alteração Lógica) ----------------
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
            
            // Re-seleciona a opção padrão no select do donatário
            const doaSelect = document.getElementById(CAMPOS_MAP.doaId.id);
            if (doaSelect) doaSelect.value = ""; 
            
        } catch (error) {
            console.error("Erro ao enviar Verificação:", error);
            exibirMensagem('erro', `Erro ao cadastrar Verificação: ${error.message || "Erro desconhecido."}`);
        }
    });

    inicializarCampos();
});