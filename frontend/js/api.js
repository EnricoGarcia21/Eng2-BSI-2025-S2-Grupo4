// ===== Sistema de Autenticação Integrado com Backend DOARC =====
const API_BASE_URL = 'http://localhost:8080/apis';

const Auth = {
    // Fazer login no sistema
    async login(email, senha) {
        try {
            const response = await fetch(`${API_BASE_URL}/acesso/logar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email, senha })
            });

            const data = await response.json();

            if (response.ok && data.token) {
                // Salvar dados no localStorage
                localStorage.setItem('token', data.token);
                localStorage.setItem('loggedIn', 'true');
                localStorage.setItem('loginTime', new Date().getTime().toString());
                
                // Salvar dados do usuário
                const userData = {
                    email: data.usuario.email,
                    nivelAcesso: data.usuario.nivelAcesso,
                    voluntarioId: data.usuario.voluntarioId,
                    nome: data.usuario.email.split('@')[0] // Usar parte do email como nome temporário
                };
                
                localStorage.setItem('user', JSON.stringify(userData));
                localStorage.setItem('nivelAcesso', data.usuario.nivelAcesso);

                return {
                    success: true,
                    data: userData,
                    message: data.mensagem
                };
            } else {
                return {
                    success: false,
                    message: data.erro || 'Erro no login'
                };
            }
        } catch (error) {
            console.error('Erro no login:', error);
            return {
                success: false,
                message: 'Erro de conexão com o servidor'
            };
        }
    },

    // Registrar novo voluntário
    async register(dadosVoluntario) {
        try {
            const response = await fetch(`${API_BASE_URL}/acesso/registrar`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(dadosVoluntario)
            });

            const data = await response.json();

            if (response.ok) {
                return {
                    success: true,
                    message: data.mensagem || 'Cadastro realizado com sucesso'
                };
            } else {
                return {
                    success: false,
                    message: data.erro || 'Erro no cadastro'
                };
            }
        } catch (error) {
            console.error('Erro no registro:', error);
            return {
                success: false,
                message: 'Erro de conexão com o servidor'
            };
        }
    },

    // Verificar se está autenticado
    isAuthenticated() {
        const token = localStorage.getItem('token');
        const loggedIn = localStorage.getItem('loggedIn') === 'true';
        const loginTime = localStorage.getItem('loginTime');
        
        if (!token || !loggedIn) {
            return false;
        }

        // Verificar se o token não expirou (24 horas)
        if (loginTime) {
            const currentTime = new Date().getTime();
            const hoursSinceLogin = (currentTime - parseInt(loginTime)) / (1000 * 60 * 60);
            
            if (hoursSinceLogin > 24) {
                this.logout();
                return false;
            }
        }

        return true;
    },

    // Obter dados do usuário
    getUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    },

    // Obter token
    getToken() {
        return localStorage.getItem('token');
    },

    // Verificar se é admin
    isAdmin() {
        const user = this.getUser();
        return user && user.nivelAcesso === 'ADMIN';
    },

    // Fazer logout
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('loggedIn');
        localStorage.removeItem('loginTime');
        localStorage.removeItem('user');
        localStorage.removeItem('nivelAcesso');
        
        window.location.href = '../index.html';
    },

    // Verificar autenticação e redirecionar se necessário
    checkAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = '../index.html';
            return false;
        }
        return true;
    },

    // Verificar se é admin e redirecionar se necessário
    requireAdmin() {
        if (!this.isAuthenticated()) {
            window.location.href = '../index.html';
            return false;
        }
        
        if (!this.isAdmin()) {
            alert('Acesso restrito a administradores');
            window.location.href = 'dashboard.html';
            return false;
        }
        
        return true;
    },

    // Fazer requisição autenticada
    async makeAuthenticatedRequest(url, options = {}) {
        const token = this.getToken();
        
        if (!token) {
            throw new Error('Token não encontrado');
        }

        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        };

        const mergedOptions = {
            ...defaultOptions,
            ...options,
            headers: {
                ...defaultOptions.headers,
                ...options.headers
            }
        };

        const response = await fetch(url, mergedOptions);

        if (response.status === 401) {
            // Token inválido ou expirado
            this.logout();
            throw new Error('Sessão expirada');
        }

        return response;
    }
};

// Exportar para uso global
window.Auth = Auth;