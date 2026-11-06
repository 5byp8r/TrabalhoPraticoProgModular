# Models da Sprint 2 - Documentação e testes feitos totalmentes por IA (Codex).

Implementação restrita a `br.com.hospital.model` e seus testes unitários.
Services, repositories, controllers e configuração da API permanecem sob responsabilidade
dos demais integrantes. Os models não dependem de Spring nem de persistência.

## Estrutura

- `ProfissionalSaude` é abstrata. `MedicoClinicoGeral`, `MedicoEspecialista` e `Cirurgiao`
  herdam os dados comuns e os históricos de consultas e internações.
- `Atendimento` é abstrata e concentra `id`, `paciente` e o contrato `validarCondicoes()`.
- `Consulta` é abstrata. `ConsultaRotina` e `ConsultaEspecializada` validam suas próprias
  condições. Emergência e procedimento cirúrgico herdam diretamente de `Atendimento`.
- `Paciente`, `Quarto` e `Internacao` completam o modelo do diagrama.
- Os seis enums representam especialidade, prioridade e situações sem textos livres.

## Contrato para os services

Os construtores recebem os objetos relacionados já localizados pelo service. Os IDs
são recebidos na construção; os models não os geram. Busca por ID, unicidade de CPF/registro/ID,
listagens, filtros, conflitos de agenda e persistência não são feitos pelos models.
O service deve reutilizar a mesma instância do paciente/profissional/quarto ao coordenar
uma operação em memória: os vínculos são verificados por referência, não apenas por ID.

Não há setters públicos para situação, médico, paciente, quarto ou ocupação. As coleções
retornadas pelos getters são cópias não modificáveis. A criação de uma consulta válida
atualiza os históricos do paciente e do médico; a criação de um procedimento válido
atualiza o histórico do cirurgião. Uma internação válida ocupa uma vaga e atualiza os
históricos do paciente e do responsável. Não repita esses registros no service.

As operações são locais e não fornecem controle de concorrência entre requisições.
O service/persistência deverá coordenar a atomicidade de operações simultâneas.
Os históricos criam relacionamentos bidirecionais. Na API, a equipe deve usar DTOs ou
definir uma estratégia de serialização para evitar ciclos ao retornar os objetos como JSON.

### Consultas

```java
ConsultaRotina consulta = new ConsultaRotina(id, paciente, clinico, dataHora, motivo);
consulta.confirmar();
consulta.iniciar();
consulta.registrarObservacoes("Avaliação realizada");
consulta.concluir();
```

| Situação atual | Operações permitidas |
| --- | --- |
| AGENDADA | confirmar, iniciar, cancelar, registrar não comparecimento, reagendar |
| CONFIRMADA | iniciar, cancelar, registrar não comparecimento, reagendar |
| EM_ATENDIMENTO | registrar observações, concluir |
| CONCLUIDA | registrar observações |
| CANCELADA / NAO_COMPARECEU | novo agendamento explícito |

`agendarConsulta(novaDataHora)` é o novo agendamento explícito, volta para `AGENDADA`
e exige que o fluxo de atendimento seja percorrido novamente. Ele não reabre consultas
iniciadas ou concluídas. `controlarConsulta(situacao)` delega às mesmas operações e não
aceita `AGENDADA`: a nova data deve ser passada a `agendarConsulta`.

Uma consulta especializada recebe `Especialidade` e exige um `MedicoEspecialista` da
mesma área. A especialidade do médico agora é um enum, inclusive no construtor e getter.
`clinico.encaminhar(id, paciente, especialista, dataHora, motivo)` cria e agenda uma
consulta especializada. `clinico.consulta(consulta)` e `especialista.realizaAtend(consulta)`
iniciam uma consulta pertencente ao profissional correspondente.

### Emergências e procedimentos

Emergências começam em `AGUARDANDO_AVALIACAO`. `iniciar()` muda para `EM_ATENDIMENTO`
e `concluir()` só aceita esse estado, mudando para `CONCLUIDO`.

Procedimentos começam em `AGENDADO`. `iniciar(dataHora)` registra o início efetivo e
muda para `EM_ANDAMENTO`. `concluir(dataHora)` exige início anterior e um horário final
igual ou posterior ao inicial. `cancelar()` só aceita procedimentos agendados.
Observações pré-operatórias são registradas antes do início; as pós-operatórias,
após a conclusão. `cirurgiao.realizaProce(procedimento)` inicia um procedimento seu.

`iniciar()` e `concluir()` sem parâmetros usam o relógio local da aplicação. As versões
com data/hora explícita permitem registrar horários efetivos e executar testes determinísticos.

### Internações e quartos

```java
Quarto quarto = new Quarto(idQuarto, "101", 1, 2);
Internacao internacao = new Internacao(idInternacao, paciente, medico, quarto,
        entrada, previsaoAlta, observacoes);
internacao.darAlta(dataAlta);
```

A capacidade precisa ser positiva. A ocupação é calculada pelas internações ativas:
`DISPONIVEL` significa que ainda há vagas, `OCUPADO` significa lotação máxima.
`colocarEmManutencao()` exige quarto vazio e bloqueia admissões;
`finalizarManutencao()` libera o quarto. Não há alteração manual da quantidade atual.

Um paciente não pode ter duas internações ativas na mesma representação em memória.
A previsão de alta é opcional e não pode anteceder a entrada. `darAlta(data)` não aceita
data anterior à entrada nem alta repetida, libera exatamente uma vaga e preserva os
históricos. `darAlta()` usa a data local da aplicação. A situação da internação é exposta
por `isAtiva()` e `dataAlta`; o enunciado não define um enum de situação para ela.

## Escolhas e pendências do enunciado

- O PDF atribui consultas de rotina ao clínico geral; a implementação exige essa categoria.
- O fluxo permite iniciar uma consulta agendada sem confirmação, pois o PDF não exige
  confirmação prévia. Cancelamento e não comparecimento se aplicam a consultas pendentes.
- As regras de capacidade, manutenção, datas de alta e internação ativa única completam
  os comportamentos do diagrama; o PDF não enumera todos esses detalhes.
- A entrega menciona **tipos de quartos**, mas o PDF e o diagrama fornecidos não definem
  categorias ou suas regras. Nenhum tipo foi inventado. `SituacaoQuarto` representa
  disponibilidade, lotação e manutenção, e não substitui um eventual tipo de quarto.
- A interface `gerenciaPacientes` do diagrama não tem contrato/realização claros e não é
  exigida pelo PDF. Ela não foi implementada como CRUD genérico nos models.
- Os dados herdados `paciente` e `consultas` aparecem somente nas superclasses, evitando
  as duplicações do diagrama. Quantidade atual do quarto é derivada para evitar divergência.
- Métodos de impressão do diagrama foram mantidos como operações auxiliares de console;
  os endpoints devem usar os dados do objeto e a representação definida pela equipe.

## Testes

No diretório `codigo/backend`, com JDK 21 ou superior:

```powershell
.\mvnw.cmd '-Dtest=br.com.hospital.model.*Test' test
```

Os testes exercitam especialidades incompatíveis, transições válidas e inválidas,
horários cirúrgicos, prioridades de emergência, capacidade/manutenção dos quartos,
alta, rejeições sem alterações parciais nos históricos e proteção das coleções.

Validação realizada: os 49 testes dos models passaram, com compilação para Java 21.
A execução da suíte completa também encontrou um erro no teste existente
`BackendApplicationTests`: o Spring não localiza `@SpringBootConfiguration`.
O teste está em `br.com.hospital`, enquanto `BackendApplication` está no subpacote
`br.com.hospital.application`. Esse teste/configuração não foi alterado nesta entrega
restrita aos models.
