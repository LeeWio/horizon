package com.sunrizon.horizon.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI (Swagger) configuration class.
 * <p>
 * This class sets up the OpenAPI specification for the Horizon application,
 * including metadata such as title, description, version, contact, and license.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Configures the global OpenAPI specification for the project.
   *
   * @return {@link OpenAPI} object used by SpringDoc to generate the Swagger UI.
   */
  @Bean
  public OpenAPI horizonOpenAPI() {
    return new OpenAPI()
        // API Info
        .info(new Info()
            .title("Horizon API")
            .description("REST API documentation for the Horizon blogging system")
            .version("v1.0.0")
            .contact(new Contact()
                .name("Sunrizon Team")
                .url("https://www.sunrizon.com")
                .email("support@sunrizon.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
        // Server URLs
        .servers(List.of(
            new Server().url("http://localhost:8080").description("Local Dev Server"),
            new Server().url("https://api.sunrizon.com").description("Production Server")))
        // External documentation (optional)
        .externalDocs(new ExternalDocumentation()
            .description("Project Wiki")
            .url("https://wiki.sunrizon.com"));
  }
}
