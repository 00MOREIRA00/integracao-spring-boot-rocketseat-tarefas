# Ambientes e profiles no Spring Boot

Uma aplicação geralmente é executada em ambientes diferentes. Cada ambiente pode precisar de configurações próprias, como endereço do banco de dados, credenciais, nível de logs e recursos habilitados.

Alguns ambientes comuns são:

- `dev`: desenvolvimento local;
- `stg`: homologação ou staging;
- `prod`: produção.

O Spring Boot utiliza **profiles** para selecionar as configurações de cada ambiente.

## Organização dos arquivos

Os arquivos de configuração ficam normalmente em `src/main/resources`:

```text
src/main/resources/
├── application.properties
├── application-dev.properties
├── application-stg.properties
└── application-prod.properties
```

O arquivo `application.properties` contém as configurações comuns a todos os ambientes:

```properties
spring.application.name=tarefas
server.port=8080
```

Os arquivos `application-{profile}.properties` contêm somente as configurações específicas de cada ambiente.

## Ambiente de desenvolvimento

O arquivo `application-dev.properties` pode usar o H2 e habilitar seu console:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=admin
spring.datasource.password=admin

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

## Ambiente de homologação

O arquivo `application-stg.properties` pode apontar para um banco PostgreSQL de homologação:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.h2.console.enabled=false
```

## Ambiente de produção

O arquivo `application-prod.properties` pode usar a mesma estrutura, recebendo valores diferentes do ambiente de produção:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.h2.console.enabled=false
```

Embora os nomes das variáveis sejam iguais em staging e produção, cada ambiente fornece seus próprios valores.

## Ativando um profile

Ao executar com o Maven, o profile pode ser informado assim:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Ao executar o arquivo JAR:

```bash
java -jar target/tarefas.jar --spring.profiles.active=prod
```

Também é possível utilizar a variável de ambiente `SPRING_PROFILES_ACTIVE`.

No PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="stg"
java -jar target/tarefas.jar
```

Em Linux ou macOS:

```bash
SPRING_PROFILES_ACTIVE=stg java -jar target/tarefas.jar
```

## Como as configurações são combinadas

Quando o profile `dev` está ativo, o Spring Boot carrega:

```text
application.properties
        +
application-dev.properties
```

As propriedades do arquivo específico sobrescrevem propriedades de mesmo nome definidas no arquivo base.

Por exemplo, se o arquivo base define:

```properties
server.port=8080
```

E `application-dev.properties` define:

```properties
server.port=8081
```

A aplicação será iniciada na porta `8081` quando o profile `dev` estiver ativo.

## Senhas e dados sensíveis

Senhas, tokens e outras informações sensíveis não devem ser gravados diretamente em arquivos versionados pelo Git. Os arquivos podem apenas referenciar variáveis de ambiente:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

Cada servidor, contêiner ou plataforma de implantação deve fornecer os valores dessas variáveis. Dessa maneira, as configurações gerais permanecem no projeto, mas os segredos ficam fora do repositório.

## Resumo

- `application.properties` guarda configurações compartilhadas.
- `application-dev.properties`, `application-stg.properties` e `application-prod.properties` guardam diferenças de cada ambiente.
- `spring.profiles.active` seleciona o ambiente utilizado.
- O arquivo do profile ativo sobrescreve configurações equivalentes do arquivo base.
- Segredos devem ser fornecidos por variáveis de ambiente, e não gravados no Git.
