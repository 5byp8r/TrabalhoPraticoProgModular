## Cartões CRC

## Sistema de Informação Hospitalar

Integrantes do Grupo:

- Lukas Nathan Matos Candeia

- Gabriel do Carmo Assis

- Francisco Berutti

- Davi Emanuel Moreira Sollar

## 1. Cartões CRC

Os cartões CRC (Classe - Responsabilidade - Colaboração) são uma técnica utilizada no processo de modelagem orientada a objetos. Eles ajudam a representar de forma simples e visual as principais responsabilidades de uma classe e como ela se relaciona com outras classes dentro de um sistema.

- Classe: define o nome da entidade e seu papel no sistema.

- Responsabilidade: descreve as funções ou comportamentos que a classe deve executar.

- Colaboração: indica com quais outras classes a classe em questão precisa interagir para cumprir suas responsabilidades.

O uso dos cartões CRC favorece a compreensão coletiva do sistema, a comunicação entre os membros da equipe e o raciocínio sobre a distribuição adequada das responsabilidades. Além disso, eles estimulam a identificação de dependências entre classes e evitam a concentração excessiva de funções em uma única classe.

Nesta etapa do projeto, cada grupo deverá elaborar cartões CRC para representar partes específicas do sistema em estudo. O objetivo é exercitar a análise de responsabilidades e colaborações, consolidando a visão orientada a objetos de forma prática e colaborativa.


## 1.1. Cartões CRC - Caso de Uso: Agendar e Realizar Consulta

| Nome da Classe | Paciente |
| --- | --- |
| Responsabilidades | Colaborações |
| 1. Conhecer seu nome, CPF e data de nascimento. 2. Conhecer seus dados de contato (telefone, endereço, e-mail) 3. Conhecer seu histórico de consultas e internações. 4. Imprimir os dados do paciente. 5. Registrar histórico de consulta do paciente. 6. Permitir edição dos dados do paciente. | Consulta, Internacao |

| Nome da Classe | ProfissionalSaude |
| --- | --- |
| Responsabilidades | Colaborações |
| 1. Conhecer seu nome, especialidade e registro profissional. 2. Conhecer seus dados de contato telefônico e e-mail. 3. Conhecer os atendimentos sob sua responsabilidade e os horários 4. Imprimir informações do profissional. 5. Imprimir atendimentos sob sua responsabilidade. 6. Permitir edição dos dados do Profissional de saúde. | Consulta, Internacao |


| Nome da Classe | Consulta |
| --- | --- |
| Responsabilidades | Colaborações |
| 1. Conhecer o paciente que será atendido. 2. Conhecer o profissional de saúde responsável. 3. Conhecer a data e o horário agendados. 4. Conhecer o motivo da consulta e as observações médicas. 5. Agendamento e controle de consultas. 6. Imprimir informações da consulta. | Paciente, ProfissionalSaude |


## 1.2. Cartões CRC - Caso de Uso: Gerenciar Internação

| Nome da Classe | Quarto |
| --- | --- |
| Responsabilidades | Colaborações |
| 1. Conhecer seu número de identificação e andar. 2. Conhecer sua capacidade máxima e a quantidade atual de internados. 3. Conhecer sua situação atual (disponível, ocupado ou manutenção). 4. Gerenciamento dos quartos. 5. Imprimir informações do quarto. | Internacao |

| Nome da Classe | Internacao |
| --- | --- |
| Responsabilidades | Colaborações |
| 1. Conhecer o paciente que está sendo internado. 2. Conhecer o profissional responsável e o quarto ocupado. 3. Conhecer a data de entrada, previsão de alta e a data efetiva de alta. 4. Conhecer as observações registradas durante o período de internação. 5. Controle de internações 6. Imprimir informações da internação. | Paciente, Quarto |

| Nome da Classe | Usuario |
| --- | --- |
| Responsabilidades | Colaborações |
| 1. Conhecer seus dados (e-mail, senha, nome de usuario) 2. Autenticar-se no sistema |  |



