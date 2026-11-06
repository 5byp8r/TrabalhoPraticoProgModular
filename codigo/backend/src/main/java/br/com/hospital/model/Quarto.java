package br.com.hospital.model;

import java.util.ArrayList;
import java.util.List;

public class Quarto {

    private final Long id;
    private final String numero;
    private final int andar;
    private final int capacidadeMaxima;
    private final List<Internacao> internacoesAtivas = new ArrayList<>();
    private SituacaoQuarto situacao = SituacaoQuarto.DISPONIVEL;

    public Quarto(Long id, String numero, int andar, int capacidadeMaxima) {
        if (numero == null || numero.isBlank() || capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Informe o número do quarto e uma capacidade positiva.");
        }
        this.id = id;
        this.numero = numero.trim();
        this.andar = andar;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public int getAndar() { return andar; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public int getQuantidadeAtual() { return internacoesAtivas.size(); }
    public SituacaoQuarto getSituacao() { return situacao; }

    public List<Internacao> getInternacoesAtivas() {
        return List.copyOf(internacoesAtivas);
    }

    public boolean possuiVaga() {
        return situacao != SituacaoQuarto.MANUTENCAO && getQuantidadeAtual() < capacidadeMaxima;
    }

    // A ocupação só muda por meio da criação de internações e da alta.
    void admitir(Internacao internacao) {
        if (internacao == null || internacao.getQuarto() != this || !internacao.isAtiva()) {
            throw new IllegalArgumentException("A internação deve estar ativa e pertencer a este quarto.");
        }
        if (!possuiVaga()) {
            throw new IllegalStateException("O quarto está sem vagas ou em manutenção.");
        }
        if (internacoesAtivas.contains(internacao)) {
            throw new IllegalStateException("A internação já ocupa uma vaga neste quarto.");
        }
        internacoesAtivas.add(internacao);
        atualizarSituacao();
    }

    void liberar(Internacao internacao) {
        if (!internacoesAtivas.remove(internacao)) {
            throw new IllegalStateException("A internação não ocupa uma vaga neste quarto.");
        }
        atualizarSituacao();
    }

    public void colocarEmManutencao() {
        if (!internacoesAtivas.isEmpty()) {
            throw new IllegalStateException("Não é possível colocar um quarto com pacientes em manutenção.");
        }
        situacao = SituacaoQuarto.MANUTENCAO;
    }

    public void finalizarManutencao() {
        if (situacao != SituacaoQuarto.MANUTENCAO) {
            throw new IllegalStateException("O quarto não está em manutenção.");
        }
        atualizarSituacao();
    }

    private void atualizarSituacao() {
        situacao = getQuantidadeAtual() == capacidadeMaxima
                ? SituacaoQuarto.OCUPADO : SituacaoQuarto.DISPONIVEL;
    }

    public void imprimirInformacoes() {
        System.out.printf("Quarto %s, andar %d: %d/%d, %s%n",
                numero, andar, getQuantidadeAtual(), capacidadeMaxima, situacao);
    }
}
