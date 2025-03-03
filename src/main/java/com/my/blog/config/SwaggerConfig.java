package com.my.blog.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Swagger（OpenAPI 3.0） Configuration class
 * Function: Automatically generate API documents and integrate JWT certification to facilitate front-end and back-end collaborative debugging
 * Access address: http://localhost:8080/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {

    /**
     * Customize OpenAPI configurations
     * @return OpenAPI instance containing document metadata and security authentication rules
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                //基础信息配置
                .info(new Info()
                        .title("Blogging system API documentation")
                        .version("1.0")
                        .description("User management and article management interface")
                        .contact(new Contact().name("lzy").email("uvau@qq.com")))
                //安全认证配置（JWT）
                .components(new Components()
                .addSecuritySchemes("JWT", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP) // The authentication type is HTTP
                        .scheme("bearer")               // use Bearer Token
                        .bearerFormat("JWT")            // the form of Token is JWT
                        .in(SecurityScheme.In.HEADER)   // The token is placed in the header of the request
                        .name("Authorization")))        // Request header field name
                // Enable JWT authentication globally
                .addSecurityItem(new SecurityRequirement().addList("JWT"));
    }

}
