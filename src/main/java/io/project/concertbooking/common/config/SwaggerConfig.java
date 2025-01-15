package io.project.concertbooking.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Concert Booking API Documentation")
                .description("콘서트 예약을 위한 다양한 기능을 제공하는 API입니다.")
                .contact(new Contact().name("depark").email("mem29238@gmail.com"))
                .version("1.0.0");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
