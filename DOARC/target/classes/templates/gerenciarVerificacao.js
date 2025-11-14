const tabelaVerificacao = document.getElementById('tabela-verificacao');
const messageConsulta = document.getElementById('message-consulta');
const filtroInput = document.getElementById('filtro-input');
const btnFiltrarObs = document.getElementById('filtrar-obs');
const btnFiltrarResultado = document.getElementById('filtrar-resultado');
const filtroStatus = document.getElementById('filtro-status');

const API_URL = 'http://localhost:8080/apis/verificacao';
// NOVO: URL da API para Donatários
const API_DONATARIO_URL = 'http://localhost:8080/apis/donatario'; 

// --- Variáveis de controle de filtro e Donatários ---
let listaVerificacao = [];
let campoFiltroAtual = 'observacao'; // Inicia filtrando por observação
let donatariosMap = {}; // Mapa para ID -> Nome
filtroStatus.textContent = 'Filtrando por Observação';

// --- Função Debounce (reutilizada) ---
function debounce(func, delay) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}

// --- Função para formatar data (reutilizada) ---
function formatarDataBrasileira(dateString) {
    if (!dateString || dateString === 'N/A') return 'N/A';
    if (dateString.includes('/')) return dateString;
    const parts = dateString.split('-');
    // Assumindo que a API retorna YYYY-MM-DD ou DD/MM/YYYY
    if (parts.length === 3) {
         // Verifica se é YYYY-MM-DD
        if (parts[0].length === 4) return `${parts[2]}/${parts[1]}/${parts[0]}`; 
        // Se for DD/MM/YYYY
        return dateString;
    }
    return dateString;
}

// --- NOVO: Função para buscar Donatários e criar Mapa ---
async function carregarDonatarios() {
    try {
        const response = await fetch(API_DONATARIO_URL); 
        if (!response.ok) throw new Error('Erro ao buscar donatários.');
        
        const data = await response.json();
        
        if (data.mensagem && !Array.isArray(data)) {
            donatariosMap = {}; 
        } else {
            // Cria um mapa ID -> Nome
            donatariosMap = data.reduce((map, donatario) => {
                map[donatario.id] = donatario.nome || 'Nome Indisponível';
                return map;
            }, {});
        }
    } catch (error) {
        console.error('Erro ao carregar mapa de donatários:', error.message);
        donatariosMap = {};
    }
}

// --- Carregar Verificações do backend ---
async function carregarVerificacoes() {
    // CHAMA O NOVO MÉTODO ANTES DE TUDO
    await carregarDonatarios(); 
    
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Erro ao buscar verificações.');
        
        const data = await response.json();
        
        // Trata se o backend retornar um objeto de Mensagem
        if (data.mensagem && !Array.isArray(data)) {
            listaVerificacao = [];
        } else {
             listaVerificacao = data;
        }

        atualizarTabela(listaVerificacao);
    } catch (error) {
        messageConsulta.style.color = '#e03e3e';
        messageConsulta.textContent = `Erro ao carregar verificações: ${error.message}`;
        tabelaVerificacao.innerHTML = '<tr><td colspan="7">Nenhum registro encontrado.</td></tr>';
    }
}

// --- Atualizar tabela ---
function atualizarTabela(verificacoes) {
    tabelaVerificacao.innerHTML = '';

    if (!verificacoes || verificacoes.length === 0) {
        tabelaVerificacao.innerHTML = '<tr><td colspan="7">Nenhuma verificação encontrada com o filtro atual.</td></tr>';
        return;
    }

    verificacoes.forEach(v => {
        const id = v.id || 'N/A';
        const data = formatarDataBrasileira(v.data || 'N/A');
        const observacao = v.observacao || 'N/A';
        const resultado = v.resultado || 'N/A';
        const volId = v.vol_id || 'N/A';
        const doaId = v.doa_id || 'N/A';
        
        // NOVO: Busca o nome usando o mapa
        const donatarioNome = donatariosMap[doaId] || (doaId !== 'N/A' ? `ID: ${doaId}` : 'N/A'); 

        // Cria parâmetros para a página de alteração
        const params = new URLSearchParams({
            id, 
            data: v.data, 
            observacao, 
            resultado, 
            vol_id: volId, 
            doa_id: doaId
        }).toString();

        const acoes = `
            <a href="alterarVerificacao.html?${params}">
                <button class="edit-btn" data-id="${id}">Alterar</button>
            </a>
            <button class="delete-btn" data-id="${id}">Excluir</button>
        `;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${id}</td>
            <td>${data}</td>
            <td>${observacao}</td>
            <td>${resultado}</td>
            <td>${volId}</td>
            <td>${donatarioNome} (ID: ${doaId})</td> 
            <td>${acoes}</td>
        `;
        tabelaVerificacao.appendChild(tr);
    });
}

// --- Filtrar localmente ---
function filtrarTabela() {
    const termo = filtroInput.value.trim().toLowerCase();

    const campoDB = campoFiltroAtual === 'observacao' ? 'observacao' : 'resultado';

    const filtrados = listaVerificacao.filter(v => {
        const valorCampo = (v[campoDB] || '').toLowerCase();
        return valorCampo.includes(termo);
    });

    filtroStatus.textContent = termo
        ? `Filtrando por ${campoFiltroAtual === 'observacao' ? 'Observação' : 'Resultado'}: "${termo}"`
        : `Filtrando por ${campoFiltroAtual === 'observacao' ? 'Observação' : 'Resultado'}`;

    atualizarTabela(filtrados);
}

// --- Excluir Verificação ---
async function excluirVerificacao(id) {
    if (!confirm(`Confirma exclusão da Verificação com ID: ${id}?`)) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        
        // Trata a resposta, que deve retornar um JSON com mensagem ou erro
        const jsonResponse = await response.json();

        if (!response.ok || jsonResponse.erro) {
            throw new Error(jsonResponse.erro || 'Erro desconhecido na exclusão.');
        }
        
        // Recarrega a tabela após a exclusão bem-sucedida
        await carregarVerificacoes();
        alert('Verificação excluída com sucesso!');
    } catch (error) {
        alert(`Erro ao excluir Verificação: ${error.message}`);
    }
}

// --- Event Listeners ---
const debouncedFiltrarTabela = debounce(filtrarTabela, 200);

btnFiltrarObs.addEventListener('click', () => {
    campoFiltroAtual = 'observacao';
    filtroStatus.textContent = 'Filtrando por Observação';
    filtrarTabela();
});

btnFiltrarResultado.addEventListener('click', () => {
    campoFiltroAtual = 'resultado';
    filtroStatus.textContent = 'Filtrando por Resultado';
    filtrarTabela();
});

filtroInput.addEventListener('input', debouncedFiltrarTabela);

tabelaVerificacao.addEventListener('click', e => {
    if (e.target.classList.contains('delete-btn')) {
        const id = parseInt(e.target.getAttribute('data-id'));
        excluirVerificacao(id);
    }
});

// Inicializa a tabela
carregarVerificacoes();