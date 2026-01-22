package br.com.sistema.github.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.sistema.github.dtos.response.GitHubFilesResponse;
import br.com.sistema.github.dtos.response.GitHubFilesResponse.FileNode;
import br.com.sistema.github.dtos.response.GitHubRepoResponse;
import br.com.sistema.github.dtos.response.GitHubRepoResponse.RepoInfo;
import br.com.sistema.github.tools.GithubAssistantTools;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubDataStructureService {
    
    private final GithubAssistantTools githubTools;
    
    // ====================================
    // Obtém lista estruturada de repositórios
    // ====================================
    public GitHubRepoResponse getRepositories() {
        log.info("📂 Buscando repositórios");
        String response = githubTools.listRepositories();
        return parseRepositories(response);
    }
    
    // ====================================
    // Obtém lista estruturada de arquivos (raiz)
    // ====================================
    public GitHubFilesResponse getRepositoryFiles(String repositoryName) {
        log.info("📂 Buscando arquivos: {}", repositoryName);
        String response = githubTools.listRepositoryFiles(repositoryName);
        return parseFiles(response, repositoryName);
    }
    
    // ====================================
    // Obtém lista estruturada de arquivos (diretório)
    // ====================================
    public GitHubFilesResponse getRepositoryFilesInDirectory(String repositoryName, String directoryPath) {
        log.info("📂 Buscando: {} / {}", repositoryName, directoryPath);
        String response = githubTools.listRepositoryFilesInDirectory(repositoryName, directoryPath);
        return parseFiles(response, repositoryName);
    }
    
    // ====================================
    // Lê conteúdo de um arquivo
    // ====================================
    public String readFileContent(String repositoryName, String filePath) {
        log.info("📖 Lendo: {} / {}", repositoryName, filePath);
        return githubTools.readFile(repositoryName, filePath);
    }
    
    // ====================================
    // Parseia resposta de listagem de repositórios
    // ====================================
    private GitHubRepoResponse parseRepositories(String response) {
        List<RepoInfo> repos = new ArrayList<>();
        
        if (response == null || response.isEmpty() || response.contains("Nenhum")) {
            return new GitHubRepoResponse(0, repos);
        }
        
        String[] lines = response.split("\n");
        RepoInfo.RepoInfoBuilder currentRepo = null;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.startsWith("📦")) {
                if (currentRepo != null) {
                    repos.add(currentRepo.build());
                }
                String name = line.substring(2).trim();
                currentRepo = RepoInfo.builder().name(name);
            } else if (line.startsWith("Descrição:") && currentRepo != null) {
                currentRepo.description(line.substring("Descrição:".length()).trim());
            } else if (line.startsWith("URL:") && currentRepo != null) {
                currentRepo.url(line.substring("URL:".length()).trim());
            } else if (line.startsWith("Linguagem:") && currentRepo != null) {
                currentRepo.language(line.substring("Linguagem:".length()).trim());
            } else if (line.contains("⭐") && currentRepo != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String stars = parts[0].replaceAll("[^0-9]", "").trim();
                    String forks = parts[1].replaceAll("[^0-9]", "").trim();
                    
                    if (!stars.isEmpty()) {
                        currentRepo.stars(Integer.parseInt(stars));
                    }
                    if (!forks.isEmpty()) {
                        currentRepo.forks(Integer.parseInt(forks));
                    }
                }
            } else if ((line.contains("Privado") || line.contains("Público")) && currentRepo != null) {
                currentRepo.isPrivate(line.contains("Privado"));
            }
        }
        
        if (currentRepo != null) {
            repos.add(currentRepo.build());
        }
        
        repos.sort(Comparator.comparing(RepoInfo::stars, Comparator.nullsLast(Comparator.reverseOrder())));
        return new GitHubRepoResponse(repos.size(), repos);
    }
    
    // ====================================
    // Parseia resposta de listagem de arquivos
    // ====================================
    private GitHubFilesResponse parseFiles(String response, String repositoryName) {
        List<FileNode> files = new ArrayList<>();
        
        if (response == null || response.isEmpty()) {
            return GitHubFilesResponse.builder().repositoryName(repositoryName).totalFiles(0).files(files).build();
        }
        
        String[] lines = response.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.isEmpty() || line.startsWith("✅") || line.contains("bytes)")) {
                continue;
            }
            
            boolean isDirectory = line.startsWith("📁");
            boolean isFile = line.startsWith("📄");
            
            if (isDirectory || isFile) {
                String[] parts = line.split(" ", 2);
                if (parts.length < 2) continue;
                
                String nameAndSize = parts[1].trim();
                String name = nameAndSize.split("\\(")[0].trim();
                
                Long size = null;
                if (nameAndSize.contains("(") && nameAndSize.contains("bytes")) {
                    String sizeStr = nameAndSize.substring(nameAndSize.indexOf("(") + 1, nameAndSize.indexOf("bytes")).trim();
                    try {
                        size = Long.parseLong(sizeStr);
                    } catch (NumberFormatException e) {
                        // Ignora
                    }
                }
                
                FileNode node = FileNode.builder()
                        .name(name)
                        .path(name)
                        .isDirectory(isDirectory)
                        .size(size)
                        .loaded(true)
                        .children(new ArrayList<>())
                        .build();
                
                files.add(node);
            }
        }
        
        files.sort((a, b) -> {
            if (a.getIsDirectory() && !b.getIsDirectory()) return -1;
            if (!a.getIsDirectory() && b.getIsDirectory()) return 1;
            return a.getName().compareToIgnoreCase(b.getName());
        });
        
        return GitHubFilesResponse.builder().repositoryName(repositoryName).totalFiles(files.size()).files(files).build();
    }
}