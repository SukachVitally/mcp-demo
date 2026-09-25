# Task MCP demo

A small [Model Context Protocol](https://modelcontextprotocol.io) server built with Spring Boot and Spring AI. It lets an MCP client, such as Claude, manage a task list stored in PostgreSQL.

Each task has a name and one of these statuses: `NEW`, `IN_PROGRESS`, `DONE` or `CANCELLED`.

## MCP tools

| Tool                 | Description                                              |
|----------------------|----------------------------------------------------------|
| `add_task`           | Create a task with status `NEW` (name: 1–255 characters) |
| `get_task`           | Get a task by id                                         |
| `update_task_status` | Change a task's status and return the updated task       |
| `get_task_list`      | List all tasks                                           |

An unknown id or an invalid name returns an MCP error result with a readable message.

## Tech stack

- Java 25, Spring Boot 4, Spring AI MCP server (stateless, streamable HTTP)
- PostgreSQL 18 with Liquibase migrations
- Spring Security with API-key authentication (`mcp-server-security`)
- Tests: JUnit 5 with Testcontainers

## Requirements

- JDK 25
- Docker, for the local database and the tests

## Running the application

1. Create your local `.env` file from the template and set your own secret in it:

   ```bash
   cp .env.tpl .env
   ```

   ```properties
   MCP_API_KEY_SECRET=change-me
   ```

   The application loads `.env` from the project root on startup (`spring.config.import` in `application.yaml`) and won't start without it. `.env` is gitignored, so your secret stays out of the repository.

2. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

3. Start the server:

   ```bash
   ./gradlew bootRun
   ```

   The MCP endpoint is at `http://localhost:8080/mcp`. Liquibase creates the schema on startup.

### Configuration

Set these in `.env` or as environment variables. Environment variables take precedence over `.env`.

| Variable             | Default                                     | Purpose                     |
|----------------------|---------------------------------------------|-----------------------------|
| `MCP_API_KEY_SECRET` | none (required)                             | Secret for the demo API key |
| `DB_URL`             | `jdbc:postgresql://localhost:5432/postgres` | JDBC URL                    |
| `DB_USERNAME`        | `postgres`                                  | Database user               |
| `DB_PASSWORD`        | `mysecretpassword`                          | Database password           |

The database defaults match `docker-compose.yaml`.

## Connecting an MCP client

Every request must send the API key in the `X-API-key` header, in the form `<key id>.<secret>`. The key id is `demo`, so with the secret above the value is `demo.change-me`.

**Claude Code:**

```bash
claude mcp add --transport http tasks http://localhost:8080/mcp \
  --header "X-API-key: demo.change-me"
```

**MCP Inspector:**

```bash
npx @modelcontextprotocol/inspector
```

Choose the *Streamable HTTP* transport, enter `http://localhost:8080/mcp`, and add the `X-API-key` header.

## Running tests

```bash
./gradlew test
```

The tests start their own PostgreSQL container with Testcontainers, so Docker must be running. You don't need the compose database. `.env` must exist, because the tests load the same `application.yaml`. The tests use their own API key secret, so the value in `.env` doesn't matter.

## Resetting the database

The schema is still changing, so migrations are sometimes edited in place. If the app fails on startup with a Liquibase checksum error, recreate the database:

```bash
docker compose down -v && docker compose up -d
```
