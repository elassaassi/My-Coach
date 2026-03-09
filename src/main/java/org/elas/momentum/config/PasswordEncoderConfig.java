package org.elas.momentum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Séparé de SecurityConfig pour éviter la référence circulaire
 * SecurityConfig → OAuth2Handler → UserModuleAPIImpl → PasswordEncoder → SecurityConfig.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
