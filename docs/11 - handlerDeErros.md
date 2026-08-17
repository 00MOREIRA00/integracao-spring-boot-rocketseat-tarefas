# Tratamento global de erros com `@ControllerAdvice`

Nesta etapa, adicionamos um handler para tratar erros que acontecem durante a leitura do corpo de uma requisição HTTP. A implementação permite centralizar o tratamento do erro e devolver uma resposta com status `400 Bad Request`, sem colocar um bloco `try/catch` em cada endpoint.

O fluxo implementado é:

```text
Cliente envia um JSON
        ↓
Jackson converte o JSON para TaskModel
        ↓
O setter de title valida o valor
        ↓
Uma exceção é lançada quando o título tem mais de 50 caracteres
        ↓
Spring gera HttpMessageNotReadableException
        ↓
ExceptionHandlerController captura a exceção
        ↓
Cliente recebe HTTP 400
```

## A validação no `TaskModel`

O método `setTitle()` foi criado para limitar o título de uma tarefa a 50 caracteres:

```java
public void setTitle(String title) throws Exception {
    if (title.length() > 50) {
        throw new Exception(
            "O campo title deve comter no maximo 50 caracteres"
        );
    }

    this.title = title;
}
```

Quando o Spring recebe um JSON no `@RequestBody`, a biblioteca Jackson cria o objeto `TaskModel` e chama seus setters para preencher os campos. Portanto, ao preencher `title`, esse método é executado automaticamente.

Se o texto possuir mais de 50 caracteres, o setter lança uma exceção. Como a falha aconteceu enquanto o Jackson convertia o JSON em um objeto Java, o Spring a apresenta como uma `HttpMessageNotReadableException`.

Isso ocorre antes da execução do método do controller. Nesse caso, o endpoint não chega a salvar a tarefa no banco.

## O handler de erros

Foi criada a classe `ExceptionHandlerController` no pacote `erros`:

```java
package br.com.rneto.tarefas.erros;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHTTPMessageNotReadableException(
        HttpMessageNotReadableException e
    ) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(e.getMessage());
    }
}
```

## `@ControllerAdvice`

A anotação `@ControllerAdvice` informa ao Spring que essa classe possui comportamentos compartilhados pelos controllers da aplicação.

Neste projeto, ela é usada para tratamento global de exceções. Assim, o handler pode tratar uma `HttpMessageNotReadableException` gerada ao processar o corpo de qualquer requisição, e não somente uma rota de `TaskController`.

Apesar do nome `ExceptionHandlerController`, a classe não é um controller com endpoints próprios. Ela observa as exceções geradas durante o processamento feito pelos controllers.

## `@ExceptionHandler`

A anotação abaixo define qual tipo de exceção o método trata:

```java
@ExceptionHandler(HttpMessageNotReadableException.class)
```

Quando uma exceção desse tipo chega à camada do Spring MVC, o framework procura um método compatível no `@ControllerAdvice` e chama `handleHTTPMessageNotReadableException()`.

É possível adicionar outros métodos à mesma classe para tratar tipos diferentes de erro:

```java
@ExceptionHandler(OutraException.class)
public ResponseEntity<String> handleOutraException(OutraException e) {
    return ResponseEntity.badRequest().body(e.getMessage());
}
```

Cada handler deve declarar o tipo de exceção que sabe tratar.

## `HttpMessageNotReadableException`

`HttpMessageNotReadableException` indica que o Spring não conseguiu ler ou converter corretamente o corpo da requisição.

Além da validação feita no setter, ela pode acontecer, por exemplo, quando:

- o JSON está com a sintaxe inválida;
- um campo contém um valor incompatível com seu tipo Java;
- uma data está em um formato que não pode ser convertido;
- um setter lança uma exceção durante a criação do objeto.

Por isso, o handler criado é mais abrangente do que apenas a regra dos 50 caracteres: ele trata outros problemas de leitura do `@RequestBody` com a mesma resposta HTTP.

## Construção da resposta

O retorno do método é:

```java
return ResponseEntity
    .status(HttpStatus.BAD_REQUEST)
    .body(e.getMessage());
```

Cada parte possui uma responsabilidade:

- `ResponseEntity<String>` representa toda a resposta HTTP e informa que seu corpo é uma `String`;
- `HttpStatus.BAD_REQUEST` define o status HTTP `400`;
- `e.getMessage()` coloca a mensagem da exceção no corpo da resposta.

O status `400 Bad Request` informa que a requisição enviada pelo cliente não pôde ser processada por causa de algum dado inválido ou ilegível.

## Exemplo de requisição inválida

Uma requisição pode ser enviada para a criação de uma tarefa com um título maior que o limite:

```http
POST http://localhost:8080/tasks/
Content-Type: application/json
Authorization: Basic <credenciais>
```

```json
{
  "title": "Este título foi escrito com mais de cinquenta caracteres permitidos",
  "description": "Descrição da tarefa",
  "priority": "ALTA",
  "startAt": "2026-08-18T10:00:00",
  "endAt": "2026-08-18T12:00:00"
}
```

O resultado esperado é uma resposta com:

```text
HTTP/1.1 400 Bad Request
```

O corpo contém a mensagem produzida por `HttpMessageNotReadableException`. Como o código utiliza `e.getMessage()`, a resposta normalmente inclui detalhes técnicos do Jackson junto com a mensagem lançada pelo setter.

## Por que o erro não é tratado no `TaskController`

O Spring precisa transformar o JSON em `TaskModel` antes de chamar este método:

```java
public ResponseEntity create(
    @RequestBody TaskModel taskModel,
    HttpServletRequest request
)
```

Se a criação de `taskModel` falhar, o método `create()` ainda não começou a executar. Por isso, colocar o tratamento dentro do endpoint não é a melhor forma de capturar esse erro. O `@ControllerAdvice` atua no ponto correto do fluxo e mantém o tratamento centralizado.

## Cuidados com a implementação atual

A solução atual demonstra como o tratamento global funciona, mas possui alguns pontos que podem ser melhorados em uma aplicação real:

- `e.getMessage()` pode expor nomes de classes e outros detalhes internos da aplicação;
- a mesma resposta é usada tanto para um título longo quanto para um JSON malformado;
- lançar `Exception` é muito genérico e dificulta identificar a regra que falhou;
- `title.length()` gera `NullPointerException` se o método receber `null`;
- a mensagem possui os erros de digitação `comter` e `maximo`;
- uma resposta estruturada em JSON é mais fácil de consumir do que uma mensagem de texto livre.

Uma evolução possível é criar uma exceção específica, como `FieldValidationException`, e devolver um objeto padronizado:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "O campo title deve conter no máximo 50 caracteres"
}
```

Outra opção comum no Spring é usar Bean Validation com anotações como `@Size(max = 50)` e aplicar `@Valid` no parâmetro do controller. Essa alternativa separa as regras de validação da conversão do JSON e permite tratar os erros de validação de maneira mais específica.

## Resumo

- O Jackson chama `setTitle()` ao converter o JSON em `TaskModel`.
- O setter lança uma exceção se o título ultrapassar 50 caracteres.
- O Spring encapsula a falha de conversão em `HttpMessageNotReadableException`.
- `@ControllerAdvice` disponibiliza um tratamento global para os controllers.
- `@ExceptionHandler` associa o método ao tipo de exceção tratado.
- `ResponseEntity` devolve status `400 Bad Request` e a mensagem do erro.
- O endpoint não é executado quando a conversão do `@RequestBody` falha.
- A implementação pode evoluir para exceções específicas, respostas JSON padronizadas e Bean Validation.
