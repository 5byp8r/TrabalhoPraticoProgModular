package br.com.hospital.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.Consulta;
import br.com.hospital.model.ConsultaEspecializada;
import br.com.hospital.model.ConsultaRotina;
import br.com.hospital.model.Especialidade;
import br.com.hospital.model.MedicoClinicoGeral;
import br.com.hospital.model.MedicoEspecialista;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.ProfissionalSaude;
import br.com.hospital.model.SituacaoConsulta;

@Service
public class ConsultaService {
    private final DadosHospitalares dados;

    public ConsultaService(DadosHospitalares dados) { this.dados = dados; }

    public ConsultaRotina agendarRotina(Long pacienteId, Long medicoId, LocalDateTime horario, String motivo) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(pacienteId);
            ProfissionalSaude medico = dados.profissionais.buscar(medicoId);
            dados.exigirDisponibilidade(medico, horario, null);
            return dados.consultas.adicionar(id ->
                    new ConsultaRotina(id, paciente, medico, horario, motivo));
        }
    }

    public ConsultaEspecializada agendarEspecializada(Long pacienteId, Long medicoId,
            LocalDateTime horario, String motivo, Especialidade especialidade) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(pacienteId);
            ProfissionalSaude medico = dados.profissionais.buscar(medicoId);
            dados.exigirDisponibilidade(medico, horario, null);
            return dados.consultas.adicionar(id ->
                    new ConsultaEspecializada(id, paciente, medico, horario, motivo, especialidade));
        }
    }

    public ConsultaEspecializada encaminhar(Long clinicoId, Long pacienteId, Long especialistaId,
            LocalDateTime horario, String motivo) {
        synchronized (dados) {
            if (!(dados.profissionais.buscar(clinicoId) instanceof MedicoClinicoGeral)) {
                throw new IllegalArgumentException("O encaminhamento deve ser realizado por um clínico geral.");
            }
            if (!(dados.profissionais.buscar(especialistaId) instanceof MedicoEspecialista especialista)) {
                throw new IllegalArgumentException("O profissional de destino deve ser especialista.");
            }
            return agendarEspecializada(pacienteId, especialistaId, horario, motivo, especialista.getEspecialidade());
        }
    }

    public Consulta buscarPorId(Long id) {
        synchronized (dados) { return dados.consultas.buscar(id); }
    }

    public List<Consulta> listar() {
        synchronized (dados) { return dados.consultas.listar(); }
    }

    public Consulta reagendar(Long id, LocalDateTime horario) {
        synchronized (dados) {
            Consulta consulta = dados.consultas.buscar(id);
            dados.exigirDisponibilidade(consulta.getMedico(), horario, consulta);
            consulta.agendarConsulta(horario);
            return consulta;
        }
    }

    public Consulta alterarSituacao(Long id, SituacaoConsulta situacao) {
        synchronized (dados) {
            Consulta consulta = dados.consultas.buscar(id);
            if (situacao == SituacaoConsulta.EM_ATENDIMENTO) {
                dados.exigirDisponibilidade(consulta.getMedico(), consulta.getDataHora(), consulta);
                dados.exigirSemAtendimentoEmAndamento(consulta.getMedico());
            }
            consulta.controlarConsulta(situacao);
            return consulta;
        }
    }

    public void registrarObservacoes(Long id, String observacoes) {
        synchronized (dados) { dados.consultas.buscar(id).registrarObservacoes(observacoes); }
    }
}
