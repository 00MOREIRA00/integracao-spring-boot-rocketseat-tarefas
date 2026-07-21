# Model

Em uma aplicação Spring Boot, o model é a classe responsável por representar os dados utilizados pela aplicação.

Ele funciona como um molde para os objetos. Por exemplo, para representar um usuário, podemos definir quais informações todo usuário terá, como nome de usuário, nome e senha.

## Papel do model

O model normalmente é responsável por:

- representar uma entidade ou um recurso da aplicação;
- definir quais dados esse recurso possui;
- transportar dados entre as camadas da aplicação;
- receber dados enviados em uma requisição JSON;
- fornecer dados que poderão ser transformados em uma resposta JSON.

Um model pode representar usuários, tarefas, produtos, pedidos ou qualquer outro conceito importante para o sistema.

## Model no padrão MVC

Model faz parte do padrão MVC, que significa Model-View-Controller.

- Model: representa os dados e as regras da aplicação.
- View: representa a parte visual ou a resposta apresentada ao usuário.
- Controller: recebe a requisição, coordena o fluxo e decide qual resposta será retornada.

Em uma API REST, o controller pode receber um JSON e pedir ao Spring para convertê-lo em um objeto Java criado a partir de um model.

## Exemplo neste projeto

Neste projeto, existe o model `UserModel`:

```java
public class UserModel {

    private String userName;
    private String name;
    private String password;
}
```

Essa classe representa um usuário com três atributos:

- `userName`: nome utilizado para identificar o usuário no sistema;
- `name`: nome da pessoa;
- `password`: senha do usuário.

Cada objeto criado a partir dessa classe terá sua própria cópia desses dados.

```java
UserModel user = new UserModel();
```

## Atributos privados

Os atributos foram declarados com `private`:

```java
private String userName;
```

Isso aplica o conceito de encapsulamento. O código externo não pode modificar o atributo diretamente; o acesso deve acontecer por meio de métodos da própria classe.

Para permitir a leitura e a alteração dos valores, normalmente são criados getters e setters:

```java
public String getUserName() {
    return userName;
}

public void setUserName(String userName) {
    this.userName = userName;
}
```

- Getter: devolve o valor de um atributo.
- Setter: recebe e altera o valor de um atributo.

O mesmo pode ser feito para `name` e `password`. Esses métodos também permitem que a biblioteca de conversão JSON preencha e leia as propriedades do objeto com segurança.

## Model recebendo JSON

No `UserController`, o model é utilizado junto com `@RequestBody`:

```java
@PostMapping("/create")
public void createUser(@RequestBody UserModel userModel) {
}
```

Quando o cliente envia este JSON:

```json
{
  "userName": "roberto",
  "name": "Roberto Neto",
  "password": "123456"
}
```

O Spring converte o JSON em um objeto `UserModel`. Cada propriedade do JSON é associada ao atributo de mesmo nome na classe Java.

O fluxo fica assim:

```text
JSON enviado pelo cliente → Spring → objeto UserModel → controller
```

## Model não é banco de dados

Uma classe model representa os dados, mas isso não significa que eles já sejam salvos em um banco de dados.

Para persistir um usuário, ainda será necessário criar a lógica de armazenamento, normalmente utilizando uma entidade, um repository e um banco de dados. No estado atual do projeto, o `UserModel` apenas recebe e transporta os dados da requisição.

## Cuidados com a senha

Senhas não devem ser exibidas em logs nem armazenadas como texto puro. Antes de salvar uma senha, a aplicação deve transformá-la utilizando um algoritmo seguro de hash, como BCrypt.

## Resumo

O model define a estrutura dos dados utilizados pela aplicação. Neste projeto, `UserModel` representa os dados de um usuário e permite que o Spring converta o JSON recebido pelo controller em um objeto Java. Como seus atributos são privados, a classe deve disponibilizar getters e setters, ou outra estratégia de construção, para que os valores possam ser acessados e preenchidos corretamente.
