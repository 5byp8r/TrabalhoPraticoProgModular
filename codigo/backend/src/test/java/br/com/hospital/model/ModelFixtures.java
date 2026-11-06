package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

final class ModelFixtures {

    static final LocalDateTime HORARIO = LocalDateTime.of(2026, 11, 5, 10, 0);
    static final LocalDate ENTRADA = HORARIO.toLocalDate();

    private ModelFixtures() { }

    static Paciente paciente() {
        return new Paciente(1L, "Ana", "12345678900", LocalDate.of(1990, 1, 1),
                "31999999999", "Rua A", "ana@example.com");
    }

    static MedicoClinicoGeral clinico() {
        return new MedicoClinicoGeral(2L, "Bruno", "CRM-1", "31988888888", "bruno@example.com");
    }

    static MedicoEspecialista especialista(Especialidade especialidade) {
        return new MedicoEspecialista(3L, "Carla", "CRM-2", "31977777777",
                "carla@example.com", especialidade);
    }

    static Cirurgiao cirurgiao() {
        return new Cirurgiao(4L, "Diego", "CRM-3", "31966666666", "diego@example.com", "Geral");
    }

    static ConsultaRotina consulta() {
        return new ConsultaRotina(5L, paciente(), clinico(), HORARIO, "Avaliação inicial");
    }

    static ProcedimentoCirurgico procedimento() {
        return new ProcedimentoCirurgico(6L, paciente(), cirurgiao(), "Apendicectomia",
                "Abdominal", HORARIO);
    }

    static Internacao internar(Paciente paciente, Quarto quarto) {
        return new Internacao(7L, paciente, clinico(), quarto, ENTRADA, ENTRADA.plusDays(2), null);
    }
}
