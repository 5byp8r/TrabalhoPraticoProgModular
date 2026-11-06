package br.com.hospital.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Paciente {

    private Long id;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String endereco;
    private String email;
    private final List<Consulta> consultas = new ArrayList<>();
    private final List<Internacao> internacoes = new ArrayList<>();

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

    public List<Consulta> getConsultas() {
        return List.copyOf(consultas);
    }

    public List<Internacao> getInternacoes() {
        return List.copyOf(internacoes);
    }

    public void registrarConsulta(Consulta consulta) {
        if (consulta == null || consulta.getPaciente() != this) {
            throw new IllegalArgumentException("A consulta deve pertencer a este paciente.");
        }
        if (!consultas.contains(consulta)) {
            consultas.add(consulta);
        }
    }

    public void registrarInternacao(Internacao internacao) {
        if (internacao == null || internacao.getPaciente() != this) {
            throw new IllegalArgumentException("A internação deve pertencer a este paciente.");
        }
        if (!internacoes.contains(internacao)) {
            internacoes.add(internacao);
        }
    }

    public void editarDados(String nome, String telefone, String endereco, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome deve ser informado.");
        }
        this.nome = nome.trim();
        atualizarContato(telefone, endereco, email);
    }

    public void imprimirDados() {
        System.out.printf("Paciente %s: %s, CPF %s, nascimento %s, telefone %s, endereço %s, e-mail %s%n",
                id, nome, cpf, dataNascimento, telefone, endereco, email);
    }
}
