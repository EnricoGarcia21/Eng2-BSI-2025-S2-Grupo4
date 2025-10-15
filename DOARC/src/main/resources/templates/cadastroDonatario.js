document.addEventListener("DOMContentLoaded", () => {
    const donatarioForm = document.getElementById("donatarioForm");
    const submitErrorMessage = document.getElementById("submitErrorMessage");
    const submitSuccessMessage = document.getElementById("submitSuccessMessage");

    const CAMPOS_MAP = {
        nome: { id: 'don_nome', required: true, validate: validarNome },
        data_nasc: { id: 'don_data_nasc', required: true, validate: validarDataNascimento },
        sexo: { id: 'don_sexo', required: true, validate: validarSelecao },
        telefone: { id: 'don_telefone', required: true, validate: validarCampoTexto },
        email: { id: 'don_email', required: true, validate: validarEmail },
        cep: { id: 'don_cep', required: true, validate: validarCampoTexto },
        uf: { id: 'don_uf', required: true, validate: validarCampoTexto },
        cidade: { id: 'don_cidade', required: true, validate: validarCampoTexto },
        bairro: { id: 'don_bairro', required: true, validate: validarCampoTexto },
        rua: { id: 'don_rua', required: true, validate: validarCampoTexto }
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
        }
    }

    function validarNome(valor) { return valor.trim().length >= 3; }
    function validarDataNascimento(valor) { return valor.trim().length > 0; }
    function validarSelecao(valor) { return valor !== '' && valor !== null; }
    function validarCampoTexto(valor) { return valor.trim().length > 0; }
    function validarEmail(valor) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(valor); }

    donatarioForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        hideMessage(submitErrorMessage);
        hideMessage(submitSuccessMessage);

        let formValido = true;
        const params = new URLSearchParams();

        for (const key in CAMPOS_MAP) {
            const campo = CAMPOS_MAP[key];
            let valor = campo.element.value.trim();
            if (!campo.validate(valor) && campo.required) formValido = false;

            // Ajustes para backend:
            if (key === 'cep') valor = valor.replace(/\D/g, ''); // remove hífen
            if (key === 'uf') valor = valor.substring(0, 2).toUpperCase(); // limita UF a 2 caracteres

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

    inicializarCampos();
});
