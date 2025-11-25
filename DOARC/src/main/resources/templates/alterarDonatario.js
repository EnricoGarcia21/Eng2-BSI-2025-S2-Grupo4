document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('donatarioForm');
    const submitErrorMsg = document.getElementById('submitErrorMessage');
    const submitSuccessMsg = document.getElementById('submitSuccessMessage');

    const urlParams = new URLSearchParams(window.location.search);
    const donId = urlParams.get('id');

    // ---------------- CONFIGURAÇÃO DOS CAMPOS ----------------
    const CAMPOS_MAP = {
        donNome: { 
            id: 'don_nome', 
            jsonProp: 'don_nome',       // Tenta buscar com esse nome
            submitProp: 'don_nome',     // NOME EXATO DO JAVA @RequestParam
            required: true, 
            validate: validarNome 
        },
        donDataNasc: { 
            id: 'don_data_nasc', 
            jsonProp: 'don_data_nasc', 
            submitProp: 'don_data_nasc', // NOME EXATO DO JAVA
            required: true, 
            validate: validarDataNascimento 
        },
        donSexo: { 
            id: 'don_sexo', 
            jsonProp: 'don_sexo', 
            submitProp: 'don_sexo', 
            required: true, 
            validate: validarSelecao 
        },
        donTelefone: { 
            id: 'don_telefone', 
            jsonProp: 'don_telefone', 
            submitProp: 'don_telefone', 
            required: true, 
            validate: validarTelefone 
        },
        donEmail: { 
            id: 'don_email', 
            jsonProp: 'don_email', 
            submitProp: 'don_email', 
            required: true, 
            validate: validarEmail 
        },
        donCep: { 
            id: 'don_cep', 
            jsonProp: 'don_cep', 
            submitProp: 'don_cep', 
            required: true, 
            validate: validarCEP 
        },
        donUf: { 
            id: 'don_uf', 
            jsonProp: 'don_uf', 
            submitProp: 'don_uf', 
            required: true, 
            validate: validarUF 
        },
        donCidade: { 
            id: 'don_cidade', 
            jsonProp: 'don_cidade', 
            submitProp: 'don_cidade', 
            required: true, 
            validate: validarCidadeSemNumero 
        },
        donBairro: { 
            id: 'don_bairro', 
            jsonProp: 'don_bairro', 
            submitProp: 'don_bairro', 
            required: true, 
            validate: validarCampoTexto 
        },
        donRua: { 
            id: 'don_rua', 
            jsonProp: 'don_rua', 
            submitProp: 'don_rua', 
            required: true, 
            validate: validarCampoTexto 
        }
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
        inputElement.classList.remove('success', 'error');
        if (isValid) inputElement.classList.add('success');
        else if (inputElement.value.trim() !== '') inputElement.classList.add('error');
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
                if (value.length > 10) { 
                    value = `(${value.substring(0, 2)}) ${value.substring(2, 7)}-${value.substring(7)}`;
                } else if (value.length > 5) {
                    value = `(${value.substring(0, 2)}) ${value.substring(2, 6)}-${value.substring(6)}`;
                }
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

    // ---------------- CARREGAR DADOS (GET) ----------------
    async function carregarDadosDonatario(id) {
        try {
            const response = await fetch(`http://localhost:8080/apis/donatario/${id}`);
            const data = await response.json();

            console.log('📦 JSON do GET:', data); // Verifique no F12

            if (!response.ok) throw new Error(data.mensagem || 'Erro ao carregar dados.');

            for (const key in CAMPOS_MAP) {
                const campo = CAMPOS_MAP[key];
                const el = document.getElementById(campo.id);
                if (!el) continue;

                // 1. Tenta pelo nome exato (don_nome)
                let valor = data[campo.jsonProp];
                
                // 2. Se não achar, tenta sem o prefixo (nome)
                if (valor === undefined) valor = data[campo.jsonProp.replace('don_', '')];
                
                // 3. Fallbacks comuns
                if (valor === undefined && campo.id === 'don_data_nasc') valor = data['dataNascimento'] || data['nascimento'];
                if (valor === undefined && campo.id === 'don_rua') valor = data['logradouro'] || data['rua'];

                if (valor !== undefined && valor !== null) {
                    if (campo.id === 'don_data_nasc' && typeof valor === 'string') {
                        valor = valor.split('T')[0];
                    }
                    
                    el.value = valor;
                    
                    if (['don_telefone', 'don_cep', 'don_uf'].includes(campo.id)) {
                        aplicarMascara(campo.id);
                    }
                    atualizarFeedback(el, campo.validate(el.value));
                }
            }
        } catch (error) {
            console.error('❌ Erro GET:', error);
            const container = document.querySelector('.form-container');
            if (container) container.innerHTML = `<h1>Erro ao carregar: ${error.message}</h1>`;
        }
    }

    // ---------------- INICIALIZAR ----------------
    function inicializarCampos() {
        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            campo.element = document.getElementById(campo.id);
            campo.label = document.querySelector(`label[for="${campo.id}"]`);
            if (campo.required && campo.label) adicionarFeedbackObrigatorio(campo.label);

            if (campo.element) {
                campo.element.addEventListener('input', () => {
                    aplicarMascara(campo.id);
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
            }
        }
    }

    if (!form || !donId) return;

    const titulo = document.querySelector('h1');
    if (titulo) titulo.textContent = `Alterar Donatário (ID: ${donId})`;

    inicializarCampos();
    carregarDadosDonatario(donId);

    // ---------------- SUBMIT (PUT) ----------------
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        let formIsValid = true;

        const formData = new URLSearchParams();
        
        // !!! IMPORTANTE !!!
        // O Java espera @RequestParam int don_id
        // Não pode ser apenas 'id', tem que ser 'don_id'
        formData.append('don_id', donId);

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            if (!campo.element) continue;

            let valor = campo.element.value.trim();

            // Limpeza de Máscaras (para enviar apenas números ou formato limpo)
            switch (campo.id) {
                case 'don_uf': valor = valor.substring(0, 2).toUpperCase(); break;
                // Remove caracteres especiais de CEP e Telefone se o backend esperar apenas números/letras
                case 'don_cep': valor = valor.replace(/\D/g, '').substring(0, 8); break;
                case 'don_telefone': valor = valor.replace(/\D/g, '').substring(0, 11); break;
            }

            if (campo.required && !campo.validate(valor)) {
                formIsValid = false;
                atualizarFeedback(campo.element, false);
            } else {
                atualizarFeedback(campo.element, true);
            }

            // Usa 'submitProp' para garantir que o nome enviado é 'don_nome', 'don_rua', etc.
            formData.append(campo.submitProp, valor);
        }

        if (!formIsValid) {
            exibirMensagem('erro', 'Preencha corretamente os campos.');
            return;
        }

        try {
            // O Java @PutMapping usa Params na URL/Body form-urlencoded
            const response = await fetch('http://localhost:8080/apis/donatario', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            const result = await response.json().catch(() => ({}));

            if (response.ok) {
                exibirMensagem('sucesso', 'Donatário alterado com sucesso!');
                setTimeout(() => window.location.href = 'gerenciarDonatarios.html', 1500);
            } else {
                // Tenta mostrar mensagem de erro do backend
                exibirMensagem('erro', result.mensagem || result.erro || `Erro ${response.status}: Verifique os dados.`);
            }
        } catch (error) {
            exibirMensagem('erro', 'Erro de conexão com o servidor.');
            console.error('Erro de rede:', error);
        }
    });
});