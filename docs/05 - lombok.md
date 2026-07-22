# Lombok

Essa lib é uma ferramenta que ajuda a reduzir a quantidade de código boilerplate em projetos Java. Com o Lombok, é possível gerar automaticamente métodos como getters, setters, construtores, `toString`, `equals`, `hashCode`, entre outros, através de anotações.

## Dependência

O Lombok foi adicionado ao `pom.xml` como uma dependência opcional:

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

O Spring Boot gerencia uma versão compatível da biblioteca, por isso não é necessário informar a versão manualmente. A opção `optional` evita que o Lombok seja incluído como uma dependência obrigatória para quem utilizar o projeto.

## Uso no `UserModel`

As anotações `@Getter` e `@Setter` foram adicionadas à classe:

```java
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModel {

    private String userName;
    private String name;
    private String password;
}
```

Durante a compilação, o Lombok gera os getters e setters de todos os atributos. Isso permite remover os métodos escritos manualmente sem alterar seu uso no restante da aplicação.

Por exemplo, estas chamadas continuam funcionando:

```java
userModel.setUserName("roberto");
String userName = userModel.getUserName();
```

## Principais anotações

- `@Getter`: gera os métodos de leitura dos atributos;
- `@Setter`: gera os métodos de alteração dos atributos;
- `@NoArgsConstructor`: gera um construtor sem argumentos;
- `@AllArgsConstructor`: gera um construtor com todos os atributos;
- `@ToString`: gera o método `toString`;
- `@EqualsAndHashCode`: gera os métodos `equals` e `hashCode`;
- `@Data`: reúne getters, setters, `toString`, `equals`, `hashCode` e um construtor para campos obrigatórios.

Neste projeto foram usadas somente `@Getter` e `@Setter`, pois elas substituem exatamente os métodos que já existiam sem gerar comportamentos adicionais.

## Configuração da IDE

O Lombok atua durante a compilação. Para que a IDE também reconheça os métodos gerados, pode ser necessário instalar ou habilitar o plugin Lombok e ativar o processamento de anotações nas configurações da IDE.

## Resumo

O Lombok reduz código repetitivo sem mudar a forma como os getters e setters são chamados. O `UserModel` ficou menor e continua oferecendo os mesmos métodos de acesso aos atributos.
