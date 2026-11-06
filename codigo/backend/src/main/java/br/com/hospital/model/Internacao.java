package br.com.hospital.model;

import java.time.LocalDate;

public class Internacao {

    private final Long id;
    private final Paciente paciente;
    private final ProfissionalSaude responsavel;
    private final LocalDate dataEntrada;
    private final LocalDate previsaoAlta;
    private LocalDate dataAlta;
    private String observacoes;
    private final Quarto quarto;

    public Internacao(Long id, Paciente paciente, ProfissionalSaude responsavel,
            Quarto quarto, LocalDate dataEntrada, LocalDate previsaoAlta, String observacoes) {
        if (paciente == null || responsavel == null || quarto == null || dataEntrada == null) {
            throw new IllegalArgumentException("Informe paciente, responsável, quarto e data de entrada.");
        }
        if (previsaoAlta != null && previsaoAlta.isBefore(dataEntrada)) {
            throw new IllegalArgumentException("A previsão de alta não pode anteceder a entrada.");
        }
        if (paciente.getInternacoes().stream().anyMatch(Internacao::isAtiva)) {
            throw new IllegalStateException("O paciente já possui uma internação ativa.");
        }
        this.id = id;
        this.paciente = paciente;
        this.responsavel = responsavel;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.previsaoAlta = previsaoAlta;
        this.observacoes = observacoes;
        quarto.admitir(this);
        paciente.registrarInternacao(this);
        responsavel.registrarInternacao(this);
    }

    public Long getId() { return id; }
    public Paciente getPaciente() { return paciente; }
    public ProfissionalSaude getResponsavel() { return responsavel; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public LocalDate getPrevisaoAlta() { return previsaoAlta; }
    public LocalDate getDataAlta() { return dataAlta; }
    public String getObservacoes() { return observacoes; }
    public Quarto getQuarto() { return quarto; }
    public boolean isAtiva() { return dataAlta == null; }

    public void registrarObservacoes(String observacoes) {
        if (observacoes == null || observacoes.isBlank()) {
            throw new IllegalArgumentException("As observações devem ser informadas.");
        }
        this.observacoes = observacoes.trim();
    }

    public void darAlta() {
        darAlta(LocalDate.now());
    }

    public void darAlta(LocalDate dataAlta) {
        if (!isAtiva()) {
            throw new IllegalStateException("A internação já recebeu alta.");
        }
        if (dataAlta == null || dataAlta.isBefore(dataEntrada)) {
            throw new IllegalArgumentException("A alta deve ser igual ou posterior à entrada.");
        }
        quarto.liberar(this);
        this.dataAlta = dataAlta;
    }

    public void imprimirInformacoes() {
        System.out.printf("Internação %s: %s, quarto %s, entrada %s, alta %s%n",
                id, paciente.getNome(), quarto.getNumero(), dataEntrada, dataAlta);
    }
}
