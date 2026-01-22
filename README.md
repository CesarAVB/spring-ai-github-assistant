# 🐙 GitHub AI Assistant

Microserviço especializado em operações do GitHub usando IA (Google Gemini).

## 🎯 Funcionalidades

- 📚 Listar repositórios
- 🔍 Analisar código
- 📝 Criar/editar arquivos
- 🐛 Gerenciar issues
- 🔀 Gerenciar pull requests
- 🔎 Buscar informações

## 🚀 Quick Start
```bash
# Configurar variáveis
export GEMINI_API_KEY=sua-chave
export GITHUB_TOKEN=seu-token

# Rodar
mvn spring-boot:run

# Acesso
http://localhost:8081
http://localhost:8081/swagger-ui.html
```

## 📡 Endpoints

- `POST /api/v1/github/chat` - Chat com assistente
- `GET /api/v1/github/repositories` - Listar repos
- `GET /api/v1/github/health` - Health check

## 🔧 Tecnologias

- Java 21
- Spring Boot 3.2.5
- LangChain4j 1.7.1
- Google Gemini AI
- GitHub API

## 📝 Exemplo de Uso
```bash
curl -X POST http://localhost:8081/api/v1/github/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Liste meus repositórios"}'
```

## 🧪 Testes
```bash
mvn test
```

## 🐳 Docker
```bash
docker build -t github-assistant .
docker run -p 8081:8081 github-assistant
```