package org.elas.momentum.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.elas.momentum.user.UserModuleAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * Après un login OAuth2 réussi (Google, Facebook…),
 * génère un JWT Momentum et redirige le navigateur vers
 * le frontend Angular avec le token en query param.
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserModuleAPI userModuleAPI;

    @Value("${momentum.oauth2.redirect-uri:http://localhost:4200/auth/callback}")
    private String redirectUri;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, UserModuleAPI userModuleAPI) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userModuleAPI = userModuleAPI;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        var oauthUser = (OAuth2User) authentication.getPrincipal();

        // Extraire les infos selon le provider (Google / Facebook / Apple)
        String email     = extractEmail(oauthUser);
        String firstName = extractAttribute(oauthUser, "given_name", "first_name", "name");
        String lastName  = extractAttribute(oauthUser, "family_name", "last_name", "");

        if (email == null) {
            log.warn("OAuth2 login sans email — accès refusé");
            getRedirectStrategy().sendRedirect(request, response,
                    redirectUri + "?error=no_email");
            return;
        }

        // Trouver ou créer l'utilisateur Momentum
        String userId = userModuleAPI.findOrCreateOAuthUser(email, firstName, lastName);

        // Générer le JWT Momentum
        String token = jwtTokenProvider.generateToken(userId, email, "USER");

        log.info("OAuth2 login OK — userId={} email={}", userId, email);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("userId", userId)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractEmail(OAuth2User user) {
        Object email = user.getAttribute("email");
        return email != null ? email.toString() : null;
    }

    private String extractAttribute(OAuth2User user, String... keys) {
        for (String key : keys) {
            Object val = user.getAttribute(key);
            if (val != null && !val.toString().isBlank()) return val.toString();
        }
        return "";
    }
}
