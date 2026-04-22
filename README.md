# Desafio Itaú — Backend

[![CI](https://github.com/DenisLopes/desafio-itau-backend/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/DenisLopes/desafio-itau-backend/actions/workflows/ci.yml)
[![Docker](https://github.com/DenisLopes/desafio-itau-backend/actions/workflows/docker.yml/badge.svg?branch=main)](https://github.com/DenisLopes/desafio-itau-backend/actions/workflows/docker.yml)

API REST em Java 17 + Spring Boot 3 que recebe transações e expõe estatísticas
agregadas (count, sum, avg, min, max) sobre uma janela de tempo configurável
(default 60 segundos, conforme especificação do desafio).

## Stack

- Java 17 · Spring Boot 3.3
- Spring Web · Bean Validation · Spring Actuator
- springdoc-openapi (Swagger UI)
- JUnit 5 · Mockito · AssertJ · MockMvc

## Arquitetura

Separação em camadas com dependências apontando sempre para o domínio (DIP):

```
api/              Camada HTTP — controllers, DTOs, handler global de erros
  ├── controller/
  ├── dto/
  ├── exception/  ApiExceptionHandler (RFC 7807 ProblemDetail)
  └── config/     OpenAPI

domain/           Regras de negócio — independente de Spring e de I/O
  ├── model/      Transacao (record imutável, BigDecimal, OffsetDateTime)
  ├── repository/ TransacaoRepository (interface — porta de saída)
  ├── service/    TransacaoService, EstatisticaService (interfaces)
  │   └── impl/
  └── exception/  TransacaoInvalidaException

infrastructure/   Adaptadores técnicos
  ├── config/     Clock bean + EstatisticaProperties (@ConfigurationProperties)
  └── repository/ InMemoryTransacaoRepository (thread-safe)
```

### Princípios SOLID em prática

| Princípio | Onde aparece |
|-----------|--------------|
| **SRP** | Controller só lida com HTTP; service aplica regra; repo persiste; handler global centraliza tradução de exceções. |
| **OCP** | Janela de estatística via `@ConfigurationProperties`; substituir a implementação do repositório não exige alteração em código de domínio. |
| **LSP** | `Transacao` é um record imutável com invariantes garantidas no construtor. |
| **ISP** | `TransacaoService` (escrita) e `EstatisticaService` (leitura/agregação) são interfaces separadas. |
| **DIP** | Services dependem da interface `TransacaoRepository`; implementação in-memory injetada pelo Spring. |

### Decisões de design relevantes

- **`BigDecimal`** em vez de `double` para valores monetários — `double` sofre erro de arredondamento (`0.1 + 0.2 != 0.3`).
- **`Clock` injetável** — testes de regras temporais (`dataHora` no futuro, janela de 60s) ficam determinísticos com `Clock.fixed(...)`.
- **`CopyOnWriteArrayList`** no repositório — leituras concorrentes durante o cálculo de estatísticas não travam escritas.
- **`ProblemDetail` (RFC 7807)** — respostas de erro padronizadas e legíveis por humanos e máquinas.
- **422 para valor negativo / data futura** e **400 para JSON malformado** — seguindo a semântica da spec do desafio.
- **Campo `dataHora`** em vez de `dateTime` — nomenclatura oficial da especificação do desafio Itaú.

## Como executar

Pré-requisitos: JDK 17.

```bash
./mvnw spring-boot:run
# ou
./mvnw package && java -jar target/desafio-itau-backend-0.0.1-SNAPSHOT.jar
```

A aplicação sobe em `http://localhost:8080`.

- Swagger UI: <http://localhost:8080/swagger-ui.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- Health: <http://localhost:8080/actuator/health>

## Endpoints

### `POST /transacao`

Registra uma transação.

```bash
curl -i -X POST http://localhost:8080/transacao \
  -H 'Content-Type: application/json' \
  -d '{"valor": 123.45, "dataHora": "2025-01-01T12:34:56.789-03:00"}'
```

| Resposta | Cenário |
|----------|---------|
| `201 Created` | Transação válida |
| `422 Unprocessable Entity` | Valor negativo, dataHora no futuro, campos obrigatórios ausentes |
| `400 Bad Request` | JSON malformado |

### `DELETE /transacao`

Remove todas as transações registradas.

```bash
curl -i -X DELETE http://localhost:8080/transacao
# 200 OK
```

### `GET /estatistica`

Retorna estatísticas das transações ocorridas na janela configurada (default 60s).

```bash
curl -s http://localhost:8080/estatistica | jq
```

```json
{
  "count": 3,
  "sum": 60.00,
  "avg": 20.00,
  "min": 10.00,
  "max": 30.00
}
```

## Configuração

`src/main/resources/application.properties`:

```properties
# Janela temporal do cálculo de estatísticas. Aceita formatos Duration:
# PT60S, PT2M, 60s, 2m, etc.
estatistica.janela=60s
```

## Testes

```bash
./mvnw test
```

A suíte cobre:

- Regras de negócio do service (valor negativo, data futura, limites).
- Cálculo de estatísticas (vazio, único, múltiplos, arredondamento).
- Repositório in-memory (inclusividade da janela, `removerTodas`).
- Controllers via `MockMvc` (201, 422, 400, 200 no delete).
- Carga de contexto Spring (`@SpringBootTest`).

## Docker

Imagem multi-stage (JDK p/ build, JRE p/ runtime; usuário não-root; healthcheck no `/actuator/health`):

```bash
docker build -t desafio-itau-backend:local .
docker run --rm -p 8080:8080 desafio-itau-backend:local
```

Imagens são publicadas automaticamente em `ghcr.io/denislopes/desafio-itau-backend` a cada push na `main` e em tags `v*.*.*`:

```bash
docker run --rm -p 8080:8080 ghcr.io/denislopes/desafio-itau-backend:latest
```

## CI/CD

Dois workflows em `.github/workflows/`:

| Workflow | Gatilho | O que faz |
|----------|---------|-----------|
| `ci.yml` | push e PR em `main` | `./mvnw verify` em JDK 17 com cache Maven; publica `surefire-reports` e o JAR como artifacts. |
| `docker.yml` | push em `main` e tags `v*.*.*` | Builda a imagem multi-arch (`linux/amd64`, `linux/arm64`) e publica no GHCR com tags `latest`, `sha-<shortsha>` e `{version}`/`{major}.{minor}` em releases. Usa `GITHUB_TOKEN` — sem secrets adicionais. |
