const tabelaHigienizacao = document.getElementById('tabela-higienizacao');
const messageConsulta = document.getElementById('message-consulta');
const filtroInput = document.getElementById('filtro-input');
const btnFiltrarDescricao = document.getElementById('filtrar-descricao');
const btnFiltrarLocal = document.getElementById('filtrar-local');
const filtroStatus = document.getElementById('filtro-status');

const API_URL = 'http://localhost:8080/apis/higienizacao';

// --- Variáveis de controle de filtro ---
let listaHigienizacao = [];
let campoFiltroAtual = 'descricao'; // Inicia filtrando por descrição
filtroStatus.textContent = 'Filtrando por Descrição';

// --- Função Debounce ---
function debounce(func, delay) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}

// --- Função para formatar data (DD/MM/AAAA) ---
function formatarDataBrasileira(dateString) {
    if (!dateString || dateString === 'N/A') return 'N/A';
    if (dateString.includes('/')) return dateString;
    const parts = dateString.split('-');
    if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
    return dateString;
}

// --- Carregar Registros do backend ---
async function carregarHigienizacao() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Erro ao buscar registros de higienização.');
        listaHigienizacao = await response.json();

        // Se o backend retornar um objeto de Mensagem em vez de um array vazio
        if (listaHigienizacao.mensagem) {
            listaHigienizacao = [];
        }

        atualizarTabela(listaHigienizacao);
    } catch (error) {
        messageConsulta.style.color = '#e03e3e';
        messageConsulta.textContent = `Erro ao carregar registros: ${error.message}`;
        tabelaHigienizacao.innerHTML = '<tr><td colspan="8">Nenhum registro encontrado.</td></tr>';
    }
}

// --- Atualizar tabela ---
function atualizarTabela(registros) {
    tabelaHigienizacao.innerHTML = '';

    if (!registros || registros.length === 0) {
        tabelaHigienizacao.innerHTML = '<tr><td colspan="8">Nenhum registro encontrado com o filtro atual.</td></tr>';
        return;
    }

    registros.forEach(h => {
        const id = h.id || 'N/A';
        const dataAgendada = formatarDataBrasileira(h.data_agendada || 'N/A');
        const descricaoRoupa = h.descricao_roupa || 'N/A';
        const local = h.local || 'N/A';
        const hora = h.hora || 'N/A';
        const valorPago = (h.valor_pago !== undefined && h.valor_pago !== null) 
                            ? `R$ ${h.valor_pago.toFixed(2).replace('.', ',')}` 
                            : 'N/A';
        const volId = h.vol_id || 'N/A';

        // Cria parâmetros para a página de alteração
        const params = new URLSearchParams({
            id: id,
            data_agendada: h.data_agendada || 'N/A',
            descricao_roupa: descricaoRoupa,
            vol_id: volId,
            local: local,
            hora: hora,
            valor_pago: h.valor_pago
        }).toString();

        const acoes = `
            <a href="alterarHigienizacao.html?${params}">
                <button class="edit-btn" data-id="${id}">Alterar</button>
            </a>
            <button class="delete-btn" data-id="${id}">Excluir</button>
        `;

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${id}</td>
            <td>${dataAgendada}</td>
            <td>${descricaoRoupa}</td>
            <td>${local}</td>
            <td>${hora}</td>
            <td>${valorPago}</td>
            <td>${volId}</td>
            <td>${acoes}</td>
        `;
        tabelaHigienizacao.appendChild(tr);
    });
}

// --- Filtrar localmente ---
function filtrarTabela() {
    const termo = filtroInput.value.trim().toLowerCase();

    const campoDB = campoFiltroAtual === 'descricao' ? 'descricao_roupa' : 'local';

    const filtrados = listaHigienizacao.filter(h => {
        const valorCampo = (h[campoDB] || '').toLowerCase();
        return valorCampo.includes(termo);
    });

    filtroStatus.textContent = termo
        ? `Filtrando por ${campoFiltroAtual === 'descricao' ? 'Descrição' : 'Local'}: "${termo}"`
        : `Filtrando por ${campoFiltroAtual === 'descricao' ? 'Descrição' : 'Local'}`;

    atualizarTabela(filtrados);
}

// --- Excluir Registro ---
async function excluirRegistro(id) {
    if (!confirm(`Confirma exclusão do Registro de Higienização com ID: ${id}?`)) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        
        // Trata a resposta, que deve retornar um JSON com mensagem ou erro
        const jsonResponse = await response.json();

        if (!response.ok || jsonResponse.erro) {
            throw new Error(jsonResponse.erro || 'Erro desconhecido na exclusão.');
        }
        
        // Recarrega a tabela após a exclusão bem-sucedida
        await carregarHigienizacao();
        alert('Registro de Higienização excluído com sucesso!');
    } catch (error) {
        alert(`Erro ao excluir Registro: ${error.message}`);
    }
}

// --- Event Listeners ---
const debouncedFiltrarTabela = debounce(filtrarTabela, 200);

btnFiltrarDescricao.addEventListener('click', () => {
    campoFiltroAtual = 'descricao';
    filtroStatus.textContent = 'Filtrando por Descrição';
    filtrarTabela();
});

btnFiltrarLocal.addEventListener('click', () => {
    campoFiltroAtual = 'local';
    filtroStatus.textContent = 'Filtrando por Local';
    filtrarTabela();
});

filtroInput.addEventListener('input', debouncedFiltrarTabela);

tabelaHigienizacao.addEventListener('click', e => {
    if (e.target.classList.contains('delete-btn')) {
        const id = parseInt(e.target.getAttribute('data-id'));
        excluirRegistro(id);
    }
});

// Inicializa a tabela
carregarHigienizacao();