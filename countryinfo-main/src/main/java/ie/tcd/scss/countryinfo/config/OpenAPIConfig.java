package ie.tcd.scss.countryinfo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Country Info API")
                        .version("1.0")
                        .description("API Documentation using SpringDoc OpenAPI")
                        .contact(new Contact()
                                .name("Insert Your Name Here")
                                .email("your.email@tcd.ie")))
                .addServersItem(new Server().url("http://localhost:8080")
                        .description("Local Development Server"));
    }
}