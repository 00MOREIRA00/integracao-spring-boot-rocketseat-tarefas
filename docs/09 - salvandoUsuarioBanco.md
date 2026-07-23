# Salvando o usuário no banco de dados

Nesta etapa, conectamos o endpoint de criação de usuários à camada de persistência. Antes, o controller apenas recebia os dados enviados na requisição e imprimia o nome do usuário no console. Agora, ele utiliza um repositório do Spring Data JPA para salvar o usuário no banco H2.

O fluxo passou a ser:

```text
Requisição HTTP
    ↓
UserController
    ↓
UserRepository
    ↓
Spring Data JPA / Hibernate
    ↓
Banco H2
```

## O repositório de usuários

Foi criada a interface `UserRepository`:

```java
public interface UserRepository extends JpaRepository<UserModel, UUID> {
}
```

Ela estende `JpaRepository`, fornecido pelo Spring Data JPA. Os dois tipos informados entre `< >` são:

- `UserModel`: a entidade que será armazenada;
- `UUID`: o tipo da chave primária da entidade.

Não foi necessário implementar manualmente essa interface. Durante a inicialização da aplicação, o Spring identifica que ela estende `JpaRepository` e cria automaticamente um objeto com a implementação das operações de banco.

Entre os métodos disponibilizados estão:

- `save()`: cria ou atualiza um registro;
- `findById()`: procura um registro pelo identificador;
- `findAll()`: lista todos os registros;
- `deleteById()`: remove um registro pelo identificador.

Neste momento, utilizamos o método `save()`.

## A instância utilizada no controller

No `UserController`, adicionamos:

```java
@Autowired
private UserRepository userRepository;
```

Embora seja comum dizer que foi criada uma “instância do banco” no controller, tecnicamente o campo `userRepository` não é uma instância do H2 nem abre o banco diretamente. Ele recebe uma instância do repositório criada e gerenciada pelo Spring.

A anotação `@Autowired` solicita que o Spring faça a injeção de dependência. Na prática, o processo é:

1. o Spring encontra a interface `UserRepository`;
2. o Spring Data JPA cria uma implementação dela;
3. o Spring instancia o `UserController`;
4. o Spring coloca a implementação do repositório no campo `userRepository`;
5. o controller pode usar esse objeto para acessar o banco.

Assim, não precisamos escrever:

```java
new UserRepository()
```

Isso nem seria possível diretamente, pois `UserRepository` é uma interface. A implementação é gerada pelo Spring.

## Salvando o usuário no endpoint

O endpoint ficou desta forma:

```java
@PostMapping("/create")
public UserModel createUser(@RequestBody UserModel userModel) {

    var userCreated = this.userRepository.save(userModel);
    return userModel;
}
```

Quando uma requisição `POST /users/create` é recebida:

1. `@RequestBody` pede ao Spring que converta o JSON para um objeto `UserModel`;
2. o objeto é passado para `userRepository.save(userModel)`;
3. o Spring Data JPA entrega a operação ao Hibernate;
4. o Hibernate gera e executa o comando SQL necessário;
5. o registro é inserido na tabela de usuários;
6. o objeto é devolvido como JSON na resposta.

O trecho responsável pela persistência é:

```java
var userCreated = this.userRepository.save(userModel);
```

O método `save()` retorna a entidade salva. Esse retorno pode conter valores gerados durante a persistência, como o `id` e o `createdAt`.

Para aproveitar diretamente o valor devolvido pelo repositório, o método também poderia retornar:

```java
return userCreated;
```

No código atual, retornamos `userModel`. Como o Hibernate normalmente atualiza a própria entidade com os valores gerados, ela também pode aparecer na resposta com esses dados. Porém, retornar `userCreated` expressa melhor a intenção de devolver o resultado da operação de salvamento.

## Participação da entidade `UserModel`

O repositório consegue persistir `UserModel` porque a classe está configurada como entidade JPA:

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

As anotações têm os seguintes papéis:

- `@Entity`: informa que a classe deve ser mapeada para o banco;
- `@Id`: marca o campo que representa a chave primária;
- `@GeneratedValue`: solicita a geração automática do UUID;
- `@CreationTimestamp`: preenche a data e a hora da criação do registro;
- `@Data`: gera getters, setters e outros métodos com Lombok.

O cliente precisa enviar apenas os dados do usuário. O identificador e a data de criação são gerados durante o salvamento.

## Exemplo de requisição

Com a aplicação em execução, a criação pode ser testada com:

```http
POST http://localhost:8080/users/create
Content-Type: application/json
```

Corpo da requisição:

```json
{
  "userName": "roberto",
  "name": "Roberto Neto",
  "password": "senha"
}
```

Uma resposta possível é:

```json
{
  "id": "b2df8388-42dc-4726-a8a4-54ee87645ab7",
  "userName": "roberto",
  "name": "Roberto Neto",
  "password": "senha",
  "createdAt": "2026-07-23T13:30:00"
}
```

O UUID e a data apresentados acima são apenas exemplos. Novos valores são gerados a cada cadastro.

## Conferindo o registro no H2

Enquanto a aplicação estiver em execução, o console pode ser acessado em:

```text
http://localhost:8080/h2-console
```

Utilize os dados configurados em `application.properties`:

| Campo | Valor |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:testdb` |
| User Name | `admin` |
| Password | `admin` |

Depois de conectar, a consulta abaixo pode ser usada para visualizar os registros:

```sql
SELECT * FROM TB_USERS;
```

Como o H2 está configurado em memória, os registros permanecem disponíveis somente enquanto a aplicação estiver executando. Ao encerrar ou reiniciar a aplicação, os dados são apagados.

## O que cada camada faz

### `UserController`

Recebe a requisição HTTP, converte o JSON em `UserModel`, chama o repositório e devolve a resposta.

### `UserRepository`

Fornece as operações de persistência. Ele evita que seja necessário escrever manualmente o código JDBC e o comando `INSERT`.

### Hibernate e JPA

Interpretam o mapeamento da entidade e transformam a chamada ao método `save()` em uma operação SQL.

### H2

É o banco de dados que armazena o registro durante a execução da aplicação.

## Cuidados importantes

O exemplo atual salva e devolve a senha em texto puro apenas como parte do aprendizado da integração. Em uma aplicação real:

- a senha deve ser protegida com BCrypt, Argon2 ou outro algoritmo adequado;
- o campo `password` não deve ser devolvido na resposta;
- os dados recebidos devem ser validados;
- nomes de usuário duplicados devem ser impedidos;
- erros de persistência devem gerar respostas HTTP apropriadas;
- é preferível injetar o repositório pelo construtor em vez de usar injeção diretamente no campo.

## Resumo

- `UserRepository` estende `JpaRepository<UserModel, UUID>`.
- O Spring cria automaticamente a implementação do repositório.
- `@Autowired` injeta essa implementação no controller.
- O campo `userRepository` representa o acesso aos dados, não uma instância direta do banco.
- `save(userModel)` persiste o usuário no H2.
- O Hibernate gera o SQL com base no mapeamento de `UserModel`.
- O UUID e a data de criação são preenchidos automaticamente.
- Por ser um H2 em memória, o banco perde os dados quando a aplicação é encerrada.
