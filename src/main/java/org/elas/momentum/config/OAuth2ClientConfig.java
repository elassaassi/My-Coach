package org.elas.momentum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers OAuth2 providers only when credentials are configured via env vars.
 * Allows the app to start in dev/CI without Google/Facebook credentials.
 *
 * Set GOOGLE_CLIENT_ID + GOOGLE_CLIENT_SECRET to enable Google login.
 * Set FACEBOOK_APP_ID + FACEBOOK_APP_SECRET to enable Facebook login.
 */
@Configuration
public class OAuth2ClientConfig {

    @Value("${GOOGLE_CLIENT_ID:}")
    private String googleClientId;

    @Value("${GOOGLE_CLIENT_SECRET:}")
    private String googleClientSecret;

    @Value("${FACEBOOK_APP_ID:}")
    private String facebookAppId;

    @Value("${FACEBOOK_APP_SECRET:}")
    private String facebookAppSecret;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();

        if (googleClientId != null && !googleClientId.isBlank()) {
            registrations.add(CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .scope("openid", "email", "profile")
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .build());
        }

        if (facebookAppId != null && !facebookAppId.isBlank()) {
            registrations.add(CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                    .clientId(facebookAppId)
                    .clientSecret(facebookAppSecret)
                    .scope("email", "public_profile")
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .build());
        }

        if (registrations.isEmpty()) {
            // No providers configured — return an empty-safe repository.
            // Attempts to initiate OAuth2 login will fail gracefully and redirect
            // back to the failure URL configured in SecurityConfig.
            return registrationId -> null;
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }
}
