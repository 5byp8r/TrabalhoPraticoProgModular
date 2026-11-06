package br.com.hospital.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.Paciente;

@Service
public class PacienteService {
    private final DadosHospitalares dados;

    public PacienteService(DadosHospitalares dados) {
        this.dados = dados;
    }

    public Paciente cadastrar(String nome, String cpf, LocalDate nascimento,
            String telefone, String endereco, String email) {
        synchronized (dados) {
            validarDados(nome, telefone, endereco, email);
            String documento = DadosHospitalares.exigirTexto(cpf, "o CPF").replaceAll("[.\\-\\s]", "");
            if (!documento.matches("[0-9]{11}")) {
                throw new IllegalArgumentException("O CPF deve conter 11 dígitos.");
            }
            if (nascimento == null || nascimento.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Informe uma data de nascimento válida.");
            }
            if (dados.pacientes.listar().stream().anyMatch(p -> p.getCpf().equals(documento))) {
                throw new IllegalStateException("Já existe um paciente com este CPF.");
            }
            return dados.pacientes.adicionar(id -> new Paciente(id, nome.trim(), documento,
                    nascimento, telefone.trim(), endereco.trim(), email.trim()));
        }
    }

    public Paciente buscarPorId(Long id) {
        synchronized (dados) { return dados.pacientes.buscar(id); }
    }

    public List<Paciente> listar() {
        synchronized (dados) { return dados.pacientes.listar(); }
    }

    public Paciente atualizar(Long id, String nome, String telefone, String endereco, String email) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(id);
            validarDados(nome, telefone, endereco, email);
            paciente.editarDados(nome, telefone.trim(), endereco.trim(), email.trim());
            return paciente;
        }
    }

    public void excluir(Long id) {
        synchronized (dados) {
            Paciente paciente = dados.pacientes.buscar(id);
            if (!paciente.getConsultas().isEmpty() || !paciente.getInternacoes().isEmpty()
                    || dados.procedimentos.listar().stream().anyMatch(p -> p.getPaciente() == paciente)
                    || dados.emergencias.listar().stream().anyMatch(e -> e.getPaciente() == paciente)) {
                throw new IllegalStateException("O paciente possui histórico de atendimentos e não pode ser excluído.");
            }
            dados.pacientes.remover(id);
        }
    }

    private void validarDados(String nome, String telefone, String endereco, String email) {
        DadosHospitalares.exigirTexto(nome, "o nome");
        DadosHospitalares.exigirTexto(telefone, "o telefone");
        DadosHospitalares.exigirTexto(endereco, "o endereço");
        DadosHospitalares.exigirTexto(email, "o e-mail");
    }
}
