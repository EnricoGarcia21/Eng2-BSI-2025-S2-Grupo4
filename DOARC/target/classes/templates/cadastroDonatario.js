document.addEventListener("DOMContentLoaded", () => {
    const donatarioForm = document.getElementById("donatarioForm");
    const submitErrorMessage = document.getElementById("submitErrorMessage");
    const submitSuccessMessage = document.getElementById("submitSuccessMessage");

    const CAMPOS_MAP = {
        nome: {
            id: 'don_nome',
            required: true,
            validate: validarNome,
            exemplo: 'Exemplo: João da Silva'
        },
        data_nasc: {
            id: 'don_data_nasc',
            required: true,
            validate: validarDataNascimento,
            exemplo: 'Exemplo: 01/01/2000'
        },
        sexo: {
            id: 'don_sexo',
            required: true,
            validate: validarSelecao,
            exemplo: 'Exemplo: Masculino ou Feminino'
        },
        telefone: {
            id: 'don_telefone',
            required: true,
            validate: validarCampoTexto,
            exemplo: 'Exemplo: (11) 91234-5678',
            mascara: aplicarMascaraTelefone
        },
        email: {
            id: 'don_email',
            required: true,
            validate: validarEmail,
            exemplo: 'Exemplo: exemplo@dominio.com'
        },
        cep: {
            id: 'don_cep',
            required: true,
            validate: validarCampoTexto,
            exemplo: 'Exemplo: 12345-678',
            mascara: aplicarMascaraCEP
        },
        uf: {
            id: 'don_uf',
            required: true,
            validate: validarUF,
            exemplo: 'Exemplo: SP'
        },
        cidade: {
            id: 'don_cidade',
            required: true,
            validate: validarCidade,
            exemplo: 'Exemplo: São Paulo'
        },
        bairro: {
            id: 'don_bairro',
            required: true,
            validate: validarCampoTexto,
            exemplo: 'Exemplo: Centro'
        },
        rua: {
            id: 'don_rua',
            required: true,
            validate: validarCampoTexto,
            exemplo: 'Exemplo: Rua das Flores, 123'
        }
    };

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

    function inicializarCampos() {
        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            campo.element = document.getElementById(campo.id);
            if (campo.mascara) {
                campo.element.addEventListener('input', campo.mascara);
            }

            // Adiciona asterisco para campos obrigatórios
            if (campo.required) {
                const label = document.querySelector(`label[for=${campo.id}]`);
                if (label) label.innerHTML = `${label.innerHTML} <span style="color: red;">*</span>`;
            }
        }
    }

    function validarNome(valor) {
        return /^[a-zA-Z\s]+$/.test(valor.trim()) && valor.trim().length >= 3;
    }

    function validarDataNascimento(valor) {
        const data = new Date(valor);
        return valor.trim().length > 0 && data <= new Date();
    }

    function validarSelecao(valor) {
        return valor !== '' && valor !== null;
    }

    function validarCampoTexto(valor) {
        return valor.trim().length > 0;
    }

    function validarEmail(valor) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor);
    }

    function validarUF(valor) {
        return /^[A-Za-z]{2}$/.test(valor.trim());
    }

    function validarCidade(valor) {
        return /^[a-zA-Z\s]+$/.test(valor.trim());
    }

    function aplicarMascaraTelefone(event) {
        const valor = event.target.value.replace(/\D/g, '').slice(0, 11); // Retira caracteres não numéricos
        event.target.value = valor.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3'); // Máscara
    }

    function aplicarMascaraCEP(event) {
        const valor = event.target.value.replace(/\D/g, '').slice(0, 8); // Retira caracteres não numéricos
        event.target.value = valor.replace(/^(\d{5})(\d{3})$/, '$1-$2'); // Máscara
    }

    donatarioForm.addEventListener('submit', async (event) => {
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
            
            if (!campoValido && campo.required) formValido = false;

            // Exibe mensagens de erro ou sucesso nos campos
            if (!campoValido) {
                campoElement.classList.add('error');
                campoElement.classList.remove('success');
                campoElement.title = `Exemplo: ${campo.exemplo}`;
                mostrarExemplo(campoElement, campo.exemplo);
            } else {
                campoElement.classList.add('success');
                campoElement.classList.remove('error');
                campoElement.title = '';
                esconderExemplo(campoElement);
            }

            // Ajustes para backend:
            if (key === 'telefone') valor = valor.replace(/\D/g, ''); // Enviar sem máscara
            if (key === 'cep') valor = valor.replace(/\D/g, ''); // Enviar sem máscara
            if (key === 'uf') valor = valor.substring(0, 2).toUpperCase(); // Limita UF a 2 caracteres

            params.append(campo.id, valor);
        }

        if (!formValido) {
            exibirMensagem('erro', 'Preencha todos os campos obrigatórios corretamente.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/apis/donatario', {
                method: 'POST',
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: params.toString()
            });

            const result = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(result.erro || result.mensagem || `Erro HTTP: ${response.status}`);

            exibirMensagem('sucesso', result.mensagem || 'Donatário cadastrado com sucesso!');
            donatarioForm.reset();
        } catch (error) {
            console.error("Erro ao enviar Donatário:", error);
            exibirMensagem('erro', `Erro ao cadastrar Donatário: ${error.message || "Erro desconhecido."}`);
        }
    });

    function mostrarExemplo(element, exemplo) {
        const exemploDiv = document.createElement('div');
        exemploDiv.classList.add('exemplo-message');
        exemploDiv.style.color = 'red';
        exemploDiv.textContent = `Exemplo: ${exemplo}`;
        element.parentElement.appendChild(exemploDiv);
    }

    function esconderExemplo(element) {
        const exemploDiv = element.parentElement.querySelector('.exemplo-message');
        if (exemploDiv) {
            exemploDiv.remove();
        }
    }

    inicializarCampos();
});
