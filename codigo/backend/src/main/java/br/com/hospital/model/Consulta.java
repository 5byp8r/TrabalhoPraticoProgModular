package br.com.hospital.model;

import java.time.LocalDateTime;

public abstract class Consulta extends Atendimento {

    private final ProfissionalSaude medico;
    private LocalDateTime dataHora;
    private final String motivo;
    private String observacoes;
    private SituacaoConsulta situacao = SituacaoConsulta.AGENDADA;

    protected Consulta(Long id, Paciente paciente, ProfissionalSaude medico,
            LocalDateTime dataHora, String motivo) {
        super(id, paciente);
        if (medico == null || dataHora == null || motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Informe médico, data, horário e motivo da consulta.");
        }
        this.medico = medico;
        this.dataHora = dataHora;
        this.motivo = motivo.trim();
    }

    // Chamado pela classe concreta somente depois de validar todos os seus atributos.
    protected final void registrarNosHistoricos() {
        getPaciente().registrarConsulta(this);
        medico.registrarConsulta(this);
    }

    public ProfissionalSaude getMedico() { return medico; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getMotivo() { return motivo; }
    public String getObservacoes() { return observacoes; }
    public SituacaoConsulta getSituacao() { return situacao; }

    public void confirmar() {
        exigirSituacao(SituacaoConsulta.AGENDADA);
        situacao = SituacaoConsulta.CONFIRMADA;
    }

    public void iniciar() {
        exigirPendente();
        validarCondicoes();
        situacao = SituacaoConsulta.EM_ATENDIMENTO;
    }

    public void concluir() {
        exigirSituacao(SituacaoConsulta.EM_ATENDIMENTO);
        situacao = SituacaoConsulta.CONCLUIDA;
    }

    public void cancelar() {
        exigirPendente();
        situacao = SituacaoConsulta.CANCELADA;
    }

    public void registrarNaoComparecimento() {
        exigirPendente();
        situacao = SituacaoConsulta.NAO_COMPARECEU;
    }

    /** Um novo agendamento é explícito; nunca reabre uma consulta já concluída. */
    public void agendarConsulta(LocalDateTime novaDataHora) {
        if (situacao == SituacaoConsulta.EM_ATENDIMENTO || situacao == SituacaoConsulta.CONCLUIDA) {
            throw new IllegalStateException("Não é possível reagendar uma consulta iniciada ou concluída.");
        }
        if (novaDataHora == null) {
            throw new IllegalArgumentException("A data e o horário devem ser informados.");
        }
        validarCondicoes();
        dataHora = novaDataHora;
        observacoes = null;
        situacao = SituacaoConsulta.AGENDADA;
    }

    public void registrarObservacoes(String observacoes) {
        if (situacao != SituacaoConsulta.EM_ATENDIMENTO && situacao != SituacaoConsulta.CONCLUIDA) {
            throw new IllegalStateException("As observações exigem uma consulta iniciada ou concluída.");
        }
        if (observacoes == null || observacoes.isBlank()) {
            throw new IllegalArgumentException("As observações devem ser informadas.");
        }
        this.observacoes = observacoes.trim();
    }

    /** Centraliza o controle sem permitir alterações diretas no atributo situação. */
    public void controlarConsulta(SituacaoConsulta novaSituacao) {
        if (novaSituacao == null) {
            throw new IllegalArgumentException("A situação deve ser informada.");
        }
        switch (novaSituacao) {
            case CONFIRMADA -> confirmar();
            case EM_ATENDIMENTO -> iniciar();
            case CONCLUIDA -> concluir();
            case CANCELADA -> cancelar();
            case NAO_COMPARECEU -> registrarNaoComparecimento();
            case AGENDADA -> throw new IllegalStateException("Use agendarConsulta para informar o novo agendamento.");
        }
    }

    private void exigirPendente() {
        if (situacao != SituacaoConsulta.AGENDADA && situacao != SituacaoConsulta.CONFIRMADA) {
            throw new IllegalStateException("A consulta deve estar agendada ou confirmada.");
        }
    }

    private void exigirSituacao(SituacaoConsulta esperada) {
        if (situacao != esperada) {
            throw new IllegalStateException("A consulta deve estar em " + esperada + ".");
        }
    }

    public void imprimirInformacoes() {
        System.out.printf("Consulta %s: %s, médico %s, %s, %s%n",
                getId(), getPaciente().getNome(), medico.getNome(), dataHora, situacao);
    }
}
