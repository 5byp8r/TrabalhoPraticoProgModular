package br.com.hospital.model;

import java.util.ArrayList;
import java.util.List;

public abstract class ProfissionalSaude {

    private Long id;
    private String nome;
    private String registroProfissional;
    private String telefone;
    private String email;
    private final List<Consulta> consultas = new ArrayList<>();
    private final List<Internacao> internacoes = new ArrayList<>();

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

    public List<Consulta> getConsultas() {
        return List.copyOf(consultas);
    }

    public List<Internacao> getInternacoes() {
        return List.copyOf(internacoes);
    }

    public void registrarConsulta(Consulta consulta) {
        if (consulta == null || consulta.getMedico() != this) {
            throw new IllegalArgumentException("A consulta deve pertencer a este profissional.");
        }
        if (!consultas.contains(consulta)) {
            consultas.add(consulta);
        }
    }

    public void registrarInternacao(Internacao internacao) {
        if (internacao == null || internacao.getResponsavel() != this) {
            throw new IllegalArgumentException("A internação deve pertencer a este profissional.");
        }
        if (!internacoes.contains(internacao)) {
            internacoes.add(internacao);
        }
    }

    public void editarDados(String nome, String telefone, String email) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome deve ser informado.");
        }
        this.nome = nome.trim();
        atualizarContato(telefone, email);
    }

    public void imprimirInformacoes() {
        System.out.printf("Profissional %s: %s, registro %s, telefone %s, e-mail %s%n",
                id, nome, registroProfissional, telefone, email);
    }

    public void imprimirAtendimentos() {
        consultas.forEach(Consulta::imprimirInformacoes);
        internacoes.forEach(Internacao::imprimirInformacoes);
    }
}
