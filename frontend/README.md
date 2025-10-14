# DOARC - Sistema de Gestão de Doações

Frontend do sistema DOARC (Doações, Organização e Apoio para a Rede Ceifeiros) desenvolvido para a Igreja Ceifeiros de Cristo.

## 📋 Sobre o Projeto

O DOARC é um sistema web completo para gerenciamento de doações que permite:
- Gerenciar cadastros de doadores, donatários, produtos, voluntários e campanhas
- Registrar recebimento e distribuição de doações
- Controlar estoque de produtos
- Agendar entregas e retiradas
- Gerar relatórios detalhados
- Notificar donatários sobre doações

## 🚀 Tecnologias Utilizadas

- **HTML5**: Estrutura das páginas
- **CSS3**: Estilização e design responsivo
- **JavaScript (ES6+)**: Lógica e interatividade
- **Backend**: Spring Boot (Java) - *A ser integrado*
- **Banco de Dados**: PostgreSQL

## 📁 Estrutura do Projeto

```
frontend/
├── css/
│   └── style.css              # Estilos globais do sistema
├── js/
│   └── app.js                 # JavaScript principal com funções utilitárias
├── pages/                     # Páginas do sistema
│   ├── dashboard.html         # Dashboard principal
│   ├── gerenciar-doadores.html
│   ├── receber-doacoes.html
│   ├── relatorios.html
│   └── ...
├── assets/                    # Imagens e recursos estáticos
├── index.html                 # Página de login
├── register.html              # Página de cadastro de voluntário
└── README.md                  # Este arquivo
```

## 🎨 Funcionalidades Implementadas

### Autenticação
- ✅ Login de voluntários
- ✅ Cadastro de novos voluntários
- ✅ Logout com confirmação

### Gerenciamento (CRUD)
- ✅ Gerenciar Doadores
- ✅ Gerenciar Donatários
- ✅ Gerenciar Produtos
- ✅ Gerenciar Categorias
- ✅ Gerenciar Voluntários
- ✅ Gerenciar Campanhas

### Operações de Doação
- ✅ Receber Doações (produtos e monetárias)
- ✅ Efetuar Doações
- ✅ Agendar Doações
- ✅ Agendar Retirada de Doações
- ✅ Notificar Donatários

### Controle de Estoque
- ✅ Visualizar Estoque
- ✅ Registrar Acerto de Estoque
- ✅ Lançar Compra/Arrecadação
- ✅ Agendar Higienização de Roupas

### Relatórios
- ✅ Relatório de Doações (com filtros)
- ✅ Relatório de Produtos
- ✅ Relatório de Voluntários
- ✅ Relatório de Campanhas
- ✅ Exportação em PDF e Excel

## 🎯 Como Usar

### 1. Acesso ao Sistema

1. Abra o arquivo `index.html` em um navegador web moderno
2. Faça login com suas credenciais de voluntário
3. Ou cadastre-se como novo voluntário através do link "Cadastre-se aqui"

### 2. Navegação

O sistema possui um menu de navegação organizado em seções:

- **Dashboard**: Visão geral com estatísticas e ações rápidas
- **Gerenciar**: CRUD completo de todas as entidades
- **Doações**: Operações de recebimento e distribuição
- **Estoque**: Controle e ajustes de inventário
- **Relatórios**: Geração e exportação de relatórios

### 3. Funcionalidades Principais

#### Receber Doações
1. Acesse "Doações > Receber Doações"
2. Selecione o doador
3. Escolha o tipo de doação (produto ou monetária)
4. Para produtos: adicione itens com quantidade e validade
5. Associe a uma campanha (opcional)
6. Confirme o recebimento

#### Gerenciar Cadastros
1. Acesse "Gerenciar" e escolha a entidade
2. Use a busca para encontrar registros
3. Clique em "Novo" para adicionar
4. Use "Editar" para modificar
5. Use "Excluir" para remover (com confirmação)

#### Gerar Relatórios
1. Acesse "Relatórios"
2. Selecione o tipo de relatório desejado
3. Aplique filtros conforme necessário
4. Clique em "Gerar Relatório"
5. Exporte em PDF ou Excel

## 🔧 Recursos JavaScript

O arquivo `js/app.js` fornece:

### Autenticação
```javascript
Auth.login(email, password)
Auth.logout()
Auth.getUser()
Auth.isAuthenticated()
```

### Validação
```javascript
Validator.isEmpty(value)
Validator.isValidEmail(email)
Validator.isValidCPF(cpf)
Validator.isValidPhone(phone)
Validator.showError(input, message)
Validator.clearError(input)
```

### Formatação
```javascript
Formatter.formatCPF(cpf)
Formatter.formatPhone(phone)
Formatter.formatDate(date)
Formatter.formatCurrency(value)
```

### UI
```javascript
UI.showAlert(message, type)
UI.showLoading()
UI.hideLoading()
UI.openModal(modalId)
UI.closeModal(modalId)
UI.confirm(message, callback)
```

### API
```javascript
API.get(endpoint)
API.post(endpoint, data)
API.put(endpoint, data)
API.delete(endpoint)
```

## 🎨 Personalização de Estilos

O arquivo `css/style.css` utiliza variáveis CSS para fácil personalização:

```css
:root {
    --primary-color: #2c5f8d;      /* Cor principal */
    --secondary-color: #4a8fc7;    /* Cor secundária */
    --accent-color: #f39c12;       /* Cor de destaque */
    --success-color: #27ae60;      /* Verde sucesso */
    --danger-color: #e74c3c;       /* Vermelho erro */
    --warning-color: #f39c12;      /* Amarelo aviso */
    --info-color: #3498db;         /* Azul informação */
}
```

## 📱 Responsividade

O sistema é totalmente responsivo e se adapta a diferentes tamanhos de tela:
- **Desktop**: 1200px+ (layout completo)
- **Tablet**: 768px-1199px (menu adaptado)
- **Mobile**: 320px-767px (layout vertical)

## 🔗 Integração com Backend

Para integrar com o backend Spring Boot:

1. Configure a URL da API em `js/app.js`:
```javascript
const API_URL = 'http://localhost:8080/api';
```

2. Descomente as chamadas de API nos arquivos HTML e remova as simulações

3. Implemente os endpoints correspondentes no backend

### Endpoints Esperados

```
POST   /api/auth/login
POST   /api/auth/register
GET    /api/dashboard/stats
GET    /api/doadores
POST   /api/doadores
PUT    /api/doadores/{id}
DELETE /api/doadores/{id}
POST   /api/doacoes/receber
POST   /api/doacoes/efetuar
GET    /api/relatorios/{tipo}
... (demais endpoints conforme necessário)
```

## 🛠️ Desenvolvimento

### Requisitos
- Navegador web moderno (Chrome, Firefox, Edge, Safari)
- Servidor web local (opcional, para desenvolvimento)
- Backend Spring Boot configurado (para funcionalidade completa)

### Executando Localmente

1. Clone o repositório
2. Abra `index.html` diretamente no navegador ou
3. Use um servidor local:
```bash
# Python 3
python -m http.server 8000

# Node.js (http-server)
npx http-server
```

4. Acesse `http://localhost:8000`

## 📝 Validações Implementadas

- CPF válido (com dígitos verificadores)
- E-mail no formato correto
- Telefone com 10 ou 11 dígitos
- Campos obrigatórios
- Datas válidas
- Números positivos
- Senhas com mínimo de 6 caracteres

## 🔒 Segurança

- Validação de dados no frontend
- Proteção de rotas (verificação de autenticação)
- Sanitização de inputs
- Conexão HTTPS recomendada
- Conformidade com LGPD

## 📄 Licença

Este projeto foi desenvolvido como trabalho acadêmico para a disciplina de Engenharia de Software I da UNOESTE.

Todos os direitos pertencem aos desenvolvedores, com licença perpétua de uso concedida à Igreja Ceifeiros de Cristo.

## 👥 Autores

- Caio Henrique Aranda Sumida - 262318911
- Enrico de Oliveira Garcia - 262321130
- Gabriel Mendes Lopes - 262412810
- Guilherme Poschl Ishida - 262318059
- Matheus Biembengut Lopez Azevedo - 262424681
- Pedro Augusto da Costa Oliveira - 262319004

## 🎓 Orientador

- Prof. Me. Bruno Santos de Lima

## 📞 Suporte

Para dúvidas ou sugestões, entre em contato com a equipe de desenvolvimento.

---

**DOARC** - Transformando doações em esperança 💙
