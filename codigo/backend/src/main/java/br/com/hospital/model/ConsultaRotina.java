package br.com.hospital.model;

import java.time.LocalDateTime;

public class ConsultaRotina extends Consulta {

    public ConsultaRotina(Long id, Paciente paciente, ProfissionalSaude medico,
            LocalDateTime dataHora, String motivo) {
        super(id, paciente, medico, dataHora, motivo);
        validarCondicoes();
        registrarNosHistoricos();
    }

    @Override
    public void validarCondicoes() {
        if (!(getMedico() instanceof MedicoClinicoGeral)) {
            throw new IllegalArgumentException("A consulta de rotina deve ser realizada por um clínico geral.");
        }
    }
}
