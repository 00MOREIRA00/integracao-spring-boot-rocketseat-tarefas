# Estrutura do projeto Spring Boot

Este projeto segue a estrutura padrão de uma aplicação Spring Boot criada com Maven. Cada pasta e arquivo principal tem uma responsabilidade específica dentro do desenvolvimento, execução e testes da aplicação.

## `.mvn`

A pasta `.mvn` guarda configurações usadas pelo Maven Wrapper. Ela permite que o projeto seja executado com uma configuração de Maven controlada pelo próprio repositório, sem depender totalmente de uma instalação global do Maven na máquina.

## `mvnw` e `mvnw.cmd`

Esses arquivos fazem parte do Maven Wrapper.

- `mvnw`: usado em Linux e macOS.
- `mvnw.cmd`: usado no Windows.

Com eles, é possível executar comandos Maven diretamente pelo projeto. Por exemplo:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

## `pom.xml`

O arquivo `pom.xml` é o arquivo de configuração principal do Maven. Nele ficam definidas informações como:

- nome e identificadores do projeto;
- versão do Java usada pela aplicação;
- dependências do Spring Boot;
- plugins usados no processo de build.

Neste projeto, o `pom.xml` indica o uso do Java 17 e inclui dependências para criar uma aplicação web com Spring MVC.

## `src/main/java`

Essa pasta contém o código-fonte principal da aplicação.

No projeto atual, a classe `TarefasApplication` é o ponto de entrada da aplicação Spring Boot. É nela que o método `main` chama o `SpringApplication.run`, iniciando o servidor e carregando o contexto do Spring.

## `src/main/resources`

Essa pasta armazena arquivos de configuração e recursos usados pela aplicação em tempo de execução.

O arquivo `application.properties`, por exemplo, pode ser usado para configurar porta do servidor, conexão com banco de dados, logs e outras propriedades do Spring Boot.

## `src/test/java`

Essa pasta contém os testes automatizados da aplicação.

No projeto atual, a classe `TarefasApplicationTests` verifica se o contexto do Spring Boot consegue ser carregado corretamente.

## `docs`

A pasta `docs` guarda a documentação do projeto. Ela pode ser usada para registrar explicações sobre estrutura, configuração, decisões técnicas e passos necessários para executar ou evoluir a aplicação.

## Resumo

A estrutura inicial do projeto separa bem código, configurações, testes e documentação. Essa organização facilita a manutenção da aplicação conforme novas funcionalidades forem adicionadas.
