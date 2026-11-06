package br.com.hospital.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.Especialidade;
import br.com.hospital.model.MedicoClinicoGeral;
import br.com.hospital.model.MedicoEspecialista;
import br.com.hospital.model.ProfissionalSaude;

@Service
public class ProfissionalSaudeService {
    private final DadosHospitalares dados;

    public ProfissionalSaudeService(DadosHospitalares dados) { this.dados = dados; }

    public MedicoClinicoGeral cadastrarClinicoGeral(String nome, String registro, String telefone, String email) {
        synchronized (dados) {
            validarCadastro(nome, registro, telefone, email);
            return dados.profissionais.adicionar(id ->
                    new MedicoClinicoGeral(id, nome.trim(), registro.trim(), telefone.trim(), email.trim()));
        }
    }

    public MedicoEspecialista cadastrarEspecialista(String nome, String registro, String telefone,
            String email, Especialidade especialidade) {
        synchronized (dados) {
            validarCadastro(nome, registro, telefone, email);
            return dados.profissionais.adicionar(id ->
                    new MedicoEspecialista(id, nome.trim(), registro.trim(), telefone.trim(), email.trim(), especialidade));
        }
    }

    public Cirurgiao cadastrarCirurgiao(String nome, String registro, String telefone, String email, String area) {
        synchronized (dados) {
            validarCadastro(nome, registro, telefone, email);
            return dados.profissionais.adicionar(id ->
                    new Cirurgiao(id, nome.trim(), registro.trim(), telefone.trim(), email.trim(), area));
        }
    }

    public ProfissionalSaude buscarPorId(Long id) {
        synchronized (dados) { return dados.profissionais.buscar(id); }
    }

    public List<ProfissionalSaude> listar() {
        synchronized (dados) { return dados.profissionais.listar(); }
    }

    public List<MedicoEspecialista> listarPorEspecialidade(Especialidade especialidade) {
        synchronized (dados) {
            if (especialidade == null) {
                throw new IllegalArgumentException("Informe a especialidade.");
            }
            return dados.profissionais.listar().stream().filter(MedicoEspecialista.class::isInstance)
                    .map(MedicoEspecialista.class::cast)
                    .filter(p -> p.getEspecialidade() == especialidade).toList();
        }
    }

    public List<MedicoEspecialista> listarDisponiveis(Especialidade especialidade, LocalDateTime horario) {
        synchronized (dados) {
            if (horario == null) {
                throw new IllegalArgumentException("Informe a data e o horário.");
            }
            return listarPorEspecialidade(especialidade).stream()
                    .filter(p -> dados.estaDisponivel(p, horario, null)).toList();
        }
    }

    public ProfissionalSaude atualizar(Long id, String nome, String telefone, String email) {
        synchronized (dados) {
            ProfissionalSaude profissional = dados.profissionais.buscar(id);
            validarContato(nome, telefone, email);
            profissional.editarDados(nome, telefone.trim(), email.trim());
            return profissional;
        }
    }

    public void excluir(Long id) {
        synchronized (dados) {
            ProfissionalSaude profissional = dados.profissionais.buscar(id);
            if (!profissional.getConsultas().isEmpty() || !profissional.getInternacoes().isEmpty()
                    || dados.procedimentos.listar().stream().anyMatch(p -> p.getCirurgiao() == profissional)
                    || dados.emergencias.listar().stream().anyMatch(e -> e.getResponsavel() == profissional)) {
                throw new IllegalStateException("O profissional possui histórico de atendimentos e não pode ser excluído.");
            }
            dados.profissionais.remover(id);
        }
    }

    private void validarCadastro(String nome, String registro, String telefone, String email) {
        validarContato(nome, telefone, email);
        String numero = DadosHospitalares.exigirTexto(registro, "o registro profissional");
        if (dados.profissionais.listar().stream().anyMatch(p -> p.getRegistroProfissional().equalsIgnoreCase(numero))) {
            throw new IllegalStateException("Já existe um profissional com este registro.");
        }
    }

    private void validarContato(String nome, String telefone, String email) {
        DadosHospitalares.exigirTexto(nome, "o nome");
        DadosHospitalares.exigirTexto(telefone, "o telefone");
        DadosHospitalares.exigirTexto(email, "o e-mail");
    }
}
