package br.com.sistema.github.dtos.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Response com lista de repositórios")
@Builder
public record GitHubRepoResponse(
        
    @Schema(description = "Total de repositórios", example = "15")
    Integer total,
    
    @Schema(description = "Lista de repositórios")
    List<RepoInfo> repositories
    
) {
    @Schema(description = "Informações de um repositório")
    @Builder
    public record RepoInfo(
            
        @Schema(description = "Nome do repositório", example = "spring-boot-api")
        String name,
        
        @Schema(description = "Descrição do repositório", example = "API REST com Spring Boot")
        String description,
        
        @Schema(description = "URL do repositório", example = "https://github.com/usuario/spring-boot-api")
        String url,
        
        @Schema(description = "Linguagem principal", example = "Java")
        String language,
        
        @Schema(description = "Número de estrelas", example = "42")
        Integer stars,
        
        @Schema(description = "Número de forks", example = "7")
        Integer forks,
        
        @Schema(description = "Se é repositório privado", example = "false")
        Boolean isPrivate
        
    ) {}
}