const API_DOACOES = 'http://localhost:8080/apis/doacoes';
const API_VOLUNTARIOS = 'http://localhost:8080/apis/voluntarios';
const API_DOADORES = 'http://localhost:8080/apis/doadores';

// Variáveis globais
let voluntarios = [];
let doadores = [];
let doacoes = [];
let doacoesFiltradas = [];
let modoEdicao = false;
let filtroVoluntarioAtivo = '';
let filtroDataAtivo = '';
let filtroValorAtivo = '';

// Função para fazer requisições
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

// Carregar voluntários
async function carregarVoluntarios() {
    try {
        const data = await fazerRequisicao(API_VOLUNTARIOS);
        voluntarios = Array.isArray(data) ? data : (data.voluntarios || data.content || []);

        const selectVoluntario = document.getElementById('volId');
        const selectFiltroVoluntario = document.getElementById('filtroVoluntario');

        if (voluntarios.length === 0) {
            selectVoluntario.innerHTML = '<option value="">Nenhum voluntário disponível</option>';
            selectFiltroVoluntario.innerHTML = '<option value="">Nenhum voluntário</option>';
            return;
        }

        selectVoluntario.innerHTML = '<option value="">Selecione um voluntário</option>';
        selectFiltroVoluntario.innerHTML = '<option value="">Todos os voluntários</option>';

        voluntarios.forEach(voluntario => {
            const option = document.createElement('option');
            option.value = voluntario.volId || voluntario.id;
            const nome = voluntario.volNome || voluntario.nome || `Voluntário ${voluntario.volId || voluntario.id}`;
            option.textContent = nome;
            selectVoluntario.appendChild(option);

            const optionFiltro = option.cloneNode(true);
            selectFiltroVoluntario.appendChild(optionFiltro);
        });

        // Atualizar estatística
        document.getElementById('totalVoluntarios').textContent = voluntarios.length;

    } catch (error) {
        console.error('Erro ao carregar voluntários:', error);
        mostrarMensagem('Erro ao carregar lista de voluntários: ' + error.message, 'danger');
    }
}

// Carregar doadores
async function carregarDoadores() {
    try {
        const data = await fazerRequisicao(API_DOADORES);
        doadores = Array.isArray(data) ? data : (data.doadores || data.content || []);

        const selectDoador = document.getElementById('doaId');

        if (doadores.length === 0) {
            selectDoador.innerHTML = '<option value="">Nenhum doador disponível</option>';
            return;
        }

        selectDoador.innerHTML = '<option value="">Selecione um doador</option>';

        doadores.forEach(doador => {
            const option = document.createElement('option');
            option.value = doador.doaId || doador.id;
            const nome = doador.doaNome || doador.nome || `Doador ${doador.doaId || doador.id}`;
            const cidade = doador.doaCidade ? ` - ${doador.doaCidade}` : '';
            option.textContent = nome + cidade;
            selectDoador.appendChild(option);
        });

        // Atualizar estatística
        document.getElementById('totalDoadores').textContent = doadores.length;

    } catch (error) {
        console.error('Erro ao carregar doadores:', error);
        mostrarMensagem('Erro ao carregar lista de doadores: ' + error.message, 'danger');
    }
}

// Carregar doações
async function carregarDoacoes() {
    const tbody = document.getElementById('tabelaDoacoes');

    try {
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted py-4">
                    <i class="fas fa-spinner fa-spin me-2"></i>Carregando doações...
                </td>
            </tr>
        `;

        const data = await fazerRequisicao(API_DOACOES);
        doacoes = Array.isArray(data.doacoes) ? data.doacoes : [];

        // Inicializar lista filtrada
        doacoesFiltradas = [...doacoes];

        // Atualizar tabela e estatísticas
        atualizarTabelaDoacoes();
        atualizarEstatisticas();

    } catch (error) {
        console.error('Erro ao carregar doações:', error);
        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted py-4">
                    <i class="fas fa-exclamation-triangle me-2"></i>Erro ao carregar doações
                    <br><small>${error.message}</small>
                </td>
            </tr>
        `;
        mostrarMensagem('Erro ao carregar doações: ' + error.message, 'danger');
    }
}

// Atualizar estatísticas
function atualizarEstatisticas() {
    // Total de doações
    const total = doacoes.reduce((sum, doacao) => sum + parseFloat(doacao.valorDoado || 0), 0);
    document.getElementById('totalDoacoes').textContent = formatarMoeda(total);

    // Doações de hoje
    const hoje = new Date().toISOString().split('T')[0];
    const doacoesHoje = doacoes.filter(doacao => doacao.dataDoacao === hoje).length;
    document.getElementById('doacoesHoje').textContent = doacoesHoje;
}

// Filtrar doações
function filtrarDoacoes() {
    const filtroVoluntario = document.getElementById('filtroVoluntario').value;
    const filtroData = document.getElementById('filtroData').value;
    const filtroValor = document.getElementById('filtroValor').value;

    filtroVoluntarioAtivo = filtroVoluntario;
    filtroDataAtivo = filtroData;
    filtroValorAtivo = filtroValor;

    // Aplicar filtros
    doacoesFiltradas = doacoes.filter(doacao => {
        let passaFiltro = true;

        // Filtro por voluntário
        if (filtroVoluntario && filtroVoluntario !== '') {
            const volId = doacao.volId || doacao.id;
            passaFiltro = passaFiltro && (volId.toString() === filtroVoluntario);
        }

        // Filtro por data
        if (filtroData && filtroData !== '') {
            passaFiltro = passaFiltro && (doacao.dataDoacao === filtroData);
        }

        // Filtro por valor
        if (filtroValor && filtroValor !== '') {
            const valor = parseFloat(doacao.valorDoado);
            switch(filtroValor) {
                case '0-50':
                    passaFiltro = passaFiltro && (valor <= 50);
                    break;
                case '50-100':
                    passaFiltro = passaFiltro && (valor > 50 && valor <= 100);
                    break;
                case '100-500':
                    passaFiltro = passaFiltro && (valor > 100 && valor <= 500);
                    break;
                case '500-1000':
                    passaFiltro = passaFiltro && (valor > 500 && valor <= 1000);
                    break;
                case '1000+':
                    passaFiltro = passaFiltro && (valor > 1000);
                    break;
            }
        }

        return passaFiltro;
    });

    // Ordenar por data mais recente primeiro
    doacoesFiltradas.sort((a, b) => new Date(b.dataDoacao) - new Date(a.dataDoacao));

    atualizarTabelaDoacoes();
}

// Limpar filtros
function limparFiltros() {
    document.getElementById('filtroVoluntario').value = '';
    document.getElementById('filtroData').value = '';
    document.getElementById('filtroValor').value = '';

    filtroVoluntarioAtivo = '';
    filtroDataAtivo = '';
    filtroValorAtivo = '';

    // Restaurar lista completa
    doacoesFiltradas = [...doacoes];
    doacoesFiltradas.sort((a, b) => new Date(b.dataDoacao) - new Date(a.dataDoacao));

    atualizarTabelaDoacoes();

    mostrarMensagem('Filtros limpos com sucesso!', 'info');
}

// Atualizar tabela de doações
function atualizarTabelaDoacoes() {
    const tbody = document.getElementById('tabelaDoacoes');
    const contador = document.getElementById('contadorDoacoes');

    // Atualizar contador
    contador.textContent = doacoesFiltradas.length;

    if (doacoesFiltradas.length === 0) {
        let mensagem = 'Nenhuma doação encontrada';

        if (filtroVoluntarioAtivo || filtroDataAtivo || filtroValorAtivo) {
            mensagem += ' com os filtros aplicados';
        }

        tbody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center text-muted py-4">
                    <i class="fas fa-donate fa-2x mb-2"></i><br>
                    ${mensagem}
                    ${(filtroVoluntarioAtivo || filtroDataAtivo || filtroValorAtivo) ?
            '<br><button class="btn btn-sm btn-outline-primary mt-2" onclick="limparFiltros()">Limpar Filtros</button>' :
            ''}
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = doacoesFiltradas.map(doacao => `
        <tr>
            <td>
                <strong>${formatarData(doacao.dataDoacao)}</strong>
            </td>
            <td>${obterNomeVoluntario(doacao.volId)}</td>
            <td>
                <span class="valor-destaque">${formatarMoeda(doacao.valorDoado)}</span>
            </td>
            <td>
                <div class="btn-group-actions">
                    <button class="btn btn-sm btn-info" onclick="verDetalhes(${doacao.doacaoId})" title="Ver detalhes">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-warning" onclick="editarDoacao(${doacao.doacaoId})" title="Editar">
                        <i class="fas fa-edit"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Salvar doação (criar ou editar)
async function salvarDoacao(event) {
    event.preventDefault();

    // Limpar validações anteriores
    limparValidacoes();

    const formData = new FormData(event.target);
    const doacaoId = document.getElementById('doacaoId').value;
    const dados = {
        volId: parseInt(formData.get('volId')),
        dataDoacao: formData.get('dataDoacao'),
        obsDoacao: formData.get('obsDoacao'),
        valorDoado: parseFloat(formData.get('valorDoado')),
        doaId: parseInt(formData.get('doaId'))
    };

    // Validações
    let isValid = true;
    let mensagensErro = [];

    // Validar voluntário
    if (!dados.volId || isNaN(dados.volId)) {
        marcarCampoInvalido('volId', 'Selecione um voluntário');
        isValid = false;
        mensagensErro.push('Selecione um voluntário');
    }

    // Validar doador
    if (!dados.doaId || isNaN(dados.doaId)) {
        marcarCampoInvalido('doaId', 'Selecione um doador');
        isValid = false;
        mensagensErro.push('Selecione um doador');
    }

    // Validar data
    if (!dados.dataDoacao) {
        marcarCampoInvalido('dataDoacao', 'Data da doação é obrigatória');
        isValid = false;
        mensagensErro.push('Data da doação é obrigatória');
    }

    // Validar valor
    if (!dados.valorDoado || dados.valorDoado <= 0) {
        marcarCampoInvalido('valorDoado', 'Valor da doação deve ser maior que zero');
        isValid = false;
        mensagensErro.push('Valor da doação deve ser maior que zero');
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
        btnSalvar.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>' + (modoEdicao ? 'Atualizando...' : 'Registrando...');
        btnSalvar.disabled = true;

        let response;
        if (modoEdicao && doacaoId) {
            // Editar doação existente
            response = await fetch(`${API_DOACOES}/${doacaoId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    volId: dados.volId.toString(),
                    dataDoacao: dados.dataDoacao,
                    obsDoacao: dados.obsDoacao || '',
                    valorDoado: dados.valorDoado.toString(),
                    doaId: dados.doaId.toString()
                })
            });
        } else {
            // Criar nova doação
            response = await fetch(API_DOACOES, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    volId: dados.volId.toString(),
                    dataDoacao: dados.dataDoacao,
                    obsDoacao: dados.obsDoacao || '',
                    valorDoado: dados.valorDoado.toString(),
                    doaId: dados.doaId.toString()
                })
            });
        }

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || (modoEdicao ? 'Erro ao atualizar doação' : 'Erro ao registrar doação'));
        }

        const resultado = await response.json();

        mostrarMensagem(modoEdicao ? 'Doação atualizada com sucesso!' : 'Doação registrada com sucesso!', 'success');

        // Limpar formulário e sair do modo edição
        cancelarEdicao();

        // Recarregar lista de doações
        await carregarDoacoes();

    } catch (error) {
        console.error('Erro ao salvar doação:', error);
        mostrarMensagem('Erro ao salvar doação: ' + error.message, 'danger');

        // Restaurar botão em caso de erro
        const btnSalvar = document.getElementById('btnSalvar');
        btnSalvar.innerHTML = modoEdicao ?
            '<i class="fas fa-edit me-2"></i>Atualizar Doação' :
            '<i class="fas fa-save me-2"></i>Registrar Doação';
        btnSalvar.disabled = false;
    }

    return false;
}

// Editar doação
function editarDoacao(doacaoId) {
    const doacao = doacoes.find(d => d.doacaoId === doacaoId);

    if (doacao) {
        // Preencher formulário com dados da doação
        document.getElementById('doacaoId').value = doacao.doacaoId;
        document.getElementById('volId').value = doacao.volId;
        document.getElementById('doaId').value = doacao.doaId;
        document.getElementById('dataDoacao').value = doacao.dataDoacao;
        document.getElementById('valorDoado').value = doacao.valorDoado;
        document.getElementById('obsDoacao').value = doacao.obsDoacao || '';

        // Mudar para modo edição
        modoEdicao = true;
        document.getElementById('formTitle').textContent = 'Editar Doação';
        document.getElementById('btnSalvar').innerHTML = '<i class="fas fa-edit me-2"></i>Atualizar Doação';
        document.getElementById('btnCancelarEdicao').style.display = 'inline-block';
        document.getElementById('btnLimpar').style.display = 'none';

        // Rolar para o topo do formulário
        document.getElementById('formDoacao').scrollIntoView({ behavior: 'smooth' });

        mostrarMensagem('Modo de edição ativado. Faça as alterações e clique em "Atualizar Doação".', 'info');
    }
}

// Cancelar edição
function cancelarEdicao() {
    modoEdicao = false;
    document.getElementById('doacaoId').value = '';
    document.getElementById('formTitle').textContent = 'Registrar Doação';
    document.getElementById('btnSalvar').innerHTML = '<i class="fas fa-save me-2"></i>Registrar Doação';
    document.getElementById('btnCancelarEdicao').style.display = 'none';
    document.getElementById('btnLimpar').style.display = 'inline-block';

    // Limpar formulário
    document.getElementById('formDoacao').reset();
    limparValidacoes();
}

// Ver detalhes da doação - FUNÇÃO CORRIGIDA
async function verDetalhes(doacaoId) {
    try {
        const response = await fetch(`${API_DOACOES}/${doacaoId}`);

        if (!response.ok) {
            throw new Error('Erro ao buscar detalhes da doação');
        }

        const data = await response.json();

        // CORREÇÃO: Verificar a estrutura da resposta
        const doacao = data.doacao || data;

        if (doacao) {
            const voluntario = obterDetalhesVoluntario(doacao.volId);
            const doador = obterDetalhesDoador(doacao.doaId);

            const modalBody = document.getElementById('modalDetalhesBody');
            modalBody.innerHTML = `
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="text-primary mb-3">
                            <i class="fas fa-donate me-2"></i>Informações da Doação
                        </h6>
                        
                        <div class="mb-2">
                            <strong>Data:</strong> ${formatarData(doacao.dataDoacao)}
                        </div>
                        <div class="mb-2">
                            <strong>Valor:</strong> 
                            <span class="valor-destaque fs-5">${formatarMoeda(doacao.valorDoado)}</span>
                        </div>
                        <div class="mb-2">
                            <strong>Observações:</strong> 
                            <p class="mt-1 p-2 bg-light rounded">${doacao.obsDoacao || 'Nenhuma observação registrada'}</p>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-primary mb-3">
                            <i class="fas fa-user-check me-2"></i>Informações do Voluntário
                        </h6>
                        <div class="mb-2">
                            <strong>Nome:</strong> 
                            ${voluntario ? (voluntario.volNome || voluntario.nome) : 'Não encontrado'}
                        </div>
                        <div class="mb-2">
                            <strong>Telefone:</strong> 
                            ${voluntario ? (voluntario.volTelefone || voluntario.telefone || 'Não informado') : 'Não informado'}
                        </div>
                        <div class="mb-2">
                            <strong>Email:</strong> 
                            ${voluntario ? (voluntario.volEmail || voluntario.email || 'Não informado') : 'Não informado'}
                        </div>
                        
                        <h6 class="text-primary mt-4 mb-3">
                            <i class="fas fa-user-friends me-2"></i>Informações do Doador
                        </h6>
                        <div class="mb-2">
                            <strong>Nome:</strong> 
                            ${doador ? (doador.doaNome || doador.nome) : 'Não encontrado'}
                        </div>
                        <div class="mb-2">
                            <strong>Telefone:</strong> 
                            ${doador ? (doador.doaTelefone || doador.telefone || 'Não informado') : 'Não informado'}
                        </div>
                        <div class="mb-2">
                            <strong>Endereço:</strong> 
                            ${doador ? `${doador.doaRua || ''}, ${doador.doaBairro || ''} - ${doador.doaCidade || ''}` : 'Não informado'}
                        </div>
                    </div>
                </div>
                <div class="row mt-4">
                    <div class="col-12">
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            <strong>Informação:</strong> Esta doação foi registrada no sistema em ${new Date().toLocaleDateString('pt-BR')}
                        </div>
                    </div>
                </div>
            `;

            const modal = new bootstrap.Modal(document.getElementById('modalDetalhes'));
            modal.show();
        } else {
            mostrarMensagem('Doação não encontrada', 'warning');
        }
    } catch (error) {
        console.error('Erro ao buscar detalhes da doação:', error);
        mostrarMensagem('Erro ao carregar detalhes: ' + error.message, 'danger');
    }
}

// Funções auxiliares
function obterNomeVoluntario(volId) {
    const voluntario = voluntarios.find(v => (v.volId === volId) || (v.id === volId));
    return voluntario ? (voluntario.volNome || voluntario.nome) : 'Voluntário não encontrado';
}

function obterDetalhesVoluntario(volId) {
    return voluntarios.find(v => (v.volId === volId) || (v.id === volId));
}

function obterDetalhesDoador(doaId) {
    return doadores.find(d => (d.doaId === doaId) || (d.id === doaId));
}

function formatarData(data) {
    try {
        if (!data) return 'Data não informada';
        const date = new Date(data + 'T00:00:00');
        if (isNaN(date.getTime())) return 'Data inválida';
        return date.toLocaleDateString('pt-BR');
    } catch (error) {
        return 'Data inválida';
    }
}

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    }).format(valor);
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
    const campos = ['volId', 'doaId', 'dataDoacao', 'valorDoado'];
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
    const campos = ['volId', 'doaId', 'dataDoacao', 'valorDoado'];
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

    // Definir data padrão para hoje
    const hoje = new Date().toISOString().split('T')[0];
    document.getElementById('dataDoacao').value = hoje;

    // Menu toggle
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

    // Fechar sidebar ao clicar no overlay
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
    carregarDoadores();
    carregarDoacoes();
});