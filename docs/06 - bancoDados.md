# Banco de dados

O projeto utiliza Spring Data JPA para integrar a aplicação a um banco de dados relacional. O JPA permite trabalhar com entidades Java e repositórios, reduzindo a necessidade de escrever SQL diretamente para operações comuns.

Durante o desenvolvimento, utilizamos o H2, um banco de dados relacional leve escrito em Java. Neste projeto, ele é executado em memória, portanto não exige instalação e todos os dados são apagados quando a aplicação é encerrada.

## Dependências

- `org.springframework.boot:spring-boot-starter-data-jpa`: adiciona o Spring Data JPA, Hibernate e os recursos necessários para persistência com JPA.
- `com.h2database:h2`: adiciona o banco de dados e o driver JDBC do H2 em tempo de execução.
- `org.springframework.boot:spring-boot-h2console`: adiciona a configuração automática do console web do H2. No Spring Boot 4, esse módulo precisa ser incluído explicitamente.

As dependências relacionadas ao H2 estão declaradas no `pom.xml`:

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-h2console</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Configuração

O arquivo `src/main/resources/application.properties` contém a configuração do banco e do console:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

A URL `jdbc:h2:mem:testdb` cria o banco `testdb` na memória do mesmo processo em que a aplicação está sendo executada.

## Acessando o console H2

Inicie a aplicação:

```bash
mvn spring-boot:run
```

Com a aplicação em execução, acesse:

```text
http://localhost:8080/h2-console
```

Preencha o formulário de login com os valores abaixo:

| Campo | Valor |
| --- | --- |
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:mem:testdb` |
| User Name | `admin` |
| Password | `admin` |

O campo **JDBC URL** precisa ser alterado, pois o console pode sugerir `jdbc:h2:~/testdb`. Essa URL aponta para um banco armazenado em arquivo no diretório do usuário e não corresponde ao banco em memória configurado pela aplicação.

## Observações

- A aplicação deve permanecer em execução enquanto o console estiver sendo utilizado.
- Os dados são perdidos sempre que a aplicação é encerrada ou reiniciada.
- O console H2 é indicado somente para desenvolvimento e testes. Ele não deve ficar habilitado em produção.
