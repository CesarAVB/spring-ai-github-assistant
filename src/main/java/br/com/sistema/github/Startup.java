package br.com.sistema.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
		System.out.println("🚀 GitHub Assistant rodando na porta 8081");
        System.out.println("📚 Swagger: http://localhost:8081/swagger-ui.html");
	}

}
