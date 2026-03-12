package org.elas.momentum.user.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.config.JwtTokenProvider;
import org.elas.momentum.shared.web.ApiResponse;
import org.elas.momentum.user.application.dto.RegisterUserCommand;
import org.elas.momentum.user.application.dto.UpdateProfileCommand;
import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.port.in.GetUserUseCase;
import org.elas.momentum.user.domain.port.in.RegisterUserUseCase;
import org.elas.momentum.user.domain.port.in.UpdateAvatarUseCase;
import org.elas.momentum.user.domain.port.in.UpdateProfileUseCase;
import org.elas.momentum.user.infrastructure.web.dto.RegisterRequest;
import org.elas.momentum.user.infrastructure.web.dto.UpdateProfileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Gestion des profils utilisateurs")
public class UserController {

    private static final Path UPLOAD_DIR = Paths.get("uploads/avatars");

    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final UpdateAvatarUseCase updateAvatarUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(RegisterUserUseCase registerUserUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateProfileUseCase updateProfileUseCase,
                          UpdateAvatarUseCase updateAvatarUseCase,
                          JwtTokenProvider jwtTokenProvider) {
        this.registerUserUseCase = registerUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.updateAvatarUseCase = updateAvatarUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public record RegisterResponse(String accessToken, String userId) {}

    @PostMapping("/register")
    @Operation(summary = "Inscription d'un nouvel utilisateur — retourne un JWT")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        var command = new RegisterUserCommand(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName()
        );
        UserResult result = registerUserUseCase.register(command);
        String token = jwtTokenProvider.generateToken(result.id(), result.email(), "USER");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(new RegisterResponse(token, result.id())));
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

    @PostMapping(value = "/me/avatar", consumes = "multipart/form-data")
    @Operation(summary = "Upload / mise à jour de la photo de profil")
    public ResponseEntity<ApiResponse<UserResult>> uploadAvatar(
            @AuthenticationPrincipal String userId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Files.createDirectories(UPLOAD_DIR);
        String ext = getExtension(file.getOriginalFilename());
        String filename = userId + "_" + UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), UPLOAD_DIR.resolve(filename),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        String avatarUrl = "/uploads/avatars/" + filename;
        return ResponseEntity.ok(ApiResponse.ok(updateAvatarUseCase.updateAvatar(userId, avatarUrl)));
    }

    private String getExtension(String filename) {
        if (filename == null) return ".jpg";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot).toLowerCase() : ".jpg";
    }
}
