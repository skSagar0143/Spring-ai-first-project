# Spring AI First Project

A beginner-friendly Spring Boot application that provides a ChatGPT-style web interface using **Spring AI** and an OpenAI chat model.

## Features

- Thymeleaf-based chat page.
- ChatGPT-inspired dark user interface.
- Send a question from the browser without reloading the page.
- Spring MVC endpoint receives the question.
- Spring AI `ChatClient` sends the prompt to the configured OpenAI model.
- The model response is displayed in the chat window.
- Loading indicator, Enter-to-send, Shift+Enter for a new line, and automatic scrolling.

> This is an introductory chat application. The current browser UI keeps messages only in the page; it does not provide database-backed conversation history, user accounts, streaming responses, or multi-user chat persistence.

## Technology

- Java
- Spring Boot / Spring MVC
- Spring AI
- OpenAI chat model (configured as `gpt-4o`)
- Thymeleaf
- HTML, CSS, JavaScript
- Lombok

Use dependency versions that are compatible with the Spring Boot and Spring AI versions in your own `pom.xml`. This README intentionally does not assume a specific version.

## Request flow

1. The browser loads `GET /`.
2. `OpenAIController` returns the Thymeleaf template `chatPage`.
3. The user enters a question and the page sends `POST /sendMsg` as form URL-encoded data with the `question` parameter.
4. The controller calls `OpenAIService.askToAi(question)`.
5. `OpenAIService` calls `ChatClient.prompt().user(question).call().content()`.
6. The response text is returned to the browser and displayed in the conversation.

## Suggested source layout

The following layout reflects the Java packages and template discussed for this project. Adjust names if your repository uses a different application class or package structure.

```text
src/
└── main/
    ├── java/
    │   └── com/ai/
    │       ├── <ApplicationClass>.java
    │       ├── config/
    │       │   └── AIConfig.java
    │       ├── controller/
    │       │   └── OpenAIController.java
    │       └── service/
    │           └── OpenAIService.java
    └── resources/
        ├── application.yml
        └── templates/
            └── chatPage.html
```

## Configuration

Configure the OpenAI key through an environment variable. **Never commit a real API key to Git.**

Example `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: spring-ai-01

  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          max-tokens: 100
```

The `max-tokens` setting limits the generated response length. Increase it if longer answers are needed and supported by the selected model/API configuration.

### Set the key on Windows PowerShell

For the current PowerShell session:

```powershell
$env:OPENAI_API_KEY = "your_openai_api_key"
```

Start the Spring Boot application from that same terminal session. For a persistent environment variable, configure it through Windows Environment Variables and open a new terminal.

OpenAI API billing is separate from a ChatGPT subscription. A valid API key with available API quota/billing is required for successful model requests.

## Spring AI configuration

A configuration class can expose a reusable `ChatClient` bean:

```java
package com.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
```

The OpenAI Spring AI starter must be present and correctly configured so Spring can provide `ChatClient.Builder`.

## Controller

The controller serves the Thymeleaf page and exposes the chat endpoint:

```java
package com.ai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ai.service.OpenAIService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAIService service;

    @GetMapping("/")
    public String askQuestions() {
        return "chatPage";
    }

    @PostMapping("/sendMsg")
    @ResponseBody
    public String sendAnswer(@RequestParam("question") String question) {
        return service.askToAi(question);
    }
}
```

## Service

Use constructor injection for the `ChatClient`. The field must be `final` when using Lombok's `@RequiredArgsConstructor`:

```java
package com.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final ChatClient chatClient;

    public String askToAi(String questions) {
        return chatClient
                .prompt()
                .user(questions)
                .call()
                .content();
    }
}
```

## Run the application

1. Install a compatible JDK and Maven.
2. Add the Spring Web, Thymeleaf, Spring AI OpenAI model starter, and Lombok dependencies to `pom.xml` (with compatible versions).
3. Set `OPENAI_API_KEY` in the environment.
4. Run the Spring Boot application from your IDE or with Maven:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   If the project does not include the Maven Wrapper, use:

   ```powershell
   mvn spring-boot:run
   ```

5. Open [http://localhost:8080/](http://localhost:8080/).

## Troubleshooting

### `NullPointerException` because `service` is null

- Ensure the controller uses `@Controller` and `@RequiredArgsConstructor`.
- Declare the dependency as `private final OpenAIService service;`.
- Ensure `OpenAIService` is annotated with `@Service`.
- Keep the application class in `com.ai` or a parent package so component scanning can find `com.ai.controller` and `com.ai.service`.

### `ChatClient` is null or cannot be injected

- Declare it as `private final ChatClient chatClient;` when using `@RequiredArgsConstructor`.
- Ensure the `AIConfig` bean is present, or use another supported Spring AI setup that provides the client.
- Ensure the Spring AI OpenAI starter is on the classpath.

### OpenAI authentication or quota errors

- Verify that `OPENAI_API_KEY` is set in the environment of the running process.
- Check that the key is an API key, not a ChatGPT account password or login.
- Check API billing, available quota, model access, and the error response from the API provider.

### POST request fails with CSRF

If Spring Security is enabled, configure the page to send the CSRF token with the JavaScript request. Do not disable CSRF protection as a general production workaround.

## Security and production notes

- Never commit API keys, passwords, or other secrets.
- Keep API calls on the server; do not expose the OpenAI key in browser JavaScript.
- Validate and limit incoming prompt size.
- Add appropriate exception handling and user-friendly error responses.
- Consider request timeouts, rate limiting, authentication, and usage-cost controls before public deployment.
- Add persistent conversation storage only when conversation history is required.

## Current scope

This repository documents the first Spring AI chat implementation: a single-turn prompt/response flow through Spring MVC, Spring AI `ChatClient`, and an OpenAI chat model, presented in a Thymeleaf web page.
