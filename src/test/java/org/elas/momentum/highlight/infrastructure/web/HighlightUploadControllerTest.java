package org.elas.momentum.highlight.infrastructure.web;

import org.elas.momentum.highlight.domain.port.in.*;
import org.elas.momentum.shared.web.GlobalExceptionHandler;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HighlightUploadControllerTest {

    @TempDir Path tmpDir;

    @Mock PublishHighlightUseCase  publishHighlightUseCase;
    @Mock LikeHighlightUseCase     likeHighlightUseCase;
    @Mock GetHighlightUseCase      getHighlightUseCase;
    @Mock GetHighlightOfDayUseCase getHighlightOfDayUseCase;
    @Mock DeleteHighlightUseCase   deleteHighlightUseCase;
    @Mock UpdateHighlightUseCase   updateHighlightUseCase;
    @Mock ArchiveHighlightUseCase  archiveHighlightUseCase;
    @Mock AddCommentUseCase        addCommentUseCase;
    @Mock GetCommentsUseCase       getCommentsUseCase;

    MockMvc mockMvc;

    // ── magic bytes ───────────────────────────────────────────────────────────
    private static final byte[] JPEG_MAGIC =
            {(byte)0xFF, (byte)0xD8, (byte)0xFF, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] PNG_MAGIC =
            {(byte)0x89, 0x50, 0x4E, 0x47, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] PDF_MAGIC =
            {0x25, 0x50, 0x44, 0x46, 0, 0, 0, 0, 0, 0, 0, 0}; // %PDF
    // MP4 magic: ftyp box at offset 4 (66 74 79 70)
    private static final byte[] MP4_MAGIC =
            {0, 0, 0, 0x18, 0x66, 0x74, 0x79, 0x70, 0x6D, 0x70, 0x34, 0x32};

    @BeforeEach
    void setUp() {
        var controller = new HighlightController(
                publishHighlightUseCase, likeHighlightUseCase, getHighlightUseCase,
                getHighlightOfDayUseCase, deleteHighlightUseCase, updateHighlightUseCase,
                archiveHighlightUseCase, addCommentUseCase, getCommentsUseCase);
        ReflectionTestUtils.setField(controller, "uploadDir", tmpDir.toString());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ── valid uploads ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("JPEG valide → 200, url commence par /uploads/")
    void upload_validJpeg_returns200WithUrl() throws Exception {
        var file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", JPEG_MAGIC);

        mockMvc.perform(multipart("/api/v1/highlights/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.url").value(Matchers.startsWith("/uploads/")))
                .andExpect(jsonPath("$.data.url").value(Matchers.endsWith(".jpg")));
    }

    @Test
    @DisplayName("PNG valide → 200, extension .png")
    void upload_validPng_returns200WithPngExtension() throws Exception {
        var file = new MockMultipartFile("file", "photo.png", "image/png", PNG_MAGIC);

        mockMvc.perform(multipart("/api/v1/highlights/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value(Matchers.endsWith(".png")));
    }

    // ── rejection cases ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Fichier vide → 400 EMPTY_FILE")
    void upload_emptyFile_returns400EmptyFile() throws Exception {
        var file = new MockMultipartFile("file", "empty.jpg", "image/jpeg", new byte[0]);

        mockMvc.perform(multipart("/api/v1/highlights/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("EMPTY_FILE"));
    }

    @Test
    @DisplayName("Magic bytes PDF → 400 INVALID_FILE_TYPE (pas d'extension client spoofing)")
    void upload_pdfMaskedAsJpeg_returns400InvalidType() throws Exception {
        // Client sends Content-Type image/jpeg but the actual bytes are PDF
        var file = new MockMultipartFile("file", "hack.jpg", "image/jpeg", PDF_MAGIC);

        mockMvc.perform(multipart("/api/v1/highlights/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE_TYPE"));
    }

    @Test
    @DisplayName("Données aléatoires → 400 INVALID_FILE_TYPE")
    void upload_randomBytes_returns400InvalidType() throws Exception {
        byte[] random = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B};
        var file = new MockMultipartFile("file", "data.bin", "application/octet-stream", random);

        mockMvc.perform(multipart("/api/v1/highlights/upload").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FILE_TYPE"));
    }

    // ── video path ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("MP4 valide — 200 avec url (fallback si ffmpeg absent, transcrit sinon)")
    void upload_validMp4_returns200WithUrl() throws Exception {
        var file = new MockMultipartFile("file", "video.mp4", "video/mp4", MP4_MAGIC);

        mockMvc.perform(multipart("/api/v1/highlights/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.url").value(Matchers.startsWith("/uploads/")))
                .andExpect(jsonPath("$.data.url").value(Matchers.endsWith(".mp4")));
    }

    @Test
    @DisplayName("MP4 masqué en JPEG (magic bytes MP4) → traité comme vidéo, pas comme image")
    void upload_mp4MaskedAsJpeg_treatedAsVideo() throws Exception {
        // Client sends Content-Type image/jpeg, but bytes are MP4 — type must be derived from magic bytes
        var file = new MockMultipartFile("file", "trick.jpg", "image/jpeg", MP4_MAGIC);

        mockMvc.perform(multipart("/api/v1/highlights/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value(Matchers.endsWith(".mp4")));
    }
}