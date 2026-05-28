package com.tpa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!prod")
public class OpenApiConfig {

    private static final String SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("TPA Insurance Claim API")
                        .version("1.0")
                        .description("AI Powered - Third Party Administrator, Insurance Claim Processing System")
                        .contact(new Contact().name("mithun").email("mithun-io@outlook.com"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT")))

                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("https://docs.tpa.com"))

                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Environment")))

                .tags(List.of(
                        new Tag()
                                .name("Authentication")
                                .description("Authentication and Authorization APIs"),
                        new Tag()
                                .name("Claims")
                                .description("Insurance Claim Management APIs"),
                        new Tag()
                                .name("Payments")
                                .description("Payment and Settlement APIs")))

                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME))

                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token in the format: Bearer eyJhbGciOiJIUzI1NiJ9...")));
    }
}