# Entidade e tabela de usuários

A classe `UserModel` representa um usuário da aplicação e está mapeada para o banco de dados por meio do JPA. Com esse mapeamento, o Hibernate consegue transformar objetos Java em registros de uma tabela.

## Estrutura atual

A entidade foi definida em `src/main/java/br/com/rneto/tarefas/user/UserModel.java`:

```java
@Data
@Entity(name = "tb_users")
public class UserModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String userName;
    private String name;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

## `@Entity`

A anotação `@Entity` informa ao JPA que a classe representa uma entidade persistente. O Hibernate lê essa classe e realiza o mapeamento entre seus atributos e as colunas do banco.

No código atual:

```java
@Entity(name = "tb_users")
```

O atributo `name` define o nome da entidade utilizado pelo JPA e pelo JPQL. Como nenhum `@Table` foi informado, esse nome também é usado como base para o nome padrão da tabela.

Quando a intenção for declarar explicitamente o nome físico da tabela, uma forma mais clara é:

```java
@Entity
@Table(name = "tb_users")
public class UserModel {
}
```

Nesse caso, `@Entity` identifica a entidade e `@Table` define diretamente a tabela do banco.

## Identificador do usuário

```java
@Id
@GeneratedValue(generator = "UUID")
private UUID id;
```

- `@Id` indica que `id` é a chave primária.
- `@GeneratedValue` informa que o valor deve ser gerado automaticamente.
- `UUID` fornece identificadores com uma probabilidade extremamente baixa de repetição, sem depender de números sequenciais do banco.

Assim, o cliente não precisa enviar o `id` ao criar um usuário.

## Dados do usuário

```java
private String userName;
private String name;
private String password;
```

Como esses campos não possuem `@Column`, o Hibernate utiliza suas convenções de nomenclatura para criar as colunas correspondentes.

Restrições podem ser adicionadas explicitamente. Por exemplo:

```java
@Column(nullable = false, unique = true)
private String userName;
```

Essa configuração torna o nome de usuário obrigatório e impede valores duplicados no banco.

## Data de criação

```java
@CreationTimestamp
private LocalDateTime createdAt;
```

`@CreationTimestamp` é uma anotação do Hibernate que preenche `createdAt` automaticamente quando a entidade é persistida pela primeira vez. O cliente não precisa informar esse valor na requisição.

## Lombok e `@Data`

```java
@Data
```

A anotação `@Data`, do Lombok, gera automaticamente getters, setters, `equals()`, `hashCode()` e `toString()`.

Como a entidade possui uma senha, é importante evitar que ela apareça em logs por meio do `toString()`. Uma opção é excluir o campo:

```java
@ToString.Exclude
private String password;
```

## Criação da tabela

O projeto utiliza H2 em memória e Spring Data JPA. Durante o desenvolvimento, o Hibernate pode gerar a estrutura da tabela ao iniciar a aplicação com base no mapeamento da entidade.

O comportamento pode ser configurado explicitamente em `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Alguns valores possíveis são:

- `create`: recria as tabelas ao iniciar;
- `create-drop`: cria ao iniciar e remove ao encerrar;
- `update`: tenta atualizar a estrutura existente;
- `validate`: apenas verifica se a estrutura corresponde às entidades;
- `none`: não realiza alterações na estrutura.

Para projetos reais e ambientes de produção, alterações no banco normalmente são controladas por ferramentas de migração, como Flyway ou Liquibase, em vez de depender de `ddl-auto=update`.

## Endpoint atual

O controller possui o endpoint:

```http
POST /users/create
```

Exemplo de corpo JSON:

```json
{
  "userName": "roberto",
  "name": "Roberto Neto",
  "password": "senha"
}
```

Atualmente, o método apenas recebe o objeto e imprime o nome do usuário:

```java
public void createUser(@RequestBody UserModel userModel) {
    System.out.println(userModel.getUserName());
}
```

Isso significa que a tabela está mapeada, mas o endpoint ainda não salva o usuário. Para persistir os dados, o próximo passo será criar um repositório JPA e chamar seu método `save`.

## Cuidados com a senha

A senha nunca deve ser salva em texto puro. Antes de persistir um usuário, ela deve ser transformada com um algoritmo próprio para armazenamento de senhas, como BCrypt ou Argon2.

Também é recomendável não devolver o campo `password` nas respostas da API e não imprimir o objeto completo em logs.

## Resumo

- `UserModel` representa a entidade de usuário.
- `@Entity` permite que o JPA mapeie a classe para o banco.
- `@Id` identifica a chave primária.
- O UUID é gerado automaticamente.
- `@CreationTimestamp` registra a data de criação.
- `@Data` gera métodos auxiliares por meio do Lombok.
- O controller ainda não persiste os dados; ele apenas recebe a requisição.
- A senha deverá ser protegida antes de ser salva.
