package br.com.hospital.controller;

import br.com.hospital.controller.Respostas.InternacaoResposta;
import br.com.hospital.controller.Respostas.QuartoResposta;
import br.com.hospital.model.Quarto;
import br.com.hospital.model.SituacaoQuarto;
import br.com.hospital.service.QuartoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService quartoService;

    public QuartoController(QuartoService quartoService) {
        this.quartoService = quartoService;
    }

    public record QuartoRequest(
            @NotBlank String numero,
            @NotNull @PositiveOrZero Integer andar,
            @NotNull @Positive Integer capacidadeMaxima) {
    }

    @GetMapping
    public ResponseEntity<List<QuartoResposta>> listar(
            @RequestParam(required = false) SituacaoQuarto situacao,
            @RequestParam(required = false, defaultValue = "false") boolean somenteComVaga) {

        List<Quarto> quartos = (somenteComVaga ? quartoService.listarDisponiveis() : quartoService.listar())
                .stream()
                .filter(q -> situacao == null || q.getSituacao() == situacao)
                .toList();
        return ResponseEntity.ok(Respostas.lista(quartos, Respostas::quarto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuartoResposta> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(Respostas.quarto(quartoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<QuartoResposta> cadastrar(@Valid @RequestBody QuartoRequest request) {
        Quarto quarto = quartoService.cadastrar(
                request.numero(),
                request.andar(),
                request.capacidadeMaxima());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(quarto.getId())
                .toUri();
        return ResponseEntity.created(location).body(Respostas.quarto(quarto));
    }

    @PatchMapping("/{id}/manutencao/iniciar")
    public ResponseEntity<QuartoResposta> colocarEmManutencao(@PathVariable Long id) {
        quartoService.colocarEmManutencao(id);
        return ResponseEntity.ok(Respostas.quarto(quartoService.buscarPorId(id)));
    }

    @PatchMapping("/{id}/manutencao/finalizar")
    public ResponseEntity<QuartoResposta> finalizarManutencao(@PathVariable Long id) {
        quartoService.finalizarManutencao(id);
        return ResponseEntity.ok(Respostas.quarto(quartoService.buscarPorId(id)));
    }

    @GetMapping("/{id}/internacoes")
    public ResponseEntity<List<InternacaoResposta>> listarInternacoesAtivas(@PathVariable Long id) {
        Quarto quarto = quartoService.buscarPorId(id);
        return ResponseEntity.ok(Respostas.lista(quarto.getInternacoesAtivas(), Respostas::internacao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        quartoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
