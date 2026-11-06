package br.com.hospital.service;

import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.Quarto;

@Service
public class QuartoService {
    private final DadosHospitalares dados;

    public QuartoService(DadosHospitalares dados) { this.dados = dados; }

    public Quarto cadastrar(String numero, int andar, int capacidadeMaxima) {
        synchronized (dados) {
            String identificacao = DadosHospitalares.exigirTexto(numero, "o número do quarto");
            if (dados.quartos.listar().stream().anyMatch(q -> q.getNumero().equalsIgnoreCase(identificacao))) {
                throw new IllegalStateException("Já existe um quarto com este número.");
            }
            return dados.quartos.adicionar(id -> new Quarto(id, identificacao, andar, capacidadeMaxima));
        }
    }

    public Quarto buscarPorId(Long id) {
        synchronized (dados) { return dados.quartos.buscar(id); }
    }

    public List<Quarto> listar() {
        synchronized (dados) { return dados.quartos.listar(); }
    }

    public List<Quarto> listarDisponiveis() {
        synchronized (dados) { return dados.quartos.listar().stream().filter(Quarto::possuiVaga).toList(); }
    }

    public void colocarEmManutencao(Long id) {
        synchronized (dados) { dados.quartos.buscar(id).colocarEmManutencao(); }
    }

    public void finalizarManutencao(Long id) {
        synchronized (dados) { dados.quartos.buscar(id).finalizarManutencao(); }
    }

    public void excluir(Long id) {
        synchronized (dados) {
            Quarto quarto = dados.quartos.buscar(id);
            if (dados.internacoes.listar().stream().anyMatch(i -> i.getQuarto() == quarto)) {
                throw new IllegalStateException("O quarto possui histórico de internações e não pode ser excluído.");
            }
            dados.quartos.remover(id);
        }
    }
}
