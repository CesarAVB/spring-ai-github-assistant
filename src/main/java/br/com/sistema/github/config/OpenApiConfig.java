package br.com.sistema.github.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para GitHub Assistant.
 * 
 * Fornece documentação interativa da API em:
 * - Swagger UI: http://localhost:8081/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8081/api-docs
 * 
 * @author César Augusto
 * @version 1.0.0
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🐙 GitHub AI Assistant API")
                        .version("1.0.0")
                        .description("""
                                API REST para assistente inteligente de GitHub com IA (Google Gemini).
                                
                                ## 🎯 Funcionalidades
                                
                                - **Automação de Repositórios**: Listagem e gerenciamento de repositórios
                                - **Análise de Código**: Análise inteligente de código-fonte
                                - **Gerenciamento de Arquivos**: Criação, edição e leitura de arquivos
                                - **Issues & PRs**: Gerenciamento de issues e pull requests
                                - **Chat com IA**: Interação em linguagem natural
                                
                                ## 🔧 Tecnologias
                                
                                - Spring Boot 3.2.5
                                - Java 21
                                - LangChain4j 1.7.1
                                - Google Gemini AI
                                - GitHub API
                                
                                ## 🚀 Como Usar
                                
                                1. Configure `GEMINI_API_KEY` e `GITHUB_TOKEN`
                                2. Envie requisições POST para `/api/v1/github/chat`
                                3. Use linguagem natural: "Liste meus repositórios"
                                
                                ## 📚 Documentação
                                
                                Para mais informações, visite o [GitHub](https://github.com/seu-usuario/spring-ai-github-assistant)
                                """)
                        .contact(new Contact()
                                .name("César Augusto")
                                .email("cesar.augusto.rj1@gmail.com")
                                .url("https://portfolio.cesaravb.com.br"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("🖥️ Servidor Local de Desenvolvimento"),
                        new Server()
                                .url("https://github-assistant.sua-empresa.com")
                                .description("🌐 Servidor de Produção")
                ))
                .tags(List.of(
                        new Tag()
                                .name("GitHub Assistant")
                                .description("Endpoints do assistente de IA para GitHub"),
                        new Tag()
                                .name("Repositórios")
                                .description("Operações relacionadas a repositórios"),
                        new Tag()
                                .name("Arquivos")
                                .description("Gerenciamento de arquivos"),
                        new Tag()
                                .name("Issues & PRs")
                                .description("Gerenciamento de issues e pull requests"),
                        new Tag()
                                .name("Health")
                                .description("Endpoints de saúde e status do serviço")
                ));
    }
}