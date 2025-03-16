package br.com.alexcosta.alexcosta.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica CORS apenas para endpoints sob /api
                .allowedOrigins("http://localhost:5173/","http://localhost:8080/", "https://alexcosta.vercel.app/") // Permite apenas o frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Métodos permitidos
                .allowedHeaders("Content-Type", "Authorization") // Cabeçalhos permitidos
                .allowCredentials(true); // Permite credenciais (cookies, tokens)
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        return resolver;
    }


}