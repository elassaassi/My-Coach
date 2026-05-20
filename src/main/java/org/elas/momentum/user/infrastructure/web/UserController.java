package org.elas.momentum.user.infrastructure.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.elas.momentum.config.TokenIssuer;
import org.elas.momentum.shared.FileValidator;
import org.elas.momentum.shared.web.ApiResponse;
import org.elas.momentum.user.application.dto.RegisterUserCommand;
import org.elas.momentum.user.application.dto.UpdateProfileCommand;
import org.elas.momentum.user.application.dto.UserResult;
import org.elas.momentum.user.domain.port.in.GetUserUseCase;
import org.elas.momentum.user.domain.port.in.RegisterUserUseCase;
import org.elas.momentum.user.domain.port.in.UpdateAvatarUseCase;
import org.elas.momentum.user.domain.port.in.UpdateProfileUseCase;
import org.elas.momentum.user.infrastructure.web.dto.PublicUserResult;
import org.elas.momentum.user.infrastructure.web.dto.RegisterRequest;
import org.elas.momentum.user.infrastructure.web.dto.UpdateProfileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Gestion des profils utilisateurs")
public class UserController {

    private static final Path UPLOAD_DIR = Paths.get("uploads/avatars").toAbsolutePath().normalize();
    private static final long MAX_AVATAR_BYTES = 5 * 1024 * 1024L; // 5 MB

    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final UpdateAvatarUseCase updateAvatarUseCase;
    private final TokenIssuer tokenIssuer;

    public UserController(RegisterUserUseCase registerUserUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateProfileUseCase updateProfileUseCase,
                          UpdateAvatarUseCase updateAvatarUseCase,
                          TokenIssuer tokenIssuer) {
        this.registerUserUseCase = registerUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateProfileUseCase = updateProfileUseCase;
        this.updateAvatarUseCase = updateAvatarUseCase;
        this.tokenIssuer = tokenIssuer;
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
        String token = tokenIssuer.generateToken(result.id(), result.email(), "USER");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(new RegisterResponse(token, result.id())));
    }

    @GetMapping("/me")
    @Operation(summary = "Profil de l'utilisateur connecté")
    public ResponseEntity<ApiResponse<UserResult>> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(ApiResponse.ok(getUserUseCase.getById(userId)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Profil public d'un utilisateur (email et coordonnées GPS exclus)")
    public ResponseEntity<ApiResponse<PublicUserResult>> getById(
            @AuthenticationPrincipal String requesterId,
            @PathVariable String userId) {
        var full = getUserUseCase.getById(userId);
        return ResponseEntity.ok(ApiResponse.ok(PublicUserResult.from(full)));
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

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("EMPTY_FILE", "Le fichier est vide"));
        }
        if (file.getSize() > MAX_AVATAR_BYTES) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("FILE_TOO_LARGE", "Taille maximale : 5 Mo"));
        }

        byte[] magic = readMagicBytes(file.getInputStream());
        String ext = FileValidator.imageExtension(magic);
        if (ext == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_FILE_TYPE", "Format non autorisé. Acceptés : JPG, PNG, WEBP, GIF"));
        }

        Files.createDirectories(UPLOAD_DIR);
        String filename = UUID.randomUUID() + ext;
        Path dest = UPLOAD_DIR.resolve(filename).normalize();
        if (!dest.startsWith(UPLOAD_DIR)) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("INVALID_FILE", "Chemin de fichier invalide"));
        }
        file.transferTo(dest);

        String avatarUrl = "/uploads/avatars/" + filename;
        return ResponseEntity.ok(ApiResponse.ok(updateAvatarUseCase.updateAvatar(userId, avatarUrl)));
    }

    private static byte[] readMagicBytes(InputStream is) throws IOException {
        try (is) {
            return is.readNBytes(FileValidator.MAGIC_BYTES_LENGTH);
        }
    }
}
