package br.com.hospital.model;

import java.time.LocalDateTime;

public class MedicoClinicoGeral extends ProfissionalSaude {

    public MedicoClinicoGeral(
            Long id,
            String nome,
            String registroProfissional,
            String telefone,
            String email) {

        super(id, nome, registroProfissional, telefone, email);
    }

    public void consulta(ConsultaRotina consulta) {
        if (consulta == null || consulta.getMedico() != this) {
            throw new IllegalArgumentException("A consulta deve pertencer a este clínico geral.");
        }
        consulta.iniciar();
    }

    public ConsultaEspecializada encaminhar(Long id, Paciente paciente,
            MedicoEspecialista especialista, LocalDateTime dataHora, String motivo) {
        if (especialista == null) {
            throw new IllegalArgumentException("O especialista deve ser informado.");
        }
        return new ConsultaEspecializada(id, paciente, especialista, dataHora,
                motivo, especialista.getEspecialidade());
    }
}
