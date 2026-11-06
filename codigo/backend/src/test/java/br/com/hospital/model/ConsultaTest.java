package br.com.hospital.model;

import static br.com.hospital.model.ModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ConsultaTest {

    @Test
    void realizaConsultaComConfirmacaoEObservacoes() {
        Consulta consulta = consulta();
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        consulta.confirmar();
        assertEquals(SituacaoConsulta.CONFIRMADA, consulta.getSituacao());
        consulta.iniciar();
        consulta.registrarObservacoes("Avaliação realizada");
        consulta.concluir();
        consulta.registrarObservacoes("Retorno em 30 dias");
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
        assertEquals("Retorno em 30 dias", consulta.getObservacoes());
    }

    @Test
    void permiteInicioSemConfirmacao() {
        ConsultaRotina consulta = consulta();
        ((MedicoClinicoGeral) consulta.getMedico()).consulta(consulta);
        assertEquals(SituacaoConsulta.EM_ATENDIMENTO, consulta.getSituacao());
    }

    @ParameterizedTest
    @EnumSource(value = SituacaoConsulta.class, names = {"AGENDADA", "CONFIRMADA", "CANCELADA", "NAO_COMPARECEU"})
    void naoConcluiConsultaQueNaoFoiIniciada(SituacaoConsulta situacao) {
        Consulta consulta = naSituacao(situacao);
        assertThrows(IllegalStateException.class, consulta::concluir);
        assertEquals(situacao, consulta.getSituacao());
    }

    @ParameterizedTest
    @EnumSource(value = SituacaoConsulta.class, names = {"CANCELADA", "NAO_COMPARECEU", "CONCLUIDA"})
    void estadosFinaisNaoPermitemAlteracaoDireta(SituacaoConsulta situacao) {
        Consulta consulta = naSituacao(situacao);
        assertThrows(IllegalStateException.class, consulta::iniciar);
        assertThrows(IllegalStateException.class, consulta::confirmar);
        assertThrows(IllegalStateException.class, consulta::cancelar);
        assertThrows(IllegalStateException.class, consulta::registrarNaoComparecimento);
        assertThrows(IllegalStateException.class, () -> consulta.controlarConsulta(SituacaoConsulta.AGENDADA));
        assertEquals(situacao, consulta.getSituacao());
    }

    @Test
    void consultaConcluidaNaoPodeSerReagendadaNemConcluidaNovamente() {
        Consulta consulta = naSituacao(SituacaoConsulta.CONCLUIDA);
        assertThrows(IllegalStateException.class, () -> consulta.agendarConsulta(HORARIO.plusDays(1)));
        assertThrows(IllegalStateException.class, consulta::concluir);
        assertEquals(HORARIO, consulta.getDataHora());
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
    }

    @ParameterizedTest
    @EnumSource(value = SituacaoConsulta.class, names = {"AGENDADA", "CONFIRMADA", "CANCELADA", "NAO_COMPARECEU"})
    void novoAgendamentoExplicitoPermiteReiniciarFluxo(SituacaoConsulta situacao) {
        Consulta consulta = naSituacao(situacao);
        consulta.agendarConsulta(HORARIO.plusDays(1));
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        assertEquals(HORARIO.plusDays(1), consulta.getDataHora());
        consulta.iniciar();
        consulta.concluir();
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
        assertEquals(1, consulta.getPaciente().getConsultas().size());
    }

    @Test
    void consultaEmAtendimentoNaoPodeSerReagendadaCanceladaOuMarcadaComoAusencia() {
        Consulta consulta = naSituacao(SituacaoConsulta.EM_ATENDIMENTO);
        assertThrows(IllegalStateException.class, () -> consulta.agendarConsulta(HORARIO.plusDays(1)));
        assertThrows(IllegalStateException.class, consulta::cancelar);
        assertThrows(IllegalStateException.class, consulta::registrarNaoComparecimento);
        assertThrows(IllegalStateException.class, consulta::iniciar);
        assertThrows(IllegalStateException.class, consulta::confirmar);
        assertEquals(SituacaoConsulta.EM_ATENDIMENTO, consulta.getSituacao());
    }

    @Test
    void controleDeSituacaoUsaAsMesmasRegras() {
        Consulta consulta = consulta();
        assertThrows(IllegalStateException.class, () -> consulta.controlarConsulta(SituacaoConsulta.CONCLUIDA));
        assertThrows(IllegalArgumentException.class, () -> consulta.controlarConsulta(null));
        consulta.controlarConsulta(SituacaoConsulta.CONFIRMADA);
        consulta.controlarConsulta(SituacaoConsulta.EM_ATENDIMENTO);
        consulta.controlarConsulta(SituacaoConsulta.CONCLUIDA);
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
    }

    @Test
    void observacoesExigemAtendimentoEConteudo() {
        Consulta consulta = consulta();
        assertThrows(IllegalStateException.class, () -> consulta.registrarObservacoes("Texto"));
        consulta.iniciar();
        assertThrows(IllegalArgumentException.class, () -> consulta.registrarObservacoes(" "));
        assertThrows(IllegalArgumentException.class, () -> consulta.registrarObservacoes(null));
    }

    @Test
    void rejeitaDadosObrigatoriosAusentesSemRegistrarNoHistorico() {
        Paciente paciente = paciente();
        MedicoClinicoGeral medico = clinico();
        assertThrows(IllegalArgumentException.class, () -> new ConsultaRotina(1L, null, medico, HORARIO, "Motivo"));
        assertThrows(IllegalArgumentException.class, () -> new ConsultaRotina(1L, paciente, null, HORARIO, "Motivo"));
        assertThrows(IllegalArgumentException.class, () -> new ConsultaRotina(1L, paciente, medico, null, "Motivo"));
        assertThrows(IllegalArgumentException.class, () -> new ConsultaRotina(1L, paciente, medico, HORARIO, " "));
        assertTrue(paciente.getConsultas().isEmpty());
        assertTrue(medico.getConsultas().isEmpty());
        Consulta consulta = new ConsultaRotina(1L, paciente, medico, HORARIO, "Motivo");
        assertThrows(IllegalArgumentException.class, () -> consulta.agendarConsulta(null));
        assertEquals(HORARIO, consulta.getDataHora());
    }

    @ParameterizedTest
    @EnumSource(Especialidade.class)
    void especialistaAtendeSuaEspecialidade(Especialidade especialidade) {
        MedicoEspecialista medico = especialista(especialidade);
        ConsultaEspecializada consulta = new ConsultaEspecializada(1L, paciente(), medico,
                HORARIO, "Avaliação especializada", especialidade);
        medico.realizaAtend(consulta);
        assertEquals(especialidade, consulta.getEspecialidade());
        assertEquals(SituacaoConsulta.EM_ATENDIMENTO, consulta.getSituacao());
    }

    @Test
    void rejeitaEspecialidadeIncompativelSemPoluirHistoricos() {
        Paciente paciente = paciente();
        MedicoEspecialista medico = especialista(Especialidade.CARDIOLOGIA);
        assertThrows(IllegalArgumentException.class, () -> new ConsultaEspecializada(1L, paciente,
                medico, HORARIO, "Avaliação", Especialidade.NEUROLOGIA));
        assertThrows(IllegalArgumentException.class, () -> new ConsultaEspecializada(1L, paciente,
                medico, HORARIO, "Avaliação", null));
        assertTrue(paciente.getConsultas().isEmpty());
        assertTrue(medico.getConsultas().isEmpty());
    }

    @Test
    void rejeitaCategoriaDeMedicoIncompativel() {
        assertThrows(IllegalArgumentException.class, () -> new ConsultaEspecializada(1L,
                paciente(), clinico(), HORARIO, "Avaliação", Especialidade.CARDIOLOGIA));
        assertThrows(IllegalArgumentException.class, () -> new ConsultaRotina(1L,
                paciente(), especialista(Especialidade.CARDIOLOGIA), HORARIO, "Avaliação"));
        assertThrows(IllegalArgumentException.class, () -> new ConsultaRotina(1L,
                paciente(), cirurgiao(), HORARIO, "Avaliação"));
    }

    @Test
    void encaminhamentoRegistraConsultaParaEspecialista() {
        Paciente paciente = paciente();
        MedicoEspecialista especialista = especialista(Especialidade.ORTOPEDIA);
        ConsultaEspecializada consulta = clinico().encaminhar(9L, paciente, especialista, HORARIO, "Dor no joelho");
        assertSame(paciente, consulta.getPaciente());
        assertSame(especialista, consulta.getMedico());
        assertEquals(Especialidade.ORTOPEDIA, consulta.getEspecialidade());
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        assertEquals(1, paciente.getConsultas().size());
        assertEquals(1, especialista.getConsultas().size());
    }

    private Consulta naSituacao(SituacaoConsulta situacao) {
        Consulta consulta = consulta();
        switch (situacao) {
            case AGENDADA -> { }
            case CONFIRMADA -> consulta.confirmar();
            case EM_ATENDIMENTO -> consulta.iniciar();
            case CONCLUIDA -> { consulta.iniciar(); consulta.concluir(); }
            case CANCELADA -> consulta.cancelar();
            case NAO_COMPARECEU -> consulta.registrarNaoComparecimento();
        }
        return consulta;
    }
}
