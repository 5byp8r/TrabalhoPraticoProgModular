package br.com.hospital.model;

public class AtendimentoEmergencia extends Atendimento {

    private final PrioridadeEmergencia prioridade;
    private final ProfissionalSaude responsavel;
    private SituacaoEmergencia situacao = SituacaoEmergencia.AGUARDANDO_AVALIACAO;

    public AtendimentoEmergencia(Long id, Paciente paciente,
            PrioridadeEmergencia prioridade, ProfissionalSaude responsavel) {
        super(id, paciente);
        this.prioridade = prioridade;
        this.responsavel = responsavel;
        validarCondicoes();
    }

    public PrioridadeEmergencia getPrioridade() { return prioridade; }
    public ProfissionalSaude getResponsavel() { return responsavel; }
    public SituacaoEmergencia getSituacao() { return situacao; }

    @Override
    public void validarCondicoes() {
        if (prioridade == null || responsavel == null) {
            throw new IllegalArgumentException("Informe a prioridade e o responsável pela emergência.");
        }
    }

    public void iniciar() {
        if (situacao != SituacaoEmergencia.AGUARDANDO_AVALIACAO) {
            throw new IllegalStateException("A emergência deve estar aguardando avaliação.");
        }
        validarCondicoes();
        situacao = SituacaoEmergencia.EM_ATENDIMENTO;
    }

    public void concluir() {
        if (situacao != SituacaoEmergencia.EM_ATENDIMENTO) {
            throw new IllegalStateException("A emergência deve ser iniciada antes da conclusão.");
        }
        situacao = SituacaoEmergencia.CONCLUIDO;
    }
}
