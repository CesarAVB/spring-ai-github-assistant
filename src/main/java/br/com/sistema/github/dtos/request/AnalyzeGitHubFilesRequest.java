package br.com.sistema.github.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Request para análise de arquivos GitHub")
public record AnalyzeGitHubFilesRequest(
        
    @Schema(
        description = "Nome do repositório",
        example = "spring-boot-api",
        required = true
    )
    String repositoryName,
    
    @Schema(
        description = "Lista de caminhos dos arquivos para analisar",
        example = "[\"src/main/java/App.java\", \"pom.xml\", \"README.md\"]",
        required = true
    )
    List<String> selectedFilePaths,
    
    @Schema(
        description = "Tipo de análise",
        example = "code_review",
        allowableValues = {"code_review", "security", "performance", "quality", "general"},
        required = true
    )
    String analysisType
    
) {
    // ====================================
    // Validação básica
    // ====================================
    public boolean isValid() {
        return repositoryName != null && !repositoryName.trim().isEmpty()
                && selectedFilePaths != null && !selectedFilePaths.isEmpty()
                && analysisType != null && !analysisType.trim().isEmpty();
    }
}