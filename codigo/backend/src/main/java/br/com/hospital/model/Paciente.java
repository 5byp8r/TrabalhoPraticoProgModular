package br.com.hospital.model;

import java.time.LocalDate;

public class Paciente {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String endereco;
    private String email;

    public Paciente(
            Long id,
            String nome,
            String cpf,
            LocalDate dataNascimento,
            String telefone,
            String endereco,
            String email) {

        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.endereco = endereco;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEmail() {
        return email;
    }

    public void atualizarContato(
            String telefone,
            String endereco,
            String email) {

        this.telefone = telefone;
        this.endereco = endereco;
        this.email = email;
    }
}