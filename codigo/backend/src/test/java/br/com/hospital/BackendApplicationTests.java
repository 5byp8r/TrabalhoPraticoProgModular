package br.com.hospital;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import br.com.hospital.application.BackendApplication;
import br.com.hospital.service.AtendimentoEmergenciaService;
import br.com.hospital.service.ConsultaService;
import br.com.hospital.service.HistoricoMedicoService;
import br.com.hospital.service.InternacaoService;
import br.com.hospital.service.PacienteService;
import br.com.hospital.service.ProcedimentoCirurgicoService;
import br.com.hospital.service.ProfissionalSaudeService;
import br.com.hospital.service.QuartoService;

@SpringBootTest(classes = BackendApplication.class)
class BackendApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        for (Class<?> service : new Class<?>[] {PacienteService.class, ProfissionalSaudeService.class,
                QuartoService.class, ConsultaService.class, InternacaoService.class,
                ProcedimentoCirurgicoService.class, AtendimentoEmergenciaService.class, HistoricoMedicoService.class}) {
            assertNotNull(context.getBean(service));
        }
    }

}
