package br.com.hospital.model;

import static br.com.hospital.model.ModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProcedimentoCirurgicoTest {

    @Test
    void registraAgendamentoExecucaoEObservacoes() {
        ProcedimentoCirurgico procedimento = procedimento();
        assertEquals(SituacaoProcedimento.AGENDADO, procedimento.getSituacao());
        assertNull(procedimento.getInicioEfetivo());
        assertNull(procedimento.getFimEfetivo());
        procedimento.registrarObservacoesPre("Jejum confirmado");
        procedimento.iniciar(HORARIO.plusMinutes(15));
        procedimento.concluir(HORARIO.plusHours(1));
        procedimento.registrarObservacoesPos("Sem intercorrências");
        assertEquals(HORARIO, procedimento.getDataHoraPrevista());
        assertEquals(HORARIO.plusMinutes(15), procedimento.getInicioEfetivo());
        assertEquals(HORARIO.plusHours(1), procedimento.getFimEfetivo());
        assertEquals("Jejum confirmado", procedimento.getObservacoesPre());
        assertEquals("Sem intercorrências", procedimento.getObservacoesPos());
        assertEquals(SituacaoProcedimento.CONCLUIDO, procedimento.getSituacao());
        assertSame(procedimento, procedimento.getCirurgiao().getProcedimentos().getFirst());
    }

    @Test
    void naoConcluiAntesDeIniciar() {
        ProcedimentoCirurgico procedimento = procedimento();
        assertThrows(IllegalStateException.class, () -> procedimento.concluir(HORARIO));
        assertEquals(SituacaoProcedimento.AGENDADO, procedimento.getSituacao());
        assertNull(procedimento.getFimEfetivo());
    }

    @Test
    void rejeitaHorarioFinalAnteriorAoInicioSemAlterarEstado() {
        ProcedimentoCirurgico procedimento = procedimento();
        procedimento.iniciar(HORARIO);
        assertThrows(IllegalArgumentException.class, () -> procedimento.concluir(HORARIO.minusMinutes(1)));
        assertThrows(IllegalArgumentException.class, () -> procedimento.concluir(null));
        assertNull(procedimento.getFimEfetivo());
        assertEquals(SituacaoProcedimento.EM_ANDAMENTO, procedimento.getSituacao());
        procedimento.concluir(HORARIO);
        assertEquals(SituacaoProcedimento.CONCLUIDO, procedimento.getSituacao());
    }

    @Test
    void cancelamentoImpedeExecucao() {
        ProcedimentoCirurgico procedimento = procedimento();
        procedimento.cancelar();
        assertThrows(IllegalStateException.class, () -> procedimento.iniciar(HORARIO));
        assertThrows(IllegalStateException.class, () -> procedimento.concluir(HORARIO));
        assertThrows(IllegalStateException.class, procedimento::cancelar);
        assertEquals(SituacaoProcedimento.CANCELADO, procedimento.getSituacao());
    }

    @Test
    void procedimentoIniciadoOuConcluidoNaoPodeReiniciarOuCancelar() {
        ProcedimentoCirurgico procedimento = procedimento();
        procedimento.iniciar(HORARIO);
        assertThrows(IllegalStateException.class, () -> procedimento.iniciar(HORARIO));
        assertThrows(IllegalStateException.class, procedimento::cancelar);
        procedimento.concluir(HORARIO.plusHours(1));
        assertThrows(IllegalStateException.class, () -> procedimento.iniciar(HORARIO));
        assertThrows(IllegalStateException.class, procedimento::cancelar);
        assertThrows(IllegalStateException.class, () -> procedimento.concluir(HORARIO.plusHours(2)));
    }

    @Test
    void observacoesRespeitamFasesDoProcedimento() {
        ProcedimentoCirurgico procedimento = procedimento();
        assertThrows(IllegalStateException.class, () -> procedimento.registrarObservacoesPos("Texto"));
        assertThrows(IllegalArgumentException.class, () -> procedimento.registrarObservacoesPre(" "));
        procedimento.iniciar(HORARIO);
        assertThrows(IllegalStateException.class, () -> procedimento.registrarObservacoesPre("Texto"));
        procedimento.concluir(HORARIO.plusHours(1));
        assertThrows(IllegalArgumentException.class, () -> procedimento.registrarObservacoesPos(null));
    }

    @Test
    void dadosInvalidosNaoRegistramProcedimentoNoCirurgiao() {
        Cirurgiao cirurgiao = cirurgiao();
        assertThrows(IllegalArgumentException.class, () -> new ProcedimentoCirurgico(1L,
                paciente(), cirurgiao, " ", "Abdominal", HORARIO));
        assertThrows(IllegalArgumentException.class, () -> new ProcedimentoCirurgico(1L,
                paciente(), cirurgiao, "Descrição", null, HORARIO));
        assertThrows(IllegalArgumentException.class, () -> new ProcedimentoCirurgico(1L,
                paciente(), cirurgiao, "Descrição", "Abdominal", null));
        assertThrows(IllegalArgumentException.class, () -> new ProcedimentoCirurgico(1L,
                paciente(), null, "Descrição", "Abdominal", HORARIO));
        assertTrue(cirurgiao.getProcedimentos().isEmpty());
        ProcedimentoCirurgico procedimento = procedimento();
        assertThrows(IllegalArgumentException.class, () -> procedimento.iniciar(null));
        assertEquals(SituacaoProcedimento.AGENDADO, procedimento.getSituacao());
    }
}
