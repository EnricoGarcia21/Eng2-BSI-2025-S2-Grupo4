const tabelaDonatarios = document.getElementById('tabela-donatarios');
const messageConsulta = document.getElementById('message-consulta');
const filtroInput = document.getElementById('filtro-input');
const btnFiltrarNome = document.getElementById('filtrar-nome');
const btnFiltrarEmail = document.getElementById('filtrar-email');
const filtroStatus = document.getElementById('filtro-status');

// --- Variáveis de controle de filtro ---
let listaDonatarios = [];
let campoFiltroAtual = 'nome'; // Inicia filtrando por nome
filtroStatus.textContent = 'Filtrando por Nome';

// --- Função Debounce ---
function debounce(func, delay) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}

// --- Função para formatar data ---
function formatarDataBrasileira(dateString) {
    if (!dateString || dateString === 'N/A') return 'N/A';
    if (dateString.includes('/')) return dateString;
    const parts = dateString.split('-');
    if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
    return dateString;
}

// --- Carregar Donatários do backend ---
async function carregarDonatarios() {
    const API_URL = 'http://localhost:8080/apis/donatario';
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Erro ao buscar donatários.');
        listaDonatarios = await response.json();
        atualizarTabela(listaDonatarios);
    } catch (error) {
        messageConsulta.style.color = '#e03e3e';
        messageConsulta.textContent = `Erro ao carregar donatários: ${error.message}`;
        tabelaDonatarios.innerHTML = '<tr><td colspan="8">Nenhum registro encontrado.</td></tr>';
    }
}

// --- Atualizar tabela ---
function atualizarTabela(donatarios) {
    tabelaDonatarios.innerHTML = '';

    if (!donatarios || donatarios.length === 0) {
        tabelaDonatarios.innerHTML = '<tr><td colspan="8">Nenhum donatário encontrado com o filtro atual.</td></tr>';
        return;
    }

    donatarios.forEach(d => {
        const id = d.id || 'N/A';
        const nome = d.nome || 'N/A';
        const dataNasc = formatarDataBrasileira(d.data_nasc || 'N/A');
        const telefone = d.telefone || 'N/A';
        const email = d.email || 'N/A';
        const cidade = d.cidade || 'N/A';
        const uf = d.uf || 'N/A';

        const params = new URLSearchParams({
            id, nome, data_nasc: d.data_nasc, telefone, email, cidade, uf,
            rua: d.rua || 'N/A', bairro: d.bairro || 'N/A', cep: d.cep || 'N/A', sexo: d.sexo || 'N/A'
        }).toString();

        const acoes = `
            <a href="alterarDonatario.html?${params}">
                <button class="edit-btn" data-id="${id}">Alterar</button>
            </a>
            <button class="delete-btn" data-id="${id}">Excluir</button>
        `;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${id}</td>
            <td>${nome}</td>
            <td>${dataNasc}</td>
            <td>${telefone}</td>
            <td>${email}</td>
            <td>${cidade}</td>
            <td>${uf}</td>
            <td>${acoes}</td>
        `;
        tabelaDonatarios.appendChild(tr);
    });
}

// --- Filtrar localmente ---
function filtrarTabela() {
    const termo = filtroInput.value.trim().toLowerCase();

    const filtrados = listaDonatarios.filter(d => {
        const valorCampo = (campoFiltroAtual === 'nome' ? d.nome : d.email || '').toLowerCase();
        return valorCampo.includes(termo);
    });

    filtroStatus.textContent = termo
        ? `Filtrando por ${campoFiltroAtual === 'nome' ? 'Nome' : 'Email'}: "${termo}"`
        : `Filtrando por ${campoFiltroAtual === 'nome' ? 'Nome' : 'Email'}`;

    atualizarTabela(filtrados);
}

// --- Excluir Donatário ---
async function excluirDonatario(id) {
    if (!confirm(`Confirma exclusão do Donatário com ID: ${id}?`)) return;

    try {
        const response = await fetch(`http://localhost:8080/apis/donatario/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Erro desconhecido na exclusão.');
        await carregarDonatarios();
        alert('Donatário excluído com sucesso!');
    } catch (error) {
        alert(`Erro ao excluir Donatário: ${error.message}`);
    }
}

// --- Event Listeners ---
const debouncedFiltrarTabela = debounce(filtrarTabela, 200);

btnFiltrarNome.addEventListener('click', () => {
    campoFiltroAtual = 'nome';
    filtrarTabela();
});

btnFiltrarEmail.addEventListener('click', () => {
    campoFiltroAtual = 'email';
    filtrarTabela();
});

filtroInput.addEventListener('input', debouncedFiltrarTabela);

tabelaDonatarios.addEventListener('click', e => {
    if (e.target.classList.contains('delete-btn')) {
        const id = parseInt(e.target.getAttribute('data-id'));
        excluirDonatario(id);
    }
});

// Inicializa a tabela
carregarDonatarios();
