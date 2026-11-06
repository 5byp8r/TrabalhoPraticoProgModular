package br.com.hospital.model;

import java.util.ArrayList;
import java.util.List;

public class Cirurgiao extends ProfissionalSaude {

    private String areaCirurgica;
    private final List<ProcedimentoCirurgico> procedimentos = new ArrayList<>();

    public Cirurgiao(
            Long id,
            String nome,
            String registroProfissional,
            String telefone,
            String email,
            String areaCirurgica) {

        super(id, nome, registroProfissional, telefone, email);

        if (areaCirurgica == null || areaCirurgica.isBlank()) {
            throw new IllegalArgumentException(
                    "A área cirúrgica deve ser informada.");
        }

        this.areaCirurgica = areaCirurgica.trim();
    }

    public String getAreaCirurgica() {
        return areaCirurgica;
    }

    public List<ProcedimentoCirurgico> getProcedimentos() {
        return List.copyOf(procedimentos);
    }

    public void registrarProcedimento(ProcedimentoCirurgico procedimento) {
        if (procedimento == null || procedimento.getCirurgiao() != this) {
            throw new IllegalArgumentException("O procedimento deve pertencer a este cirurgião.");
        }
        if (!procedimentos.contains(procedimento)) {
            procedimentos.add(procedimento);
        }
    }

    public void realizaProce(ProcedimentoCirurgico procedimento) {
        if (procedimento == null || procedimento.getCirurgiao() != this) {
            throw new IllegalArgumentException("O procedimento deve pertencer a este cirurgião.");
        }
        procedimento.iniciar();
    }
}
