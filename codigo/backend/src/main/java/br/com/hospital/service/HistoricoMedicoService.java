package br.com.hospital.service;

import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.model.Consulta;
import br.com.hospital.model.Internacao;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.ProcedimentoCirurgico;

@Service
public class HistoricoMedicoService {
    private final DadosHospitalares dados;

    public HistoricoMedicoService(DadosHospitalares dados) { this.dados = dados; }

    /** Inclui todos os registros, inclusive cancelados, para conservar o histórico. */
    public HistoricoMedico consultar(Long pacienteId) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(pacienteId);
            return new HistoricoMedico(paciente.getConsultas(), paciente.getInternacoes(),
                    dados.procedimentos.listar().stream().filter(p -> p.getPaciente() == paciente).toList(),
                    dados.emergencias.listar().stream().filter(e -> e.getPaciente() == paciente).toList());
        }
    }

    public record HistoricoMedico(List<Consulta> consultas, List<Internacao> internacoes,
            List<ProcedimentoCirurgico> procedimentos, List<AtendimentoEmergencia> emergencias) {
        public HistoricoMedico {
            consultas = List.copyOf(consultas);
            internacoes = List.copyOf(internacoes);
            procedimentos = List.copyOf(procedimentos);
            emergencias = List.copyOf(emergencias);
        }
    }
}
