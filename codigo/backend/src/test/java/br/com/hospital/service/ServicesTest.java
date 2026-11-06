package br.com.hospital.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.Consulta;
import br.com.hospital.model.ConsultaRotina;
import br.com.hospital.model.Especialidade;
import br.com.hospital.model.Internacao;
import br.com.hospital.model.MedicoClinicoGeral;
import br.com.hospital.model.MedicoEspecialista;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.PrioridadeEmergencia;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.model.Quarto;
import br.com.hospital.model.SituacaoConsulta;
import br.com.hospital.model.SituacaoEmergencia;
import br.com.hospital.model.SituacaoProcedimento;
import br.com.hospital.model.SituacaoQuarto;

class ServicesTest {
    private static final LocalDateTime HORARIO = LocalDateTime.of(2026, 11, 5, 10, 0);
    private PacienteService pacientes;
    private ProfissionalSaudeService profissionais;
    private QuartoService quartos;
    private ConsultaService consultas;
    private InternacaoService internacoes;
    private ProcedimentoCirurgicoService procedimentos;
    private AtendimentoEmergenciaService emergencias;
    private HistoricoMedicoService historicos;
    private Paciente paciente;
    private MedicoClinicoGeral clinico;

    @BeforeEach
    void preparar() {
        DadosHospitalares dados = new DadosHospitalares();
        pacientes = new PacienteService(dados);
        profissionais = new ProfissionalSaudeService(dados);
        quartos = new QuartoService(dados);
        consultas = new ConsultaService(dados);
        internacoes = new InternacaoService(dados);
        procedimentos = new ProcedimentoCirurgicoService(dados);
        emergencias = new AtendimentoEmergenciaService(dados);
        historicos = new HistoricoMedicoService(dados);
        paciente = cadastrarPaciente("12345678900");
        clinico = profissionais.cadastrarClinicoGeral("Bruno", "CRM-1", "31999999999", "bruno@example.com");
    }

    @Test
    void validaCadastrosEDuplicidadesSemAlterarEstado() {
        assertThrows(IllegalStateException.class, () -> cadastrarPaciente("123.456.789-00"));
        assertThrows(IllegalArgumentException.class, () -> cadastrarPaciente("123"));
        assertThrows(IllegalArgumentException.class, () -> pacientes.cadastrar("Ana", "11111111111",
                LocalDate.now().plusDays(1), "31", "Rua A", "a@example.com"));
        assertThrows(IllegalStateException.class, () -> profissionais.cadastrarCirurgiao(
                "Carlos", " crm-1 ", "31", "c@example.com", "Geral"));
        assertThrows(IllegalArgumentException.class, () -> profissionais.cadastrarEspecialista(
                "Carlos", "CRM-2", "31", "c@example.com", null));
        Quarto quarto = quartos.cadastrar("101", 1, 2);
        assertThrows(IllegalStateException.class, () -> quartos.cadastrar(" 101 ", 2, 1));
        assertEquals(1, pacientes.listar().size());
        assertEquals(1, profissionais.listar().size());
        assertSame(quarto, quartos.buscarPorId(quarto.getId()));
    }

    @Test
    void atualizacaoInvalidaNaoAlteraContato() {
        assertThrows(IllegalArgumentException.class, () -> pacientes.atualizar(paciente.getId(), "Outro", " ", "Rua", "email"));
        assertEquals("Ana", paciente.getNome());
        pacientes.atualizar(paciente.getId(), "Ana Maria", "32", "Rua B", "ana@example.com");
        profissionais.atualizar(clinico.getId(), "Bruno Silva", "32", "bruno@example.com");
        assertEquals("Ana Maria", paciente.getNome());
        assertEquals("Bruno Silva", clinico.getNome());
    }

    @Test
    void identificadoresAusentesOuDesconhecidosNaoCriamAtendimento() {
        assertThrows(IllegalArgumentException.class, () -> pacientes.buscarPorId(null));
        assertThrows(NoSuchElementException.class, () -> pacientes.buscarPorId(999L));
        assertThrows(NoSuchElementException.class, () -> consultas.agendarRotina(
                paciente.getId(), 999L, HORARIO, "Rotina"));
        assertTrue(consultas.listar().isEmpty());
        assertTrue(paciente.getConsultas().isEmpty());
    }

    @Test
    void impedeAgendaDuplicadaSemPoluirHistoricos() {
        Consulta primeira = rotina(HORARIO);
        Paciente outro = cadastrarPaciente("98765432100");
        assertThrows(IllegalStateException.class, () -> consultas.agendarRotina(
                outro.getId(), clinico.getId(), HORARIO, "Rotina"));
        assertEquals(List.of(primeira), consultas.listar());
        assertTrue(outro.getConsultas().isEmpty());
        assertEquals(1, clinico.getConsultas().size());
        assertDoesNotThrow(() -> rotina(HORARIO.plusHours(1)));
    }

    @Test
    void reagendamentoConflitantePreservaHorarioESituacao() {
        Consulta primeira = rotina(HORARIO);
        Consulta segunda = rotina(HORARIO.plusHours(1));
        consultas.alterarSituacao(segunda.getId(), SituacaoConsulta.CONFIRMADA);
        assertThrows(IllegalStateException.class, () -> consultas.reagendar(segunda.getId(), HORARIO));
        assertEquals(HORARIO.plusHours(1), segunda.getDataHora());
        assertEquals(SituacaoConsulta.CONFIRMADA, segunda.getSituacao());
        consultas.alterarSituacao(primeira.getId(), SituacaoConsulta.CANCELADA);
        consultas.reagendar(segunda.getId(), HORARIO);
        assertEquals(HORARIO, segunda.getDataHora());
        assertEquals(2, paciente.getConsultas().size());
    }

    @Test
    void cancelamentoEAusenciaLiberamAgendaSemApagarRegistros() {
        Consulta primeira = rotina(HORARIO);
        consultas.alterarSituacao(primeira.getId(), SituacaoConsulta.NAO_COMPARECEU);
        Consulta segunda = rotina(HORARIO);
        consultas.alterarSituacao(segunda.getId(), SituacaoConsulta.CANCELADA);
        assertThrows(IllegalStateException.class, () -> consultas.alterarSituacao(segunda.getId(), SituacaoConsulta.CONCLUIDA));
        consultas.reagendar(segunda.getId(), HORARIO.plusDays(1));
        assertEquals(2, historicos.consultar(paciente.getId()).consultas().size());
    }

    @Test
    void fluxoDeConsultaPreservaObservacoesEImpedeReabertura() {
        Consulta consulta = rotina(HORARIO);
        consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.CONFIRMADA);
        consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.EM_ATENDIMENTO);
        consultas.registrarObservacoes(consulta.getId(), "Retorno em 30 dias");
        consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.CONCLUIDA);
        assertThrows(IllegalStateException.class, () -> consultas.reagendar(consulta.getId(), HORARIO.plusDays(1)));
        assertEquals("Retorno em 30 dias", historicos.consultar(paciente.getId()).consultas().getFirst().getObservacoes());
    }

    @Test
    void especialidadeEEncaminhamentoRespeitamCategoriasEAgenda() {
        MedicoEspecialista especialista = profissionais.cadastrarEspecialista("Carla", "CRM-2", "31",
                "c@example.com", Especialidade.CARDIOLOGIA);
        assertThrows(IllegalArgumentException.class, () -> consultas.agendarEspecializada(paciente.getId(),
                especialista.getId(), HORARIO, "Dor", Especialidade.NEUROLOGIA));
        assertThrows(IllegalArgumentException.class, () -> consultas.agendarRotina(paciente.getId(), especialista.getId(), HORARIO, "Rotina"));
        assertTrue(paciente.getConsultas().isEmpty());
        assertEquals(List.of(especialista), profissionais.listarDisponiveis(Especialidade.CARDIOLOGIA, HORARIO));
        Consulta consulta = consultas.encaminhar(clinico.getId(), paciente.getId(), especialista.getId(), HORARIO, "Avaliação");
        assertTrue(profissionais.listarDisponiveis(Especialidade.CARDIOLOGIA, HORARIO).isEmpty());
        assertThrows(IllegalStateException.class, () -> consultas.encaminhar(clinico.getId(), paciente.getId(), especialista.getId(), HORARIO, "Retorno"));
        consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.CANCELADA);
        assertEquals(List.of(especialista), profissionais.listarDisponiveis(Especialidade.CARDIOLOGIA, HORARIO));
    }

    @Test
    void capacidadeAltaEHistoricoDeInternacaoPermanecemConsistentes() {
        Quarto quarto = quartos.cadastrar("101", 1, 1);
        Internacao primeira = internacoes.internar(paciente.getId(), clinico.getId(), quarto.getId(), HORARIO.toLocalDate(), null, "Monitorar");
        Paciente outro = cadastrarPaciente("98765432100");
        assertThrows(IllegalStateException.class, () -> internacoes.internar(outro.getId(), clinico.getId(), quarto.getId(), HORARIO.toLocalDate(), null, null));
        assertTrue(outro.getInternacoes().isEmpty());
        assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
        assertThrows(IllegalStateException.class, () -> quartos.colocarEmManutencao(quarto.getId()));
        assertThrows(IllegalArgumentException.class, () -> internacoes.darAlta(primeira.getId(), HORARIO.toLocalDate().minusDays(1)));
        assertEquals(1, quarto.getQuantidadeAtual());
        internacoes.darAlta(primeira.getId(), HORARIO.toLocalDate().plusDays(1));
        assertTrue(quarto.possuiVaga());
        internacoes.internar(paciente.getId(), clinico.getId(), quarto.getId(), HORARIO.toLocalDate().plusDays(2), null, null);
        assertEquals(2, historicos.consultar(paciente.getId()).internacoes().size());
        assertEquals(1, internacoes.listarAtivas().size());
    }

    @Test
    void manutencaoEDuplaInternacaoNaoDeixamOcupacaoParcial() {
        Quarto primeiro = quartos.cadastrar("101", 1, 2);
        Quarto segundo = quartos.cadastrar("102", 1, 1);
        quartos.colocarEmManutencao(primeiro.getId());
        assertFalse(quartos.listarDisponiveis().contains(primeiro));
        assertThrows(IllegalStateException.class, () -> internacoes.internar(paciente.getId(), clinico.getId(), primeiro.getId(), HORARIO.toLocalDate(), null, null));
        quartos.finalizarManutencao(primeiro.getId());
        internacoes.internar(paciente.getId(), clinico.getId(), primeiro.getId(), HORARIO.toLocalDate(), null, null);
        assertThrows(IllegalStateException.class, () -> internacoes.internar(paciente.getId(), clinico.getId(), segundo.getId(), HORARIO.toLocalDate(), null, null));
        assertEquals(0, segundo.getQuantidadeAtual());
        assertEquals(1, primeiro.getQuantidadeAtual());
        assertTrue(primeiro.possuiVaga());
    }

    @Test
    void cirurgiaExigeCirurgiaoAgendaLivreEInicioAntesDaConclusao() {
        Cirurgiao cirurgiao = profissionais.cadastrarCirurgiao("Diego", "CRM-3", "31", "d@example.com", "Geral");
        assertThrows(IllegalArgumentException.class, () -> procedimentos.agendar(paciente.getId(), clinico.getId(), "Descrição", "Geral", HORARIO));
        ProcedimentoCirurgico procedimento = procedimentos.agendar(paciente.getId(), cirurgiao.getId(), "Descrição", "Geral", HORARIO);
        assertThrows(IllegalStateException.class, () -> procedimentos.agendar(paciente.getId(), cirurgiao.getId(), "Outra", "Geral", HORARIO));
        assertEquals(1, cirurgiao.getProcedimentos().size());
        assertThrows(IllegalStateException.class, () -> procedimentos.concluir(procedimento.getId(), HORARIO));
        procedimentos.registrarObservacoesPre(procedimento.getId(), "Jejum");
        procedimentos.iniciar(procedimento.getId(), HORARIO);
        assertThrows(IllegalArgumentException.class, () -> procedimentos.concluir(procedimento.getId(), HORARIO.minusMinutes(1)));
        procedimentos.concluir(procedimento.getId(), HORARIO.plusHours(1));
        procedimentos.registrarObservacoesPos(procedimento.getId(), "Sem intercorrências");
        assertEquals(SituacaoProcedimento.CONCLUIDO, procedimento.getSituacao());
        assertEquals("Sem intercorrências", historicos.consultar(paciente.getId()).procedimentos().getFirst().getObservacoesPos());
    }

    @Test
    void cancelamentoCirurgicoLiberaAgendaEPreservaHistorico() {
        Cirurgiao cirurgiao = profissionais.cadastrarCirurgiao("Diego", "CRM-3", "31", "d@example.com", "Geral");
        ProcedimentoCirurgico primeiro = procedimentos.agendar(paciente.getId(), cirurgiao.getId(), "Descrição", "Geral", HORARIO);
        procedimentos.cancelar(primeiro.getId());
        procedimentos.agendar(paciente.getId(), cirurgiao.getId(), "Nova", "Geral", HORARIO);
        assertEquals(2, historicos.consultar(paciente.getId()).procedimentos().size());
    }

    @Test
    void emergenciasSaoOrdenadasPorGravidadeComEmpatePorRegistro() {
        AtendimentoEmergencia baixa = emergencias.registrar(paciente.getId(), clinico.getId(), PrioridadeEmergencia.BAIXA);
        AtendimentoEmergencia critica = emergencias.registrar(paciente.getId(), clinico.getId(), PrioridadeEmergencia.CRITICA);
        AtendimentoEmergencia outraCritica = emergencias.registrar(paciente.getId(), clinico.getId(), PrioridadeEmergencia.CRITICA);
        assertEquals(List.of(critica, outraCritica, baixa), emergencias.listarAguardandoAvaliacao());
        assertThrows(IllegalStateException.class, () -> emergencias.concluir(critica.getId()));
        emergencias.iniciar(critica.getId());
        emergencias.concluir(critica.getId());
        assertEquals(List.of(outraCritica, baixa), emergencias.listarAguardandoAvaliacao());
    }

    @Test
    void profissionalNaoIniciaConsultaEnquantoAtendeEmergenciaENemOInverso() {
        Consulta consulta = rotina(HORARIO);
        AtendimentoEmergencia emergencia = emergencias.registrar(paciente.getId(), clinico.getId(), PrioridadeEmergencia.ALTA);
        emergencias.iniciar(emergencia.getId());
        assertThrows(IllegalStateException.class, () -> consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.EM_ATENDIMENTO));
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        emergencias.concluir(emergencia.getId());
        consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.EM_ATENDIMENTO);
        AtendimentoEmergencia outra = emergencias.registrar(paciente.getId(), clinico.getId(), PrioridadeEmergencia.CRITICA);
        assertThrows(IllegalStateException.class, () -> emergencias.iniciar(outra.getId()));
        assertEquals(SituacaoEmergencia.AGUARDANDO_AVALIACAO, outra.getSituacao());
    }

    @Test
    void profissionalNaoIniciaCirurgiaEnquantoAtendeEmergencia() {
        Cirurgiao cirurgiao = profissionais.cadastrarCirurgiao("Diego", "CRM-3", "31", "d@example.com", "Geral");
        ProcedimentoCirurgico cirurgia = procedimentos.agendar(paciente.getId(), cirurgiao.getId(), "Descrição", "Geral", HORARIO);
        AtendimentoEmergencia emergencia = emergencias.registrar(paciente.getId(), cirurgiao.getId(), PrioridadeEmergencia.ALTA);
        emergencias.iniciar(emergencia.getId());
        assertThrows(IllegalStateException.class, () -> procedimentos.iniciar(cirurgia.getId(), HORARIO));
        assertNull(cirurgia.getInicioEfetivo());
    }

    @Test
    void historicoFiltraPacienteEListasNaoPermitemRemocao() {
        rotina(HORARIO);
        Paciente outro = cadastrarPaciente("98765432100");
        emergencias.registrar(outro.getId(), clinico.getId(), PrioridadeEmergencia.BAIXA);
        var historico = historicos.consultar(paciente.getId());
        assertEquals(1, historico.consultas().size());
        assertTrue(historico.emergencias().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> historico.consultas().clear());
        assertThrows(UnsupportedOperationException.class, () -> pacientes.listar().clear());
        assertThrows(IllegalStateException.class, () -> pacientes.excluir(paciente.getId()));
        assertThrows(IllegalStateException.class, () -> profissionais.excluir(clinico.getId()));
        assertThrows(IllegalStateException.class, () -> pacientes.excluir(outro.getId()));
    }

    @Test
    void permiteExcluirCadastrosSemVinculosEProtegeQuartoComHistorico() {
        Paciente outro = cadastrarPaciente("98765432100");
        pacientes.excluir(outro.getId());
        assertThrows(NoSuchElementException.class, () -> pacientes.buscarPorId(outro.getId()));
        Quarto quarto = quartos.cadastrar("101", 1, 1);
        Internacao internacao = internacoes.internar(paciente.getId(), clinico.getId(), quarto.getId(), HORARIO.toLocalDate(), null, null);
        internacoes.darAlta(internacao.getId(), HORARIO.toLocalDate());
        assertThrows(IllegalStateException.class, () -> quartos.excluir(quarto.getId()));
        Quarto vazio = quartos.cadastrar("102", 1, 1);
        quartos.excluir(vazio.getId());
        assertEquals(List.of(quarto), quartos.listar());
    }

    @Test
    void agendamentosConcorrentesNaoReservamMesmoHorario() throws Exception {
        CountDownLatch inicio = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var tarefa = (java.util.concurrent.Callable<Boolean>) () -> {
                if (!inicio.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Tempo de espera excedido.");
                }
                try {
                    rotina(HORARIO);
                    return true;
                } catch (IllegalStateException conflito) {
                    return false;
                }
            };
            var primeira = executor.submit(tarefa);
            var segunda = executor.submit(tarefa);
            inicio.countDown();
            assertNotEquals(primeira.get(5, TimeUnit.SECONDS), segunda.get(5, TimeUnit.SECONDS));
        }
        assertEquals(1, consultas.listar().size());
        assertEquals(1, paciente.getConsultas().size());
    }

    @Test
    void internacoesConcorrentesNaoUltrapassamCapacidade() throws Exception {
        Quarto quarto = quartos.cadastrar("101", 1, 1);
        Paciente outro = cadastrarPaciente("98765432100");
        CountDownLatch inicio = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var tarefa = (java.util.function.Function<Paciente, Boolean>) p -> {
                try {
                    if (!inicio.await(5, TimeUnit.SECONDS)) {
                        throw new AssertionError("Tempo de espera excedido.");
                    }
                    internacoes.internar(p.getId(), clinico.getId(), quarto.getId(), HORARIO.toLocalDate(), null, null);
                    return true;
                } catch (IllegalStateException conflito) {
                    return false;
                } catch (InterruptedException interrupcao) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(interrupcao);
                }
            };
            var primeira = executor.submit(() -> tarefa.apply(paciente));
            var segunda = executor.submit(() -> tarefa.apply(outro));
            inicio.countDown();
            assertNotEquals(primeira.get(5, TimeUnit.SECONDS), segunda.get(5, TimeUnit.SECONDS));
        }
        assertEquals(1, quarto.getQuantidadeAtual());
        assertEquals(1, internacoes.listar().size());
        assertEquals(1, paciente.getInternacoes().size() + outro.getInternacoes().size());
    }

    private Paciente cadastrarPaciente(String cpf) {
        return pacientes.cadastrar("Ana", cpf, LocalDate.of(1990, 1, 1), "31999999999", "Rua A", "ana@example.com");
    }

    private ConsultaRotina rotina(LocalDateTime horario) {
        return consultas.agendarRotina(paciente.getId(), clinico.getId(), horario, "Avaliação");
    }
}
