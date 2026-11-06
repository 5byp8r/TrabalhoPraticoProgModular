package br.com.hospital.controller;

import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.Consulta;
import br.com.hospital.model.ConsultaEspecializada;
import br.com.hospital.model.Especialidade;
import br.com.hospital.model.Internacao;
import br.com.hospital.model.MedicoEspecialista;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.PrioridadeEmergencia;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.model.ProfissionalSaude;
import br.com.hospital.model.Quarto;
import br.com.hospital.model.SituacaoConsulta;
import br.com.hospital.model.SituacaoEmergencia;
import br.com.hospital.model.SituacaoProcedimento;
import br.com.hospital.model.SituacaoQuarto;
import br.com.hospital.service.HistoricoMedicoService.HistoricoMedico;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public final class Respostas {

    private Respostas() {
    }

    public record PacienteResposta(
            Long id, String nome, String cpf, LocalDate dataNascimento,
            String telefone, String endereco, String email) {
    }

    public record ProfissionalResposta(
            Long id, String tipo, String nome, String registroProfissional,
            String telefone, String email, Especialidade especialidade, String areaCirurgica) {
    }

    public record ConsultaResposta(
            Long id, String tipo, Long pacienteId, String pacienteNome,
            Long medicoId, String medicoNome, LocalDateTime dataHora, String motivo,
            String observacoes, SituacaoConsulta situacao, Especialidade especialidade) {
    }

    public record InternacaoResposta(
            Long id, Long pacienteId, String pacienteNome, Long responsavelId, String responsavelNome,
            Long quartoId, String quartoNumero, LocalDate dataEntrada, LocalDate previsaoAlta,
            LocalDate dataAlta, String observacoes, boolean ativa) {
    }

    public record QuartoResposta(
            Long id, String numero, int andar, int capacidadeMaxima, int quantidadeAtual,
            SituacaoQuarto situacao, boolean possuiVaga) {
    }

    public record ProcedimentoResposta(
            Long id, Long pacienteId, String pacienteNome, Long cirurgiaoId, String cirurgiaoNome,
            String descricao, String tipoCirurgia, LocalDateTime dataHoraPrevista,
            LocalDateTime inicioEfetivo, LocalDateTime fimEfetivo,
            String observacoesPre, String observacoesPos, SituacaoProcedimento situacao) {
    }

    public record EmergenciaResposta(
            Long id, Long pacienteId, String pacienteNome, Long responsavelId, String responsavelNome,
            PrioridadeEmergencia prioridade, SituacaoEmergencia situacao) {
    }

    public record HistoricoResposta(
            List<ConsultaResposta> consultas,
            List<InternacaoResposta> internacoes,
            List<ProcedimentoResposta> procedimentos,
            List<EmergenciaResposta> emergencias) {
    }

    public static PacienteResposta paciente(Paciente p) {
        return new PacienteResposta(p.getId(), p.getNome(), p.getCpf(), p.getDataNascimento(),
                p.getTelefone(), p.getEndereco(), p.getEmail());
    }

    public static ProfissionalResposta profissional(ProfissionalSaude p) {
        String tipo = "CLINICO_GERAL";
        Especialidade especialidade = null;
        String areaCirurgica = null;
        if (p instanceof MedicoEspecialista e) {
            tipo = "ESPECIALISTA";
            especialidade = e.getEspecialidade();
        } else if (p instanceof Cirurgiao c) {
            tipo = "CIRURGIAO";
            areaCirurgica = c.getAreaCirurgica();
        }
        return new ProfissionalResposta(p.getId(), tipo, p.getNome(), p.getRegistroProfissional(),
                p.getTelefone(), p.getEmail(), especialidade, areaCirurgica);
    }

    public static ConsultaResposta consulta(Consulta c) {
        boolean especializada = c instanceof ConsultaEspecializada;
        Especialidade especialidade = especializada ? ((ConsultaEspecializada) c).getEspecialidade() : null;
        return new ConsultaResposta(c.getId(), especializada ? "ESPECIALIZADA" : "ROTINA",
                c.getPaciente().getId(), c.getPaciente().getNome(),
                c.getMedico().getId(), c.getMedico().getNome(),
                c.getDataHora(), c.getMotivo(), c.getObservacoes(), c.getSituacao(), especialidade);
    }

    public static InternacaoResposta internacao(Internacao i) {
        return new InternacaoResposta(i.getId(),
                i.getPaciente().getId(), i.getPaciente().getNome(),
                i.getResponsavel().getId(), i.getResponsavel().getNome(),
                i.getQuarto().getId(), i.getQuarto().getNumero(),
                i.getDataEntrada(), i.getPrevisaoAlta(), i.getDataAlta(), i.getObservacoes(), i.isAtiva());
    }

    public static QuartoResposta quarto(Quarto q) {
        return new QuartoResposta(q.getId(), q.getNumero(), q.getAndar(), q.getCapacidadeMaxima(),
                q.getQuantidadeAtual(), q.getSituacao(), q.possuiVaga());
    }

    public static ProcedimentoResposta procedimento(ProcedimentoCirurgico p) {
        return new ProcedimentoResposta(p.getId(),
                p.getPaciente().getId(), p.getPaciente().getNome(),
                p.getCirurgiao().getId(), p.getCirurgiao().getNome(),
                p.getDescricao(), p.getTipoCirurgia(), p.getDataHoraPrevista(),
                p.getInicioEfetivo(), p.getFimEfetivo(),
                p.getObservacoesPre(), p.getObservacoesPos(), p.getSituacao());
    }

    public static EmergenciaResposta emergencia(AtendimentoEmergencia e) {
        return new EmergenciaResposta(e.getId(),
                e.getPaciente().getId(), e.getPaciente().getNome(),
                e.getResponsavel().getId(), e.getResponsavel().getNome(),
                e.getPrioridade(), e.getSituacao());
    }

    public static HistoricoResposta historico(HistoricoMedico h) {
        return new HistoricoResposta(
                lista(h.consultas(), Respostas::consulta),
                lista(h.internacoes(), Respostas::internacao),
                lista(h.procedimentos(), Respostas::procedimento),
                lista(h.emergencias(), Respostas::emergencia));
    }

    public static <T, R> List<R> lista(List<T> origem, Function<T, R> conversor) {
        return origem.stream().map(conversor).toList();
    }
}
