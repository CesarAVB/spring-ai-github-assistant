package br.com.sistema.github.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.sistema.github.dtos.request.AnalyzeGitHubFilesRequest;
import br.com.sistema.github.dtos.request.GitHubChatRequest;
import br.com.sistema.github.dtos.response.AssistantResponse;
import br.com.sistema.github.dtos.response.GitHubFilesResponse;
import br.com.sistema.github.dtos.response.GitHubRepoResponse;
import br.com.sistema.github.service.GitHubAssistantService;
import br.com.sistema.github.service.GitHubDataStructureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/github")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "GitHub Assistant", description = "Assistente de IA para GitHub")
public class GitHubController {
    
    private final GitHubAssistantService assistantService;
    private final GitHubDataStructureService dataService;
    
    // ====================================
    // Chat com o assistente GitHub
    // ====================================
    @PostMapping("/chat")
    @Operation(summary = "Chat com assistente GitHub", description = "Envie uma mensagem em linguagem natural para o assistente processar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mensagem processada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssistantResponse.class))),
        @ApiResponse(responseCode = "400", description = "Request inválido")
    })
    public ResponseEntity<AssistantResponse> chat(@RequestBody GitHubChatRequest request) {
        log.info("💬 Chat recebido");
        
        if (!request.isValid()) {
            return ResponseEntity.badRequest().body(AssistantResponse.error(request.message(), "Mensagem não pode ser vazia"));
        }
        
        try {
            String response = assistantService.processMessage(request.message());
            return ResponseEntity.ok(AssistantResponse.success(request.message(), response));
            
        } catch (Exception e) {
            log.error("❌ Erro ao processar chat", e);
            return ResponseEntity.internalServerError().body(AssistantResponse.error(request.message(), "Erro ao processar mensagem: " + e.getMessage()));
        }
    }
    
    // ====================================
    // Lista repositórios do usuário
    // ====================================
    @GetMapping("/repositories")
    @Operation(summary = "Listar repositórios", description = "Retorna lista de todos os repositórios do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Lista obtida com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = GitHubRepoResponse.class)))
    public ResponseEntity<GitHubRepoResponse> listRepositories() {
        log.info("📚 Listando repositórios");
        
        try {
            GitHubRepoResponse response = dataService.getRepositories();
            log.info("✅ {} repositórios retornados", response.total());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Erro ao listar repositórios", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ====================================
    // Lista arquivos de um repositório
    // ====================================
    @GetMapping("/repositories/{name}/files")
    @Operation(summary = "Listar arquivos de repositório", description = "Lista arquivos da raiz ou de um diretório específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Arquivos listados com sucesso"),
        @ApiResponse(responseCode = "404", description = "Repositório não encontrado")
    })
    public ResponseEntity<GitHubFilesResponse> listFiles(
            @Parameter(description = "Nome do repositório")
            @PathVariable("name") String repositoryName,
            
            @Parameter(description = "Caminho do diretório (opcional)")
            @RequestParam(value = "path", defaultValue = "", required = false) String path) {
        
        log.info("📂 Listando: {} | path: {}", repositoryName, path.isEmpty() ? "RAIZ" : path);
        
        if (repositoryName == null || repositoryName.isBlank()) {
            log.warn("❌ Nome do repositório vazio");
            return ResponseEntity.badRequest().build();
        }
        
        try {
            GitHubFilesResponse response;
            
            if (path == null || path.trim().isEmpty()) {
                response = dataService.getRepositoryFiles(repositoryName);
            } else {
                response = dataService.getRepositoryFilesInDirectory(repositoryName, path);
            }
            
            log.info("✅ {} arquivos retornados", response.getTotalFiles());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Erro ao listar arquivos", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ====================================
    // Analisa arquivos selecionados com IA
    // ====================================
    @PostMapping("/analyze")
    @Operation(summary = "Analisar arquivos", description = "Analisa múltiplos arquivos usando IA")
    @ApiResponse(responseCode = "200", description = "Análise concluída")
    public ResponseEntity<AssistantResponse> analyzeFiles(@RequestBody AnalyzeGitHubFilesRequest request) {
        
        log.info("🔍 Analisando arquivos: {}", request.repositoryName());
        
        if (!request.isValid()) {
            return ResponseEntity.badRequest().body(AssistantResponse.error("Análise", "Request inválido"));
        }
        
        try {
            StringBuilder filesContent = new StringBuilder();
            filesContent.append(String.format("Análise de %d arquivos do repositório %s:\n\n", request.selectedFilePaths().size(), request.repositoryName()));
            
            for (String filePath : request.selectedFilePaths()) {
                String content = dataService.readFileContent(request.repositoryName(), filePath);
                filesContent.append(content).append("\n\n");
            }
            
            String analysisPrompt = String.format("Faça uma análise %s dos seguintes arquivos:\n\n%s", request.analysisType(), filesContent);
            String analysis = assistantService.processMessage(analysisPrompt);
            return ResponseEntity.ok(AssistantResponse.success("Análise de arquivos", analysis));
            
        } catch (Exception e) {
            log.error("❌ Erro ao analisar arquivos", e);
            return ResponseEntity.internalServerError().body(AssistantResponse.error("Análise", "Erro: " + e.getMessage()));
        }
    }
    
    // ====================================
    // Health check do serviço
    // ====================================
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica se o serviço está online")
    @ApiResponse(responseCode = "200", description = "Serviço online")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("✅ GitHub Assistant Online");
    }
}