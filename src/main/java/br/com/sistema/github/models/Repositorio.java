package br.com.sistema.github.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repositorio {
    
    private Long id;
    private String nome;
    private String descricao;
    private String url;
    private String linguagem;
    private Integer stars;
    private Integer forks;
    private Integer issuesAbertas;
    private String dataCriacao;
    private String dataAtualizacao;
    private Boolean privado;
}