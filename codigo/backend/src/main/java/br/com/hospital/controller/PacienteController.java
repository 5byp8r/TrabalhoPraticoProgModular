package br.com.hospital.controller;

import br.com.hospital.controller.Respostas.HistoricoResposta;
import br.com.hospital.controller.Respostas.PacienteResposta;
import br.com.hospital.model.Paciente;
import br.com.hospital.service.HistoricoMedicoService;
import br.com.hospital.service.PacienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;
    private final HistoricoMedicoService historicoService;

    public PacienteController(PacienteService pacienteService, HistoricoMedicoService historicoService) {
        this.pacienteService = pacienteService;
        this.historicoService = historicoService;
    }

    public record PacienteRequest(
            @NotBlank String nome,
            @NotBlank String cpf,
            @NotNull @Past LocalDate dataNascimento,
            @NotBlank String telefone,
            @NotBlank String endereco,
            @NotBlank @Email String email) {
    }

    public record AtualizacaoPacienteRequest(
            @NotBlank String nome,
            @NotBlank String telefone,
            @NotBlank String endereco,
            @NotBlank @Email String email) {
    }

    @GetMapping
    public ResponseEntity<List<PacienteResposta>> listar(@RequestParam(required = false) String busca) {
        String termo = busca == null ? "" : busca.trim().toLowerCase();
        String termoCpf = termo.replaceAll("\\D", "");

        List<Paciente> pacientes = pacienteService.listar().stream()
                .filter(p -> termo.isEmpty()
                        || p.getNome().toLowerCase().contains(termo)
                        || (!termoCpf.isEmpty() && p.getCpf().contains(termoCpf)))
                .toList();
        return ResponseEntity.ok(Respostas.lista(pacientes, Respostas::paciente));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.paciente(pacienteService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<PacienteResposta> cadastrar(@Valid @RequestBody PacienteRequest request) {
        Paciente paciente = pacienteService.cadastrar(
                request.nome(),
                request.cpf(),
                request.dataNascimento(),
                request.telefone(),
                request.endereco(),
                request.email());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(paciente.getId())
                .toUri();
        return ResponseEntity.created(location).body(Respostas.paciente(paciente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResposta> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizacaoPacienteRequest request) {
        Paciente paciente = pacienteService.atualizar(
                id, request.nome(), request.telefone(), request.endereco(), request.email());
        return ResponseEntity.ok(Respostas.paciente(paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pacienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<HistoricoResposta> historico(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.historico(historicoService.consultar(id)));
    }
}
