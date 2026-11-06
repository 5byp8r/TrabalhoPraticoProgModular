package br.com.hospital.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/** Armazenamento temporário; acessado sob o monitor de DadosHospitalares. */
final class CadastroEmMemoria<T> {
    private final Map<Long, T> registros = new LinkedHashMap<>();
    private long proximoId = 1;

    <S extends T> S adicionar(Function<Long, S> fabrica) {
        S registro = fabrica.apply(proximoId);
        registros.put(proximoId++, registro);
        return registro;
    }

    T buscar(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador deve ser informado.");
        }
        T registro = registros.get(id);
        if (registro == null) {
            throw new NoSuchElementException("Registro não encontrado: " + id);
        }
        return registro;
    }

    List<T> listar() {
        return List.copyOf(registros.values());
    }

    void remover(Long id) {
        buscar(id);
        registros.remove(id);
    }
}
