package br.com.hospital.model;

import java.time.LocalDateTime;

public class ConsultaEspecializada extends Consulta {

    private final Especialidade especialidade;

    public ConsultaEspecializada(Long id, Paciente paciente, ProfissionalSaude medico,
            LocalDateTime dataHora, String motivo, Especialidade especialidade) {
        super(id, paciente, medico, dataHora, motivo);
        this.especialidade = especialidade;
        validarCondicoes();
        registrarNosHistoricos();
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    @Override
    public void validarCondicoes() {
        if (especialidade == null) {
            throw new IllegalArgumentException("A especialidade da consulta deve ser informada.");
        }
        if (!(getMedico() instanceof MedicoEspecialista especialista)
                || especialista.getEspecialidade() != especialidade) {
            throw new IllegalArgumentException("O médico deve ser especialista na área solicitada.");
        }
    }
}
