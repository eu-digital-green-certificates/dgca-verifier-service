package eu.europa.ec.dgc.verifier.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Generated
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final BuildProperties buildProperties;

    /**
     * Configure the OpenApi bean with title and version.
     *
     * @return the OpenApi bean.
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Digital Green Certificate Verifier Service")
                .description("The API defines how to exchange verification information for digital green certificates.")
                .version(buildProperties.getVersion())
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
