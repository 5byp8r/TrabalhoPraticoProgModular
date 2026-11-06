package br.com.hospital.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.PrioridadeEmergencia;
import br.com.hospital.model.ProfissionalSaude;
import br.com.hospital.model.SituacaoEmergencia;

@Service
public class AtendimentoEmergenciaService {
    private final DadosHospitalares dados;

    public AtendimentoEmergenciaService(DadosHospitalares dados) { this.dados = dados; }

    public AtendimentoEmergencia registrar(Long pacienteId, Long responsavelId, PrioridadeEmergencia prioridade) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(pacienteId);
            ProfissionalSaude responsavel = dados.profissionais.buscar(responsavelId);
            return dados.emergencias.adicionar(id -> new AtendimentoEmergencia(id, paciente, prioridade, responsavel));
        }
    }

    public AtendimentoEmergencia buscarPorId(Long id) {
        synchronized (dados) { return dados.emergencias.buscar(id); }
    }

    public List<AtendimentoEmergencia> listar() {
        synchronized (dados) { return dados.emergencias.listar(); }
    }

    /** Ordem decrescente de gravidade; empates conservam a ordem de registro. */
    public List<AtendimentoEmergencia> listarAguardandoAvaliacao() {
        synchronized (dados) {
            return dados.emergencias.listar().stream()
                    .filter(e -> e.getSituacao() == SituacaoEmergencia.AGUARDANDO_AVALIACAO)
                    .sorted(Comparator.comparing(AtendimentoEmergencia::getPrioridade).reversed()).toList();
        }
    }

    public AtendimentoEmergencia iniciar(Long id) {
        synchronized (dados) {
            AtendimentoEmergencia emergencia = dados.emergencias.buscar(id);
            dados.exigirSemAtendimentoEmAndamento(emergencia.getResponsavel());
            emergencia.iniciar();
            return emergencia;
        }
    }

    public AtendimentoEmergencia concluir(Long id) {
        synchronized (dados) {
            AtendimentoEmergencia emergencia = dados.emergencias.buscar(id);
            emergencia.concluir();
            return emergencia;
        }
    }
}
