package br.com.hospital.model;

public abstract class Atendimento {

    private final Long id;
    private final Paciente paciente;

    protected Atendimento(Long id, Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("O paciente deve ser informado.");
        }
        this.id = id;
        this.paciente = paciente;
    }

    public Long getId() {
        return id;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public abstract void validarCondicoes();
}
