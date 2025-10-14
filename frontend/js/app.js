// ===== DOARC - Sistema de Gestão de Doações =====
// JavaScript Principal

// ===== Configurações Globais =====
const API_URL = 'http://localhost:8080/api'; // URL do backend Spring Boot
const STORAGE_KEY = 'doarc_user';

// ===== Funções de Autenticação =====
const Auth = {
    // Verifica se usuário está logado
    isAuthenticated() {
        const user = localStorage.getItem(STORAGE_KEY);
        return user !== null;
    },

    // Obtém dados do usuário logado
    getUser() {
        const user = localStorage.getItem(STORAGE_KEY);
        return user ? JSON.parse(user) : null;
    },

    // Salva dados do usuário
    setUser(userData) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(userData));
    },

    // Remove dados do usuário (logout)
    logout() {
        localStorage.removeItem(STORAGE_KEY);
        window.location.href = '../index.html';
    },

    // Verifica autenticação em páginas protegidas
    checkAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = '../index.html';
        }
    },

    // Atualiza informações do usuário no header
    updateUserInfo() {
        const user = this.getUser();
        if (user) {
            const userNameElement = document.getElementById('userName');
            if (userNameElement) {
                userNameElement.textContent = user.nome || 'Voluntário';
            }
        }
    }
};

// ===== Funções de Validação =====
const Validator = {
    // Valida se campo está vazio
    isEmpty(value) {
        return !value || value.trim() === '';
    },

    // Valida email
    isValidEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },

    // Valida CPF
    isValidCPF(cpf) {
        cpf = cpf.replace(/[^\d]/g, '');
        if (cpf.length !== 11) return false;

        // Verifica se todos os dígitos são iguais
        if (/^(\d)\1+$/.test(cpf)) return false;

        // Validação dos dígitos verificadores
        let sum = 0;
        for (let i = 0; i < 9; i++) {
            sum += parseInt(cpf.charAt(i)) * (10 - i);
        }
        let digit = 11 - (sum % 11);
        if (digit >= 10) digit = 0;
        if (digit !== parseInt(cpf.charAt(9))) return false;

        sum = 0;
        for (let i = 0; i < 10; i++) {
            sum += parseInt(cpf.charAt(i)) * (11 - i);
        }
        digit = 11 - (sum % 11);
        if (digit >= 10) digit = 0;
        if (digit !== parseInt(cpf.charAt(10))) return false;

        return true;
    },

    // Valida telefone
    isValidPhone(phone) {
        const cleaned = phone.replace(/[^\d]/g, '');
        return cleaned.length === 10 || cleaned.length === 11;
    },

    // Valida data
    isValidDate(date) {
        const dateObj = new Date(date);
        return dateObj instanceof Date && !isNaN(dateObj);
    },

    // Valida se data não é no passado
    isNotPastDate(date) {
        const dateObj = new Date(date);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return dateObj >= today;
    },

    // Valida número positivo
    isPositiveNumber(value) {
        const num = parseFloat(value);
        return !isNaN(num) && num > 0;
    },

    // Mostra erro em campo
    showError(input, message) {
        input.classList.add('error');
        let errorDiv = input.parentElement.querySelector('.form-error');
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'form-error';
            input.parentElement.appendChild(errorDiv);
        }
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    },

    // Remove erro de campo
    clearError(input) {
        input.classList.remove('error');
        const errorDiv = input.parentElement.querySelector('.form-error');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    },

    // Limpa todos os erros do formulário
    clearAllErrors(form) {
        const inputs = form.querySelectorAll('.form-control');
        inputs.forEach(input => this.clearError(input));
    }
};

// ===== Funções de Formatação =====
const Formatter = {
    // Formata CPF
    formatCPF(cpf) {
        cpf = cpf.replace(/[^\d]/g, '');
        return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    },

    // Formata telefone
    formatPhone(phone) {
        phone = phone.replace(/[^\d]/g, '');
        if (phone.length === 11) {
            return phone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
        }
        return phone.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    },

    // Formata data para exibição
    formatDate(date) {
        if (!date) return '';
        const d = new Date(date);
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        return `${day}/${month}/${year}`;
    },

    // Formata data para input
    formatDateInput(date) {
        if (!date) return '';
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    },

    // Formata moeda
    formatCurrency(value) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value);
    }
};

// ===== Funções de UI =====
const UI = {
    // Mostra mensagem de alerta
    showAlert(message, type = 'info') {
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type}`;
        alertDiv.innerHTML = `
            <span>${message}</span>
            <button onclick="this.parentElement.remove()" style="margin-left: auto; background: none; border: none; cursor: pointer;">&times;</button>
        `;
        alertDiv.style.display = 'flex';

        const container = document.querySelector('.container') || document.querySelector('.container-fluid');
        if (container) {
            container.insertBefore(alertDiv, container.firstChild);

            // Remove automaticamente após 5 segundos
            setTimeout(() => {
                alertDiv.remove();
            }, 5000);
        }
    },

    // Mostra loading
    showLoading() {
        const overlay = document.createElement('div');
        overlay.className = 'loading-overlay active';
        overlay.id = 'loadingOverlay';
        overlay.innerHTML = '<div class="spinner"></div>';
        document.body.appendChild(overlay);
    },

    // Esconde loading
    hideLoading() {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.remove();
        }
    },

    // Abre modal
    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('active');
        }
    },

    // Fecha modal
    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
        }
    },

    // Confirma ação
    confirm(message, callback) {
        if (window.confirm(message)) {
            callback();
        }
    }
};

// ===== Funções de API =====
const API = {
    // Requisição GET
    async get(endpoint) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            UI.hideLoading();

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição GET:', error);
            UI.showAlert('Erro ao buscar dados. Tente novamente.', 'danger');
            throw error;
        }
    },

    // Requisição POST
    async post(endpoint, data) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            UI.hideLoading();

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição POST:', error);
            UI.showAlert('Erro ao salvar dados. Tente novamente.', 'danger');
            throw error;
        }
    },

    // Requisição PUT
    async put(endpoint, data) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });
            UI.hideLoading();

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição PUT:', error);
            UI.showAlert('Erro ao atualizar dados. Tente novamente.', 'danger');
            throw error;
        }
    },

    // Requisição DELETE
    async delete(endpoint) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            UI.hideLoading();

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return true;
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição DELETE:', error);
            UI.showAlert('Erro ao excluir dados. Tente novamente.', 'danger');
            throw error;
        }
    }
};

// ===== Máscaras de Input =====
const Masks = {
    // Aplica máscara de CPF
    cpf(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d]/g, '');
            if (value.length > 11) value = value.slice(0, 11);

            if (value.length > 9) {
                value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
            } else if (value.length > 6) {
                value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
            } else if (value.length > 3) {
                value = value.replace(/(\d{3})(\d{1,3})/, '$1.$2');
            }

            e.target.value = value;
        });
    },

    // Aplica máscara de telefone
    phone(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d]/g, '');
            if (value.length > 11) value = value.slice(0, 11);

            if (value.length > 10) {
                value = value.replace(/(\d{2})(\d{5})(\d{1,4})/, '($1) $2-$3');
            } else if (value.length > 6) {
                value = value.replace(/(\d{2})(\d{4})(\d{1,4})/, '($1) $2-$3');
            } else if (value.length > 2) {
                value = value.replace(/(\d{2})(\d{1,5})/, '($1) $2');
            }

            e.target.value = value;
        });
    },

    // Aplica máscara de CEP
    cep(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d]/g, '');
            if (value.length > 8) value = value.slice(0, 8);

            if (value.length > 5) {
                value = value.replace(/(\d{5})(\d{1,3})/, '$1-$2');
            }

            e.target.value = value;
        });
    },

    // Aplica máscara de moeda
    currency(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d]/g, '');
            value = (parseFloat(value) / 100).toFixed(2);
            e.target.value = value;
        });
    }
};

// ===== Inicialização =====
document.addEventListener('DOMContentLoaded', function() {
    // Verifica autenticação em páginas protegidas (exceto login e registro)
    if (!window.location.pathname.includes('index.html') &&
        !window.location.pathname.includes('register.html') &&
        !window.location.pathname.endsWith('/')) {
        Auth.checkAuth();
        Auth.updateUserInfo();
    }

    // Adiciona evento de logout
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            UI.confirm('Deseja realmente sair do sistema?', function() {
                Auth.logout();
            });
        });
    }

    // Fecha modais ao clicar fora
    window.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal')) {
            e.target.classList.remove('active');
        }
    });

    // Aplica máscaras automaticamente
    document.querySelectorAll('[data-mask="cpf"]').forEach(input => Masks.cpf(input));
    document.querySelectorAll('[data-mask="phone"]').forEach(input => Masks.phone(input));
    document.querySelectorAll('[data-mask="cep"]').forEach(input => Masks.cep(input));
    document.querySelectorAll('[data-mask="currency"]').forEach(input => Masks.currency(input));

    // Remove erros ao digitar
    document.querySelectorAll('.form-control').forEach(input => {
        input.addEventListener('input', function() {
            Validator.clearError(this);
        });
    });
});

// ===== Exporta funções globais =====
window.Auth = Auth;
window.Validator = Validator;
window.Formatter = Formatter;
window.UI = UI;
window.API = API;
window.Masks = Masks;
