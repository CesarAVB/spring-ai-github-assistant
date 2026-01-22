# 🐙 GitHub AI Assistant

Um microserviço inteligente que combina a **GitHub API** com **Google Gemini** para automatizar operações em repositórios GitHub através de conversa em linguagem natural.

---

## 🎯 Visão Geral

Este projeto oferece uma interface inteligente para gerenciar repositórios GitHub sem necessidade de conhecimentos técnicos profundos. Você conversa naturalmente com a IA, e ela executa as operações solicitadas usando a GitHub API.

**Exemplo:**
```
Você: "Liste meus repositórios mais populares"
IA: [Analisa seus repos e retorna os com mais stars]

Você: "Crie um arquivo README.md no projeto-x"
IA: [Cria o arquivo e confirma a operação]
```

---

## ✨ Funcionalidades

### 📂 Gerenciamento de Repositórios
- ✅ Listar todos os repositórios
- ✅ Buscar repositórios por nome ou palavra-chave
- ✅ Visualizar detalhes (stars, forks, issues abertas)
- ✅ Filtrar por tipo (público/privado)

### 📄 Operações com Arquivos
- ✅ Listar arquivos da raiz ou diretórios específicos
- ✅ Ler conteúdo completo de arquivos
- ✅ Criar novos arquivos
- ✅ Atualizar arquivos existentes
- ✅ Deletar arquivos

### 📝 Gerenciamento de Commits
- ✅ Listar últimos commits
- ✅ Visualizar detalhes de commits
- ✅ Ver histórico de alterações

### 🔖 Gerenciamento de Issues
- ✅ Listar issues abertas
- ✅ Visualizar detalhes de issues
- ✅ Filtrar por status

### 🔄 Chat Inteligente
- ✅ Conversa em linguagem natural (português brasileiro)
- ✅ Análise de código com IA
- ✅ Sugestões automáticas
- ✅ Respostas contextualizadas

---

## 🔧 Tecnologias

| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 21+ | Linguagem base |
| **Spring Boot** | 3.2.5 | Framework web e injeção de dependências |
| **Spring Web** | 3.2.5 | REST API |
| **Lombok** | 1.18.30 | Reduz boilerplate (getters, setters, logs) |
| **LangChain4j** | 1.7.1 | Integração com modelos de IA |
| **Google Gemini** | 2.5-flash | Modelo de IA generativa |
| **GitHub API** | Latest | Integração com GitHub (via kohsuke/github) |
| **Swagger/OpenAPI** | 3.0 | Documentação interativa |
| **Maven** | 3.8+ | Gerenciador de dependências |

---

## 📦 Pré-requisitos

Antes de iniciar, você precisa ter:

1. **Java 21+** instalado
   ```bash
   java -version
   ```

2. **Maven 3.8+**
   ```bash
   mvn -version
   ```

3. **Conta GitHub** com acesso a repositórios
   - Personal Access Token (PAT) gerado
   - [Como criar um token](https://docs.github.com/pt/authentication/keeping-your-data-secure-and-secure/managing-your-personal-access-tokens)

4. **Chave de API Google Gemini**
   - Obtenha em: https://ai.google.dev/
   - Gere uma API key gratuita

5. **Git** instalado (opcional, mas recomendado)

---

## 🚀 Instalação

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/github-ai-assistant.git
cd github-ai-assistant
```

### 2. Instale as dependências
```bash
mvn clean install
```

### 3. Configure as variáveis de ambiente (próxima seção)

### 4. Inicie a aplicação
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: **http://localhost:8081**

---

## ⚙️ Configuração

### Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto ou configure as variáveis no seu sistema:

```bash
# Google Gemini API
export GEMINI_API_KEY="sua-chave-api-google-gemini"

# GitHub
export GITHUB_TOKEN="seu-personal-access-token"
export GITHUB_USERNAME="seu-usuario-github"
```

### application.properties

Você também pode configurar via `src/main/resources/application.properties`:

```properties
# Server
server.port=8081
server.servlet.context-path=/

# Logging
logging.level.root=INFO
logging.level.br.com.sistema.github=DEBUG

# Google Gemini
spring.langchain4j.google-ai.gemini.api-key=${GEMINI_API_KEY}
spring.langchain4j.google-ai.gemini.model-name=gemini-2.5-flash
spring.langchain4j.google-ai.gemini.temperature=0.7

# GitHub
spring.github.token=${GITHUB_TOKEN}
spring.github.username=${GITHUB_USERNAME}

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### Permissões do Token GitHub

Seu Personal Access Token precisa das seguintes permissões:

- ✅ `repo` - Acesso completo a repositórios
- ✅ `read:user` - Ler dados do usuário
- ✅ `read:repo_hook` - Ler webhooks
- ✅ `admin:repo_hook` - Gerenciar webhooks (opcional)

---

## 📖 Como Usar

### Iniciar a Aplicação

**Desenvolvimento:**
```bash
mvn spring-boot:run
```

**Com parâmetros customizados:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9000"
```

### Acessar a Documentação

1. **Swagger UI (Interface Interativa):**
   - URL: http://localhost:8081/swagger-ui.html
   - Teste os endpoints direto na interface

2. **OpenAPI JSON:**
   - URL: http://localhost:8081/api-docs

3. **Arquivos de Documentação:**
   - README.md (este arquivo)
   - Comentários no código

---

## 📡 Endpoints da API

### Chat com Assistente
```http
POST /api/v1/github/chat
Content-Type: application/json

{
  "message": "Liste meus repositórios"
}
```

**Resposta (200 OK):**
```json
{
  "success": true,
  "userMessage": "Liste meus repositórios",
  "assistantResponse": "📂 Seus repositórios:\n1. projeto-api...",
  "timestamp": "2024-01-22T10:30:00Z"
}
```

---

### Listar Repositórios
```http
GET /api/v1/github/repositories
```

**Resposta (200 OK):**
```json
{
  "total": 5,
  "repositories": [
    {
      "name": "spring-boot-api",
      "description": "API REST com Spring Boot",
      "url": "https://github.com/user/spring-boot-api",
      "language": "Java",
      "stars": 42,
      "forks": 8,
      "isPrivate": false
    }
  ]
}
```

---

### Listar Arquivos de Repositório
```http
GET /api/v1/github/repositories/{name}/files
GET /api/v1/github/repositories/{name}/files?path=src/main
```

**Resposta (200 OK):**
```json
{
  "repositoryName": "meu-projeto",
  "totalFiles": 8,
  "files": [
    {
      "name": "README.md",
      "path": "README.md",
      "isDirectory": false,
      "size": 2048,
      "loaded": true,
      "children": []
    },
    {
      "name": "src",
      "path": "src",
      "isDirectory": true,
      "size": null,
      "loaded": true,
      "children": []
    }
  ]
}
```

---

### Analisar Arquivos
```http
POST /api/v1/github/analyze
Content-Type: application/json

{
  "repositoryName": "meu-projeto",
  "analysisType": "de segurança",
  "selectedFilePaths": ["src/main/java/App.java", "pom.xml"]
}
```

**Resposta (200 OK):**
```json
{
  "success": true,
  "userMessage": "Análise de arquivos",
  "assistantResponse": "Análise de segurança realizada:\n- Arquivo App.java...",
  "timestamp": "2024-01-22T10:35:00Z"
}
```

---

### Health Check
```http
GET /api/v1/github/health
```

**Resposta (200 OK):**
```
✅ GitHub Assistant Online
```

---

## 💬 Exemplos de Uso

### Exemplo 1: Chat Simples
```bash
curl -X POST http://localhost:8081/api/v1/github/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Liste meus repositórios"}'
```

### Exemplo 2: Listar Repositórios (Frontend)

```javascript
const response = await fetch('http://localhost:8081/api/v1/github/repositories', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
});

const data = await response.json();
console.log(data.repositories);
```

### Exemplo 3: Chat com Análise (Angular)

```typescript
// service.ts
import { HttpClient } from '@angular/common/http';

constructor(private http: HttpClient) {}

analyzeFiles(repo: string, files: string[], type: string) {
  return this.http.post('/api/v1/github/analyze', {
    repositoryName: repo,
    selectedFilePaths: files,
    analysisType: type
  });
}
```

```typescript
// component.ts
this.service.analyzeFiles('meu-repo', ['README.md', 'pom.xml'], 'de código').subscribe(
  (result) => console.log(result)
);
```

---

## 📁 Estrutura do Projeto

```
github-ai-assistant/
├── src/
│   ├── main/
│   │   ├── java/br/com/sistema/github/
│   │   │   ├── config/
│   │   │   │   ├── AssistantConfig.java      # Configuração Gemini
│   │   │   │   ├── CorsConfig.java           # CORS da API
│   │   │   │   ├── GeminiConfig.java         # Configuração base Gemini
│   │   │   │   └── OpenApiConfig.java        # Swagger/OpenAPI
│   │   │   ├── controller/
│   │   │   │   └── GitHubController.java     # Endpoints REST
│   │   │   ├── dtos/
│   │   │   │   ├── request/                  # DTOs de entrada
│   │   │   │   └── response/                 # DTOs de resposta
│   │   │   ├── models/
│   │   │   │   └── Repositorio.java          # Modelo de dados
│   │   │   ├── service/
│   │   │   │   ├── GitHubAssistantService.java
│   │   │   │   └── GitHubDataStructureService.java
│   │   │   ├── tools/
│   │   │   │   └── GithubAssistantTools.java # Tools da IA
│   │   │   └── GitHubAssistantApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/...
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## 🎯 Roadmap Futuro

- [ ] Adicionar suporte a branches
- [ ] Integração com GitHub Actions
- [ ] Dashboard web interativo
- [ ] Sistema de agendamento de tarefas
- [ ] Suporte a múltiplas contas GitHub
- [ ] Cache de requisições
- [ ] Autenticação e autorização avançada
- [ ] Métricas e analytics

---

**⭐ Se este projeto foi útil, considere dar uma estrela!**