# Sistema de Importação e Validação de Pedidos

Sistema web para importar pedidos de compra via arquivo CSV. O usuário faz o upload de um arquivo, o sistema valida cada linha conforme as regras de negócio, salva os pedidos válidos, registra os erros das linhas inválidas e permite acompanhar o status da importação. O processamento do arquivo é feito de forma assíncrona, fora da requisição de upload.

## Tecnologias

- **Backend:** Java 21 + Spring Boot
- **Banco de dados:** PostgreSQL
- **Mensageria (processamento assíncrono):** RabbitMQ
- **Migrations:** Flyway
- **Frontend:** Angular
- **Infraestrutura:** Docker Compose

## Pré-requisitos

- Docker e Docker Compose instalados (com o Docker Desktop aberto)
- Java 21
- Node.js e Angular CLI (para o frontend)

## Como rodar

### 1. Subir a infraestrutura (Postgres + RabbitMQ)

Na raiz do projeto:

```bash
docker compose up -d
```

Isso sobe dois containers:
- PostgreSQL na porta `5432`
- RabbitMQ na porta `5672`, com painel de administração em `http://localhost:15672` (usuário/senha: `guest`/`guest`)

### 2. Rodar o backend

```bash
cd backend
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080`. Ao iniciar, o Flyway executa a migration e cria as tabelas automaticamente.

### 3. Rodar o frontend

```bash
cd frontend
npm install
ng serve
```

O frontend abre em `http://localhost:4200`.

## Como usar

1. Acesse `http://localhost:4200`
2. Na tela de **Upload**, selecione um arquivo `.csv` e envie (há um arquivo de exemplo na raiz do projeto: `pedidos_exemplo.csv`)
3. Na tela de **Histórico**, acompanhe o status da importação (o processamento é assíncrono, use o botão Atualizar)
4. Clique em uma importação para ver o **Detalhe** (resumo, erros e pedidos gerados)
5. Na tela de **Pedidos**, consulte os pedidos importados com filtros por número, cliente, data e importação de origem

## Layout do CSV

```
numero_pedido,cliente,documento_cliente,produto,quantidade,valor_unitario,data_pedido
PED-001,Empresa Alfa,12345678000199,Notebook Dell,2,3500.00,2026-06-01
```

### Regras de validação

- `numero_pedido`, `cliente`, `documento_cliente` e `produto` são obrigatórios
- `quantidade` deve ser maior que zero
- `valor_unitario` deve ser maior que zero
- `data_pedido` não pode ser futura
- não pode haver dois pedidos com o mesmo `numero_pedido`

Linhas válidas viram pedidos; linhas inválidas viram registros de erro, sem interromper o processamento das demais.

## Arquitetura

O backend segue uma **arquitetura em camadas**, separando as responsabilidades em pacotes:

- `controller` — entrada das requisições HTTP (endpoints REST)
- `service` — lógica de negócio e orquestração
- `listener` — consumo da fila do RabbitMQ (processamento assíncrono)
- `validation` — regras de validação das linhas, isoladas e sem dependência de banco ou fila (facilita os testes)
- `repository` — acesso ao banco de dados (Spring Data JPA)
- `entity` — entidades mapeadas para as tabelas
- `dto` — objetos de transporte de dados entre as camadas
- `config` — configurações (RabbitMQ, CORS)

Essa separação por responsabilidade facilita a manutenção e o teste de cada parte de forma independente.

O fluxo da aplicação:

```
Frontend (Angular) 
   -> POST /importacoes (upload do CSV)
      -> Backend salva o arquivo e cria a importação (status RECEBIDA)
      -> Publica o id da importação na fila do RabbitMQ
      -> Responde o id imediatamente (a requisição não espera o processamento)

RabbitMQ (fila)
   -> Listener consome a mensagem
      -> Lê o CSV, valida linha a linha
      -> Salva pedidos válidos e erros
      -> Atualiza contadores e status final (CONCLUIDA / CONCLUIDA_COM_ERROS / FALHOU)

Frontend consulta o status e os resultados pelos endpoints GET
```

### Por que processamento assíncrono?

O processamento do CSV pode ser demorado e não deve travar a requisição HTTP de upload (risco de timeout e de prender recursos do servidor). Por isso o upload apenas registra a importação e publica o id na fila, respondendo na hora. O processamento pesado acontece em segundo plano, consumido pelo listener do RabbitMQ.

### Por que RabbitMQ?

Optei por RabbitMQ por ser uma solução de mensageria simples de configurar e integrar com o Spring (`spring-boot-starter-amqp`), adequada ao escopo do desafio. Em relação a processar numa thread interna, a fila garante que a mensagem não se perde caso a aplicação caia no meio do processamento (a mensagem só é confirmada/removida após o consumo bem-sucedido — ACK).

### Status da importação

- `RECEBIDA` — arquivo recebido, aguardando processamento
- `PROCESSANDO` — processamento em andamento
- `CONCLUIDA` — finalizada sem erros
- `CONCLUIDA_COM_ERROS` — finalizada com linhas inválidas
- `FALHOU` — erro inesperado durante o processamento

## Endpoints (API REST)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/importacoes` | Upload do CSV (form-data, campo `arquivo`) |
| GET | `/importacoes` | Lista o histórico de importações |
| GET | `/importacoes/{id}` | Detalhe de uma importação (resumo, pedidos e erros) |
| GET | `/pedidos` | Lista pedidos, com filtros opcionais (`numeroPedido`, `cliente`, `dataPedido`, `importacaoId`) |

## Banco de dados

Três tabelas principais:
- `importacao` — registro de cada arquivo importado, com status e contadores
- `pedido` — pedidos válidos, vinculados à importação de origem
- `erro_importacao` — erros encontrados, vinculados à importação de origem

As tabelas são criadas pelo Flyway (script em `backend/src/main/resources/db/migration/V1__create_tables.sql`).

## Testes

Os testes unitários cobrem as regras de negócio da validação das linhas (classe `ValidadorLinha`):

```bash
cd backend
./mvnw test
```

## O que eu melhoraria com mais tempo

- **Parsing de CSV mais robusto:** hoje uso `split(",")`, que não trata campos com vírgula dentro de aspas. Usaria uma biblioteca dedicada de parsing.
- **Dead letter queue no RabbitMQ:** para tratar reprocessamento de mensagens que falham, em vez de só marcar a importação como FALHOU.
- **Testes de integração:** cobrir o fluxo completo (upload → fila → processamento → banco), além dos testes de unidade.
- **Filtro de importação na tela de pedidos:** trocar o campo de ID numérico por um seletor com nome e data da importação, mais amigável para o usuário.
- **Caminho de upload configurável:** hoje é um caminho relativo; tornaria configurável via `application.yml`.
- **Idempotência no reprocessamento:** garantir que reprocessar o mesmo arquivo não duplique pedidos.
