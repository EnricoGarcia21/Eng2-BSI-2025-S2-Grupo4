document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('verificacaoForm');
    const submitErrorMsg = document.getElementById('submitErrorMessage');
    const submitSuccessMsg = document.getElementById('submitSuccessMessage');

    const API_BASE_URL = 'http://localhost:8080/apis/verificacao';
    // NOVA URL PARA BUSCAR DONATÁRIOS
    const API_DONATARIO_URL = 'http://localhost:8080/apis/donatario';

    // Obtém o ID da verificação da URL
    const urlParams = new URLSearchParams(window.location.search);
    const verId = urlParams.get('id');

    // Mapeamento dos campos: Chave (Controller/Backend) -> ID do elemento no Front
    const CAMPOS_MAP = {
        // Campos do Controller: data, observacao, resultado, vol_id, dona_id
        data: { id: 'ver_data', required: true, validate: validarDataHojeOuPassada, name: 'data' },
        observacao: { id: 'ver_obs', required: false, validate: validarObservacao, name: 'observacao' },
        resultado: { id: 'ver_resultado', required: true, validate: validarSelecao, name: 'resultado' },
        vol_id: { id: 'vol_id', required: true, validate: validarIdNumerico, name: 'vol_id' },
        // CORREÇÃO: Chave do backend agora é 'dona_id'
        dona_id: { id: 'doa_select', required: true, validate: validarIdNumerico, name: 'dona_id' } 
    };

    // ---------------- VALIDADORES (Mantidos) ----------------
    function validarDataHojeOuPassada(valor) {
        if (!valor) return false;
        const dataInput = new Date(valor + 'T00:00:00'); 
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0);
        return !isNaN(dataInput.getTime()) && dataInput.getTime() <= hoje.getTime();
    }
    function validarObservacao(valor) {
        return valor.length <= 500;
    }
    function validarSelecao(valor) { 
        // Verifica se o valor não é a opção padrão vazia
        return valor !== '' && valor !== null && valor !== undefined; 
    }
    function validarIdNumerico(valor) { 
        // Verifica se o valor é um número inteiro positivo
        return !isNaN(parseInt(valor)) && parseInt(valor) >= 1; 
    }

    // ---------------- FEEDBACK VISUAL (Mantido) ----------------
    function adicionarFeedbackObrigatorio(labelElement) {
        if (!labelElement) return;
        if (!labelElement.querySelector('.required-indicator')) {
            const span = document.createElement('span');
            span.className = 'required-indicator';
            span.textContent = ' *';
            span.style.color = 'red';
            labelElement.appendChild(span);
        }
    }

    function atualizarFeedback(inputElement, isValid) {
        if (!inputElement) return;
        const labelElement = document.querySelector(`label[for="${inputElement.id}"]`);
        const indicator = labelElement ? labelElement.querySelector('.required-indicator') : null;
        
        inputElement.classList.remove('success', 'error');
        
        if (isValid) {
            if (indicator) indicator.style.color = 'green';
            inputElement.classList.add('success');
        } else if (inputElement.value.trim() !== '' || inputElement.hasAttribute('required')) {
            if (indicator) indicator.style.color = 'red';
            inputElement.classList.add('error');
        } else {
             if (indicator) indicator.style.color = 'red';
        }
    }

    function exibirMensagem(tipo, mensagem) {
        if (submitErrorMsg) submitErrorMsg.style.display = 'none';
        if (submitSuccessMsg) submitSuccessMsg.style.display = 'none';

        if (tipo === 'erro' && submitErrorMsg) {
            submitErrorMsg.textContent = mensagem;
            submitErrorMsg.style.display = 'block';
        } else if (tipo === 'sucesso' && submitSuccessMsg) {
            submitSuccessMsg.textContent = mensagem;
            submitSuccessMsg.style.display = 'block';
        }
    }

    // ---------------- CARREGAR LISTA DE DONATÁRIOS ----------------
    async function carregarDonatarios() {
        const doaSelect = document.getElementById(CAMPOS_MAP.dona_id.id); // CORREÇÃO: Usa 'dona_id'
        doaSelect.innerHTML = '<option value="">Carregando Donatários...</option>';
        doaSelect.disabled = true;

        try {
            const response = await fetch(API_DONATARIO_URL);
            if (!response.ok) throw new Error('Erro ao buscar lista de donatários.');
            
            const listaDonatarios = await response.json();
            
            doaSelect.innerHTML = '<option value="" disabled selected>Selecione</option>'; // Opção padrão para Alterar
            
            if (Array.isArray(listaDonatarios) && listaDonatarios.length > 0) {
                listaDonatarios.forEach(donatario => {
                    const option = document.createElement('option');
                    // O valor enviado é o ID (dona_id)
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
        }
    }


    // ---------------- CARREGAR DADOS DA VERIFICAÇÃO ----------------
    async function carregarDadosVerificacao(id) {
        // 1. Garante que a lista de donatários está carregada antes de tentar pré-selecionar
        await carregarDonatarios(); 
        
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`);
            const data = await response.json();

            if (!response.ok) throw new Error(data.mensagem || 'Erro ao carregar os dados da Verificação.');

            // Preenche cada campo com o valor retornado
            // Usamos Object.entries para iterar sobre o CAMPOS_MAP e garantir que 'dona_id' seja usado corretamente.
            for (const [backendKey, campo] of Object.entries(CAMPOS_MAP)) {
                const el = document.getElementById(campo.id);
                if (!el) continue;

                // A chave no objeto de dados retornado é a chave do backend (ex: 'dona_id')
                const valor = data[backendKey] || ''; 
                
                if (valor !== '') {
                    el.value = valor;
                    
                    // Tratamento específico para a data (garante formato YYYY-MM-DD)
                    if (campo.id === 'ver_data' && valor.length >= 10) {
                        el.value = valor.substring(0, 10);
                    }
                    
                    // Atualiza o feedback visual após preenchimento
                    // O valor é garantido de ser o ID correto, então a validação deve passar.
                    atualizarFeedback(el, campo.validate(String(el.value))); 
                }
            }
            
            // Corrige o título após o carregamento bem-sucedido
            const titulo = document.querySelector('h1');
            if (titulo) titulo.textContent = `Alterar Verificação (ID: ${id})`;

        } catch (error) {
            const container = document.querySelector('.form-container');
            if (container) container.innerHTML = `<h1>Erro: ${error.message}</h1>`;
            console.error('❌ Erro ao carregar dados:', error);
            if (form) form.style.display = 'none';
        }
    }

    // ---------------- INICIALIZAR CAMPOS (Mantido) ----------------
    function inicializarCampos() {
        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            campo.element = document.getElementById(campo.id);
            campo.label = document.querySelector(`label[for="${campo.id}"]`);
            
            if (campo.required) adicionarFeedbackObrigatorio(campo.label);

            if (campo.element) {
                // Adiciona listeners para validação em tempo real
                campo.element.addEventListener('input', () => {
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
                campo.element.addEventListener('change', () => { // Essencial para inputs type=date, select
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
            }
        }
    }

    // ---------------- FLUXO PRINCIPAL ----------------
    if (!form) { console.error('Form #verificacaoForm não encontrado.'); return; }
    if (!verId) {
        document.querySelector('.form-container').innerHTML = '<h1>Erro: ID da Verificação não encontrado na URL.</h1>';
        return;
    }

    inicializarCampos();
    carregarDadosVerificacao(verId);

    // ---------------- SUBMIT DO FORM ----------------
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        let formIsValid = true;

        // Adiciona ID para o PUT
        const formData = new URLSearchParams();
        formData.append('id', verId); 

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            if (!campo.element) continue;

            let valor = campo.element.value.trim();
            
            // Validação final
            const isValid = campo.validate(valor);
            if (!isValid && campo.required) formIsValid = false;
            atualizarFeedback(campo.element, isValid);

            // Adiciona ao payload. Usa campo.name (data, observacao, dona_id, etc.)
            formData.append(campo.name, valor);
        }

        if (!formIsValid) {
            exibirMensagem('erro', 'Preencha corretamente todos os campos obrigatórios.');
            return;
        }

        try {
            const response = await fetch(API_BASE_URL, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            const result = await response.json().catch(() => ({}));

            if (response.ok) {
                exibirMensagem('sucesso', 'Verificação alterada com sucesso! Redirecionando...');
                // Redireciona para a tela de consulta
                setTimeout(() => window.location.href = 'consultaVerificacao.html', 1200); 
            } else {
                // O backend (View) espera o ID na URL para o PUT, mas estamos enviando no corpo.
                // Se o backend espera o ID na URL, a View deve ser ajustada.
                exibirMensagem('erro', result.mensagem || `Erro HTTP ${response.status}`);
            }
        } catch (error) {
            exibirMensagem('erro', 'Erro de conexão com o servidor.');
            console.error('Erro de rede:', error);
        }
    });
});