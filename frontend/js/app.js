// ===== DOARC - Sistema de Gestão de Doações =====
// JavaScript Principal

// ===== Configurações Globais =====
const API_URL = 'http://localhost:8080/apis';
const STORAGE_KEY = 'doarc_user';

// ===== Funções de Autenticação =====
const Auth = {
    isAuthenticated() {
        const user = localStorage.getItem(STORAGE_KEY);
        return user !== null;
    },

    getUser() {
        const user = localStorage.getItem(STORAGE_KEY);
        return user ? JSON.parse(user) : null;
    },

    setUser(userData) {
        localStorage.setItem(STORAGE_KEY, JSON.stringify(userData));
    },

    logout() {
        localStorage.removeItem(STORAGE_KEY);
        window.location.href = '../index.html';
    },

    checkAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = '../index.html';
        }
    },

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
    
    validateForm(form) {
        let isValid = true;
        
        const requiredInputs = form.querySelectorAll('.required, [required]');
        
        requiredInputs.forEach(input => {
            const value = input.value;
            const name = input.id || input.name;
            
            this.clearError(input);
            
            if (this.isEmpty(value)) {
                this.showError(input, 'Este campo é obrigatório.');
                isValid = false;
                return; 
            }
            
            if (name.includes('email') && !this.isValidEmail(value)) {
                this.showError(input, 'E-mail inválido.');
                isValid = false;
                return;
            }
            
            if ((input.type === 'date' || name.includes('data')) && !this.isNotPastDate(value)) {
                this.showError(input, 'A data não pode ser anterior a hoje.');
                isValid = false;
                return;
            }
            
            if (input.tagName === 'SELECT' && this.isEmpty(value)) {
                this.showError(input, 'Selecione uma opção.');
                isValid = false;
                return;
            }
        });
        
        return isValid;
    },
    
    isEmpty(value) {
        return !value || value.trim() === '';
    },

    isValidEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },

    isValidCPF(cpf) {
        cpf = cpf.replace(/[^\d]/g, '');
        if (cpf.length !== 11) return false;

        if (/^(\d)\1+$/.test(cpf)) return false;

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

    isValidPhone(phone) {
        const cleaned = phone.replace(/[^\d]/g, '');
        return cleaned.length === 10 || cleaned.length === 11;
    },

    isValidDate(date) {
        const dateObj = new Date(date);
        return dateObj instanceof Date && !isNaN(dateObj);
    },

    isNotPastDate(date) {
        const dateObj = new Date(date);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return dateObj >= today;
    },

    isPositiveNumber(value) {
        const num = parseFloat(value);
        return !isNaN(num) && num > 0;
    },

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

    clearError(input) {
        input.classList.remove('error');
        const errorDiv = input.parentElement.querySelector('.form-error');
        if (errorDiv) {
            errorDiv.style.display = 'none';
        }
    },

    clearAllErrors(form) {
        const inputs = form.querySelectorAll('.form-control, select');
        inputs.forEach(input => this.clearError(input));
    }
};

// ===== Funções de Formatação =====
const Formatter = {
    formatCPF(cpf) {
        cpf = cpf.replace(/[^\d]/g, '');
        return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    },

    formatPhone(phone) {
        phone = phone.replace(/[^\d]/g, '');
        if (phone.length === 11) {
            return phone.replace(/(\d{2})(\d{5})(\d{4})/, '($1) $2-$3');
        }
        return phone.replace(/(\d{2})(\d{4})(\d{4})/, '($1) $2-$3');
    },

    formatDate(date) {
        if (!date) return '';
        const d = new Date(date);
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        return `${day}/${month}/${year}`;
    },

    formatDateInput(date) {
        if (!date) return '';
        const d = new Date(date);
        const year = d.getFullYear();
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const day = String(d.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    },

    formatCurrency(value) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(value);
    }
};

// ===== Funções de UI =====
const UI = {
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

            setTimeout(() => {
                alertDiv.remove();
            }, 5000);
        }
    },

    showLoading() {
        const overlay = document.createElement('div');
        overlay.className = 'loading-overlay active';
        overlay.id = 'loadingOverlay';
        overlay.innerHTML = '<div class="spinner"></div>';
        document.body.appendChild(overlay);
    },

    hideLoading() {
        const overlay = document.getElementById('loadingOverlay');
        if (overlay) {
            overlay.remove();
        }
    },

    openModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('active');
        }
    },

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('active');
        }
    },

    confirm(message, callback) {
        return new Promise((resolve) => {
            if (window.confirm(message)) {
                if (callback) callback();
                resolve(true);
            } else {
                resolve(false);
            }
        });
    }
};

// ===== Funções de API =====
const API = {
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
    if (!window.location.pathname.includes('index.html') &&
        !window.location.pathname.includes('register.html') &&
        !window.location.pathname.endsWith('/')) {
        Auth.checkAuth();
        Auth.updateUserInfo();
    }

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            UI.confirm('Deseja realmente sair do sistema?', function() {
                Auth.logout();
            });
        });
    }

    window.addEventListener('click', function(e) {
        if (e.target.classList.contains('modal')) {
            e.target.classList.remove('active');
        }
    });

    document.querySelectorAll('[data-mask="cpf"]').forEach(input => Masks.cpf(input));
    document.querySelectorAll('[data-mask="phone"]').forEach(input => Masks.phone(input));
    document.querySelectorAll('[data-mask="cep"]').forEach(input => Masks.cep(input));
    document.querySelectorAll('[data-mask="currency"]').forEach(input => Masks.currency(input));

    document.querySelectorAll('.form-control').forEach(input => {
        input.addEventListener('input', function() {
            Validator.clearError(this);
        });
    });
});

window.Auth = Auth;
window.Validator = Validator;
window.Formatter = Formatter;
window.UI = UI;
window.API = API;
window.Masks = Masks;