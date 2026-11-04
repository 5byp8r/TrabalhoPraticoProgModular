package br.com.hospital.model;

public class Cirurgiao extends ProfissionalSaude {

    private String areaCirurgica;

    public Cirurgiao(
            Long id,
            String nome,
            String registroProfissional,
            String telefone,
            String email,
            String areaCirurgica) {

        super(id, nome, registroProfissional, telefone, email);

        if (areaCirurgica == null || areaCirurgica.isBlank()) {
            throw new IllegalArgumentException(
                    "A área cirúrgica deve ser informada.");
        }

        this.areaCirurgica = areaCirurgica.trim();
    }

    public String getAreaCirurgica() {
        return areaCirurgica;
    }
}