document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('higienizacaoForm');
    const submitErrorMsg = document.getElementById('submitErrorMessage');
    const submitSuccessMsg = document.getElementById('submitSuccessMessage');

    const urlParams = new URLSearchParams(window.location.search);
    const higId = urlParams.get('id'); // O ID do registro a ser alterado

    // Mapeamento dos campos baseado na tabela higienizacao_roupas
    const CAMPOS_MAP = {
        higDataAgendada: { id: 'hig_data_agendada', required: true, validate: validarDataFuturaOuHoje },
        higHora: { id: 'hig_hora', required: true, validate: validarHora },
        higLocal: { id: 'hig_local', required: true, validate: validarCampoTexto },
        higDescricaoRoupa: { id: 'hig_descricao_roupa', required: true, validate: validarCampoTexto },
        higValorPago: { id: 'hig_valorpago', required: true, validate: validarValorNumerico },
        volId: { id: 'vol_id', required: true, validate: validarIdNumerico }, // ID do Voluntário
    };

    // ---------------- VALIDADORES ----------------
    function validarCampoTexto(valor) { return valor.trim().length >= 3; }
    function validarHora(valor) { return /^\d{2}:\d{2}$/.test(valor); } // HH:MM
    function validarValorNumerico(valor) { return !isNaN(parseFloat(valor)) && isFinite(valor) && parseFloat(valor) >= 0; }
    function validarIdNumerico(valor) { return validarValorNumerico(valor) && parseInt(valor) > 0; }
    function validarDataFuturaOuHoje(valor) {
        if (!valor) return false;
        const dataInput = new Date(valor);
        const hoje = new Date();
        hoje.setHours(0, 0, 0, 0); // Zera hora para comparar apenas a data
        return !(isNaN(dataInput) || dataInput < hoje);
    }

    // ---------------- FEEDBACK VISUAL (Mantido do código Donatário) ----------------
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
        } else if (inputElement.value.trim() !== '') {
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

    // ---------------- CARREGAR DADOS DO REGISTRO ----------------
    async function carregarDadosHigienizacao(id) {
        try {
            const response = await fetch(`http://localhost:8080/apis/higienizacao/${id}`);
            const data = await response.json();

            if (!response.ok) throw new Error(data.mensagem || 'Erro ao carregar os dados do Registro.');

            // Preenche cada campo com o valor retornado
            for (const key in CAMPOS_MAP) {
                const campo = CAMPOS_MAP[key];
                const el = document.getElementById(campo.id);
                if (!el) continue;

                // Mapeamento: 'higDataAgendada' -> 'data_agendada' (do JSON do Controller)
                const jsonKey = campo.id.replace('hig_', '').replace('_', '');
                
                let valor = data[jsonKey] || data[campo.id] || data[key] || '';

                if (valor) {
                    // Trata a formatação da data/hora para campos <input type="date"> e <input type="time">
                    if (campo.id === 'hig_data_agendada' && valor.length >= 10) {
                        valor = valor.substring(0, 10);
                    }
                    if (campo.id === 'vol_id') {
                        valor = parseInt(valor); // Garante que vol_id é um número
                    }
                    
                    el.value = valor;
                    atualizarFeedback(el, campo.validate(el.value));
                }
            }
            
        } catch (error) {
            const container = document.querySelector('.form-container');
            if (container) container.innerHTML = `<h1>Erro: ${error.message}</h1>`;
            console.error('❌ Erro ao carregar dados:', error);
            if (form) form.style.display = 'none';
        }
    }

    // ---------------- INICIALIZAR CAMPOS ----------------
    function inicializarCampos() {
        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            campo.element = document.getElementById(campo.id);
            campo.label = document.querySelector(`label[for="${campo.id}"]`);
            if (campo.required) adicionarFeedbackObrigatorio(campo.label);

            if (campo.element) {
                campo.element.addEventListener('input', () => {
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
                campo.element.addEventListener('change', () => {
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
            }
        }
    }

    // ---------------- FLUXO PRINCIPAL ----------------
    if (!form) { console.error('Form #higienizacaoForm não encontrado.'); return; }
    if (!higId) {
        document.querySelector('.form-container').innerHTML = '<h1>Erro: ID do Registro de Higienização não encontrado na URL.</h1>';
        return;
    }

    const titulo = document.querySelector('h1');
    if (titulo) titulo.textContent = `Alterar Registro de Higienização (ID: ${higId})`;
    const btn = document.querySelector('.btn-gradient');
    if (btn) btn.textContent = "Salvar Alterações";

    inicializarCampos();
    carregarDadosHigienizacao(higId);

    // ---------------- SUBMIT DO FORM ----------------
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        let formIsValid = true;

        const formData = new URLSearchParams();
        formData.append('id', higId); // O Controller espera 'id' ou 'don_id' para alteração via @RequestParam

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            if (!campo.element) continue;

            let valor = campo.element.value.trim();
            
            // O Controller espera os nomes dos campos do DB, então mapeamos 'id' para 'campo.id'
            let paramName = campo.id.replace('hig_', '').replace('vol_', ''); 
            // O Controller espera 'id', 'data_agendada', 'descricao_roupa', etc. 
            // Mas a View original enviava tudo como 'don_nome', 'don_data_nasc', etc.
            // Para garantir a compatibilidade com o Controller Donatario (que usava 'don_id', 'don_nome'),
            // vamos usar os nomes exatos do @RequestParam do HigienizacaoRoupaView.
            
            // Corrigindo para os nomes exatos do HigienizacaoRoupaView:
            // id, data_agendada, descricao_roupa, vol_id, local, hora, valor_pago
            
            let finalParamName;
            switch(campo.id) {
                case 'hig_data_agendada': finalParamName = 'data_agendada'; break;
                case 'hig_descricao_roupa': finalParamName = 'descricao_roupa'; break;
                case 'hig_local': finalParamName = 'local'; break;
                case 'hig_hora': finalParamName = 'hora'; break;
                case 'hig_valorpago': finalParamName = 'valor_pago'; break;
                case 'vol_id': finalParamName = 'vol_id'; break;
                default: finalParamName = campo.id;
            }

            if (!campo.validate(valor) && campo.required) formIsValid = false;
            atualizarFeedback(campo.element, campo.validate(valor));

            formData.append(finalParamName, valor);
        }

        if (!formIsValid) {
            exibirMensagem('erro', 'Preencha corretamente todos os campos obrigatórios.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/apis/higienizacao', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            const result = await response.json().catch(() => ({}));

            if (response.ok) {
                exibirMensagem('sucesso', 'Registro alterado com sucesso! Redirecionando...');
                setTimeout(() => window.location.href = 'consultaHigienizacao.html', 1200);
            } else {
                exibirMensagem('erro', result.mensagem || `Erro HTTP ${response.status}`);
            }
        } catch (error) {
            exibirMensagem('erro', 'Erro de conexão com o servidor.');
            console.error('Erro de rede:', error);
        }
    });
});