package br.com.aplrm.aplrm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableMethodSecurity(jsr250Enabled = true,prePostEnabled = false,securedEnabled = true)
@EnableScheduling
public class AplrmApplication{

	public static void main(String[] args) {
		SpringApplication.run(AplrmApplication.class, args);
	}
}