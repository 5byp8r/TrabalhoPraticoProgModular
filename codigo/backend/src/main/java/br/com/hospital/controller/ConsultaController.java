package br.com.hospital.controller;

import br.com.hospital.controller.Respostas.ConsultaResposta;
import br.com.hospital.controller.Respostas.EmergenciaResposta;
import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.model.Consulta;
import br.com.hospital.model.Especialidade;
import br.com.hospital.model.PrioridadeEmergencia;
import br.com.hospital.model.SituacaoConsulta;
import br.com.hospital.service.AtendimentoEmergenciaService;
import br.com.hospital.service.ConsultaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final AtendimentoEmergenciaService emergenciaService;

    public ConsultaController(
            ConsultaService consultaService,
            AtendimentoEmergenciaService emergenciaService) {
        this.consultaService = consultaService;
        this.emergenciaService = emergenciaService;
    }

    public record ConsultaRotinaRequest(
            @NotNull Long pacienteId,
            @NotNull Long medicoId,
            @NotNull LocalDateTime dataHora,
            @NotBlank String motivo) {
    }

    public record ConsultaEspecializadaRequest(
            @NotNull Long pacienteId,
            @NotNull Long medicoId,
            @NotNull LocalDateTime dataHora,
            @NotBlank String motivo,
            @NotNull Especialidade especialidade) {
    }

    public record EncaminhamentoRequest(
            @NotNull Long clinicoId,
            @NotNull Long pacienteId,
            @NotNull Long especialistaId,
            @NotNull LocalDateTime dataHora,
            @NotBlank String motivo) {
    }

    public record ReagendamentoRequest(@NotNull LocalDateTime dataHora) {
    }

    public record SituacaoRequest(@NotNull SituacaoConsulta situacao) {
    }

    public record ObservacoesRequest(@NotBlank String observacoes) {
    }

    public record EmergenciaRequest(
            @NotNull Long pacienteId,
            @NotNull Long responsavelId,
            @NotNull PrioridadeEmergencia prioridade) {
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResposta>> listar(
            @RequestParam(required = false) SituacaoConsulta situacao,
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long profissionalId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<Consulta> consultas = consultaService.listar().stream()
                .filter(c -> situacao == null || c.getSituacao() == situacao)
                .filter(c -> pacienteId == null || c.getPaciente().getId().equals(pacienteId))
                .filter(c -> profissionalId == null || c.getMedico().getId().equals(profissionalId))
                .filter(c -> data == null || c.getDataHora().toLocalDate().equals(data))
                .toList();
        return ResponseEntity.ok(Respostas.lista(consultas, Respostas::consulta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.consulta(consultaService.buscarPorId(id)));
    }

    @PostMapping("/rotina")
    public ResponseEntity<ConsultaResposta> agendarRotina(@Valid @RequestBody ConsultaRotinaRequest request) {
        Consulta consulta = consultaService.agendarRotina(
                request.pacienteId(),
                request.medicoId(),
                request.dataHora(),
                request.motivo());
        return criada("/consultas/{id}", consulta);
    }

    @PostMapping("/especializada")
    public ResponseEntity<ConsultaResposta> agendarEspecializada(
            @Valid @RequestBody ConsultaEspecializadaRequest request) {
        Consulta consulta = consultaService.agendarEspecializada(
                request.pacienteId(),
                request.medicoId(),
                request.dataHora(),
                request.motivo(),
                request.especialidade());
        return criada("/consultas/{id}", consulta);
    }

    @PostMapping("/encaminhamentos")
    public ResponseEntity<ConsultaResposta> encaminhar(@Valid @RequestBody EncaminhamentoRequest request) {
        Consulta consulta = consultaService.encaminhar(
                request.clinicoId(),
                request.pacienteId(),
                request.especialistaId(),
                request.dataHora(),
                request.motivo());
        return criada("/consultas/{id}", consulta);
    }

    @PatchMapping("/{id}/reagendar")
    public ResponseEntity<ConsultaResposta> reagendar(
            @PathVariable Long id,
            @Valid @RequestBody ReagendamentoRequest request) {
        return ResponseEntity.ok(Respostas.consulta(consultaService.reagendar(id, request.dataHora())));
    }

    @PatchMapping("/{id}/situacao")
    public ResponseEntity<ConsultaResposta> alterarSituacao(
            @PathVariable Long id,
            @Valid @RequestBody SituacaoRequest request) {
        return ResponseEntity.ok(Respostas.consulta(consultaService.alterarSituacao(id, request.situacao())));
    }

    @PutMapping("/{id}/observacoes")
    public ResponseEntity<ConsultaResposta> registrarObservacoes(
            @PathVariable Long id,
            @Valid @RequestBody ObservacoesRequest request) {
        consultaService.registrarObservacoes(id, request.observacoes());
        return ResponseEntity.ok(Respostas.consulta(consultaService.buscarPorId(id)));
    }

    @GetMapping("/emergencias")
    public ResponseEntity<List<EmergenciaResposta>> listarEmergencias(
            @RequestParam(required = false, defaultValue = "false") boolean aguardando) {
        List<AtendimentoEmergencia> emergencias = aguardando
                ? emergenciaService.listarAguardandoAvaliacao()
                : emergenciaService.listar();
        return ResponseEntity.ok(Respostas.lista(emergencias, Respostas::emergencia));
    }

    @GetMapping("/emergencias/{id}")
    public ResponseEntity<EmergenciaResposta> buscarEmergencia(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.emergencia(emergenciaService.buscarPorId(id)));
    }

    @PostMapping("/emergencias")
    public ResponseEntity<EmergenciaResposta> registrarEmergencia(
            @Valid @RequestBody EmergenciaRequest request) {
        AtendimentoEmergencia emergencia = emergenciaService.registrar(
                request.pacienteId(),
                request.responsavelId(),
                request.prioridade());

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/consultas/emergencias/{id}")
                .buildAndExpand(emergencia.getId())
                .toUri();
        return ResponseEntity.created(location).body(Respostas.emergencia(emergencia));
    }

    @PatchMapping("/emergencias/{id}/iniciar")
    public ResponseEntity<EmergenciaResposta> iniciarEmergencia(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.emergencia(emergenciaService.iniciar(id)));
    }

    @PatchMapping("/emergencias/{id}/concluir")
    public ResponseEntity<EmergenciaResposta> concluirEmergencia(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.emergencia(emergenciaService.concluir(id)));
    }

    private ResponseEntity<ConsultaResposta> criada(String caminho, Consulta consulta) {
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(caminho)
                .buildAndExpand(consulta.getId())
                .toUri();
        return ResponseEntity.created(location).body(Respostas.consulta(consulta));
    }
}
