const calendarContainer = document.getElementById('calendar-days');
const currentMonthYear = document.getElementById('current-month-year');
const messageConsulta = document.getElementById('message-consulta');
const btnPrevMonth = document.getElementById('btn-prev-month');
const btnNextMonth = document.getElementById('btn-next-month');

// Elementos do Modal
const dayModal = document.getElementById('dayModal');
const closeBtn = document.querySelector('.close-btn');
const modalDateTitle = document.getElementById('modalDateTitle');
const modalEventList = document.getElementById('modalEventList');
const modalActions = document.getElementById('modalActions');

const API_URL = 'http://localhost:8080/apis/higienizacao';

let dataAtual = new Date();
dataAtual.setDate(1); 

const hoje = new Date();
hoje.setHours(0, 0, 0, 0); 

let listaHigienizacao = [];

const meses = [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
];
const diasSemana = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];


// --- Carregar Registros do backend ---
async function carregarHigienizacao() {
    // ... (Mantém a mesma lógica de carregamento) ...
    try {
        messageConsulta.textContent = 'Carregando agendamentos...';
        const response = await fetch(API_URL);
        
        const data = await response.json();

        if (data.mensagem) {
            listaHigienizacao = [];
        } else if (!response.ok) {
            throw new Error('Erro ao buscar registros de higienização.');
        } else {
            listaHigienizacao = data.map(h => ({
                ...h,
                data_agendada_str: h.data_agendada ? h.data_agendada.split('T')[0] : null 
            }));
        }
        
        messageConsulta.textContent = ''; 
        renderizarCalendario();
    } catch (error) {
        messageConsulta.style.color = '#e03e3e';
        messageConsulta.textContent = `Erro ao carregar registros: ${error.message}`;
        renderizarCalendario();
    }
}

// --- Função para formatar data (DD/MM/AAAA) ---
function formatarDataBrasileira(dateString) {
    if (!dateString) return '';
    const parts = dateString.split('-'); // Espera YYYY-MM-DD
    if (parts.length === 3) return `${parts[2]}/${parts[1]}/${parts[0]}`;
    return dateString;
}

// --- Abre o Modal com Detalhes do Dia ---
function abrirModal(dataCompletaStr, agendamentosDoDia, isPastDate) {
    
    modalDateTitle.textContent = `Detalhes do Dia: ${formatarDataBrasileira(dataCompletaStr)}`;
    modalEventList.innerHTML = '';
    modalActions.innerHTML = '';
    
    // Lista os eventos detalhadamente
    agendamentosDoDia.forEach(h => {
        const p = document.createElement('p');
        
        let acoesHtml = '';
        if (!isPastDate) {
            // Ações permitidas (Alterar/Excluir)
            acoesHtml = `
                <a href="alterarHigienizacao.html?${new URLSearchParams({
                    id: h.id,
                    data_agendada: h.data_agendada_str,
                    descricao_roupa: h.descricao_roupa,
                    vol_id: h.vol_id,
                    local: h.local,
                    hora: h.hora,
                    valor_pago: h.valor_pago
                }).toString()}" title="Alterar">
                    <button class="btn-acao edit-btn" style="background-color: #28A745; margin-right: 5px;">Alterar</button>
                </a>
                <button class="btn-acao delete-btn" data-id="${h.id}" style="background-color: #DC3545;">Excluir</button>
            `;
        } else {
            // Ações bloqueadas (Visualização de Histórico)
            acoesHtml = `<span style="color: #999; font-style: italic;">Registro antigo (🔒)</span>`;
        }

        p.innerHTML = `
            <strong>ID:</strong> ${h.id} (${h.hora})<br>
            <strong>Local:</strong> ${h.local}<br>
            <strong>Valor:</strong> R$ ${h.valor_pago !== undefined ? h.valor_pago.toFixed(2).replace('.', ',') : 'N/A'}<br>
            <strong>Voluntário ID:</strong> ${h.vol_id}<br>
            <strong>Descrição:</strong> ${h.descricao_roupa}<br>
            <div style="margin-top: 10px; text-align: right;">${acoesHtml}</div>
        `;
        
        modalEventList.appendChild(p);
    });

    // Adiciona botão Novo Agendamento (se não for passado)
    if (!isPastDate) {
        modalActions.innerHTML = `
            <a href="cadastroHigienizacao.html?data_agendada=${dataCompletaStr}" class="btn-acao" style="background-color: #17A2B8;">
                Novo Agendamento neste dia
            </a>
        `;
    }

    dayModal.style.display = 'block';

    // Adiciona listener de exclusão dentro do modal
    modalEventList.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const id = parseInt(e.target.getAttribute('data-id'));
            dayModal.style.display = 'none'; // Fecha o modal antes de confirmar
            excluirRegistro(id);
        });
    });
}


// --- Função principal para desenhar o Calendário ---
function renderizarCalendario() {
    calendarContainer.innerHTML = '';
    
    const ano = dataAtual.getFullYear();
    const mes = dataAtual.getMonth();

    currentMonthYear.textContent = `${meses[mes]} de ${ano}`;

    // 1. Determina o primeiro dia e o último dia do mês
    const primeiroDiaMes = new Date(ano, mes, 1);
    const ultimoDiaMes = new Date(ano, mes + 1, 0).getDate();
    const diaInicio = primeiroDiaMes.getDay(); 
    
    // 2. Preencher células vazias iniciais
    for (let i = 0; i < diaInicio; i++) {
        const emptyCell = document.createElement('div');
        emptyCell.classList.add('calendar-day', 'empty');
        calendarContainer.appendChild(emptyCell);
    }

    // 3. Preencher os dias do mês
    for (let dia = 1; dia <= ultimoDiaMes; dia++) {
        const dataCompleta = new Date(ano, mes, dia);
        dataCompleta.setHours(0, 0, 0, 0); 
        
        const dataCompletaStr = `${ano}-${String(mes + 1).padStart(2, '0')}-${String(dia).padStart(2, '0')}`;
        
        const dayCell = document.createElement('div');
        dayCell.classList.add('calendar-day');
        dayCell.innerHTML = `<div class="day-number">${dia}</div>`;

        // Verifica se há agendamentos para este dia
        const agendamentosDoDia = listaHigienizacao.filter(h => 
            h.data_agendada_str === dataCompletaStr
        );
        
        const isPastDate = dataCompleta.getTime() < hoje.getTime(); 

        if (agendamentosDoDia.length > 0) {
            dayCell.classList.add('has-events');
            
            // Exibe a contagem e um indicador, sem os detalhes completos
            const countIndicator = document.createElement('div');
            countIndicator.classList.add('event-indicator');
            countIndicator.textContent = `${agendamentosDoDia.length} Agendamento(s)`;
            dayCell.appendChild(countIndicator);
            
            // Adiciona o listener para abrir o modal
            dayCell.addEventListener('click', () => {
                abrirModal(dataCompletaStr, agendamentosDoDia, isPastDate);
            });
        }
        
        // Adiciona o link rápido para cadastro (visível na célula)
        if (!isPastDate && agendamentosDoDia.length === 0) {
            dayCell.title = `Clique para agendar em ${dataCompletaStr}`;
            const btnNovo = document.createElement('a');
            btnNovo.href = `cadastroHigienizacao.html?data_agendada=${dataCompletaStr}`;
            btnNovo.innerHTML = '<span class="action-icon new-icon">➕</span>';
            btnNovo.classList.add('new-schedule');
            dayCell.appendChild(btnNovo);
        } else if (isPastDate) {
            dayCell.classList.add('past-date');
        }
        
        // Se for o dia de hoje, destaca
        if (dataCompleta.getTime() === hoje.getTime()) {
             dayCell.style.border = '2px solid #17A2B8';
        }

        calendarContainer.appendChild(dayCell);
    }
    
    btnPrevMonth.disabled = false;
}

// --- Navegação e Event Listeners Globais ---
btnPrevMonth.addEventListener('click', () => {
    dataAtual.setMonth(dataAtual.getMonth() - 1);
    renderizarCalendario();
});

btnNextMonth.addEventListener('click', () => {
    dataAtual.setMonth(dataAtual.getMonth() + 1);
    renderizarCalendario();
});

// Fechar Modal ao clicar no X
closeBtn.addEventListener('click', () => {
    dayModal.style.display = 'none';
});

// Fechar Modal ao clicar fora
window.addEventListener('click', (event) => {
    if (event.target === dayModal) {
        dayModal.style.display = 'none';
    }
});


// --- Excluir Registro ---
async function excluirRegistro(id) {
    if (!confirm(`Confirma exclusão do Registro de Higienização com ID: ${id}?`)) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        const jsonResponse = await response.json();

        if (!response.ok || jsonResponse.erro) {
            throw new Error(jsonResponse.erro || 'Erro desconhecido na exclusão.');
        }
        
        await carregarHigienizacao();
        alert('Registro de Higienização excluído com sucesso! Calendário atualizado.');
    } catch (error) {
        alert(`Erro ao excluir Registro: ${error.message}`);
    }
}

// Inicializa o calendário
carregarHigienizacao();