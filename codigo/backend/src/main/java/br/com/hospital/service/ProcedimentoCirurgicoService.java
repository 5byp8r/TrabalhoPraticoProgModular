package br.com.hospital.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.ProcedimentoCirurgico;

@Service
public class ProcedimentoCirurgicoService {
    private final DadosHospitalares dados;

    public ProcedimentoCirurgicoService(DadosHospitalares dados) { this.dados = dados; }

    public ProcedimentoCirurgico agendar(Long pacienteId, Long cirurgiaoId, String descricao,
            String tipoCirurgia, LocalDateTime horario) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(pacienteId);
            if (!(dados.profissionais.buscar(cirurgiaoId) instanceof Cirurgiao cirurgiao)) {
                throw new IllegalArgumentException("O procedimento exige um cirurgião responsável.");
            }
            dados.exigirDisponibilidade(cirurgiao, horario, null);
            return dados.procedimentos.adicionar(id ->
                    new ProcedimentoCirurgico(id, paciente, cirurgiao, descricao, tipoCirurgia, horario));
        }
    }

    public ProcedimentoCirurgico buscarPorId(Long id) {
        synchronized (dados) { return dados.procedimentos.buscar(id); }
    }

    public List<ProcedimentoCirurgico> listar() {
        synchronized (dados) { return dados.procedimentos.listar(); }
    }

    public ProcedimentoCirurgico iniciar(Long id, LocalDateTime inicio) {
        synchronized (dados) {
            ProcedimentoCirurgico procedimento = dados.procedimentos.buscar(id);
            dados.exigirSemAtendimentoEmAndamento(procedimento.getCirurgiao());
            dados.exigirDisponibilidade(procedimento.getCirurgiao(), inicio, procedimento);
            procedimento.iniciar(inicio);
            return procedimento;
        }
    }

    public ProcedimentoCirurgico concluir(Long id, LocalDateTime fim) {
        synchronized (dados) {
            ProcedimentoCirurgico procedimento = dados.procedimentos.buscar(id);
            procedimento.concluir(fim);
            return procedimento;
        }
    }

    public void cancelar(Long id) {
        synchronized (dados) { dados.procedimentos.buscar(id).cancelar(); }
    }

    public void registrarObservacoesPre(Long id, String observacoes) {
        synchronized (dados) { dados.procedimentos.buscar(id).registrarObservacoesPre(observacoes); }
    }

    public void registrarObservacoesPos(Long id, String observacoes) {
        synchronized (dados) { dados.procedimentos.buscar(id).registrarObservacoesPos(observacoes); }
    }
}
