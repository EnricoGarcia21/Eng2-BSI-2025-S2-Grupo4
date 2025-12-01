// ===== DOARC - Sistema de Gestão de Doações =====
// JavaScript Principal

// ===== Configurações Globais =====
const API_URL = 'http://localhost:8080/apis'; // URL do backend Spring Boot
const STORAGE_KEY = 'doarc_user';

// ===== Funções de Autenticação =====
const Auth = {
    // Verifica se usuário está logado
    isAuthenticated() {
        const token = localStorage.getItem('token');
        return !!token;
    },

    // Obtém dados do usuário logado
    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    // Salva dados do usuário
    setUser(userData) {
        localStorage.setItem('user', JSON.stringify(userData));
    },

    // Remove dados do usuário (logout)
    logout() {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        localStorage.removeItem('loggedIn');
        localStorage.removeItem('loginTime');
        window.location.href = '../index.html';
    },

    // Verifica autenticação em páginas protegidas
    checkAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = '../index.html';
            return false;
        }
        return true;
    },

    // Verifica se usuário é admin (aceita vários formatos)
    isAdmin() {
        const user = this.getUser();
        if (!user || !user.nivelAcesso) return false;

        const nivel = user.nivelAcesso.toUpperCase();
        return nivel === 'ADMIN' || nivel === 'ADMINISTRADOR';
    },

    // ✅ NOVA FUNÇÃO: Protege rotas de Admin
    guardAdminRoute() {
        // 1. Verifica se está logado
        if (!this.isAuthenticated()) {
            window.location.href = '../index.html';
            return false;
        }

        // 2. Verifica se é Admin
        if (!this.isAdmin()) {
            // Bloqueia o corpo da página para não mostrar conteúdo piscando
            document.body.style.display = 'none'; 
            
            // Mostra o Pop-up Vermelho e Redireciona
            setTimeout(() => {
                alert("⛔ ACESSO NEGADO!\n\nVocê não tem permissão de administrador para acessar esta página.");
                window.location.href = 'dashboard.html';
            }, 100);
            
            return false;
        }
        return true;
    },

    // Atualiza informações do usuário no header
    updateUserInfo() {
        const user = this.getUser();
        if (user) {
            const displayName = user.nome || user.email || 'Voluntário';

            const userNameElement = document.getElementById('userName');
            if (userNameElement) {
                userNameElement.textContent = displayName;
            }

            const userWelcomeElement = document.getElementById('userWelcome');
            if (userWelcomeElement) {
                userWelcomeElement.textContent = displayName;
            }

            // Esconde link de admin se não for admin
            const adminNav = document.getElementById('adminNav');
            if(adminNav && !this.isAdmin()) {
                adminNav.style.display = 'none';
            }
        }
    },

    // Inicializa autenticação e interface
    init() {
        // Atualizar informações do usuário
        this.updateUserInfo();

        // Configurar botão de logout
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                if (confirm('Deseja realmente sair?')) {
                    this.logout();
                }
            });
        }
    }
};

// Auto-inicializar quando a página carregar
document.addEventListener('DOMContentLoaded', function() {
    // Se houver elemento userName, significa que é uma página protegida
    if (document.getElementById('userName')) {
        Auth.init();
    }
});

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

// ===== Funções de UI (TOASTS) =====
const UI = {
    // Cria o container de notificações se não existir
    getContainer() {
        let container = document.getElementById('notification-container');
        if (!container) {
            container = document.createElement('div');
            container.id = 'notification-container';
            document.body.appendChild(container);
        }
        return container;
    },

    // Mostra notificação moderna (Toast)
    showAlert(message, type = 'info', title = '') {
        const container = this.getContainer();
        
        // Define ícones e títulos padrão
        let icon = 'ℹ️';
        if (!title) {
            if (type === 'success') { title = 'Sucesso!'; icon = '✅'; }
            else if (type === 'danger') { title = 'Erro!'; icon = '❌'; }
            else if (type === 'warning') { title = 'Atenção!'; icon = '⚠️'; }
            else { title = 'Informação'; }
        }

        // Cria o elemento HTML
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <div class="toast-icon" style="font-size: 1.5rem;">${icon}</div>
            <div class="toast-content">
                <div class="toast-title">${title}</div>
                <div class="toast-message">${message}</div>
            </div>
            <button class="toast-close" onclick="this.parentElement.remove()">&times;</button>
        `;

        // Adiciona ao container
        container.appendChild(toast);

        // Remove automaticamente após 4 segundos
        setTimeout(() => {
            toast.classList.add('hide');
            toast.addEventListener('animationend', () => {
                toast.remove();
            });
        }, 4000);
    },

    // Mostra loading
    showLoading() {
        if (document.getElementById('loadingOverlay')) return;
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
        if (modal) modal.classList.add('active');
    },

    // Fecha modal
    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) modal.classList.remove('active');
    },

    // Confirmação
    confirm(message, callback) {
        if (window.confirm(message)) {
            callback();
        }
    }
};

// ===== Funções de API (CORRIGIDA PARA LER ERROS DO BACKEND) =====
const API = {
    getAuthHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        const token = localStorage.getItem('token') || localStorage.getItem('jwtToken');
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    },

    // Helper para tratar respostas
    async handleResponse(response) {
        if (response.ok) {
            return response.json();
        }
        // Tenta ler o corpo do erro
        const errorBody = await response.json().catch(() => ({}));
        // Pega a mensagem de 'erro' (padrão do seu controller) ou 'message'
        const errorMessage = errorBody.erro || errorBody.message || `Erro HTTP ${response.status}`;
        
        // Lança erro com a mensagem específica do backend
        throw new Error(errorMessage);
    },

    async get(endpoint) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'GET',
                headers: this.getAuthHeaders()
            });
            UI.hideLoading();
            return await this.handleResponse(response);
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição GET:', error);
            UI.showAlert(error.message || 'Erro ao buscar dados.', 'danger');
            throw error;
        }
    },

    async post(endpoint, data) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify(data)
            });
            UI.hideLoading();
            return await this.handleResponse(response);
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição POST:', error);
            // Exibe a mensagem exata retornada pelo backend (ex: validação de data)
            UI.showAlert(error.message || 'Erro ao salvar dados.', 'danger');
            throw error;
        }
    },

    async put(endpoint, data) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'PUT',
                headers: this.getAuthHeaders(),
                body: JSON.stringify(data)
            });
            UI.hideLoading();
            return await this.handleResponse(response);
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição PUT:', error);
            UI.showAlert(error.message || 'Erro ao atualizar dados.', 'danger');
            throw error;
        }
    },

    async delete(endpoint) {
        try {
            UI.showLoading();
            const response = await fetch(`${API_URL}${endpoint}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders()
            });
            UI.hideLoading();
            return await this.handleResponse(response);
        } catch (error) {
            UI.hideLoading();
            console.error('Erro na requisição DELETE:', error);
            UI.showAlert(error.message || 'Erro ao excluir dados.', 'danger');
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
            if (value.length > 9) value = value.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, '$1.$2.$3-$4');
            else if (value.length > 6) value = value.replace(/(\d{3})(\d{3})(\d{1,3})/, '$1.$2.$3');
            else if (value.length > 3) value = value.replace(/(\d{3})(\d{1,3})/, '$1.$2');
            e.target.value = value;
        });
    },
    phone(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d]/g, '');
            if (value.length > 11) value = value.slice(0, 11);
            if (value.length > 10) value = value.replace(/(\d{2})(\d{5})(\d{1,4})/, '($1) $2-$3');
            else if (value.length > 6) value = value.replace(/(\d{2})(\d{4})(\d{1,4})/, '($1) $2-$3');
            else if (value.length > 2) value = value.replace(/(\d{2})(\d{1,5})/, '($1) $2');
            e.target.value = value;
        });
    },
    cep(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/[^\d]/g, '');
            if (value.length > 8) value = value.slice(0, 8);
            if (value.length > 5) value = value.replace(/(\d{5})(\d{1,3})/, '$1-$2');
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

// ===== Exporta funções globais =====
window.Auth = Auth;
window.Validator = Validator;
window.Formatter = Formatter;
window.UI = UI;
window.API = API;
window.Masks = Masks;