package br.com.hospital.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;
import br.com.hospital.model.Atendimento;
import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.model.Consulta;
import br.com.hospital.model.Internacao;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.model.ProfissionalSaude;
import br.com.hospital.model.Quarto;
import br.com.hospital.model.SituacaoConsulta;
import br.com.hospital.model.SituacaoEmergencia;
import br.com.hospital.model.SituacaoProcedimento;

/**
 * Estado volátil compartilhado pelos services, sem persistência.
 * O monitor desta instância protege verificações e alterações entre cadastros.
 * As entidades retornadas devem ser alteradas pelos services.
 */
@Component
public class DadosHospitalares {
    final CadastroEmMemoria<Paciente> pacientes = new CadastroEmMemoria<>();
    final CadastroEmMemoria<ProfissionalSaude> profissionais = new CadastroEmMemoria<>();
    final CadastroEmMemoria<Quarto> quartos = new CadastroEmMemoria<>();
    final CadastroEmMemoria<Consulta> consultas = new CadastroEmMemoria<>();
    final CadastroEmMemoria<Internacao> internacoes = new CadastroEmMemoria<>();
    final CadastroEmMemoria<ProcedimentoCirurgico> procedimentos = new CadastroEmMemoria<>();
    final CadastroEmMemoria<AtendimentoEmergencia> emergencias = new CadastroEmMemoria<>();

    void exigirDisponibilidade(ProfissionalSaude profissional, LocalDateTime horario, Atendimento ignorado) {
        if (!estaDisponivel(profissional, horario, ignorado)) {
            throw new IllegalStateException("O profissional já possui um atendimento neste horário.");
        }
    }

    boolean estaDisponivel(ProfissionalSaude profissional, LocalDateTime horario, Atendimento ignorado) {
        if (horario == null) {
            throw new IllegalArgumentException("A data e o horário devem ser informados.");
        }
        boolean consultaConflitante = consultas.listar().stream().anyMatch(c -> c != ignorado
                && c.getMedico() == profissional && horario.equals(c.getDataHora())
                && c.getSituacao() != SituacaoConsulta.CANCELADA
                && c.getSituacao() != SituacaoConsulta.NAO_COMPARECEU);
        boolean cirurgiaConflitante = procedimentos.listar().stream().anyMatch(p -> p != ignorado
                && p.getCirurgiao() == profissional && horario.equals(p.getDataHoraPrevista())
                && p.getSituacao() != SituacaoProcedimento.CANCELADO);
        return !consultaConflitante && !cirurgiaConflitante;
    }

    void exigirSemAtendimentoEmAndamento(ProfissionalSaude profissional) {
        boolean ocupado = consultas.listar().stream().anyMatch(c -> c.getMedico() == profissional
                && c.getSituacao() == SituacaoConsulta.EM_ATENDIMENTO)
                || procedimentos.listar().stream().anyMatch(p -> p.getCirurgiao() == profissional
                && p.getSituacao() == SituacaoProcedimento.EM_ANDAMENTO)
                || emergencias.listar().stream().anyMatch(e -> e.getResponsavel() == profissional
                && e.getSituacao() == SituacaoEmergencia.EM_ATENDIMENTO);
        if (ocupado) {
            throw new IllegalStateException("O profissional já está realizando um atendimento.");
        }
    }

    static String exigirTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Informe " + campo + ".");
        }
        return valor.trim();
    }
}
