# API de Tarefas

API REST para cadastro de usuários e gerenciamento de tarefas, desenvolvida durante um evento da Rocketseat.

## Tecnologias

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- H2 Database
- Lombok
- Maven
- Docker

## Funcionalidades

- Cadastro de usuários com senha protegida por BCrypt
- Autenticação HTTP Basic nas rotas de tarefas
- Criação, listagem e atualização de tarefas
- Persistência local em banco H2
- Validação de datas e do tamanho do título

## Como executar

### Pré-requisitos

- Java 17
- Maven 3.9 ou o Maven Wrapper incluído no projeto

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Em Linux ou macOS:

```bash
./mvnw spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8080`.

### Com Docker

```bash
docker build -t api-tarefas .
docker run --rm -p 8080:8080 api-tarefas
```

## Endpoints

| Método | Rota | Autenticação | Descrição |
| --- | --- | --- | --- |
| `POST` | `/users/create` | Não | Cadastra um usuário |
| `POST` | `/tasks/` | HTTP Basic | Cria uma tarefa |
| `GET` | `/tasks/` | HTTP Basic | Lista as tarefas do usuário |
| `PUT` | `/tasks/{id}` | HTTP Basic | Atualiza uma tarefa do usuário |

Exemplo de usuário:

```json
{
  "username": "usuario",
  "name": "Nome do usuário",
  "password": "senha"
}
```

Exemplo de tarefa:

```json
{
  "title": "Estudar Spring Boot",
  "description": "Revisar autenticação e filtros",
  "priority": "ALTA",
  "startAt": "2026-08-18T09:00:00",
  "endAt": "2026-08-18T11:00:00"
}
```

## Banco de dados

O banco H2 é armazenado em `./data/tarefas`. O console fica disponível em `http://localhost:8080/h2-console` com:

- JDBC URL: `jdbc:h2:file:./data/tarefas`
- Usuário: `admin`
- Senha: `admin`

## Testes

```bash
mvn test
```
