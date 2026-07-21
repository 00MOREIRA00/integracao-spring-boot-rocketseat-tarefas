# Controller

Em uma aplicação Spring Boot, o controller é a camada responsável por receber as requisições HTTP e devolver uma resposta para quem chamou a API.

Ele funciona como a porta de entrada da aplicação. Quando alguém acessa uma URL, como `http://localhost:8080/tarefas/health`, o Spring procura um controller que saiba responder para aquele caminho.

## Papel do controller

O controller normalmente é responsável por:

- definir quais URLs a aplicação terá;
- receber dados enviados pela requisição;
- chamar regras de negócio quando necessário;
- devolver uma resposta para o cliente.

Em uma API REST, o cliente pode ser um navegador, um sistema externo, um aplicativo mobile ou uma ferramenta como Postman e Insomnia.

## Controller no padrão MVC

Controller faz parte do padrão MVC, que significa Model-View-Controller.

- Model: representa os dados e as regras da aplicação.
- View: representa a parte visual ou a resposta apresentada ao usuário.
- Controller: recebe a requisição, coordena o fluxo e decide qual resposta será retornada.

Em APIs REST com Spring Boot, geralmente não trabalhamos com páginas HTML como resposta principal. Em vez disso, o controller costuma retornar textos, objetos JSON, status HTTP ou outros dados.

## Exemplo neste projeto

Neste projeto, existe o controller `TarefasController`:

```java
@RestController
@RequestMapping("/tarefas")
public class TarefasController {

    @GetMapping("/health")
    public String healthCheack() {
        return "OK";
    }
}
```

Esse código cria uma rota HTTP:

```text
GET /tarefas/health
```

Quando a aplicação estiver rodando, essa rota pode ser acessada em:

```text
http://localhost:8080/tarefas/health
```

Como resposta, ela retorna:

```text
OK
```

## Principais anotações

### `@RestController`

Indica que a classe é um controller REST.

Com essa anotação, o Spring entende que os métodos dessa classe podem responder requisições HTTP diretamente.

### `@RequestMapping`

Define um caminho base para todas as rotas do controller.

No exemplo:

```java
@RequestMapping("/tarefas")
```

Todas as rotas dentro desse controller começam com `/tarefas`.

### `@GetMapping`

Define uma rota que responde a requisições HTTP do tipo `GET`.

No exemplo:

```java
@GetMapping("/health")
```

Essa rota responde quando alguém acessa `/tarefas/health`.

## Resumo

O controller organiza os endpoints da aplicação. Ele recebe chamadas HTTP, conecta essas chamadas ao código Java e devolve uma resposta. Em uma aplicação Spring Boot, é comum criar controllers para representar recursos da API, como tarefas, usuários, produtos ou pedidos.
