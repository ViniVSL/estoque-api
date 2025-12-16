package com.estoque.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Nome da chave de segurança que será usada para referenciar o Bearer Token
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. Configuração da Documentação
                .info(new Info()
                        .title("API de Gestão de Estoque")
                        .description("Esta API é responsável por gerenciar produtos, entradas, saídas e alertas de estoque.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Seu Nome / Equipe")
                                .email("contato@exemplo.com.br"))
                        .license(new License()
                                .name("Licença Padrão API")
                                .url("http://www.seusite.com.br/licenca"))
                )

                // 2. Configuração do Bearer Token (Segurança)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Insira o token JWT no campo (Ex: Bearer <seu_token_aqui>)")
                        )
                );
    }
}