# Filtros no Spring

Um filtro é um componente que intercepta requisições e respostas HTTP. Ele é executado antes de a requisição chegar ao controller e pode também executar código depois que o controller produz a resposta.

O conceito é semelhante ao de um **middleware** em outros frameworks. No Spring, o filtro utilizado neste projeto pertence à especificação Jakarta Servlet.

O fluxo básico é:

```text
Cliente
   ↓
Filtro
   ↓
Controller
   ↓
Filtro
   ↓
Cliente
```

Filtros são usados com frequência para:

- autenticação e autorização;
- registro de logs;
- validação de cabeçalhos;
- configuração de CORS;
- alteração de requisições ou respostas;
- medição do tempo de processamento.

## O filtro deste projeto

O projeto possui a classe `FilterTaskAuth`:

```java
@Component
public class FilterTaskAuth implements Filter {

    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {

        System.out.println("Chegou no filtro de autenticação");
        chain.doFilter(request, response);
    }
}
```

### `@Component`

A anotação `@Component` faz o Spring encontrar a classe durante a inicialização da aplicação. Como a classe implementa `Filter`, o Spring Boot registra o componente como um filtro Servlet.

Sem uma configuração adicional de URL, esse filtro é aplicado às requisições recebidas pela aplicação, inclusive requisições para controllers diferentes e URLs inexistentes. Portanto, o nome `FilterTaskAuth` não faz com que ele seja limitado automaticamente às rotas `/tasks`.

### `implements Filter`

A interface `Filter`, do pacote `jakarta.servlet`, define o contrato de um filtro. Seu principal método é `doFilter()`.

Os argumentos desse método são:

- `request`: representa a requisição recebida;
- `response`: representa a resposta que será enviada;
- `chain`: representa os próximos filtros e, ao final da cadeia, o recurso solicitado.

### `chain.doFilter()`

A chamada abaixo permite que o processamento continue:

```java
chain.doFilter(request, response);
```

Se existir outro filtro, ele será executado. Depois de todos os filtros, a requisição poderá chegar ao controller.

Se o filtro não chamar `chain.doFilter()`, a cadeia é interrompida. Nesse caso, o próprio filtro deve preparar a resposta HTTP. Esse comportamento é útil para bloquear uma requisição não autenticada.

## Executando código antes e depois do controller

Código colocado antes de `chain.doFilter()` é executado antes do restante da aplicação. Código colocado depois é executado quando o processamento da cadeia retorna:

```java
System.out.println("Antes do controller");

chain.doFilter(request, response);

System.out.println("Depois do controller");
```

Isso pode ser utilizado, por exemplo, para calcular o tempo total de uma requisição:

```java
long inicio = System.currentTimeMillis();

chain.doFilter(request, response);

long duracao = System.currentTimeMillis() - inicio;
System.out.println("Duração: " + duracao + " ms");
```

## Trabalhando com informações HTTP

`ServletRequest` e `ServletResponse` são interfaces genéricas. Para acessar método HTTP, URL, cabeçalhos e status, é possível convertê-las para os tipos HTTP:

```java
var httpRequest = (HttpServletRequest) request;
var httpResponse = (HttpServletResponse) response;

System.out.println(httpRequest.getMethod());
System.out.println(httpRequest.getRequestURI());

chain.doFilter(httpRequest, httpResponse);
```

Os imports correspondentes são:

```java
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
```

## Bloqueando uma requisição

Um filtro pode verificar uma credencial e encerrar o processamento quando ela não for válida:

```java
var httpRequest = (HttpServletRequest) request;
var httpResponse = (HttpServletResponse) response;

String authorization = httpRequest.getHeader("Authorization");

if (authorization == null) {
    httpResponse.sendError(
        HttpServletResponse.SC_UNAUTHORIZED,
        "Não autorizado"
    );
    return;
}

chain.doFilter(request, response);
```

O `return` deixa explícito que o método termina depois de enviar o erro. Como `chain.doFilter()` não é chamado nesse caminho, a requisição não chega ao controller.

Esse código é apenas uma demonstração do funcionamento de um filtro. Em uma aplicação real, autenticação e autorização normalmente são implementadas com Spring Security, que já possui uma cadeia de filtros especializada e recursos seguros para esse objetivo.

## Limitando o filtro a determinadas URLs

É possível registrar o filtro explicitamente com `FilterRegistrationBean` e definir os padrões de URL:

```java
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<FilterTaskAuth> taskAuthFilter(
        FilterTaskAuth filter
    ) {
        var registration = new FilterRegistrationBean<FilterTaskAuth>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/tasks/*");
        registration.setOrder(1);
        return registration;
    }
}
```

Nesse exemplo, o filtro é executado somente para URLs que começam com `/tasks/`. O número informado em `setOrder()` define sua posição quando há mais de um filtro: valores menores são executados primeiro.

Ao usar esse registro, é importante não deixar o mesmo filtro ser registrado duas vezes. O `FilterRegistrationBean` deve ser a forma responsável pela configuração do filtro.

## `Filter` e `OncePerRequestFilter`

O Spring também oferece `OncePerRequestFilter`, uma classe base conveniente para filtros HTTP:

```java
@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        filterChain.doFilter(request, response);
    }
}
```

Ela já fornece `HttpServletRequest` e `HttpServletResponse` e ajuda a garantir uma única execução por ciclo de requisição, inclusive em cenários que envolvem diferentes despachos internos do container Servlet.

## Filtro, interceptor e Spring Security

Esses recursos atuam em pontos diferentes:

| Recurso | Atua onde | Uso comum |
|---|---|---|
| Servlet `Filter` | Antes do processamento do Spring MVC | Logs, cabeçalhos e processamento HTTP geral |
| `HandlerInterceptor` | Dentro do Spring MVC, em torno dos controllers | Regras relacionadas aos endpoints e controllers |
| Spring Security | Em uma cadeia própria de filtros | Autenticação, autorização e proteção da aplicação |

Para aprendizado, implementar um filtro simples ajuda a entender o caminho da requisição. Para segurança de produção, é preferível utilizar Spring Security em vez de criar manualmente todo o mecanismo de autenticação.

## Resumo

- Um filtro é o equivalente conceitual a um middleware.
- `@Component` faz o Spring Boot registrar o filtro deste projeto.
- `doFilter()` é executado quando a requisição passa pelo filtro.
- `chain.doFilter()` permite que a requisição continue.
- Não chamar a cadeia permite bloquear a requisição.
- O filtro atual não está limitado automaticamente às rotas de tarefas.
- `FilterRegistrationBean` permite selecionar URLs e definir a ordem.
- Para autenticação real, Spring Security é a opção indicada.
