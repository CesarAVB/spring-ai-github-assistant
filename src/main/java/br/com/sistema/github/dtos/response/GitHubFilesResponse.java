package br.com.sistema.github.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response com estrutura de arquivos")
public class GitHubFilesResponse {
    
    @Schema(description = "Nome do repositório", example = "spring-boot-api")
    private String repositoryName;
    
    @Schema(description = "Total de arquivos", example = "47")
    private Integer totalFiles;
    
    @Schema(description = "Árvore de arquivos e diretórios")
    private List<FileNode> files;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Nó de arquivo ou diretório")
    public static class FileNode {
        
        @Schema(description = "Nome do arquivo/diretório", example = "App.java")
        private String name;
        
        @Schema(description = "Caminho completo", example = "src/main/java/App.java")
        private String path;
        
        @Schema(description = "Se é diretório", example = "false")
        private Boolean isDirectory;
        
        @Schema(description = "Tamanho em bytes (se for arquivo)", example = "2048")
        private Long size;
        
        @Schema(description = "Se foi carregado (lazy loading)", example = "true")
        private Boolean loaded;
        
        @Schema(description = "Filhos (se for diretório)")
        @Builder.Default
        private List<FileNode> children = new ArrayList<>();
    }
}