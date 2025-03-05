    package br.com.alexcosta.alexcosta.security;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import static org.springframework.security.config.Customizer.withDefaults;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfigurations {

        @Autowired
        private SecurityFilter securityFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeRequests(authorizeRequests -> authorizeRequests
                            .requestMatchers("/publico/**").permitAll()
                            .requestMatchers("/usuarios/*/delete").hasAuthority("ADMIN")
                            .requestMatchers("/usuarios/senha","/usuarios/email","/usuarios/endereco","usuarios/*/atualizar").authenticated()
                            .requestMatchers("usuarios/cadastro").permitAll()
                            .requestMatchers("/produtos/filtro/**").permitAll()
                            .requestMatchers("/produtos/buscar","/produtos/{id}","/produtos/buscarpornome", "/produtos/lista","produtos/procurarCategoria").permitAll()
                            .requestMatchers("/produtos/**", "/produtos/paginarTodos","/produtos/*/deletar","/produtos/*/atualizar").hasAuthority("ADMIN")
                            .anyRequest().permitAll()
                    )
                    .headers(headers -> headers
                            .frameOptions(frameOptions -> frameOptions.disable())
                    )
                    .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                    .cors(withDefaults());

            return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
