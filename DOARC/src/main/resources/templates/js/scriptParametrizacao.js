// JavaScript completo para o formulário de cadastro de empresa

// === ADICIONE ESTAS NOVAS FUNÇÕES AQUI ===

// Função para verificar se existe empresa cadastrada
async function verificarEmpresaCadastrada() {
    try {
        const response = await fetch('http://localhost:8080/apis/parametrizacao/1');
        if (response.ok) {
            const empresa = await response.json();
            return empresa;
        } else if (response.status === 400) {
            return null; // Não existe empresa cadastrada
        }
        return null;
    } catch (error) {
        console.error('Erro ao verificar empresa:', error);
        return null;
    }
}

// Função para aplicar máscaras nos dados carregados
function aplicarMascarasDadosCarregados() {
    // Máscara para CNPJ
    const cnpjInput = document.getElementById('cnpj');
    if (cnpjInput.value) {
        let cnpjValue = cnpjInput.value.replace(/\D/g, '');
        if (cnpjValue.length === 14) {
            cnpjValue = cnpjValue.replace(/(\d{2})(\d)/, '$1.$2');
            cnpjValue = cnpjValue.replace(/(\d{3})(\d)/, '$1.$2');
            cnpjValue = cnpjValue.replace(/(\d{3})(\d)/, '$1/$2');
            cnpjValue = cnpjValue.replace(/(\d{4})(\d{1,2})$/, '$1-$2');
            cnpjInput.value = cnpjValue;
        }
    }

    // Máscara para CEP
    const cepInput = document.getElementById('cep');
    if (cepInput.value) {
        let cepValue = cepInput.value.replace(/\D/g, '');
        if (cepValue.length === 8) {
            cepValue = cepValue.replace(/(\d{5})(\d{1,3})$/, '$1-$2');
            cepInput.value = cepValue;
        }
    }

    // Máscara para telefone
    const telefoneInput = document.getElementById('telefone');
    if (telefoneInput.value) {
        let telefoneValue = telefoneInput.value.replace(/\D/g, '');
        if (telefoneValue.length === 10) {
            telefoneValue = telefoneValue.replace(/(\d{2})(\d{4})(\d{4})$/, '($1) $2-$3');
            telefoneInput.value = telefoneValue;
        } else if (telefoneValue.length === 11) {
            telefoneValue = telefoneValue.replace(/(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
            telefoneInput.value = telefoneValue;
        }
    }
}

// Função para carregar dados da empresa no formulário
// Função para carregar dados da empresa no formulário
function carregarDadosEmpresa(empresa) {
    document.getElementById('razaoSocial').value = empresa.razaoSocial || '';
    document.getElementById('nomeFantasia').value = empresa.nomeFantasia || '';
    document.getElementById('cnpj').value = empresa.cnpj || '';
    document.getElementById('rua').value = empresa.rua || '';
    document.getElementById('numero').value = empresa.numero || '';
    document.getElementById('bairro').value = empresa.bairro || '';
    document.getElementById('cidade').value = empresa.cidade || '';
    document.getElementById('uf').value = empresa.uf || '';
    document.getElementById('cep').value = empresa.cep || '';
    document.getElementById('telefone').value = empresa.telefone || '';
    document.getElementById('email').value = empresa.email || '';
    document.getElementById('site').value = empresa.site || '';

    // Aplicar máscaras nos dados carregados
    aplicarMascarasDadosCarregados();

    // Mudar o texto do botão para "Alterar"
    const btnSubmit = document.querySelector('button[type="submit"]');
    btnSubmit.innerHTML = '<i class="fas fa-edit me-2"></i>Alterar';
    btnSubmit.classList.remove('btn-primary');
    btnSubmit.classList.add('btn-warning');
}

// Função para carregar dados para edição
async function carregarDadosParaEdicao() {
    const empresa = await verificarEmpresaCadastrada();
    if (empresa) {
        carregarDadosEmpresa(empresa);
    }
}

// Função para abrir tela de parametrização
function abrirParametrizacao() {
    // Fechar sidebar
    toggleSidebar();

    // Já estamos na página de parametrização, então apenas recarregamos os dados
    carregarDadosParaEdicao();

    // Rolagem suave para o topo do formulário
    document.getElementById('content').scrollTo({ top: 0, behavior: 'smooth' });
}

// Função para carregar logo da empresa
function carregarLogoEmpresa() {
    // Adicione um timestamp para evitar cache
    const logoUrl = `http://localhost:8080/img/logo.png?t=${new Date().getTime()}`;
    const logoPreview = document.getElementById('logoPreview');

    // Tenta carregar a logo
    logoPreview.src = logoUrl;
    logoPreview.onload = function() {
        logoPreview.classList.remove('d-none');
    };
    logoPreview.onerror = function() {
        logoPreview.classList.add('d-none');
    };
}

// === FIM DAS NOVAS FUNÇÕES ===


// Função para alternar a visibilidade da sidebar
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('overlay');
    const content = document.getElementById('content');
    const isOpen = sidebar.style.left === '0px';

    if (isOpen) {
        sidebar.style.left = '-250px';
        overlay.style.display = 'none';
        content.style.marginLeft = '0';
    } else {
        sidebar.style.left = '0px';
        overlay.style.display = 'block';
        content.style.marginLeft = '250px';
    }
}

// Adicionar event listener ao ícone do menu (fa-bars) para abrir/fechar sidebar
// ATUALIZE o DOMContentLoaded para ficar assim:
document.addEventListener('DOMContentLoaded', function() {
    const menuIcon = document.querySelector('.sidebar h4 i');
    if (menuIcon) {
        menuIcon.addEventListener('click', toggleSidebar);
    }

    // Fechar sidebar ao clicar no overlay
    const overlay = document.getElementById('overlay');
    overlay.addEventListener('click', toggleSidebar);

    // Inicializar máscaras e validações nos campos
    initMasksAndValidations();

    // === NOVAS LINHAS ADICIONADAS ===
    // Verificar se existe empresa e carregar dados
    carregarDadosParaEdicao();

    // Carregar logo da empresa
    carregarLogoEmpresa();
});

// Função para inicializar máscaras e validações nos campos
// Função para inicializar máscaras e validações nos campos
function initMasksAndValidations() {
    // Campos que só aceitam números com limites de caracteres
    const numericFields = [
        { id: 'cnpj', maxLength: 14 },
        { id: 'cep', maxLength: 8 },
        { id: 'telefone', maxLength: 11 },
        { id: 'numero', maxLength: 5 } // Limite arbitrário para número da casa
    ];
    numericFields.forEach(field => {
        const input = document.getElementById(field.id);
        input.addEventListener('input', function(e) {
            // Remover qualquer caractere que não seja número e limitar comprimento
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > field.maxLength) {
                value = value.substring(0, field.maxLength);
            }
            e.target.value = value;
        });
    });

    // Máscara para CNPJ (versão melhorada)
    const cnpjInput = document.getElementById('cnpj');
    cnpjInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');

        // Limitar a 14 caracteres
        if (value.length > 14) {
            value = value.substring(0, 14);
        }

        // Aplicar máscara progressivamente
        if (value.length <= 2) {
            value = value;
        } else if (value.length <= 5) {
            value = value.replace(/(\d{2})(\d)/, '$1.$2');
        } else if (value.length <= 8) {
            value = value.replace(/(\d{2})(\d{3})(\d)/, '$1.$2.$3');
        } else if (value.length <= 12) {
            value = value.replace(/(\d{2})(\d{3})(\d{3})(\d)/, '$1.$2.$3/$4');
        } else {
            value = value.replace(/(\d{2})(\d{3})(\d{3})(\d{4})(\d)/, '$1.$2.$3/$4-$5');
        }

        e.target.value = value;
    });

    // Máscara para CEP (versão melhorada)
    const cepInput = document.getElementById('cep');
    cepInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');

        // Limitar a 8 caracteres
        if (value.length > 8) {
            value = value.substring(0, 8);
        }

        // Aplicar máscara
        if (value.length > 5) {
            value = value.replace(/(\d{5})(\d)/, '$1-$2');
        }

        e.target.value = value;
    });

    // Máscara para telefone (versão melhorada)
    const telefoneInput = document.getElementById('telefone');
    telefoneInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');

        // Limitar a 11 caracteres
        if (value.length > 11) {
            value = value.substring(0, 11);
        }

        // Aplicar máscara progressivamente
        if (value.length <= 2) {
            value = value;
        } else if (value.length <= 6) {
            value = value.replace(/(\d{2})(\d)/, '($1) $2');
        } else if (value.length <= 10) {
            value = value.replace(/(\d{2})(\d{4})(\d)/, '($1) $2-$3');
        } else {
            value = value.replace(/(\d{2})(\d{5})(\d)/, '($1) $2-$3');
        }

        e.target.value = value;
    });

    // Validações em tempo real para campos obrigatórios
    const requiredFields = ['razaoSocial', 'nomeFantasia', 'cnpj', 'rua', 'numero', 'bairro', 'cidade', 'uf', 'cep', 'telefone', 'email'];

    requiredFields.forEach(id => {
        const input = document.getElementById(id);
        input.addEventListener('blur', function() {
            validateField(input);
        });
        input.addEventListener('input', function() {
            if (input.classList.contains('is-invalid')) {
                validateField(input); // Revalidar ao digitar
            }
        });
    });

    // Validação específica para email
    const emailInput = document.getElementById('email');
    emailInput.addEventListener('blur', function() {
        const value = this.value.trim();
        const feedback = document.getElementById('emailFeedback');

        if (value === '') {
            this.classList.add('is-invalid');
            feedback.innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>Este campo é obrigatório.';
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
            this.classList.add('is-invalid');
            feedback.innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>Email inválido.';
        } else {
            this.classList.remove('is-invalid');
            feedback.innerHTML = '';
        }
    });

    // Validação específica para UF (garantir que foi selecionada)
    const ufInput = document.getElementById('uf');
    ufInput.addEventListener('change', function() {
        validateField(this);
    });
}

// Função para validar um campo individual
function validateField(input) {
    const value = input.value.trim();
    const feedbackId = input.id + 'Feedback';
    let feedbackDiv = document.getElementById(feedbackId);

    // Se não existir div de feedback, criar uma
    if (!feedbackDiv) {
        feedbackDiv = document.createElement('div');
        feedbackDiv.id = feedbackId;
        feedbackDiv.className = 'form-error';
        input.parentNode.appendChild(feedbackDiv);
    }

    if (value === '') {
        input.classList.add('is-invalid');
        feedbackDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>Este campo é obrigatório.';
    } else {
        input.classList.remove('is-invalid');
        feedbackDiv.innerHTML = '';
    }
}

// Função para validar CNPJ
function validarCNPJ(cnpj) {
    cnpj = cnpj.replace(/[^\d]+/g, '');
    if (cnpj.length !== 14) return false;
    if (/^(\d)\1+$/.test(cnpj)) return false; // CNPJ com todos dígitos iguais

    let tamanho = cnpj.length - 2;
    let numeros = cnpj.substring(0, tamanho);
    let digitos = cnpj.substring(tamanho);
    let soma = 0;
    let pos = tamanho - 7;

    for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) pos = 9;
    }
    let resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
    if (resultado != digitos.charAt(0)) return false;

    tamanho = tamanho + 1;
    numeros = cnpj.substring(0, tamanho);
    soma = 0;
    pos = tamanho - 7;
    for (let i = tamanho; i >= 1; i--) {
        soma += numeros.charAt(tamanho - i) * pos--;
        if (pos < 2) pos = 9;
    }
    resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
    if (resultado != digitos.charAt(1)) return false;

    return true;
}

// Função para prévia da logo
function previewLogo(input) {
    const logoPreview = document.getElementById('logoPreview');
    if (input.files && input.files[0]) {
        const file = input.files[0];
        // Verificar tamanho (máximo 2MB)
        if (file.size > 2 * 1024 * 1024) {
            alert('Arquivo muito grande. Tamanho máximo: 2MB');
            input.value = '';
            logoPreview.classList.add('d-none');
            return;
        }
        // Verificar tipo
        if (!file.type.match('image.*')) {
            alert('Por favor, selecione uma imagem válida.');
            input.value = '';
            logoPreview.classList.add('d-none');
            return;
        }
        const reader = new FileReader();
        reader.onload = function(e) {
            logoPreview.src = e.target.result;
            logoPreview.classList.remove('d-none');
        };
        reader.readAsDataURL(file);
    } else {
        logoPreview.classList.add('d-none');
    }
}

// Função para enviar o formulário
// === SUBSTITUA esta função enviarFormulario pela versão abaixo ===

// Função para enviar o formulário (AGORA SUPORTA POST E PUT)
async function enviarFormulario(event) {
    event.preventDefault();

    const form = document.getElementById('formEmpresa');
    const formData = new FormData(form);
    const empresaExistente = await verificarEmpresaCadastrada();

    // DEBUG: Verificar todos os campos do FormData
    console.log('=== DADOS DO FORMULÁRIO ===');
    for (let [key, value] of formData.entries()) {
        console.log(key + ': ' + value);
    }

    // Validações adicionais
    let isValid = true;
    const requiredFields = ['razaoSocial', 'nomeFantasia', 'cnpj', 'rua', 'numero', 'bairro', 'cidade', 'uf', 'cep', 'telefone', 'email'];

    // Validar todos os campos obrigatórios
    requiredFields.forEach(id => {
        const input = document.getElementById(id);
        validateField(input);
        if (input.classList.contains('is-invalid')) {
            isValid = false;
        }
    });

    const cnpj = formData.get('cnpj').replace(/\D/g, '');
    if (!validarCNPJ(cnpj)) {
        document.getElementById('cnpjFeedback').innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>CNPJ inválido.';
        document.getElementById('cnpj').classList.add('is-invalid');
        isValid = false;
    }

    const cep = formData.get('cep').replace(/\D/g, '');
    if (cep.length !== 8) {
        document.getElementById('cepFeedback').innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>CEP deve ter 8 dígitos.';
        document.getElementById('cep').classList.add('is-invalid');
        isValid = false;
    }

    const telefone = formData.get('telefone').replace(/\D/g, '');
    if (telefone.length < 10 || telefone.length > 11) {
        document.getElementById('telefoneFeedback').innerHTML = '<i class="fas fa-exclamation-triangle me-1"></i>Telefone inválido.';
        document.getElementById('telefone').classList.add('is-invalid');
        isValid = false;
    }

    if (!isValid) {
        console.log('Formulário inválido, não enviando...');
        return false;
    }

    const url = 'http://localhost:8080/apis/parametrizacao';
    const method = empresaExistente ? 'PUT' : 'POST';

    // Se for edição, adicionar o ID ao FormData
    if (empresaExistente && method === 'PUT') {
        formData.append('id', empresaExistente.id);
    }

    console.log('Enviando para:', url, 'Método:', method);

    try {
        const response = await fetch(url, {
            method: method,
            body: formData
        });

        console.log('Status da resposta:', response.status);

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`Erro HTTP! status: ${response.status} - ${errorText}`);
        }

        const data = await response.json();
        const mensagem = method === 'POST'
            ? 'Empresa cadastrada com sucesso!'
            : 'Empresa alterada com sucesso!';

        alert(mensagem);

        // Se era um cadastro novo, mudar para modo edição
        if (method === 'POST') {
            carregarDadosParaEdicao();
        }

        // Recarregar a logo após salvar
        carregarLogoEmpresa();

    } catch (error) {
        console.error('Erro completo:', error);
        alert(`Erro ao ${method === 'POST' ? 'cadastrar' : 'alterar'} empresa: ${error.message}`);
    }

    return false;
}