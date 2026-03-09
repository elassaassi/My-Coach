package org.elas.momentum.user.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.shared.web.ApiResponse;
import org.elas.momentum.user.application.dto.RegisterUserCommand;
import org.elas.momentum.user.application.dto.UpdateProfileCommand;
import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.port.in.GetUserUseCase;
import org.elas.momentum.user.domain.port.in.RegisterUserUseCase;
import org.elas.momentum.user.domain.port.in.UpdateProfileUseCase;
import org.elas.momentum.user.infrastructure.web.dto.RegisterRequest;
import org.elas.momentum.user.infrastructure.web.dto.UpdateProfileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Gestion des profils utilisateurs")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateProfileUseCase updateProfileUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
    }

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur")
    public ResponseEntity<ApiResponse<UserResult>> register(@Valid @RequestBody RegisterRequest request) {
        var command = new RegisterUserCommand(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName()
        );
        UserResult result = registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(result));
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté")
    public ResponseEntity<ApiResponse<UserResult>> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(getUserUseCase.getById(userId)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Profil d'un utilisateur par ID")
    public ResponseEntity<ApiResponse<UserResult>> getById(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.ok(getUserUseCase.getById(userId)));
    }

    @PutMapping("/me/profile")
    @Operation(summary = "Mise à jour du profil sportif")
    public ResponseEntity<ApiResponse<UserResult>> updateProfile(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody UpdateProfileRequest request) {

        List<UpdateProfileCommand.SportLevelDto> sportLevels = request.sports() == null
                ? java.util.List.of()
                : request.sports().stream()
                        .map(s -> new UpdateProfileCommand.SportLevelDto(s.sport(), s.proficiency(), s.yearsExperience()))
                        .collect(Collectors.toList());

        var command = new UpdateProfileCommand(
                request.firstName(),
                request.lastName(),
                sportLevels,
                request.latitude(),
                request.longitude(),
                request.city(),
                request.country()
        );

        return ResponseEntity.ok(ApiResponse.ok(updateProfileUseCase.updateProfile(userId, command)));
    }
}
