package org.elas.momentum.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers OAuth2 providers only when credentials are configured.
 * App starts without credentials — social login is simply unavailable.
 *
 * Set GOOGLE_CLIENT_ID + GOOGLE_CLIENT_SECRET to enable Google login.
 * Set FACEBOOK_APP_ID + FACEBOOK_APP_SECRET to enable Facebook login.
 */
@Configuration(proxyBeanMethods = false)
public class OAuth2ClientConfig {

    /**
     * Tracks which OAuth2 providers are actually configured.
     * Injected into SecurityConfig and AuthController to conditionally
     * enable the oauth2Login filter chain and expose a /providers endpoint.
     */
    public record OAuth2Availability(boolean googleEnabled, boolean facebookEnabled) {
        public boolean anyEnabled() { return googleEnabled || facebookEnabled; }
    }

    @Bean
    public OAuth2Availability oAuth2Availability(
            @Value("${GOOGLE_CLIENT_ID:}") String googleClientId,
            @Value("${FACEBOOK_APP_ID:}") String facebookAppId) {
        return new OAuth2Availability(!googleClientId.isBlank(), !facebookAppId.isBlank());
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            @Value("${GOOGLE_CLIENT_ID:}") String googleClientId,
            @Value("${GOOGLE_CLIENT_SECRET:}") String googleClientSecret,
            @Value("${FACEBOOK_APP_ID:}") String facebookAppId,
            @Value("${FACEBOOK_APP_SECRET:}") String facebookAppSecret) {

        List<ClientRegistration> registrations = new ArrayList<>();

        if (!googleClientId.isBlank()) {
            registrations.add(CommonOAuth2Provider.GOOGLE.getBuilder("google")
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .scope("openid", "email", "profile")
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .build());
        }

        if (!facebookAppId.isBlank()) {
            registrations.add(CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
                    .clientId(facebookAppId)
                    .clientSecret(facebookAppSecret)
                    .scope("email", "public_profile")
                    .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                    .build());
        }

        // When no credentials are set: return a no-op repository (safer than a lambda
        // in Spring Security 6.x — avoids NPE in OAuth2AuthorizationRequestRedirectFilter).
        if (registrations.isEmpty()) {
            return registrationId -> null;
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }
}
