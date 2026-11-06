package br.com.hospital.model;

import static br.com.hospital.model.ModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InternacaoTest {

    @Test
    void internaEDaAltaMantendoHistoricosEOcupacao() {
        Paciente paciente = paciente();
        Quarto quarto = new Quarto(1L, "101", 1, 1);
        Internacao internacao = internar(paciente, quarto);
        assertTrue(internacao.isAtiva());
        assertEquals(1, quarto.getQuantidadeAtual());
        assertFalse(quarto.possuiVaga());
        assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
        assertSame(internacao, paciente.getInternacoes().getFirst());
        assertSame(internacao, internacao.getResponsavel().getInternacoes().getFirst());

        internacao.darAlta(ENTRADA.plusDays(1));
        assertFalse(internacao.isAtiva());
        assertEquals(ENTRADA.plusDays(1), internacao.getDataAlta());
        assertEquals(0, quarto.getQuantidadeAtual());
        assertTrue(quarto.possuiVaga());
        assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
        assertEquals(1, paciente.getInternacoes().size());
        assertEquals(1, internacao.getResponsavel().getInternacoes().size());
        assertTrue(quarto.getInternacoesAtivas().isEmpty());
        assertThrows(IllegalStateException.class, () -> internacao.darAlta(ENTRADA.plusDays(2)));
        assertEquals(0, quarto.getQuantidadeAtual());

        Internacao retorno = internar(paciente, quarto);
        assertTrue(retorno.isAtiva());
        assertEquals(2, paciente.getInternacoes().size());
    }

    @Test
    void lotacaoRejeitaAdmissaoSemAlterarHistoricos() {
        Quarto quarto = new Quarto(1L, "101", 1, 2);
        internar(paciente(), quarto);
        assertTrue(quarto.possuiVaga());
        assertEquals(SituacaoQuarto.DISPONIVEL, quarto.getSituacao());
        internar(paciente(), quarto);
        Paciente terceiro = paciente();
        ProfissionalSaude responsavel = clinico();
        assertThrows(IllegalStateException.class, () -> new Internacao(3L, terceiro,
                responsavel, quarto, ENTRADA, null, null));
        assertEquals(2, quarto.getQuantidadeAtual());
        assertEquals(SituacaoQuarto.OCUPADO, quarto.getSituacao());
        assertTrue(terceiro.getInternacoes().isEmpty());
        assertTrue(responsavel.getInternacoes().isEmpty());
    }

    @Test
    void manutencaoImpedeAdmissaoEExigeQuartoVazio() {
        Quarto quarto = new Quarto(1L, "101", 1, 2);
        quarto.colocarEmManutencao();
        assertFalse(quarto.possuiVaga());
        assertEquals(SituacaoQuarto.MANUTENCAO, quarto.getSituacao());
        Paciente paciente = paciente();
        assertThrows(IllegalStateException.class, () -> internar(paciente, quarto));
        assertTrue(paciente.getInternacoes().isEmpty());
        assertEquals(0, quarto.getQuantidadeAtual());
        quarto.finalizarManutencao();
        assertTrue(quarto.possuiVaga());
        Internacao internacao = internar(paciente, quarto);
        assertThrows(IllegalStateException.class, quarto::colocarEmManutencao);
        internacao.darAlta(ENTRADA);
        quarto.colocarEmManutencao();
        assertEquals(SituacaoQuarto.MANUTENCAO, quarto.getSituacao());
    }

    @Test
    void pacienteNaoPodeOcuparDoisQuartosAoMesmoTempo() {
        Paciente paciente = paciente();
        Quarto primeiro = new Quarto(1L, "101", 1, 2);
        Quarto segundo = new Quarto(2L, "102", 1, 2);
        internar(paciente, primeiro);
        assertThrows(IllegalStateException.class, () -> internar(paciente, segundo));
        assertEquals(1, primeiro.getQuantidadeAtual());
        assertEquals(0, segundo.getQuantidadeAtual());
        assertEquals(1, paciente.getInternacoes().size());
    }

    @Test
    void rejeitaDatasInconsistentesSemAlterarOcupacao() {
        Quarto quarto = new Quarto(1L, "101", 1, 1);
        Paciente paciente = paciente();
        assertThrows(IllegalArgumentException.class, () -> new Internacao(1L, paciente,
                clinico(), quarto, ENTRADA, ENTRADA.minusDays(1), null));
        assertEquals(0, quarto.getQuantidadeAtual());
        assertTrue(paciente.getInternacoes().isEmpty());
        Internacao internacao = internar(paciente, quarto);
        assertThrows(IllegalArgumentException.class, () -> internacao.darAlta(ENTRADA.minusDays(1)));
        assertThrows(IllegalArgumentException.class, () -> internacao.darAlta(null));
        assertTrue(internacao.isAtiva());
        assertEquals(1, quarto.getQuantidadeAtual());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void capacidadeDeveSerPositiva(int capacidade) {
        assertThrows(IllegalArgumentException.class, () -> new Quarto(1L, "101", 1, capacidade));
    }

    @Test
    void exigeDadosParaAdmissao() {
        Quarto quarto = new Quarto(1L, "101", 1, 1);
        assertThrows(IllegalArgumentException.class, () -> new Internacao(1L, null, clinico(), quarto, ENTRADA, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Internacao(1L, paciente(), null, quarto, ENTRADA, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Internacao(1L, paciente(), clinico(), null, ENTRADA, null, null));
        assertThrows(IllegalArgumentException.class, () -> new Internacao(1L, paciente(), clinico(), quarto, null, null, null));
        assertEquals(0, quarto.getQuantidadeAtual());
    }
}
