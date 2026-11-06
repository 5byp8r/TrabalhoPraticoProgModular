package br.com.hospital.controller;

import br.com.hospital.controller.Respostas.ProcedimentoResposta;
import br.com.hospital.controller.Respostas.ProfissionalResposta;
import br.com.hospital.model.Especialidade;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.model.ProfissionalSaude;
import br.com.hospital.service.ProcedimentoCirurgicoService;
import br.com.hospital.service.ProfissionalSaudeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalSaudeService profissionalService;
    private final ProcedimentoCirurgicoService procedimentoService;

    public ProfissionalController(
            ProfissionalSaudeService profissionalService,
            ProcedimentoCirurgicoService procedimentoService) {
        this.profissionalService = profissionalService;
        this.procedimentoService = procedimentoService;
    }

    public enum TipoProfissional {
        CLINICO_GERAL, ESPECIALISTA, CIRURGIAO
    }

    public record ProfissionalRequest(
            @NotNull TipoProfissional tipo,
            @NotBlank String nome,
            @NotBlank String registroProfissional,
            @NotBlank String telefone,
            @NotBlank @Email String email,
            Especialidade especialidade,
            String areaCirurgica) {
    }

    public record AtualizacaoProfissionalRequest(
            @NotBlank String nome,
            @NotBlank String telefone,
            @NotBlank @Email String email) {
    }

    public record ProcedimentoRequest(
            @NotNull Long pacienteId,
            @NotBlank String descricao,
            @NotBlank String tipoCirurgia,
            @NotNull LocalDateTime dataHoraPrevista,
            String observacoesPre) {
    }

    public record ObservacoesRequest(@NotBlank String observacoes) {
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalResposta>> listar(
            @RequestParam(required = false) TipoProfissional tipo,
            @RequestParam(required = false) Especialidade especialidade,
            @RequestParam(required = false) String busca) {

        List<? extends ProfissionalSaude> profissionais = especialidade != null
                ? profissionalService.listarPorEspecialidade(especialidade)
                : profissionalService.listar();

        String termo = busca == null ? "" : busca.trim().toLowerCase();

        List<ProfissionalResposta> resposta = profissionais.stream()
                .map(Respostas::profissional)
                .filter(p -> tipo == null || p.tipo().equals(tipo.name()))
                .filter(p -> termo.isEmpty()
                        || p.nome().toLowerCase().contains(termo)
                        || p.registroProfissional().toLowerCase().contains(termo))
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/especialidades")
    public ResponseEntity<List<Especialidade>> listarEspecialidades() {
        return ResponseEntity.ok(Arrays.asList(Especialidade.values()));
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<ProfissionalResposta>> listarDisponiveis(
            @RequestParam Especialidade especialidade,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime horario) {
        return ResponseEntity.ok(Respostas.lista(
                profissionalService.listarDisponiveis(especialidade, horario), Respostas::profissional));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.profissional(profissionalService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ProfissionalResposta> cadastrar(@Valid @RequestBody ProfissionalRequest request) {
        ProfissionalSaude profissional = switch (request.tipo()) {
            case CLINICO_GERAL -> profissionalService.cadastrarClinicoGeral(
                    request.nome(), request.registroProfissional(), request.telefone(), request.email());
            case ESPECIALISTA -> profissionalService.cadastrarEspecialista(
                    request.nome(), request.registroProfissional(), request.telefone(), request.email(),
                    request.especialidade());
            case CIRURGIAO -> profissionalService.cadastrarCirurgiao(
                    request.nome(), request.registroProfissional(), request.telefone(), request.email(),
                    request.areaCirurgica());
        };

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(profissional.getId())
                .toUri();
        return ResponseEntity.created(location).body(Respostas.profissional(profissional));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResposta> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoProfissionalRequest request) {
        ProfissionalSaude profissional = profissionalService.atualizar(
                id, request.nome(), request.telefone(), request.email());
        return ResponseEntity.ok(Respostas.profissional(profissional));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        profissionalService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/procedimentos")
    public ResponseEntity<List<ProcedimentoResposta>> listarProcedimentos(@PathVariable Long id) {
        profissionalService.buscarPorId(id); // 404 se o profissional não existir
        List<ProcedimentoCirurgico> procedimentos = procedimentoService.listar().stream()
                .filter(p -> p.getCirurgiao().getId().equals(id))
                .toList();
        return ResponseEntity.ok(Respostas.lista(procedimentos, Respostas::procedimento));
    }

    @PostMapping("/{id}/procedimentos")
    public ResponseEntity<ProcedimentoResposta> agendarProcedimento(
            @PathVariable Long id,
            @Valid @RequestBody ProcedimentoRequest request) {
        ProcedimentoCirurgico procedimento = procedimentoService.agendar(
                request.pacienteId(),
                id,
                request.descricao(),
                request.tipoCirurgia(),
                request.dataHoraPrevista());

        if (request.observacoesPre() != null && !request.observacoesPre().isBlank()) {
            procedimentoService.registrarObservacoesPre(procedimento.getId(), request.observacoesPre());
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{procedimentoId}")
                .buildAndExpand(procedimento.getId())
                .toUri();
        return ResponseEntity.created(location).body(Respostas.procedimento(procedimento));
    }

    @GetMapping("/{id}/procedimentos/{procedimentoId}")
    public ResponseEntity<ProcedimentoResposta> buscarProcedimento(
            @PathVariable Long id,
            @PathVariable Long procedimentoId) {
        return ResponseEntity.ok(Respostas.procedimento(procedimentoDoCirurgiao(id, procedimentoId)));
    }

    @PatchMapping("/{id}/procedimentos/{procedimentoId}/iniciar")
    public ResponseEntity<ProcedimentoResposta> iniciarProcedimento(
            @PathVariable Long id,
            @PathVariable Long procedimentoId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio) {
        procedimentoDoCirurgiao(id, procedimentoId);
        return ResponseEntity.ok(Respostas.procedimento(procedimentoService.iniciar(
                procedimentoId, inicio != null ? inicio : LocalDateTime.now())));
    }

    @PatchMapping("/{id}/procedimentos/{procedimentoId}/concluir")
    public ResponseEntity<ProcedimentoResposta> concluirProcedimento(
            @PathVariable Long id,
            @PathVariable Long procedimentoId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        procedimentoDoCirurgiao(id, procedimentoId);
        return ResponseEntity.ok(Respostas.procedimento(procedimentoService.concluir(
                procedimentoId, fim != null ? fim : LocalDateTime.now())));
    }

    @PatchMapping("/{id}/procedimentos/{procedimentoId}/cancelar")
    public ResponseEntity<ProcedimentoResposta> cancelarProcedimento(
            @PathVariable Long id,
            @PathVariable Long procedimentoId) {
        procedimentoDoCirurgiao(id, procedimentoId);
        procedimentoService.cancelar(procedimentoId);
        return ResponseEntity.ok(Respostas.procedimento(procedimentoService.buscarPorId(procedimentoId)));
    }

    @PutMapping("/{id}/procedimentos/{procedimentoId}/observacoes-pre")
    public ResponseEntity<ProcedimentoResposta> registrarObservacoesPre(
            @PathVariable Long id,
            @PathVariable Long procedimentoId,
            @Valid @RequestBody ObservacoesRequest request) {
        procedimentoDoCirurgiao(id, procedimentoId);
        procedimentoService.registrarObservacoesPre(procedimentoId, request.observacoes());
        return ResponseEntity.ok(Respostas.procedimento(procedimentoService.buscarPorId(procedimentoId)));
    }

    @PutMapping("/{id}/procedimentos/{procedimentoId}/observacoes-pos")
    public ResponseEntity<ProcedimentoResposta> registrarObservacoesPos(
            @PathVariable Long id,
            @PathVariable Long procedimentoId,
            @Valid @RequestBody ObservacoesRequest request) {
        procedimentoDoCirurgiao(id, procedimentoId);
        procedimentoService.registrarObservacoesPos(procedimentoId, request.observacoes());
        return ResponseEntity.ok(Respostas.procedimento(procedimentoService.buscarPorId(procedimentoId)));
    }

    private ProcedimentoCirurgico procedimentoDoCirurgiao(Long cirurgiaoId, Long procedimentoId) {
        ProcedimentoCirurgico procedimento = procedimentoService.buscarPorId(procedimentoId);
        if (!procedimento.getCirurgiao().getId().equals(cirurgiaoId)) {
            throw new NoSuchElementException(
                    "O procedimento " + procedimentoId + " não pertence ao cirurgião " + cirurgiaoId + ".");
        }
        return procedimento;
    }
}
