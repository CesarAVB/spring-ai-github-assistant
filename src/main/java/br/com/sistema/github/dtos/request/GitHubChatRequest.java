package br.com.sistema.github.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request para chat com GitHub Assistant")
public record GitHubChatRequest(
        
    @Schema(
        description = "Mensagem em linguagem natural para o assistente",
        example = "Liste meus repositórios públicos com mais de 10 stars",
        required = true
    )
    String message
    
) {
    // ====================================
    // Validação básica
    // ====================================
    public boolean isValid() {
        return message != null && !message.trim().isEmpty();
    }
}