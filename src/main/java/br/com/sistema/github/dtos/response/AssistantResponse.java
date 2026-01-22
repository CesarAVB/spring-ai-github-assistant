package br.com.sistema.github.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response do GitHub Assistant")
public record AssistantResponse(
        
    @Schema(description = "Se operação foi bem-sucedida", example = "true")
    Boolean success,
    
    @Schema(description = "Nome do assistente", example = "GitHubAssistant")
    String assistant,
    
    @Schema(description = "Tipo de resposta", example = "chat")
    String type,
    
    @Schema(description = "Pergunta original do usuário", example = "Liste meus repositórios")
    String question,
    
    @Schema(description = "Dados da resposta")
    String data,
    
    @Schema(description = "Mensagem de erro (se houver)")
    String error,
    
    @Schema(description = "Timestamp da resposta", example = "2025-01-22T15:30:00")
    String timestamp
    
) {
    // ====================================
    // Cria resposta de sucesso
    // ====================================
    public static AssistantResponse success(String question, String data) {
        return new AssistantResponse(
                true,
                "GitHubAssistant",
                "chat",
                question,
                data,
                null,
                java.time.LocalDateTime.now().toString()
        );
    }
    
    // ====================================
    // Cria resposta de erro
    // ====================================
    public static AssistantResponse error(String question, String error) {
        return new AssistantResponse(
                false,
                "GitHubAssistant",
                "error",
                question,
                null,
                error,
                java.time.LocalDateTime.now().toString()
        );
    }
}