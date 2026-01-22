package br.com.sistema.github.tools;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class GithubAssistantTools {
    
    @Value("${spring.github.token}")
    private String githubToken;
    
    @Value("${spring.github.username}")
    private String githubUsername;
    
    private GitHub github;
    
    // ====================================
    // Conecta ao GitHub (lazy initialization)
    // ====================================
    private GitHub getGitHub() throws IOException {
        if (github == null) {
            if (githubToken == null || githubToken.isEmpty()) {
                throw new IOException("❌ github.token não configurado");
            }
            if (githubUsername == null || githubUsername.isEmpty()) {
                throw new IOException("❌ github.username não configurado");
            }
            
            log.info("🔌 Conectando ao GitHub: {}", githubUsername);
            github = GitHub.connectUsingOAuth(githubToken);
            log.info("✅ Conectado ao GitHub");
        }
        return github;
    }
    
    // ====================================
    // Lista todos os repositórios do usuário
    // ====================================
    @Tool("Lista todos os repositórios do usuário no GitHub")
    public String listRepositories() {
        try {
            log.info("📂 Listando repositórios: {}", githubUsername);
            
            GitHub gh = getGitHub();
            List<GHRepository> repos = gh.getUser(githubUsername)
                    .listRepositories()
                    .toList();
            
            if (repos.isEmpty()) {
                return "Nenhum repositório encontrado.";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("✅ Total: %d repositórios\n\n", repos.size()));
            
            for (GHRepository repo : repos) {
                sb.append(String.format("📦 %s\n", repo.getName()));
                
                if (repo.getDescription() != null) {
                    sb.append(String.format("   Descrição: %s\n", repo.getDescription()));
                }
                
                sb.append(String.format("   URL: %s\n", repo.getHtmlUrl()));
                
                if (repo.getLanguage() != null) {
                    sb.append(String.format("   Linguagem: %s\n", repo.getLanguage()));
                }
                
                sb.append(String.format("   ⭐ %d | 🔱 %d | 🐛 %d issues\n",
                        repo.getStargazersCount(),
                        repo.getForksCount(),
                        repo.getOpenIssueCount()));
                
                sb.append(String.format("   %s\n\n",
                        repo.isPrivate() ? "🔒 Privado" : "🌐 Público"));
            }
            
            return sb.toString();
            
        } catch (IOException e) {
            log.error("❌ Erro ao listar repositórios", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Lista arquivos da raiz de um repositório
    // ====================================
    @Tool("Lista arquivos de um repositório específico")
    public String listRepositoryFiles(String repositoryName) {
        try {
            log.info("📂 Listando arquivos: {}", repositoryName);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            List<GHContent> contents = repo.getDirectoryContent("/");
            
            if (contents.isEmpty()) {
                return "Repositório vazio.";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("✅ Arquivos de %s:\n\n", repositoryName));
            
            for (GHContent content : contents) {
                String icon = content.isDirectory() ? "📁" : "📄";
                sb.append(String.format("%s %s", icon, content.getName()));
                
                if (content.isFile()) {
                    sb.append(String.format(" (%d bytes)", content.getSize()));
                }
                
                sb.append("\n");
            }
            
            return sb.toString();
            
        } catch (IOException e) {
            log.error("❌ Erro ao listar arquivos", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Lista arquivos de um diretório específico
    // ====================================
    @Tool("Lista arquivos de um diretório específico do repositório")
    public String listRepositoryFilesInDirectory(String repositoryName, String directoryPath) {
        try {
            log.info("📂 Listando: {} / {}", repositoryName, directoryPath);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            List<GHContent> contents = repo.getDirectoryContent(directoryPath);
            
            if (contents.isEmpty()) {
                return String.format("Diretório %s está vazio.", directoryPath);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("✅ %s / %s:\n\n", repositoryName, directoryPath));
            
            for (GHContent content : contents) {
                String icon = content.isDirectory() ? "📁" : "📄";
                sb.append(String.format("%s %s", icon, content.getName()));
                
                if (content.isFile()) {
                    sb.append(String.format(" (%d bytes)", content.getSize()));
                }
                
                sb.append("\n");
            }
            
            return sb.toString();
            
        } catch (IOException e) {
            log.error("❌ Erro ao listar diretório", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Lê conteúdo completo de um arquivo
    // ====================================
    @Tool("Lê o conteúdo completo de um arquivo do repositório")
    public String readFile(String repositoryName, String filePath) {
        try {
            log.info("📖 Lendo: {} / {}", repositoryName, filePath);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            GHContent content = repo.getFileContent(filePath);
            
            if (content.isDirectory()) {
                return String.format("❌ %s é um diretório, não um arquivo.", filePath);
            }
            
            String fileContent = content.getContent();
            
            return String.format("""
                    ✅ Arquivo: %s
                    Tamanho: %d bytes
                    Encoding: %s
                    
                    Conteúdo:
                    ─────────────────────────────────
                    %s
                    ─────────────────────────────────
                    """,
                    filePath,
                    content.getSize(),
                    content.getEncoding(),
                    fileContent);
            
        } catch (IOException e) {
            log.error("❌ Erro ao ler arquivo", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Cria um novo arquivo no repositório
    // ====================================
    @Tool("Cria um novo arquivo no repositório")
    public String createFile(String repositoryName, String filePath, 
                           String content, String commitMessage) {
        try {
            log.info("📝 Criando arquivo: {} / {}", repositoryName, filePath);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            repo.createContent()
                    .path(filePath)
                    .content(content)
                    .message(commitMessage)
                    .commit();
            
            return String.format("✅ Arquivo %s criado com sucesso!", filePath);
            
        } catch (IOException e) {
            log.error("❌ Erro ao criar arquivo", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Atualiza um arquivo existente
    // ====================================
    @Tool("Atualiza o conteúdo de um arquivo existente")
    public String updateFile(String repositoryName, String filePath, 
                           String content, String commitMessage) {
        try {
            log.info("✏️ Atualizando: {} / {}", repositoryName, filePath);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            GHContent oldContent = repo.getFileContent(filePath);
            
            oldContent.update(content, commitMessage);
            
            return String.format("✅ Arquivo %s atualizado com sucesso!", filePath);
            
        } catch (IOException e) {
            log.error("❌ Erro ao atualizar arquivo", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Deleta um arquivo do repositório
    // ====================================
    @Tool("Deleta um arquivo do repositório")
    public String deleteFile(String repositoryName, String filePath, String commitMessage) {
        try {
            log.info("🗑️ Deletando: {} / {}", repositoryName, filePath);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            GHContent content = repo.getFileContent(filePath);
            content.delete(commitMessage);
            
            return String.format("✅ Arquivo %s deletado com sucesso!", filePath);
            
        } catch (IOException e) {
            log.error("❌ Erro ao deletar arquivo", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Lista últimos commits de um repositório
    // ====================================
    @Tool("Lista os últimos commits de um repositório")
    public String listCommits(String repositoryName, Integer limit) {
        try {
            int maxCommits = limit != null && limit > 0 ? limit : 10;
            
            log.info("📝 Listando commits: {} (max: {})", repositoryName, maxCommits);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            List<GHCommit> commits = repo.listCommits()
                    .withPageSize(maxCommits)
                    .toList()
                    .subList(0, Math.min(maxCommits, repo.listCommits().toList().size()));
            
            if (commits.isEmpty()) {
                return "Nenhum commit encontrado.";
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("✅ Últimos %d commits de %s:\n\n", commits.size(), repositoryName));
            
            for (GHCommit commit : commits) {
                GHCommit.ShortInfo info = commit.getCommitShortInfo();
                sb.append(String.format("📝 %s\n", info.getMessage()));
                sb.append(String.format("   Autor: %s\n", info.getAuthor().getName()));
                sb.append(String.format("   Data: %s\n", info.getAuthor().getDate()));
                sb.append(String.format("   SHA: %s\n\n", commit.getSHA1().substring(0, 7)));
            }
            
            return sb.toString();
            
        } catch (IOException e) {
            log.error("❌ Erro ao listar commits", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Lista issues abertas de um repositório
    // ====================================
    @Tool("Lista issues abertas de um repositório")
    public String listOpenIssues(String repositoryName) {
        try {
            log.info("🐛 Listando issues: {}", repositoryName);
            
            GitHub gh = getGitHub();
            GHRepository repo = gh.getRepository(githubUsername + "/" + repositoryName);
            
            List<GHIssue> issues = repo.getIssues(GHIssueState.OPEN);
            
            if (issues.isEmpty()) {
                return String.format("✅ %s não tem issues abertas.", repositoryName);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("🐛 Issues abertas em %s: %d\n\n", 
                    repositoryName, issues.size()));
            
            for (GHIssue issue : issues) {
                sb.append(String.format("#%d: %s\n", issue.getNumber(), issue.getTitle()));
                
                if (issue.getBody() != null && !issue.getBody().isEmpty()) {
                    String body = issue.getBody().length() > 100 
                            ? issue.getBody().substring(0, 100) + "..." 
                            : issue.getBody();
                    sb.append(String.format("   %s\n", body));
                }
                
                sb.append(String.format("   Criada em: %s\n", issue.getCreatedAt()));
                sb.append(String.format("   URL: %s\n\n", issue.getHtmlUrl()));
            }
            
            return sb.toString();
            
        } catch (IOException e) {
            log.error("❌ Erro ao listar issues", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Busca repositórios por nome ou palavra-chave
    // ====================================
    @Tool("Busca repositórios do usuário por nome ou palavra-chave")
    public String searchRepository(String query) {
        try {
            log.info("🔍 Buscando: {}", query);
            
            GitHub gh = getGitHub();
            List<GHRepository> repos = gh.getUser(githubUsername)
                    .listRepositories()
                    .toList();
            
            List<GHRepository> matches = repos.stream()
                    .filter(repo -> repo.getName().toLowerCase().contains(query.toLowerCase())
                            || (repo.getDescription() != null && 
                                repo.getDescription().toLowerCase().contains(query.toLowerCase())))
                    .toList();
            
            if (matches.isEmpty()) {
                return String.format("❌ Nenhum repositório encontrado para: %s", query);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("✅ Encontrados %d repositórios:\n\n", matches.size()));
            
            for (GHRepository repo : matches) {
                sb.append(String.format("📦 %s\n", repo.getName()));
                if (repo.getDescription() != null) {
                    sb.append(String.format("   %s\n", repo.getDescription()));
                }
                sb.append(String.format("   %s\n\n", repo.getHtmlUrl()));
            }
            
            return sb.toString();
            
        } catch (IOException e) {
            log.error("❌ Erro ao buscar repositórios", e);
            return formatError(e);
        }
    }
    
    // ====================================
    // Formata mensagem de erro de forma amigável
    // ====================================
    private String formatError(IOException e) {
        String errorMsg = e.getMessage() != null ? e.getMessage() : "Erro desconhecido";
        
        if (errorMsg.contains("401") || errorMsg.contains("Unauthorized")) {
            return "❌ Erro de autenticação: Token GitHub inválido ou expirado";
        }
        
        if (errorMsg.contains("404") || errorMsg.contains("Not Found")) {
            return "❌ Recurso não encontrado: Repositório ou arquivo não existe";
        }
        
        if (errorMsg.contains("403") || errorMsg.contains("Forbidden")) {
            return "❌ Acesso negado: Token sem permissão suficiente";
        }
        
        return "❌ Erro: " + errorMsg;
    }
}