package br.com.hospital.model;

public class MedicoEspecialista extends ProfissionalSaude {

    private String especialidade;

    public MedicoEspecialista(
            Long id,
            String nome,
            String registroProfissional,
            String telefone,
            String email,
            String especialidade) {

        super(id, nome, registroProfissional, telefone, email);

        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException(
                    "A especialidade deve ser informada.");
        }

        this.especialidade = especialidade.trim();
    }

    public String getEspecialidade() {
        return especialidade;
    }
}