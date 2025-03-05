package br.com.alexcosta.alexcosta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(jsr250Enabled = true,prePostEnabled = false,securedEnabled = true)
@EnableScheduling
public class AlexCostaApplication{

	public static void main(String[] args) {
		SpringApplication.run(AlexCostaApplication.class, args);
	}
}