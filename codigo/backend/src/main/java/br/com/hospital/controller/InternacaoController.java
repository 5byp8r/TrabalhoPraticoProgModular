package br.com.hospital.controller;

import br.com.hospital.controller.Respostas.InternacaoResposta;
import br.com.hospital.model.Internacao;
import br.com.hospital.service.InternacaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDate;
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
@RequestMapping("/internacoes")
public class InternacaoController {

    private final InternacaoService internacaoService;

    public InternacaoController(InternacaoService internacaoService) {
        this.internacaoService = internacaoService;
    }

    public record InternacaoRequest(
            @NotNull Long pacienteId,
            @NotNull Long responsavelId,
            @NotNull Long quartoId,
            LocalDate dataEntrada,
            LocalDate previsaoAlta,
            String observacoes) {
    }

    public record ObservacoesRequest(@NotBlank String observacoes) {
    }

    @GetMapping
    public ResponseEntity<List<InternacaoResposta>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean ativas,
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long quartoId) {

        List<Internacao> internacoes = (ativas ? internacaoService.listarAtivas() : internacaoService.listar())
                .stream()
                .filter(i -> pacienteId == null || i.getPaciente().getId().equals(pacienteId))
                .filter(i -> quartoId == null || i.getQuarto().getId().equals(quartoId))
                .toList();
        return ResponseEntity.ok(Respostas.lista(internacoes, Respostas::internacao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternacaoResposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.internacao(internacaoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<InternacaoResposta> internar(@Valid @RequestBody InternacaoRequest request) {
        Internacao internacao = internacaoService.internar(
                request.pacienteId(),
                request.responsavelId(),
                request.quartoId(),
                request.dataEntrada() != null ? request.dataEntrada() : LocalDate.now(),
                request.previsaoAlta(),
                request.observacoes());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(internacao.getId())
                .toUri();
        return ResponseEntity.created(location).body(Respostas.internacao(internacao));
    }

    @PatchMapping("/{id}/alta")
    public ResponseEntity<InternacaoResposta> darAlta(
            @PathVariable Long id,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAlta) {
        Internacao internacao = internacaoService.darAlta(id, dataAlta != null ? dataAlta : LocalDate.now());
        return ResponseEntity.ok(Respostas.internacao(internacao));
    }

    @PutMapping("/{id}/observacoes")
    public ResponseEntity<InternacaoResposta> registrarObservacoes(
            @PathVariable Long id,
            @Valid @RequestBody ObservacoesRequest request) {
        internacaoService.registrarObservacoes(id, request.observacoes());
        return ResponseEntity.ok(Respostas.internacao(internacaoService.buscarPorId(id)));
    }
}
