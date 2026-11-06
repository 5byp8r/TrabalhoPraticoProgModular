# Camada de serviços - Documentação e testes feitos totalmentes por IA (Codex).

Os services coordenam as operações reutilizando as validações e transições das classes de domínio. São componentes Spring com injeção por construtor.

| Service                        | Operações principais                                                                                           |
| ------------------------------ | -------------------------------------------------------------------------------------------------------------- |
| `PacienteService`              | Cadastro, busca, listagem, edição de dados e exclusão sem vínculos                                             |
| `ProfissionalSaudeService`     | Cadastro por categoria, edição, exclusão sem vínculos, busca por especialidade e disponibilidade em um horário |
| `QuartoService`                | Cadastro, busca, listagem de vagas, manutenção e exclusão sem histórico                                        |
| `ConsultaService`              | Agendamento de rotina e especializado, encaminhamento, reagendamento, situação e observações                   |
| `InternacaoService`            | Admissão, listagem, internações ativas, alta e observações                                                     |
| `ProcedimentoCirurgicoService` | Agendamento, início e fim efetivos, cancelamento e observações pré e pós-operatórias                           |
| `AtendimentoEmergenciaService` | Registro, fila por prioridade, início e conclusão                                                              |
| `HistoricoMedicoService`       | Consultas, internações, cirurgias e emergências de um paciente                                                 |

## Uso sem banco

`DadosHospitalares` compartilha os cadastros em memória. Os IDs são gerados
automaticamente por cadastro; todos os dados são perdidos ao encerrar a aplicação.
Para uso fora do Spring, passe a mesma instância de `DadosHospitalares` aos
construtores de todos os services.

```java
DadosHospitalares dados = new DadosHospitalares();
PacienteService pacientes = new PacienteService(dados);
ProfissionalSaudeService profissionais = new ProfissionalSaudeService(dados);
ConsultaService consultas = new ConsultaService(dados);

var paciente = pacientes.cadastrar("Ana", "12345678900",
        LocalDate.of(1990, 1, 1), "31999999999", "Rua A", "ana@example.com");
var medico = profissionais.cadastrarClinicoGeral(
        "Bruno", "CRM-1", "31988888888", "bruno@example.com");
var consulta = consultas.agendarRotina(paciente.getId(), medico.getId(),
        LocalDateTime.of(2026, 11, 10, 10, 0), "Avaliação inicial");
consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.EM_ATENDIMENTO);
consultas.registrarObservacoes(consulta.getId(), "Retorno em 30 dias");
consultas.alterarSituacao(consulta.getId(), SituacaoConsulta.CONCLUIDA);
```

O exemplo usa classes dos pacotes `br.com.hospital.service`,
`br.com.hospital.model` e `java.time`.

## Regras e decisões

- Os vínculos são resolvidos por ID antes de criar atendimentos. Não é necessário
  construir entidades manualmente para usar os services.
- CPF é armazenado sem pontuação, com 11 dígitos, e não pode ser repetido.
  Esta validação de formato não calcula os dígitos verificadores. Registro
  profissional e número de quarto também são únicos, ignorando maiúsculas e
  minúsculas e espaços nas extremidades.
- O conflito de agenda considera data e horário exatos, pois o domínio não
  define duração. Cancelamentos e não comparecimentos liberam o horário.
  Reagendamentos verificam conflitos antes de alterar a consulta.
- Um profissional não pode iniciar outro atendimento enquanto possui consulta,
  cirurgia ou emergência em andamento. Emergências não reservam horário futuro,
  pois o modelo não possui data/hora para essa modalidade.
- As condições específicas e mudanças de situação são validadas pelo domínio.
  Quartos em manutenção ou sem vagas não recebem internações; altas liberam a
  ocupação sem remover o histórico.
- A fila de emergência ordena crítica, alta, moderada e baixa, mantendo a ordem
  de registro nos empates. A ordenação auxilia a consulta e não impõe a escolha
  do próximo atendimento.
- O histórico inclui registros de todas as situações e as respectivas
  observações. Atendimentos não têm operação de exclusão; pacientes,
  profissionais e quartos com histórico também não podem ser excluídos.
- O enunciado menciona tipos de quartos na entrega, mas não define categorias
  ou regras e o UML apresenta apenas `Quarto` e `SituacaoQuarto`. A implementação
  acompanha esse modelo.

As listas retornadas são cópias não modificáveis, mas contêm as entidades do
domínio. Altere essas entidades pelos services para respeitar as verificações
entre cadastros. As operações dos services usam um monitor compartilhado para
tornar a verificação e a alteração atômicas nesta instância da aplicação; isso
não substitui transações ou restrições de um futuro banco de dados.

Dados inválidos lançam `IllegalArgumentException`, conflitos ou estados
incompatíveis lançam `IllegalStateException` e IDs desconhecidos lançam
`NoSuchElementException`. O mapeamento dessas exceções para HTTP fica para a
implementação dos controllers.

## Verificação

Com Maven e JDK 21 ou superior, execute na pasta `codigo/backend`:

```shell
mvn test
```

Os testes de services verificam integridade após falhas, agenda, concorrência,
especialidades, internações, cirurgias, emergências e histórico. O teste do
contexto Spring também verifica a descoberta dos oito services.
