package br.com.hospital.model;

public class MedicoEspecialista extends ProfissionalSaude {

    private final Especialidade especialidade;

    public MedicoEspecialista(
            Long id,
            String nome,
            String registroProfissional,
            String telefone,
            String email,
            Especialidade especialidade) {

        super(id, nome, registroProfissional, telefone, email);

        if (especialidade == null) {
            throw new IllegalArgumentException(
                    "A especialidade deve ser informada.");
        }

        this.especialidade = especialidade;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void realizaAtend(ConsultaEspecializada consulta) {
        if (consulta == null || consulta.getMedico() != this) {
            throw new IllegalArgumentException("A consulta deve pertencer a este especialista.");
        }
        consulta.iniciar();
    }
}
