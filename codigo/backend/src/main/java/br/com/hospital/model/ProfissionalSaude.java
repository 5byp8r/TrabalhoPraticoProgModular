package br.com.hospital.model;

public abstract class ProfissionalSaude {

    private Long id;
    private String nome;
    private String registroProfissional;
    private String telefone;
    private String email;

    protected ProfissionalSaude(
            Long id,
            String nome,
            String registroProfissional,
            String telefone,
            String email) {

        this.id = id;
        this.nome = nome;
        this.registroProfissional = registroProfissional;
        this.telefone = telefone;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public void atualizarContato(String telefone, String email) {
        this.telefone = telefone;
        this.email = email;
    }
}