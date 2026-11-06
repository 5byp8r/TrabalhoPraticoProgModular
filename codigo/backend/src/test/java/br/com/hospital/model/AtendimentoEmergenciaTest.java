package br.com.hospital.model;

import static br.com.hospital.model.ModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AtendimentoEmergenciaTest {

    @ParameterizedTest
    @EnumSource(PrioridadeEmergencia.class)
    void acompanhaAvaliacaoDeTodasAsPrioridades(PrioridadeEmergencia prioridade) {
        AtendimentoEmergencia emergencia = new AtendimentoEmergencia(1L, paciente(), prioridade, clinico());
        assertEquals(prioridade, emergencia.getPrioridade());
        assertEquals(SituacaoEmergencia.AGUARDANDO_AVALIACAO, emergencia.getSituacao());
        emergencia.iniciar();
        assertEquals(SituacaoEmergencia.EM_ATENDIMENTO, emergencia.getSituacao());
        emergencia.concluir();
        assertEquals(SituacaoEmergencia.CONCLUIDO, emergencia.getSituacao());
    }

    @Test
    void rejeitaTransicoesInvalidas() {
        AtendimentoEmergencia emergencia = new AtendimentoEmergencia(1L, paciente(), PrioridadeEmergencia.CRITICA, clinico());
        assertThrows(IllegalStateException.class, emergencia::concluir);
        emergencia.iniciar();
        assertThrows(IllegalStateException.class, emergencia::iniciar);
        emergencia.concluir();
        assertThrows(IllegalStateException.class, emergencia::iniciar);
        assertThrows(IllegalStateException.class, emergencia::concluir);
        assertEquals(SituacaoEmergencia.CONCLUIDO, emergencia.getSituacao());
    }

    @Test
    void exigePacientePrioridadeEResponsavel() {
        assertThrows(IllegalArgumentException.class, () -> new AtendimentoEmergencia(1L, null, PrioridadeEmergencia.BAIXA, clinico()));
        assertThrows(IllegalArgumentException.class, () -> new AtendimentoEmergencia(1L, paciente(), null, clinico()));
        assertThrows(IllegalArgumentException.class, () -> new AtendimentoEmergencia(1L, paciente(), PrioridadeEmergencia.BAIXA, null));
    }
}
