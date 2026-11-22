package cl.duoc.msHabito.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerInfo() {
        return new OpenAPI().info(new Info()
                .title("PagoFlex - Microservicio de Hábitos")
                .version("1.0.0")
                .description("Avisos de vencimiento, recomendaciones y reconocimientos por pagar a tiempo."));
    }
}
