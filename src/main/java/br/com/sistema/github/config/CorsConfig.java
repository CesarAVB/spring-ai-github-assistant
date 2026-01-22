package br.com.sistema.github.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuração CORS (Cross-Origin Resource Sharing) para GitHub Assistant.
 * 
 * Permite que o frontend (Angular, React, Vue, etc) acesse a API
 * de diferentes origens (localhost:4200, domínio de produção, etc).
 * 
 * Segurança:
 * - Desenvolvimento: Permite todas as origens (localhost)
 * - Produção: Deve restringir apenas origens confiáveis
 * 
 * @author César Augusto
 * @version 1.0.0
 */
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // ============================================
        // ORIGENS PERMITIDAS
        // ============================================
        
        // DESENVOLVIMENTO: Permite localhost em várias portas
        config.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",           // Qualquer porta local
                "http://127.0.0.1:*",           // Loopback
                "https://localhost:*"           // HTTPS local
        ));
        
        // PRODUÇÃO: Descomente e adicione suas origens
        // config.setAllowedOrigins(Arrays.asList(
        //     "https://seu-frontend.com",
        //     "https://app.sua-empresa.com"
        // ));
        
        // ============================================
        // MÉTODOS HTTP PERMITIDOS
        // ============================================
        
        config.setAllowedMethods(Arrays.asList(
                "GET",      // Consultas
                "POST",     // Criação
                "PUT",      // Atualização completa
                "PATCH",    // Atualização parcial
                "DELETE",   // Exclusão
                "OPTIONS"   // Preflight
        ));
        
        // ============================================
        // HEADERS PERMITIDOS
        // ============================================
        
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",        // Tokens de autenticação
                "Content-Type",         // Tipo de conteúdo
                "Accept",              // Tipo aceito
                "X-Requested-With",    // AJAX requests
                "X-API-Key",           // API Keys customizadas
                "Cache-Control"        // Controle de cache
        ));
        
        // ============================================
        // HEADERS EXPOSTOS AO CLIENTE
        // ============================================
        
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Total-Count",       // Total de registros
                "X-Page-Number",       // Número da página
                "X-Page-Size"          // Tamanho da página
        ));
        
        // ============================================
        // CONFIGURAÇÕES ADICIONAIS
        // ============================================
        
        // Permite envio de credenciais (cookies, auth headers)
        config.setAllowCredentials(true);
        
        // Tempo que o browser pode cachear a resposta preflight (1 hora)
        config.setMaxAge(3600L);
        
        // ============================================
        // APLICAR CONFIGURAÇÃO
        // ============================================
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // Aplica CORS a todos os endpoints
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}