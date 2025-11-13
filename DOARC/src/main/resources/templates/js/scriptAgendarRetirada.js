
const API_BASE = 'http://localhost:8080/apis/agendar-retirada';
const API_VOLUNTARIOS = 'http://localhost:8080/apis/voluntarios';
const API_DOACOES = 'http://localhost:8080/apis/doadores';

// Elementos do DOM
let voluntarios = [];
let doacoes = [];
let agendamentos = [];
let agendamentosFiltrados = [];
let modoEdicao = false;
let filtroVoluntarioAtivo = '';
let filtroDataAtivo = '';

// Função para fazer requisições com tratamento de erro
async function fazerRequisicao(url, options = {}) {
    try {
        const response = await fetch(url, {
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP ${response.status}: ${errorText || response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error(`Erro na requisição para ${url}:`, error);
        throw error;
    }
}

// Carregar lista de voluntários
async function carregarVoluntarios() {
    try {
        const data = await fazerRequisicao(API_VOLUNTARIOS);
        voluntarios = Array.isArray(data) ? data : (data.voluntarios || data.content || []);

        const selectVoluntario = document.getElementById('volId');

        if (voluntarios.length === 0) {
            selectVoluntario.innerHTML = '<option value="">Nenhum voluntário disponível</option>';
            return;
        }

        selectVoluntario.innerHTML = '<option value="">Selecione um voluntário</option>';

        voluntarios.forEach(voluntario => {
            const option = document.createElement('option');
            option.value = voluntario.volId || voluntario.id;
            const nome = voluntario.volNome || voluntario.nome || `Voluntário ${voluntario.volId || voluntario.id}`;
            const telefone = voluntario.volTelefone || voluntario.telefone ? ` - ${voluntario.volTelefone || voluntario.telefone}` : '';
            option.textContent = nome + telefone;
            selectVoluntario.appendChild(option);
        });

    } catch (error) {
        console.error('Erro ao carregar voluntários:', error);
        const selectVoluntario = document.getElementById('volId');
        selectVoluntario.innerHTML = '<option value="">Erro ao carregar voluntários</option>';
        mostrarMensagem('Erro ao carregar lista de voluntários: ' + error.message, 'danger');
    }
}

// Carregar lista de doações (doadores)
async function carregarDoacoes() {
    try {
        const data = await fazerRequisicao(API_DOACOES);
        doacoes = Array.isArray(data) ? data : (data.doadores || data.content || []);

        const selectDoacao = document.getElementById('doaId');

        if (doacoes.length === 0) {
            selectDoacao.innerHTML = '<option value="">Nenhum doador disponível</option>';
            return;
        }

        selectDoacao.innerHTML = '<option value="">Selecione um doador</option>';

        doacoes.forEach(doacao => {
            const option = document.createElement('option');
            option.value = doacao.doaId || doacao.id;
            const nome = doacao.doaNome || doacao.nome || `Doador ${doacao.doaId || doacao.id}`;
            const endereco = doacao.doaCidade ? ` - ${doacao.doaCidade}` : '';
            option.textContent = nome + endereco;
            selectDoacao.appendChild(option);
        });

    } catch (error) {
        console.error('Erro ao carregar doadores:', error);
        const selectDoacao = document.getElementById('doaId');
        selectDoacao.innerHTML = '<option value="">Erro ao carregar doadores</option>';
        mostrarMensagem('Erro ao carregar lista de doadores: ' + error.message, 'danger');
    }
}

// Carregar agendamentos
async function carregarAgendamentos() {
    const tbody = document.getElementById('tabelaAgendamentos');

    try {
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted py-4">
                    <i class="fas fa-spinner fa-spin me-2"></i>Carregando agendamentos...
                </td>
            </tr>
        `;

        const data = await fazerRequisicao(API_BASE);
        agendamentos = Array.isArray(data) ? data : (data.agendamentos || data.content || []);

        // Inicializar lista filtrada com todos os agendamentos
        agendamentosFiltrados = [...agendamentos];

        // Carregar voluntários no filtro
        carregarFiltroVoluntarios();

        // Atualizar tabela
        atualizarTabelaAgendamentos();

    } catch (error) {
        console.error('Erro ao carregar agendamentos:', error);
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted py-4">
                    <i class="fas fa-exclamation-triangle me-2"></i>Erro ao carregar agendamentos
                    <br><small>${error.message}</small>
                </td>
            </tr>
        `;
        mostrarMensagem('Erro ao carregar agendamentos: ' + error.message, 'danger');
    }
}

// Carregar voluntários no filtro
function carregarFiltroVoluntarios() {
    const selectFiltro = document.getElementById('filtroVoluntario');

    // Limpar opções existentes (exceto a primeira)
    selectFiltro.innerHTML = '<option value="">Todos os voluntários</option>';

    if (voluntarios.length === 0) {
        return;
    }

    // Ordenar voluntários por nome
    const voluntariosOrdenados = [...voluntarios].sort((a, b) => {
        const nomeA = (a.volNome || a.nome || '').toLowerCase();
        const nomeB = (b.volNome || b.nome || '').toLowerCase();
        return nomeA.localeCompare(nomeB);
    });

    voluntariosOrdenados.forEach(voluntario => {
        const option = document.createElement('option');
        option.value = voluntario.volId || voluntario.id;
        const nome = voluntario.volNome || voluntario.nome || `Voluntário ${voluntario.volId || voluntario.id}`;
        option.textContent = nome;
        selectFiltro.appendChild(option);
    });
}

// Filtrar agendamentos
function filtrarAgendamentos() {
    const filtroVoluntario = document.getElementById('filtroVoluntario').value;
    const filtroData = document.getElementById('filtroData').value;

    filtroVoluntarioAtivo = filtroVoluntario;
    filtroDataAtivo = filtroData;

    // Aplicar filtros
    agendamentosFiltrados = agendamentos.filter(agendamento => {
        let passaFiltro = true;

        // Filtro por voluntário
        if (filtroVoluntario && filtroVoluntario !== '') {
            const volId = agendamento.volId || agendamento.id;
            passaFiltro = passaFiltro && (volId.toString() === filtroVoluntario);
        }

        // Filtro por data
        if (filtroData && filtroData !== '') {
            const dataAgendamento = new Date(agendamento.dataRetiro + 'T00:00:00');
            const dataFiltro = new Date(filtroData + 'T00:00:00');
            passaFiltro = passaFiltro && (dataAgendamento.toISOString().split('T')[0] === dataFiltro.toISOString().split('T')[0]);
        }

        return passaFiltro;
    });

    // Ordenar por data mais recente primeiro
    agendamentosFiltrados.sort((a, b) => {
        const dataA = new Date(a.dataRetiro + 'T' + a.horaRetiro);
        const dataB = new Date(b.dataRetiro + 'T' + b.horaRetiro);
        return dataB - dataA;
    });

    atualizarTabelaAgendamentos();
}

// Limpar filtros
function limparFiltros() {
    document.getElementById('filtroVoluntario').value = '';
    document.getElementById('filtroData').value = '';

    filtroVoluntarioAtivo = '';
    filtroDataAtivo = '';

    // Restaurar lista completa
    agendamentosFiltrados = [...agendamentos];

    // Reordenar por data mais recente
    agendamentosFiltrados.sort((a, b) => {
        const dataA = new Date(a.dataRetiro + 'T' + a.horaRetiro);
        const dataB = new Date(b.dataRetiro + 'T' + b.horaRetiro);
        return dataB - dataA;
    });

    atualizarTabelaAgendamentos();

    mostrarMensagem('Filtros limpos com sucesso!', 'info');
}

// Atualizar tabela de agendamentos
function atualizarTabelaAgendamentos() {
    const tbody = document.getElementById('tabelaAgendamentos');
    const contador = document.getElementById('contadorAgendamentos');

    // Atualizar contador
    contador.textContent = agendamentosFiltrados.length;

    if (agendamentosFiltrados.length === 0) {
        let mensagem = 'Nenhum agendamento encontrado';

        if (filtroVoluntarioAtivo || filtroDataAtivo) {
            mensagem += ' com os filtros aplicados';
        }

        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted py-4">
                    <i class="fas fa-calendar-times fa-2x mb-2"></i><br>
                    ${mensagem}
                    ${(filtroVoluntarioAtivo || filtroDataAtivo) ?
            '<br><button class="btn btn-sm btn-outline-primary mt-2" onclick="limparFiltros()">Limpar Filtros</button>' :
            ''}
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = agendamentosFiltrados.map(agendamento => {
        const status = agendamento.status || 'AGENDADO';
        const podeEditar = status === 'AGENDADO' || status === 'SCHEDULED';

        return `
        <tr>
            <td>
                <strong>${formatarData(agendamento.dataRetiro)}</strong><br>
                <small class="text-muted">${agendamento.horaRetiro}</small>
            </td>
            <td>${obterNomeVoluntario(agendamento.volId)}</td>
            <td>
                <span class="status-badge ${obterClasseStatus(status)}">
                    ${obterTextoStatus(status)}
                </span>
            </td>
            <td>
                <div class="btn-group-actions">
                    <button class="btn btn-sm btn-info" onclick="verDetalhes(${agendamento.agendaId || agendamento.id})" title="Ver detalhes">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-warning" onclick="editarAgendamento(${agendamento.agendaId || agendamento.id})" 
                            ${!podeEditar ? 'disabled' : ''} title="${podeEditar ? 'Editar' : 'Não é possível editar agendamentos concluídos ou cancelados'}">
                        <i class="fas fa-edit"></i>
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="cancelarAgendamento(${agendamento.agendaId || agendamento.id})" 
                            ${!podeEditar ? 'disabled' : ''} title="${podeEditar ? 'Cancelar' : 'Não é possível cancelar agendamentos concluídos ou cancelados'}">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </td>
        </tr>
        `;
    }).join('');
}

// Função para validar horário comercial (8:00 às 18:00)
function validarHorarioComercial(hora) {
    if (!hora) return false;

    const [horas, minutos] = hora.split(':').map(Number);
    const totalMinutos = horas * 60 + minutos;

    return totalMinutos >= 480 && totalMinutos <= 1080;
}

// Salvar agendamento (criar ou editar)
async function salvarAgendamento(event) {
    event.preventDefault();

    // Limpar validações anteriores
    limparValidacoes();

    const formData = new FormData(event.target);
    const agendaId = document.getElementById('agendaId').value;
    const dados = {
        dataRetiro: formData.get('dataRetiro'),
        horaRetiro: formData.get('horaRetiro'),
        obsRetiro: formData.get('obsRetiro'),
        volId: parseInt(formData.get('volId')),
        doaId: parseInt(formData.get('doaId'))
    };

    // Validações
    let isValid = true;
    let mensagensErro = [];

    // Validar data
    if (!dados.dataRetiro) {
        marcarCampoInvalido('dataRetiro', 'Data da retirada é obrigatória');
        isValid = false;
        mensagensErro.push('Data da retirada é obrigatória');
    } else {
        const hoje = new Date();
        const dataSelecionada = new Date(dados.dataRetiro + 'T00:00:00');
        if (dataSelecionada < hoje.setHours(0, 0, 0, 0)) {
            marcarCampoInvalido('dataRetiro', 'Data não pode ser anterior ao dia atual.');
            isValid = false;
            mensagensErro.push('Data deve ser hoje ou futura');
        }
    }

    // Validar hora
    if (!dados.horaRetiro) {
        marcarCampoInvalido('horaRetiro', 'Hora da retirada é obrigatória');
        isValid = false;
        mensagensErro.push('Hora da retirada é obrigatória');
    } else {
        // Validar horário comercial (8:00 às 18:00)
        if (!validarHorarioComercial(dados.horaRetiro)) {
            marcarCampoInvalido('horaRetiro', 'Horário deve ser entre 8:00 e 18:00');
            isValid = false;
            mensagensErro.push('Horário deve ser entre 8:00 e 18:00');
        } else if (dados.dataRetiro) {
            const hoje = new Date();
            const dataSelecionada = new Date(dados.dataRetiro + 'T00:00:00');
            const horaSelecionada = new Date(dados.dataRetiro + 'T' + dados.horaRetiro + ':00');
            if (dataSelecionada.toDateString() === hoje.toDateString() && horaSelecionada <= hoje) {
                marcarCampoInvalido('horaRetiro', 'Hora deve ser futura se for hoje.');
                isValid = false;
                mensagensErro.push('Hora deve ser futura para o dia de hoje');
            }
        }
    }

    // Validar voluntário
    if (!dados.volId || isNaN(dados.volId)) {
        marcarCampoInvalido('volId', 'Selecione um voluntário');
        isValid = false;
        mensagensErro.push('Selecione um voluntário');
    }

    // Validar doação
    if (!dados.doaId || isNaN(dados.doaId)) {
        marcarCampoInvalido('doaId', 'Selecione um doador');
        isValid = false;
        mensagensErro.push('Selecione um doador');
    }

    if (!isValid) {
        const mensagem = mensagensErro.length > 0 ? mensagensErro.join(', ') : 'Preencha todos os campos obrigatórios corretamente.';
        mostrarMensagem(mensagem, 'warning');
        return false;
    }

    try {
        // Mostrar loading no botão
        const btnSalvar = document.getElementById('btnSalvar');
        const originalText = btnSalvar.innerHTML;
        btnSalvar.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>' + (modoEdicao ? 'Atualizando...' : 'Agendando...');
        btnSalvar.disabled = true;

        const formDataToSend = new FormData();
        formDataToSend.append('dataRetiro', dados.dataRetiro);
        formDataToSend.append('horaRetiro', dados.horaRetiro);
        formDataToSend.append('obsRetiro', dados.obsRetiro || '');
        formDataToSend.append('volId', dados.volId.toString());
        formDataToSend.append('doaId', dados.doaId.toString());

        let response;
        if (modoEdicao && agendaId) {
            // Editar agendamento existente
            response = await fetch(`${API_BASE}/${agendaId}`, {
                method: 'PUT',
                body: formDataToSend
            });
        } else {
            // Criar novo agendamento
            response = await fetch(API_BASE, {
                method: 'POST',
                body: formDataToSend
            });
        }

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || (modoEdicao ? 'Erro ao atualizar retirada' : 'Erro ao agendar retirada'));
        }

        const resultado = await response.json();

        mostrarMensagem(modoEdicao ? 'Retirada atualizada com sucesso!' : 'Retirada agendada com sucesso!', 'success');

        cancelarEdicao();

        await carregarAgendamentos();

    } catch (error) {
        console.error('Erro ao salvar agendamento:', error);
        mostrarMensagem('Erro ao salvar agendamento: ' + error.message, 'danger');

        // Restaurar botão em caso de erro
        const btnSalvar = document.getElementById('btnSalvar');
        btnSalvar.innerHTML = modoEdicao ?
            '<i class="fas fa-edit me-2"></i>Atualizar Retirada' :
            '<i class="fas fa-calendar-plus me-2"></i>Agendar Retirada';
        btnSalvar.disabled = false;
    }

    return false;
}

// Editar agendamento
function editarAgendamento(agendaId) {
    const agendamento = agendamentos.find(a =>
        (a.agendaId === agendaId) || (a.id === agendaId)
    );

    if (agendamento) {
        // Preencher formulário com dados do agendamento
        document.getElementById('agendaId').value = agendamento.agendaId || agendamento.id;
        document.getElementById('dataRetiro').value = agendamento.dataRetiro;
        document.getElementById('horaRetiro').value = agendamento.horaRetiro;
        document.getElementById('volId').value = agendamento.volId;
        document.getElementById('doaId').value = agendamento.doaId;
        document.getElementById('obsRetiro').value = agendamento.obsRetiro || '';

        // Mudar para modo edição
        modoEdicao = true;
        document.getElementById('formTitle').textContent = 'Editar Agendamento';
        document.getElementById('btnSalvar').innerHTML = '<i class="fas fa-edit me-2"></i>Atualizar Retirada';
        document.getElementById('btnCancelarEdicao').style.display = 'inline-block';
        document.getElementById('btnLimpar').style.display = 'none';

        // Rolar para o topo do formulário
        document.getElementById('formAgendamento').scrollIntoView({ behavior: 'smooth' });

        mostrarMensagem('Modo de edição ativado. Preencha os campos e clique em "Atualizar Retirada".', 'info');
    }
}

// Cancelar edição
function cancelarEdicao() {
    modoEdicao = false;
    document.getElementById('agendaId').value = '';
    document.getElementById('formTitle').textContent = 'Dados do Agendamento';
    document.getElementById('btnSalvar').innerHTML = '<i class="fas fa-calendar-plus me-2"></i>Agendar Retirada';
    document.getElementById('btnCancelarEdicao').style.display = 'none';
    document.getElementById('btnLimpar').style.display = 'inline-block';

    // Limpar formulário
    document.getElementById('formAgendamento').reset();
    limparValidacoes();
}

// Funções auxiliares
function obterNomeVoluntario(volId) {
    const voluntario = voluntarios.find(v =>
        (v.volId === volId) || (v.id === volId)
    );
    return voluntario ? (voluntario.volNome || voluntario.nome) : 'Voluntário não encontrado';
}

function obterNomeDoador(doaId) {
    const doacao = doacoes.find(d =>
        (d.doaId === doaId) || (d.id === doaId)
    );
    return doacao ? (doacao.doaNome || doacao.nome) : 'Doador não encontrado';
}

function obterDetalhesVoluntario(volId) {
    return voluntarios.find(v =>
        (v.volId === volId) || (v.id === volId)
    );
}

function obterDetalhesDoador(doaId) {
    return doacoes.find(d =>
        (d.doaId === doaId) || (d.id === doaId)
    );
}

function obterClasseStatus(status) {
    const statusNormalizado = (status || '').toUpperCase();
    switch(statusNormalizado) {
        case 'AGENDADO':
        case 'SCHEDULED': return 'status-agendado';
        case 'CONCLUIDO':
        case 'COMPLETED': return 'status-concluido';
        case 'CANCELADO':
        case 'CANCELLED': return 'status-cancelado';
        default: return 'status-agendado';
    }
}

function obterTextoStatus(status) {
    const statusNormalizado = (status || '').toUpperCase();
    switch(statusNormalizado) {
        case 'AGENDADO':
        case 'SCHEDULED': return 'Agendado';
        case 'CONCLUIDO':
        case 'COMPLETED': return 'Concluído';
        case 'CANCELADO':
        case 'CANCELLED': return 'Cancelado';
        default: return status || 'Agendado';
    }
}

function formatarData(data) {
    try {
        if (!data) return 'Data não informada';

        // Tenta diferentes formatos de data
        const date = new Date(data.includes('T') ? data : data + 'T00:00:00');
        if (isNaN(date.getTime())) {
            return 'Data inválida';
        }
        return date.toLocaleDateString('pt-BR');
    } catch (error) {
        return 'Data inválida';
    }
}

function mostrarMensagem(mensagem, tipo) {
    const toastContainer = document.createElement('div');
    toastContainer.className = `custom-toast`;

    const alertClass = tipo === 'success' ? 'alert-success' :
        tipo === 'warning' ? 'alert-warning' :
            tipo === 'danger' ? 'alert-danger' : 'alert-info';

    const icon = tipo === 'success' ? 'fa-check-circle' :
        tipo === 'warning' ? 'fa-exclamation-triangle' :
            tipo === 'danger' ? 'fa-times-circle' : 'fa-info-circle';

    toastContainer.innerHTML = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            <i class="fas ${icon} me-2"></i>
            ${mensagem}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    document.body.appendChild(toastContainer);

    setTimeout(() => {
        if (toastContainer.parentNode) {
            toastContainer.parentNode.removeChild(toastContainer);
        }
    }, 5000);
}

// Funções para validação
function marcarCampoInvalido(campoId, mensagem = null) {
    const campo = document.getElementById(campoId);
    const inputGroup = campo.closest('.input-group');

    if (inputGroup) {
        campo.classList.add('is-invalid');

        if (mensagem) {
            const feedback = inputGroup.querySelector('.invalid-feedback');
            if (feedback) {
                feedback.textContent = mensagem;
            }
        }

        const icon = inputGroup.querySelector('.invalid-icon');
        if (icon) {
            icon.style.display = 'block';
        }
    }
}

function limparValidacoes() {
    const campos = ['dataRetiro', 'horaRetiro', 'volId', 'doaId'];
    campos.forEach(id => {
        const campo = document.getElementById(id);
        const inputGroup = campo.closest('.input-group');

        if (inputGroup) {
            campo.classList.remove('is-invalid');

            const feedback = inputGroup.querySelector('.invalid-feedback');
            if (feedback) {
                feedback.textContent = '';
            }

            const icon = inputGroup.querySelector('.invalid-icon');
            if (icon) {
                icon.style.display = 'none';
            }
        }
    });
}

// Inicialização
document.addEventListener('DOMContentLoaded', function() {
    // Configurar validação em tempo real
    const campos = ['dataRetiro', 'horaRetiro', 'volId', 'doaId'];
    campos.forEach(id => {
        const campo = document.getElementById(id);
        campo.addEventListener('input', () => {
            const inputGroup = campo.closest('.input-group');
            if (inputGroup) {
                campo.classList.remove('is-invalid');
                const feedback = inputGroup.querySelector('.invalid-feedback');
                if (feedback) {
                    feedback.textContent = '';
                }
                const icon = inputGroup.querySelector('.invalid-icon');
                if (icon) {
                    icon.style.display = 'none';
                }
            }
        });
    });

    const hoje = new Date().toISOString().split('T')[0];
    document.getElementById('dataRetiro').setAttribute('min', hoje);

    document.getElementById('menuBtn').addEventListener('click', function() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        const content = document.getElementById('content');

        if (sidebar.style.left === '0px') {
            sidebar.style.left = '-250px';
            overlay.style.display = 'none';
            content.style.marginLeft = '0';
        } else {
            sidebar.style.left = '0';
            overlay.style.display = 'block';
            content.style.marginLeft = '250px';
        }
    });

    document.getElementById('overlay').addEventListener('click', function() {
        const sidebar = document.getElementById('sidebar');
        const overlay = document.getElementById('overlay');
        const content = document.getElementById('content');

        sidebar.style.left = '-250px';
        overlay.style.display = 'none';
        content.style.marginLeft = '0';
    });

    // Carregar dados iniciais
    carregarVoluntarios();
    carregarDoacoes();
    carregarAgendamentos();
});

// Funções para ações
function verDetalhes(agendaId) {
    const agendamento = agendamentos.find(a =>
        (a.agendaId === agendaId) || (a.id === agendaId)
    );

    if (agendamento) {
        const voluntario = obterDetalhesVoluntario(agendamento.volId);
        const doador = obterDetalhesDoador(agendamento.doaId);

        const modalBody = document.getElementById('modalDetalhesBody');
        modalBody.innerHTML = `
            <div class="row">
                <div class="col-md-6">
                    <h6 class="text-primary">Informações do Agendamento</h6>
                    <p><strong>Data:</strong> ${formatarData(agendamento.dataRetiro)}</p>
                    <p><strong>Hora:</strong> ${agendamento.horaRetiro}</p>
                    <p><strong>Status:</strong> <span class="status-badge ${obterClasseStatus(agendamento.status)}">${obterTextoStatus(agendamento.status)}</span></p>
                    <p><strong>Observações:</strong> ${agendamento.obsRetiro || 'Nenhuma'}</p>
                </div>
                <div class="col-md-6">
                    <h6 class="text-primary">Informações do Voluntário</h6>
                    <p><strong>Nome:</strong> ${voluntario ? (voluntario.volNome || voluntario.nome) : 'Não encontrado'}</p>
                    <p><strong>Telefone:</strong> ${voluntario ? (voluntario.volTelefone || voluntario.telefone || 'Não informado') : 'Não informado'}</p>
                    <p><strong>Cidade:</strong> ${voluntario ? (voluntario.volCidade || voluntario.cidade || 'Não informado') : 'Não informado'}</p>
                    
                    <h6 class="text-primary mt-3">Informações do Doador</h6>
                    <p><strong>Nome:</strong> ${doador ? (doador.doaNome || doador.nome) : 'Não encontrado'}</p>
                    <p><strong>Telefone:</strong> ${doador ? (doador.doaTelefone || doador.telefone || 'Não informado') : 'Não informado'}</p>
                    <p><strong>Endereço:</strong> ${doador ? `${doador.doaRua || ''}, ${doador.doaBairro || ''} - ${doador.doaCidade || ''}` : 'Não informado'}</p>
                </div>
            </div>
            
        `;
        const modal = new bootstrap.Modal(document.getElementById('modalDetalhes'));
        modal.show();
    }
}

async function cancelarAgendamento(agendaId) {
    if (confirm('Tem certeza que deseja cancelar este agendamento?')) {
        try {
            await fazerRequisicao(`${API_BASE}/${agendaId}`, {
                method: 'DELETE'
            });

            mostrarMensagem('Agendamento cancelado com sucesso!', 'success');
            await carregarAgendamentos();
        } catch (error) {
            console.error('Erro ao cancelar agendamento:', error);
            mostrarMensagem('Erro ao cancelar agendamento: ' + error.message, 'danger');
        }
    }
}