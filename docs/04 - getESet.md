# Getters e setters

Em Java, getters e setters são métodos utilizados para acessar e modificar atributos privados de uma classe.

Eles fazem parte do conceito de encapsulamento. Em vez de permitir que outras classes tenham acesso direto aos atributos de um objeto, a própria classe controla como esses valores serão lidos ou alterados.

## Atributos privados

No `UserModel`, os atributos foram declarados com `private`:

```java
public class UserModel {

    private String userName;
    private String name;
    private String password;
}
```

Por serem privados, eles não podem ser acessados diretamente fora da classe. O código abaixo, por exemplo, não é permitido:

```java
UserModel user = new UserModel();
user.userName = "roberto";
```

Para trabalhar com esses valores, podemos criar métodos públicos chamados getters e setters.

## Getter

O getter é o método responsável por devolver o valor de um atributo.

No `UserModel`, o getter de `userName` é:

```java
public String getUserName() {
    return userName;
}
```

Esse método possui:

- `public`: permite que ele seja chamado por outras classes;
- `String`: informa o tipo do valor que será devolvido;
- `getUserName`: segue a convenção `get` mais o nome do atributo;
- `return userName`: devolve o valor armazenado no atributo.

Para ler o nome de usuário, outra classe pode fazer:

```java
String userName = user.getUserName();
```

Neste projeto, o `UserController` utiliza esse getter para ler o valor recebido:

```java
System.out.println(userModel.getUserName());
```

## Setter

O setter é o método responsável por receber um novo valor e armazená-lo no atributo.

No `UserModel`, o setter de `userName` é:

```java
public void setUserName(String userName) {
    this.userName = userName;
}
```

Esse método possui:

- `public`: permite que ele seja chamado por outras classes;
- `void`: indica que o método não devolve um valor;
- `setUserName`: segue a convenção `set` mais o nome do atributo;
- `String userName`: recebe o novo valor como parâmetro;
- `this.userName = userName`: guarda o valor recebido no atributo do objeto.

Para alterar o nome de usuário, podemos fazer:

```java
UserModel user = new UserModel();
user.setUserName("roberto");
```

Depois disso, o getter pode ser usado para consultar o valor:

```java
System.out.println(user.getUserName());
```

## Uso do `this`

Dentro do setter, o atributo e o parâmetro possuem o mesmo nome:

```java
public void setUserName(String userName) {
    this.userName = userName;
}
```

O `this` representa o objeto atual. Dessa forma:

- `this.userName` é o atributo pertencente ao objeto;
- `userName` é o parâmetro recebido pelo método.

Portanto, a instrução atribui ao atributo do objeto o valor enviado para o setter.

## Getters e setters para os demais atributos

O mesmo padrão pode ser aplicado aos atributos `name` e `password`:

```java
public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}

public String getPassword() {
    return password;
}

public void setPassword(String password) {
    this.password = password;
}
```

Não é obrigatório que todo atributo tenha os dois métodos. Um atributo pode ter somente getter quando deve ser apenas consultado, ou somente setter quando sua leitura não deve ser exposta. A decisão depende das regras da aplicação.

## Controle e validação

Além de proteger o acesso direto aos atributos, os setters permitem validar um valor antes de armazená-lo. Por exemplo:

```java
public void setUserName(String userName) {
    if (userName == null || userName.isBlank()) {
        throw new IllegalArgumentException("O nome de usuário é obrigatório");
    }

    this.userName = userName;
}
```

Assim, a própria classe impede que um nome de usuário vazio seja atribuído ao objeto.

## Relação com JSON no Spring Boot

Quando o Spring recebe um JSON por meio de `@RequestBody`, a biblioteca responsável pela conversão pode utilizar os setters para preencher o objeto. Da mesma forma, os getters podem ser usados para ler suas propriedades ao gerar uma resposta JSON.

Exemplo de JSON:

```json
{
  "userName": "roberto",
  "name": "Roberto Neto",
  "password": "123456"
}
```

Para que todas essas propriedades possam ser tratadas dessa maneira, o model deve disponibilizar os métodos de acesso necessários para cada atributo.

## Resumo

Getters permitem consultar os valores dos atributos, enquanto setters permitem alterá-los. Esses métodos preservam o encapsulamento, mantêm os atributos privados e oferecem um ponto de controle para aplicar validações e regras. No Spring Boot, eles também ajudam na conversão entre objetos Java e dados JSON.
