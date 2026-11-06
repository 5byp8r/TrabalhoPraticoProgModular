package br.com.hospital.model;

import static br.com.hospital.model.ModelFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HistoricosTest {

    @Test
    void historicosDeConsultaNaoDuplicamNemPermitemAlteracaoExterna() {
        Consulta consulta = consulta();
        Paciente paciente = consulta.getPaciente();
        ProfissionalSaude medico = consulta.getMedico();
        paciente.registrarConsulta(consulta);
        medico.registrarConsulta(consulta);
        assertEquals(1, paciente.getConsultas().size());
        assertEquals(1, medico.getConsultas().size());
        assertThrows(UnsupportedOperationException.class, () -> paciente.getConsultas().clear());
        assertThrows(UnsupportedOperationException.class, () -> medico.getConsultas().clear());
        assertThrows(IllegalArgumentException.class, () -> paciente().registrarConsulta(consulta));
        assertThrows(IllegalArgumentException.class, () -> clinico().registrarConsulta(consulta));
    }

    @Test
    void historicosDeInternacaoNaoDuplicamNemPermitemAlteracaoExterna() {
        Quarto quarto = new Quarto(1L, "101", 1, 2);
        Internacao internacao = internar(paciente(), quarto);
        Paciente paciente = internacao.getPaciente();
        ProfissionalSaude medico = internacao.getResponsavel();
        paciente.registrarInternacao(internacao);
        medico.registrarInternacao(internacao);
        assertEquals(1, paciente.getInternacoes().size());
        assertEquals(1, medico.getInternacoes().size());
        assertEquals(1, quarto.getQuantidadeAtual());
        assertThrows(UnsupportedOperationException.class, () -> paciente.getInternacoes().clear());
        assertThrows(UnsupportedOperationException.class, () -> medico.getInternacoes().clear());
        assertThrows(UnsupportedOperationException.class, () -> quarto.getInternacoesAtivas().clear());
        assertThrows(IllegalArgumentException.class, () -> paciente().registrarInternacao(internacao));
        assertThrows(IllegalArgumentException.class, () -> clinico().registrarInternacao(internacao));
    }

    @Test
    void procedimentosNaoDuplicamNemPermitemAlteracaoExterna() {
        ProcedimentoCirurgico procedimento = procedimento();
        Cirurgiao cirurgiao = procedimento.getCirurgiao();
        cirurgiao.registrarProcedimento(procedimento);
        assertEquals(1, cirurgiao.getProcedimentos().size());
        assertThrows(UnsupportedOperationException.class, () -> cirurgiao.getProcedimentos().clear());
        assertThrows(IllegalArgumentException.class, () -> cirurgiao().registrarProcedimento(procedimento));
    }

    @Test
    void profissionaisNaoExecutamAtendimentoDeOutroResponsavel() {
        ConsultaRotina consulta = consulta();
        assertThrows(IllegalArgumentException.class, () -> clinico().consulta(consulta));
        MedicoEspecialista medico = especialista(Especialidade.CARDIOLOGIA);
        ConsultaEspecializada especializada = new ConsultaEspecializada(1L, paciente(), medico,
                HORARIO, "Avaliação", Especialidade.CARDIOLOGIA);
        assertThrows(IllegalArgumentException.class,
                () -> especialista(Especialidade.CARDIOLOGIA).realizaAtend(especializada));
        ProcedimentoCirurgico procedimento = procedimento();
        assertThrows(IllegalArgumentException.class, () -> cirurgiao().realizaProce(procedimento));
        assertEquals(SituacaoConsulta.AGENDADA, consulta.getSituacao());
        assertEquals(SituacaoConsulta.AGENDADA, especializada.getSituacao());
        assertEquals(SituacaoProcedimento.AGENDADO, procedimento.getSituacao());
    }
}
