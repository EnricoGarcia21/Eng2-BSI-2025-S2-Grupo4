document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('donatarioForm');
    const submitErrorMsg = document.getElementById('submitErrorMessage');
    const submitSuccessMsg = document.getElementById('submitSuccessMessage');

    const urlParams = new URLSearchParams(window.location.search);
    const donId = urlParams.get('id');

    const CAMPOS_MAP = {
        donNome: { id: 'don_nome', required: true, validate: validarNome },
        donDataNasc: { id: 'don_data_nasc', required: true, validate: validarDataNascimento },
        donSexo: { id: 'don_sexo', required: true, validate: validarSelecao },
        donTelefone: { id: 'don_telefone', required: true, validate: validarTelefone },
        donEmail: { id: 'don_email', required: true, validate: validarEmail },
        donCep: { id: 'don_cep', required: true, validate: validarCEP },
        donUf: { id: 'don_uf', required: true, validate: validarUF },
        donCidade: { id: 'don_cidade', required: true, validate: validarCidadeSemNumero },
        donBairro: { id: 'don_bairro', required: true, validate: validarCampoTexto },
        donRua: { id: 'don_rua', required: true, validate: validarCampoTexto }
    };

    // ---------------- VALIDADORES ----------------
    function validarNome(valor) { return /^[a-zA-ZÀ-ÿ\s]{3,}$/.test(valor.trim()); }
    function validarDataNascimento(valor) {
        if (!valor) return false;
        const data = new Date(valor);
        const hoje = new Date();
        return !(isNaN(data) || data > hoje || data.getFullYear() < 1900);
    }
    function validarTelefone(valor) { const num = valor.replace(/\D/g, ''); return num.length >= 10 && num.length <= 11; }
    function validarEmail(valor) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor); }
    function validarCEP(valor) { const num = valor.replace(/\D/g, ''); return num.length === 8; }
    function validarUF(valor) { return valor.length === 2 && /^[A-Z]{2}$/.test(valor); }
    function validarCidadeSemNumero(valor) { return valor.trim().length > 0 && !/\d/.test(valor); }
    function validarSelecao(valor) { return valor !== '' && valor !== null && valor !== 'N/A'; }
    function validarCampoTexto(valor) { return valor.trim().length > 0; }

    // ---------------- FEEDBACK VISUAL ----------------
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

    // ---------------- MÁSCARAS ----------------
    function aplicarMascara(id) {
        const input = document.getElementById(id);
        if (!input) return;
        let value = input.value.replace(/\D/g, "");

        switch (id) {
            case 'don_telefone':
                if (value.length > 11) value = value.substring(0, 11);
                input.value = value;
                break;
            case 'don_cep':
                if (value.length > 8) value = value.substring(0, 8);
                if (value.length > 5) value = `${value.substring(0, 5)}-${value.substring(5)}`;
                input.value = value;
                break;
            case 'don_uf':
                input.value = input.value.toUpperCase().substring(0, 2).replace(/[^A-Z]/g, '');
                break;
        }
    }

    // ---------------- CARREGAR DADOS DO DONATÁRIO ----------------
    async function carregarDadosDonatario(id) {
        try {
            const response = await fetch(`http://localhost:8080/apis/donatario/${id}`);
            const data = await response.json();

            console.log('📦 Dados recebidos da API:', data);

            if (!response.ok) throw new Error(data.mensagem || 'Erro ao carregar os dados do Donatário.');

            // Preenche cada campo com o valor retornado
            for (const key in CAMPOS_MAP) {
                const campo = CAMPOS_MAP[key];
                const el = document.getElementById(campo.id);
                if (!el) continue;

                // Verifica se o backend retornou um campo com o mesmo nome
                const valor = data[campo.id] || data[key] || data[campo.id.replace('don_', '')] || '';

                if (valor) {
                    el.value = valor;
                    if (campo.id === 'don_data_nasc' && valor.length >= 10) {
                        el.value = valor.substring(0, 10);
                    }
                    aplicarMascara(campo.id);
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
                    aplicarMascara(campo.id);
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
                campo.element.addEventListener('change', () => {
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
            }
        }
    }

    // ---------------- FLUXO PRINCIPAL ----------------
    if (!form) { console.error('Form #donatarioForm não encontrado.'); return; }
    if (!donId) {
        document.querySelector('.form-container').innerHTML = '<h1>Erro: ID do Donatário não encontrado na URL.</h1>';
        return;
    }

    const titulo = document.querySelector('h1');
    if (titulo) titulo.textContent = `Alterar Donatário (ID: ${donId})`;
    const btn = document.querySelector('.btn-gradient');
    if (btn) btn.textContent = "Salvar Alterações";

    inicializarCampos();
    carregarDadosDonatario(donId);

    // ---------------- SUBMIT DO FORM ----------------
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        let formIsValid = true;

        const formData = new URLSearchParams();
        formData.append('don_id', donId);

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            if (!campo.element) continue;

            let valor = campo.element.value.trim();

            switch (campo.id) {
                case 'don_uf': valor = valor.substring(0, 2).toUpperCase(); break;
                case 'don_cep': valor = valor.replace(/\D/g, '').substring(0, 8); break;
                case 'don_telefone': valor = valor.replace(/\D/g, '').substring(0, 11); break;
            }

            if (!campo.validate(valor) && campo.required) formIsValid = false;
            atualizarFeedback(campo.element, campo.validate(valor));

            formData.append(campo.id, valor);
        }

        if (!formIsValid) {
            exibirMensagem('erro', 'Preencha corretamente todos os campos obrigatórios.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/apis/donatario', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            const result = await response.json().catch(() => ({}));

            if (response.ok) {
                exibirMensagem('sucesso', 'Donatário alterado com sucesso! Redirecionando...');
                setTimeout(() => window.location.href = 'gerenciarDonatarios.html', 1200);
            } else {
                exibirMensagem('erro', result.mensagem || `Erro HTTP ${response.status}`);
            }
        } catch (error) {
            exibirMensagem('erro', 'Erro de conexão com o servidor.');
            console.error('Erro de rede:', error);
        }
    });
});
