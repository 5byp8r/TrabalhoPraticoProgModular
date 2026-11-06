package br.com.hospital.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.Internacao;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.ProfissionalSaude;
import br.com.hospital.model.Quarto;

@Service
public class InternacaoService {
    private final DadosHospitalares dados;

    public InternacaoService(DadosHospitalares dados) { this.dados = dados; }

    public Internacao internar(Long pacienteId, Long responsavelId, Long quartoId,
            LocalDate entrada, LocalDate previsaoAlta, String observacoes) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(pacienteId);
            ProfissionalSaude responsavel = dados.profissionais.buscar(responsavelId);
            Quarto quarto = dados.quartos.buscar(quartoId);
            return dados.internacoes.adicionar(id ->
                    new Internacao(id, paciente, responsavel, quarto, entrada, previsaoAlta, observacoes));
        }
    }

    public Internacao buscarPorId(Long id) {
        synchronized (dados) { return dados.internacoes.buscar(id); }
    }

    public List<Internacao> listar() {
        synchronized (dados) { return dados.internacoes.listar(); }
    }

    public List<Internacao> listarAtivas() {
        synchronized (dados) { return dados.internacoes.listar().stream().filter(Internacao::isAtiva).toList(); }
    }

    public Internacao darAlta(Long id, LocalDate dataAlta) {
        synchronized (dados) {
            Internacao internacao = dados.internacoes.buscar(id);
            internacao.darAlta(dataAlta);
            return internacao;
        }
    }

    public void registrarObservacoes(Long id, String observacoes) {
        synchronized (dados) { dados.internacoes.buscar(id).registrarObservacoes(observacoes); }
    }
}
