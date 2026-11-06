package br.com.hospital.model;

import java.time.LocalDateTime;

public class ProcedimentoCirurgico extends Atendimento {

    private final Cirurgiao cirurgiao;
    private final String descricao;
    private final String tipoCirurgia;
    private final LocalDateTime dataHoraPrevista;
    private LocalDateTime inicioEfetivo;
    private LocalDateTime fimEfetivo;
    private String observacoesPre;
    private String observacoesPos;
    private SituacaoProcedimento situacao = SituacaoProcedimento.AGENDADO;

    public ProcedimentoCirurgico(Long id, Paciente paciente, Cirurgiao cirurgiao,
            String descricao, String tipoCirurgia, LocalDateTime dataHoraPrevista) {
        super(id, paciente);
        this.cirurgiao = cirurgiao;
        this.descricao = descricao == null ? null : descricao.trim();
        this.tipoCirurgia = tipoCirurgia == null ? null : tipoCirurgia.trim();
        this.dataHoraPrevista = dataHoraPrevista;
        validarCondicoes();
        cirurgiao.registrarProcedimento(this);
    }

    public Cirurgiao getCirurgiao() { return cirurgiao; }
    public String getDescricao() { return descricao; }
    public String getTipoCirurgia() { return tipoCirurgia; }
    public LocalDateTime getDataHoraPrevista() { return dataHoraPrevista; }
    public LocalDateTime getInicioEfetivo() { return inicioEfetivo; }
    public LocalDateTime getFimEfetivo() { return fimEfetivo; }
    public String getObservacoesPre() { return observacoesPre; }
    public String getObservacoesPos() { return observacoesPos; }
    public SituacaoProcedimento getSituacao() { return situacao; }

    @Override
    public void validarCondicoes() {
        if (cirurgiao == null || descricao == null || descricao.isBlank()
                || tipoCirurgia == null || tipoCirurgia.isBlank() || dataHoraPrevista == null) {
            throw new IllegalArgumentException("Informe cirurgião, descrição, tipo, data e horário previstos.");
        }
    }

    public void iniciar() {
        iniciar(LocalDateTime.now());
    }

    public void iniciar(LocalDateTime inicio) {
        exigirSituacao(SituacaoProcedimento.AGENDADO);
        if (inicio == null) {
            throw new IllegalArgumentException("O horário de início deve ser informado.");
        }
        validarCondicoes();
        inicioEfetivo = inicio;
        situacao = SituacaoProcedimento.EM_ANDAMENTO;
    }

    public void concluir() {
        concluir(LocalDateTime.now());
    }

    public void concluir(LocalDateTime fim) {
        exigirSituacao(SituacaoProcedimento.EM_ANDAMENTO);
        if (fim == null || fim.isBefore(inicioEfetivo)) {
            throw new IllegalArgumentException("O fim deve ser igual ou posterior ao início do procedimento.");
        }
        fimEfetivo = fim;
        situacao = SituacaoProcedimento.CONCLUIDO;
    }

    public void cancelar() {
        exigirSituacao(SituacaoProcedimento.AGENDADO);
        situacao = SituacaoProcedimento.CANCELADO;
    }

    public void registrarObservacoesPre(String observacoes) {
        exigirSituacao(SituacaoProcedimento.AGENDADO);
        observacoesPre = validarObservacoes(observacoes);
    }

    public void registrarObservacoesPos(String observacoes) {
        exigirSituacao(SituacaoProcedimento.CONCLUIDO);
        observacoesPos = validarObservacoes(observacoes);
    }

    private String validarObservacoes(String observacoes) {
        if (observacoes == null || observacoes.isBlank()) {
            throw new IllegalArgumentException("As observações devem ser informadas.");
        }
        return observacoes.trim();
    }

    private void exigirSituacao(SituacaoProcedimento esperada) {
        if (situacao != esperada) {
            throw new IllegalStateException("O procedimento deve estar em " + esperada + ".");
        }
    }
}
