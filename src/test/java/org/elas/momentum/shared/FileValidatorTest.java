package org.elas.momentum.shared;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FileValidatorTest {

    // ── magic byte helpers ────────────────────────────────────────────────────

    private static byte[] jpeg()    { return new byte[]{(byte)0xFF,(byte)0xD8,(byte)0xFF,0,0,0,0,0,0,0,0,0}; }
    private static byte[] png()     { return new byte[]{(byte)0x89,0x50,0x4E,0x47,0,0,0,0,0,0,0,0}; }
    private static byte[] webp()    { return new byte[]{0x52,0x49,0x46,0x46,0,0,0,0,0x57,0x45,0x42,0x50}; }
    private static byte[] gif()     { return new byte[]{0x47,0x49,0x46,0x38,0,0,0,0,0,0,0,0}; }
    private static byte[] mp4()     { return new byte[]{0,0,0,0,0x66,0x74,0x79,0x70,0,0,0,0}; }
    private static byte[] webm()    { return new byte[]{0x1A,0x45,(byte)0xDF,(byte)0xA3,0,0,0,0,0,0,0,0}; }
    private static byte[] avi()     { return new byte[]{0x52,0x49,0x46,0x46,0,0,0,0,0x41,0x56,0x49,0x20}; }
    private static byte[] pdf()     { return new byte[]{0x25,0x50,0x44,0x46,0,0,0,0,0,0,0,0}; }
    private static byte[] empty()   { return new byte[0]; }
    private static byte[] tooShort(){ return new byte[]{(byte)0xFF,(byte)0xD8}; }

    // ── MAGIC_BYTES_LENGTH ────────────────────────────────────────────────────

    @Test
    @DisplayName("MAGIC_BYTES_LENGTH est 12")
    void magicBytesLength_is12() {
        assertThat(FileValidator.MAGIC_BYTES_LENGTH).isEqualTo(12);
    }

    // ── isAllowedImage ────────────────────────────────────────────────────────

    @Test @DisplayName("isAllowedImage: JPEG reconnu")
    void isAllowedImage_jpeg()  { assertThat(FileValidator.isAllowedImage(jpeg())).isTrue(); }

    @Test @DisplayName("isAllowedImage: PNG reconnu")
    void isAllowedImage_png()   { assertThat(FileValidator.isAllowedImage(png())).isTrue(); }

    @Test @DisplayName("isAllowedImage: WebP reconnu")
    void isAllowedImage_webp()  { assertThat(FileValidator.isAllowedImage(webp())).isTrue(); }

    @Test @DisplayName("isAllowedImage: GIF reconnu")
    void isAllowedImage_gif()   { assertThat(FileValidator.isAllowedImage(gif())).isTrue(); }

    @Test @DisplayName("isAllowedImage: PDF rejeté")
    void isAllowedImage_pdf()   { assertThat(FileValidator.isAllowedImage(pdf())).isFalse(); }

    @Test @DisplayName("isAllowedImage: MP4 rejeté")
    void isAllowedImage_mp4()   { assertThat(FileValidator.isAllowedImage(mp4())).isFalse(); }

    @Test @DisplayName("isAllowedImage: tableau vide rejeté")
    void isAllowedImage_empty() { assertThat(FileValidator.isAllowedImage(empty())).isFalse(); }

    @Test @DisplayName("isAllowedImage: trop court rejeté (2 octets)")
    void isAllowedImage_tooShort() { assertThat(FileValidator.isAllowedImage(tooShort())).isFalse(); }

    // ── isAllowedVideo ────────────────────────────────────────────────────────

    @Test @DisplayName("isAllowedVideo: MP4 reconnu")
    void isAllowedVideo_mp4()   { assertThat(FileValidator.isAllowedVideo(mp4())).isTrue(); }

    @Test @DisplayName("isAllowedVideo: WebM reconnu")
    void isAllowedVideo_webm()  { assertThat(FileValidator.isAllowedVideo(webm())).isTrue(); }

    @Test @DisplayName("isAllowedVideo: AVI reconnu")
    void isAllowedVideo_avi()   { assertThat(FileValidator.isAllowedVideo(avi())).isTrue(); }

    @Test @DisplayName("isAllowedVideo: JPEG rejeté")
    void isAllowedVideo_jpeg()  { assertThat(FileValidator.isAllowedVideo(jpeg())).isFalse(); }

    @Test @DisplayName("isAllowedVideo: PDF rejeté")
    void isAllowedVideo_pdf()   { assertThat(FileValidator.isAllowedVideo(pdf())).isFalse(); }

    @Test @DisplayName("isAllowedVideo: tableau vide rejeté")
    void isAllowedVideo_empty() { assertThat(FileValidator.isAllowedVideo(empty())).isFalse(); }

    // ── imageExtension ────────────────────────────────────────────────────────

    @Test @DisplayName("imageExtension: JPEG → .jpg")
    void imageExtension_jpeg()  { assertThat(FileValidator.imageExtension(jpeg())).isEqualTo(".jpg"); }

    @Test @DisplayName("imageExtension: PNG → .png")
    void imageExtension_png()   { assertThat(FileValidator.imageExtension(png())).isEqualTo(".png"); }

    @Test @DisplayName("imageExtension: WebP → .webp")
    void imageExtension_webp()  { assertThat(FileValidator.imageExtension(webp())).isEqualTo(".webp"); }

    @Test @DisplayName("imageExtension: GIF → .gif")
    void imageExtension_gif()   { assertThat(FileValidator.imageExtension(gif())).isEqualTo(".gif"); }

    @Test @DisplayName("imageExtension: inconnu → null")
    void imageExtension_unknown(){ assertThat(FileValidator.imageExtension(pdf())).isNull(); }

    // ── videoExtension ────────────────────────────────────────────────────────

    @Test @DisplayName("videoExtension: MP4 → .mp4")
    void videoExtension_mp4()   { assertThat(FileValidator.videoExtension(mp4())).isEqualTo(".mp4"); }

    @Test @DisplayName("videoExtension: WebM → .webm")
    void videoExtension_webm()  { assertThat(FileValidator.videoExtension(webm())).isEqualTo(".webm"); }

    @Test @DisplayName("videoExtension: AVI → .avi")
    void videoExtension_avi()   { assertThat(FileValidator.videoExtension(avi())).isEqualTo(".avi"); }

    @Test @DisplayName("videoExtension: inconnu → null")
    void videoExtension_unknown(){ assertThat(FileValidator.videoExtension(jpeg())).isNull(); }

    // ── spoofing : WebP qui ressemble à AVI (même préfixe RIFF) ──────────────

    @Test
    @DisplayName("WebP et AVI partagent RIFF mais sont bien distingués")
    void webp_vs_avi_distinguished() {
        assertThat(FileValidator.isAllowedImage(webp())).isTrue();
        assertThat(FileValidator.isAllowedVideo(webp())).isFalse();
        assertThat(FileValidator.isAllowedVideo(avi())).isTrue();
        assertThat(FileValidator.isAllowedImage(avi())).isFalse();
    }
}