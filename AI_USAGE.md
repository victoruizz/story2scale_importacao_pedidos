# Registro de Uso de IA

## Visão geral

Usei IA (Claude, da Anthropic) como ferramenta de apoio durante o desenvolvimento, para acelerar partes que eu já dominava e para aprender conceitos que eu ainda não conhecia. As decisões de arquitetura, a escrita das regras de negócio e a validação final do que entrou no projeto foram minhas. Não compartilhei o PDF do desafio com a IA. Descrevi o problema com minhas próprias palavras e fui pedindo ajuda por partes.

## Ferramentas de IA utilizadas

- **Claude (Anthropic)** pela interface web, usado para entender conceitos, organizar o fluxo da aplicação, revisar lógica e tirar dúvidas pontuais.
- **Claude Code (no VS Code)**, usado para acelerar a geração de código mais repetitivo (configurações e telas do frontend) e para resolver alguns erros de configuração.

---

# BACKEND

## Entendimento inicial do problema

Antes de codar, usei a IA para organizar o fluxo e validar a ordem das etapas que eu tinha pensado.

**Prompt que enviei:**
> "Vou construir um sistema de importação de pedidos via CSV: o usuário faz upload de um arquivo, o sistema valida cada linha por regras de negócio, salva os pedidos válidos e registra os erros das linhas inválidas, com acompanhamento de status. Quero usar Spring Boot, PostgreSQL e processamento assíncrono. Me ajuda a organizar o fluxo geral e a ordem das etapas, do mais simples ao mais complexo?"

A partir disso montei a ordem do desenvolvimento (infra → entidades → upload → fila → processamento → consultas) e fui seguindo etapa por etapa, testando cada parte antes de avançar.

## Onde eu já tinha domínio e usei IA só para acelerar

Entidades JPA, repositories, estrutura de controller/service e os endpoints REST eu escrevi, porque já tinha base de Java/Spring. Nessas partes a IA serviu para tirar dúvidas pontuais de sintaxe e revisar o que eu fiz.

## Onde eu não tinha conhecimento e precisei aprender

### Processamento assíncrono / RabbitMQ

Essa foi a parte que eu não sabia fazer. Nunca tinha mexido com mensageria e precisei entender o conceito antes de aplicar.

**Prompt que enviei:**
> "Preciso fazer um processamento assíncrono num sistema de importação de CSV em Spring Boot. O processamento não pode travar a requisição de upload. Quais as opções e qual seria mais simples para eu estudar e aplicar?"

A IA me apresentou duas opções principais: **RabbitMQ** e **Kafka**. Pesquisei sobre as duas e decidi por **RabbitMQ** pelos motivos:

- É mais simples de configurar e entender para um primeiro contato com mensageria.
- Kafka é orientado a streaming de altíssimo volume, o que seria over-engineering para o escopo do desafio (o edital pede uma solução simples).
- Boa integração com Spring via `spring-boot-starter-amqp`, e o painel de administração (porta 15672) me ajudou a visualizar as mensagens entrando e saindo da fila enquanto eu estudava.

Depois de escolher, fui pedindo explicações de conceito (producer, consumer, fila, ACK) e exemplos mínimos, mas a montagem do fluxo (declaração da fila, publicação do id no upload e o `@RabbitListener` consumindo) eu fui implementando e testando por etapas.

**Decisão minha:** mandar apenas o `id` da importação na fila, em vez do arquivo inteiro. O arquivo é salvo em disco no upload e o listener busca pelo id. Mantém a mensagem leve e evita sobrecarregar o broker.

### Upload de arquivo (MultipartFile)

Eu não sabia como receber um arquivo via upload no Spring.

**Prompt que enviei:**
> "Como recebo um arquivo enviado pelo frontend num endpoint Spring Boot? Quero salvar esse arquivo e depois processar."

A IA explicou o tipo `MultipartFile`, o uso de `@RequestParam` no controller e a gravação em disco com `Files.createDirectories` e `transferTo`. Implementei e testei pelo Postman (form-data) até ver a pasta `uploads/` sendo criada com o arquivo dentro.

## Regras de negócio (validação)

A validação das linhas foi uma parte que eu também tinha dificuldade, principalmente nas conversões de tipo. Fui construindo regra por regra.

**Decisão minha:** separar a validação numa classe própria (`ValidadorLinha`), em vez de deixar tudo dentro do listener. Fiz isso porque essa classe não depende de banco nem de fila, o que torna os testes simples de escrever: instancio a classe, passo uma linha e verifico os erros retornados.

**Decisão minha:** tratar a duplicidade de `numero_pedido` em camadas: verificação dentro do próprio arquivo (com um `Set` de números já vistos), verificação contra o banco (`existsByNumeroPedido`) e, como defesa final, constraint `unique` na coluna. A duplicidade ficou no listener (não no validador) porque depende de consulta ao banco.

## Migration (Flyway)

Um amigo já tinha me mostrado o Flyway antes e achei a dependência fácil de usar, então resolvi adotá-la para versionar o schema em vez de deixar o `ddl-auto` criando as tabelas. O script SQL de criação das tabelas (`V1__create_tables.sql`) eu escrevi, entendendo cada parte (tipos, chave primária, foreign key, constraint unique).

## Testes

Os testes unitários cobrem as regras de negócio da validação (classe `ValidadorLinha`). Usei a IA para acelerar a escrita dos casos de teste no padrão JUnit; revisei cada um e entendo o que cada teste verifica (linha válida, campos obrigatórios, quantidade/valor inválidos, data futura). Mantive os testes isolados, sem dependência de banco, justamente pela separação da classe de validação.

---

# FRONTEND

O frontend foi feito em Angular. Tenho familiaridade com Angular e com consumo de API via HttpClient, então usei o **Claude Code** para acelerar a codificação das telas, revisando o resultado de cada uma e conferindo a integração com os endpoints.

Forneci ao Claude Code um arquivo de instruções (CLAUDE.md) com o contrato da API, as 4 telas exigidas, o padrão (um componente por tela, um service centralizando as chamadas, roteamento) e a orientação de gerar de forma incremental, uma tela por vez, para eu revisar antes de seguir.

As telas geradas (upload, histórico, detalhe e pedidos com filtros) eu revisei e testei manualmente, confirmando o fluxo completo: upload do CSV → acompanhamento do status no histórico → detalhe com pedidos e erros → consulta de pedidos com filtros.

## Decisão de produto minha (filtro por importação)

Ao revisar a tela de pedidos, percebi um problema de usabilidade: o filtro "importação de origem" usava o **ID numérico** da importação, mas o usuário, tendo acesso só ao frontend, não teria como saber esse ID de cabeça. Captei isso testando.

Para resolver, pedi ao Claude Code para adicionar, na tela de detalhe da importação, um botão "ver pedidos desta importação" que leva à tela de pedidos já com o filtro preenchido pelo ID da importação atual, assim o usuário navega sem precisar saber o número.

Registrei também, nas melhorias futuras, que o ideal seria trocar o campo de ID por um seletor com nome e data da importação.

---

# Registro geral do uso de IA

## Decisões/sugestões da IA que aproveitei

- Escolha do RabbitMQ após comparação com Kafka.
- Estrutura inicial do `docker-compose.yml` e do `application.yml` (revisadas e simplificadas por mim).
- Padrão de separação da validação numa classe isolada.
- Uso de DTO de resposta para o detalhe da importação, evitando expor a entidade direto.
- Geração acelerada das telas do frontend.

## Decisões/sugestões da IA que descartei ou ajustei

- **Healthchecks no docker-compose:** a configuração inicial vinha com blocos de `healthcheck`. Achei desnecessário para o escopo (a aplicação roda fora do Docker no desenvolvimento), então removi para deixar o arquivo mais enxuto.
- **Variáveis interpoladas no docker-compose** (`${POSTGRES_USER:-postgres}`): troquei por valores fixos, mais simples para o escopo.
- **Catch genérico:** uma versão da validação usava `catch (RuntimeException e)`; ajustei para `catch (NumberFormatException e)` (o específico), depois de entender que catch largo demais esconde bugs.
- **Filtro por ID na tela de pedidos:** a sugestão funcionava, mas tinha o problema de usabilidade que descrevi acima; resolvi com o botão de navegação a partir do detalhe.

## Erros/limitações encontrados nas respostas da IA

No geral a IA não errava quando o assunto era sintaxe. As limitações que encontrei foram de outro tipo: às vezes ela sugeria soluções ou conceitos além da minha base de conhecimento, que eu preferi não usar para não colocar no projeto algo que não entendia; e em alguns momentos eu não fui claro o suficiente no que pedi, e ela acabava entregando algo diferente do que eu precisava, o que exigia eu reformular o pedido.

## Como conferi a solução antes da entrega

- Testei cada endpoint manualmente no Postman antes de seguir para o próximo.
- Acompanhei o painel do RabbitMQ (15672) para confirmar mensagens entrando ("Ready") e sendo consumidas (fila zerando).
- Conferi os resultados direto no banco (DBeaver), validando contadores, status e o cálculo de `valorTotal`.
- Usei arquivos CSV de teste com casos válidos e inválidos de propósito e confirmei que cada linha caía no resultado esperado.
- No frontend, testei o fluxo completo das 4 telas com o backend no ar.

## Pontos da solução em que tenho mais confiança

- Fluxo de processamento assíncrono (upload, fila, listener).
- Validação das regras de negócio e o cálculo do valor total.
- Persistência de pedidos e erros vinculados à importação de origem.

## Dúvidas, limitações e melhorias que ficariam registradas

- **Parsing de CSV:** o `split(",")` simples não trata campos com vírgula dentro de aspas. Funciona para o layout do desafio. A IA sugeriu, como melhoria para produção, usar uma biblioteca dedicada de parsing, algo que pretendo estudar.
- O caminho da pasta de uploads é relativo; eu o tornaria configurável via `application.yml`.
- O filtro de pedidos por importação usa o ID; eu trocaria por um seletor com nome e data.
- Testes de integração cobrindo o fluxo completo (fila + banco) seriam o próximo passo, além dos testes de unidade.