package br.com.hospital.model;

public class MedicoClinicoGeral extends ProfissionalSaude {

    public MedicoClinicoGeral(
            Long id,
            String nome,
            String registroProfissional,
            String telefone,
            String email) {

        super(id, nome, registroProfissional, telefone, email);
    }
}