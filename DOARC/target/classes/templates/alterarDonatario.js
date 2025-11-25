document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('donatarioForm');
    const submitErrorMsg = document.getElementById('submitErrorMessage');
    const submitSuccessMsg = document.getElementById('submitSuccessMessage');

    const urlParams = new URLSearchParams(window.location.search);
    const donId = urlParams.get('id');

    // ---------------- CONFIGURAÇÃO DOS CAMPOS ----------------
    // jsonProp: O nome provável que vem do Java/Banco de Dados
    const CAMPOS_MAP = {
        donNome: { 
            id: 'don_nome', 
            jsonProp: 'nome', 
            required: true, 
            validate: validarNome 
        },
        donDataNasc: { 
            id: 'don_data_nasc', 
            jsonProp: 'data_nasc', // Tenta também 'dataNascimento' automaticamente
            required: true, 
            validate: validarDataNascimento 
        },
        donSexo: { 
            id: 'don_sexo', 
            jsonProp: 'sexo', 
            required: true, 
            validate: validarSelecao 
        },
        donTelefone: { 
            id: 'don_telefone', 
            jsonProp: 'telefone', 
            required: true, 
            validate: validarTelefone 
        },
        donEmail: { 
            id: 'don_email', 
            jsonProp: 'email', 
            required: true, 
            validate: validarEmail 
        },
        donCep: { 
            id: 'don_cep', 
            jsonProp: 'cep', 
            required: true, 
            validate: validarCEP 
        },
        donUf: { 
            id: 'don_uf', 
            jsonProp: 'uf', // Pode ser 'estado'
            required: true, 
            validate: validarUF 
        },
        donCidade: { 
            id: 'don_cidade', 
            jsonProp: 'cidade', 
            required: true, 
            validate: validarCidadeSemNumero 
        },
        donBairro: { 
            id: 'don_bairro', 
            jsonProp: 'bairro', 
            required: true, 
            validate: validarCampoTexto 
        },
        donRua: { 
            id: 'don_rua', 
            jsonProp: 'rua', // Pode ser 'logradouro' ou 'endereco'
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
        // Validação básica: não pode ser futuro nem ano < 1900
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
                if (value.length > 10) { // Celular (XX) XXXXX-XXXX
                    value = `(${value.substring(0, 2)}) ${value.substring(2, 7)}-${value.substring(7)}`;
                } else if (value.length > 5) { // Fixo (XX) XXXX-XXXX
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

    // ---------------- CARREGAR DADOS DO DONATÁRIO ----------------
    async function carregarDadosDonatario(id) {
        try {
            const response = await fetch(`http://localhost:8080/apis/donatario/${id}`);
            const data = await response.json();

            // LOG IMPORTANTE: Verifica o que chegou do Banco no Console (F12)
            console.log('📦 Dados recebidos da API Donatário:', data);

            if (!response.ok) throw new Error(data.mensagem || 'Erro ao carregar os dados do Donatário.');

            // Preenche cada campo com o valor retornado
            for (const key in CAMPOS_MAP) {
                const campo = CAMPOS_MAP[key];
                const el = document.getElementById(campo.id);
                if (!el) continue;

                // --- LÓGICA DE BUSCA DE DADOS (FALLBACKS) ---
                // 1. Tenta pelo jsonProp definido no MAP (ex: 'nome')
                let valor = data[campo.jsonProp];
                
                // 2. Se não achou, tenta variações comuns (CamelCase, snake_case)
                if (valor === undefined) valor = data[campo.jsonProp.replace('_', '')]; // data_nasc -> datanasc
                if (valor === undefined) valor = data['don_' + campo.jsonProp]; // nome -> don_nome

                // 3. Casos Específicos comuns em Java
                if (valor === undefined && campo.id === 'don_data_nasc') valor = data['dataNascimento'] || data['nascimento'];
                if (valor === undefined && campo.id === 'don_rua') valor = data['logradouro'] || data['endereco'];
                if (valor === undefined && campo.id === 'don_uf') valor = data['estado'];

                // 4. Última tentativa: pelo ID do HTML
                if (valor === undefined) valor = data[campo.id];

                // --- PREENCHIMENTO ---
                if (valor !== undefined && valor !== null) {
                    
                    // Tratamento de Data (YYYY-MM-DD)
                    if (campo.id === 'don_data_nasc' && typeof valor === 'string') {
                        valor = valor.split('T')[0];
                    }
                    
                    el.value = valor;
                    
                    // Reaplica máscaras (CEP e Telefone) para ficarem formatados visualmente
                    if (['don_telefone', 'don_cep', 'don_uf'].includes(campo.id)) {
                        aplicarMascara(campo.id);
                    }

                    atualizarFeedback(el, campo.validate(el.value));
                } else {
                    console.warn(`⚠️ Campo ${campo.id} não encontrou valor correspondente no JSON.`);
                }
            }
        } catch (error) {
            const container = document.querySelector('.form-container');
            if (container) container.innerHTML = `<h1>Erro: ${error.message}</h1><a href="gerenciarDonatarios.html">Voltar</a>`;
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
            if (campo.required && campo.label) adicionarFeedbackObrigatorio(campo.label);

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

    inicializarCampos();
    carregarDadosDonatario(donId);

    // ---------------- SUBMIT DO FORM ----------------
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        let formIsValid = true;

        const formData = new URLSearchParams();
        formData.append('don_id', donId); // Garante que o ID vai junto para o UPDATE

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            if (!campo.element) continue;

            let valor = campo.element.value.trim();

            // Limpa máscaras antes de enviar para o banco
            switch (campo.id) {
                case 'don_uf': valor = valor.substring(0, 2).toUpperCase(); break;
                case 'don_cep': valor = valor.replace(/\D/g, '').substring(0, 8); break;
                case 'don_telefone': valor = valor.replace(/\D/g, '').substring(0, 11); break;
            }

            if (campo.required && !campo.validate(valor)) {
                formIsValid = false;
                atualizarFeedback(campo.element, false);
            } else {
                atualizarFeedback(campo.element, true);
            }

            // Envia com o nome mapeado (jsonProp) para facilitar pro Backend, ou o ID original se preferir
            // Aqui estamos mantendo o envio padrão que funcionava antes (usando o ID sem 'don_')
            // Se o backend esperar 'nome', 'cep', etc:
            formData.append(campo.jsonProp, valor);
            
            // Se o backend antigo esperava 'don_nome', descomente a linha abaixo e comente a de cima:
            // formData.append(campo.id, valor);
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