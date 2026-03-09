package org.elas.momentum.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.elas.momentum.shared.exception.BusinessException;
import org.elas.momentum.shared.web.ApiResponse;
import org.elas.momentum.user.AuthenticateUserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentification")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase,
                          JwtTokenProvider jwtTokenProvider) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    @Operation(summary = "Connexion — retourne un JWT")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            var result = authenticateUserUseCase.authenticate(request.email(), request.password());
            String token = jwtTokenProvider.generateToken(result.userId(), result.email(), result.role());
            return ResponseEntity.ok(ApiResponse.ok(new LoginResponse(token, result.userId())));
        } catch (BusinessException ex) {
            int status = "ACCOUNT_SUSPENDED".equals(ex.getCode()) ? 403 : 401;
            return ResponseEntity.status(status)
                    .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
        }
    }

    public record LoginRequest(
            @NotBlank(message = "Email obligatoire") String email,
            @NotBlank(message = "Mot de passe obligatoire") String password
    ) {}

    public record LoginResponse(String accessToken, String userId) {}
}
