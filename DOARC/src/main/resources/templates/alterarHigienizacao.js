document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('higienizacaoForm');
    const submitErrorMsg = document.getElementById('submitErrorMessage');
    const submitSuccessMsg = document.getElementById('submitSuccessMessage');

    const urlParams = new URLSearchParams(window.location.search);
    const higId = urlParams.get('id');

    // ---------------- AUXILIARES DE MOEDA ----------------
    
    function aplicarMascaraMoeda(valor) {
        if (!valor) return "";
        let valorStr = valor.toString().replace(/\D/g, "");
        if (valorStr === "") return "";
        const numero = parseFloat(valorStr) / 100;
        return numero.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
    }

    function limparValorMoeda(valorFormatado) {
        if (!valorFormatado) return "";
        // Remove R$, espaços e pontos, troca vírgula por ponto
        return valorFormatado.replace(/[^\d,]/g, '').replace(',', '.');
    }

    // ---------------- CONFIGURAÇÃO DOS CAMPOS ----------------
    const CAMPOS_MAP = {
        higDataAgendada: { 
            id: 'hig_data_agendada', 
            jsonProp: 'data_agendada', 
            required: true, 
            validate: validarDataFuturaOuHoje 
        },
        higHora: { 
            id: 'hig_hora', 
            jsonProp: 'hora', 
            required: true, 
            validate: validarHora 
        },
        higLocal: { 
            id: 'hig_local', 
            jsonProp: 'local', 
            required: true, 
            validate: validarCampoTexto 
        },
        higDescricaoRoupa: { 
            id: 'hig_descricao_roupa', 
            jsonProp: 'descricao_roupa', 
            required: true, 
            validate: validarCampoTexto 
        },
        higValorPago: { 
            id: 'hig_valorpago', 
            jsonProp: 'valor_pago', 
            required: true, 
            validate: validarValorMoeda, 
            isMoeda: true // Flag importante
        },
        volId: { 
            id: 'vol_id', 
            jsonProp: 'vol_id', 
            required: true, 
            validate: validarIdNumerico 
        },
    };

    // ---------------- VALIDADORES ----------------
    function validarCampoTexto(valor) { return valor && valor.trim().length >= 3; }
    function validarHora(valor) { return /^\d{2}:\d{2}$/.test(valor); } 
    function validarIdNumerico(valor) { return !isNaN(parseFloat(valor)) && isFinite(valor) && parseInt(valor) > 0; }
    
    function validarValorMoeda(valorComMascara) {
        const valorLimpo = limparValorMoeda(valorComMascara);
        return !isNaN(parseFloat(valorLimpo)) && isFinite(valorLimpo) && parseFloat(valorLimpo) >= 0;
    }

    function validarDataFuturaOuHoje(valor) {
        if (!valor) return false;
        const dataInput = new Date(valor);
        return !(isNaN(dataInput));
    }

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

    // ---------------- CARREGAR DADOS DO REGISTRO ----------------
    async function carregarDadosHigienizacao(id) {
        try {
            const response = await fetch(`http://localhost:8080/apis/higienizacao/${id}`);
            const data = await response.json();
            
            console.log("Dados do Backend:", data);

            if (!response.ok) throw new Error(data.mensagem || 'Erro ao carregar dados.');

            for (const key in CAMPOS_MAP) {
                const campo = CAMPOS_MAP[key];
                const el = document.getElementById(campo.id);
                if (!el) continue;

                let valor = data[campo.jsonProp];
                if (valor === undefined) valor = data[campo.id];
                
                // Tenta achar o valor em outros nomes se for moeda
                if (campo.isMoeda && (valor === undefined || valor === null)) {
                    valor = data['valor_pago'] || data['valorPago'] || data['valor'];
                }

                if (valor !== undefined && valor !== null) {
                    if (campo.id === 'hig_data_agendada' && typeof valor === 'string') {
                        valor = valor.split('T')[0];
                    }
                    
                    // --- CORREÇÃO DO ERRO ---
                    if (campo.isMoeda) {
                        // FORÇA O CAMPO A SER TEXTO para aceitar o R$
                        el.type = 'text'; 
                        el.value = parseFloat(valor).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
                    } else {
                        el.value = valor;
                    }

                    atualizarFeedback(el, campo.validate(el.value));
                }
            }
            
        } catch (error) {
            const container = document.querySelector('.form-container');
            if (container) container.innerHTML = `<h1>Erro: ${error.message}</h1><br><a href="consultaHigienizacao.html">Voltar</a>`;
            console.error('❌ Erro:', error);
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
                // Se for moeda, garante que é texto desde o início
                if (campo.isMoeda) campo.element.type = 'text';

                campo.element.addEventListener('input', (e) => {
                    if (campo.isMoeda) {
                        e.target.value = aplicarMascaraMoeda(e.target.value);
                    }
                    atualizarFeedback(campo.element, campo.validate(campo.element.value));
                });
            }
        }
    }

    if (!form || !higId) return;

    const titulo = document.querySelector('h1');
    if (titulo) titulo.textContent = `Alterar Registro (ID: ${higId})`;

    inicializarCampos();
    carregarDadosHigienizacao(higId);

    // ---------------- SUBMIT ----------------
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        let formIsValid = true;

        const formData = new URLSearchParams();
        formData.append('id', higId);

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            if (!campo.element) continue;

            let valorVisual = campo.element.value.trim();
            
            if (campo.required && !campo.validate(valorVisual)) {
                formIsValid = false;
                atualizarFeedback(campo.element, false);
            } else {
                atualizarFeedback(campo.element, true);
            }

            let valorParaEnvio = valorVisual;
            if (campo.isMoeda) {
                valorParaEnvio = limparValorMoeda(valorVisual);
            }

            formData.append(campo.jsonProp, valorParaEnvio);
        }

        if (!formIsValid) {
            exibirMensagem('erro', 'Verifique os campos em vermelho.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/apis/higienizacao', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });

            if (response.ok) {
                exibirMensagem('sucesso', 'Salvo com sucesso!');
                setTimeout(() => window.location.href = 'consultaHigienizacao.html', 1500);
            } else {
                const result = await response.json().catch(() => ({}));
                exibirMensagem('erro', result.mensagem || 'Erro ao salvar.');
            }
        } catch (error) {
            exibirMensagem('erro', 'Erro de conexão.');
        }
    });
});