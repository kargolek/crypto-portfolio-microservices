package pl.kargolek.cryptopriceservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Karol Kuta-Orlowicz
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customConfig() {
        return new OpenAPI().info(new Info()
                .title("Crypto price service API documentation")
                .version("1.0.0")
                .description("Service created for store, manipulate and get cryptocurrency prices" +
                        " from http://coinmarketcap.com")
                .contact(new Contact()
                        .email("karol.orlowicz@gmail.com")));
    }

}
