package org.elas.momentum.shared;

/**
 * Validates uploaded files by reading their magic bytes (first 12 octets).
 * Never trust client-supplied Content-Type or file extension.
 */
public final class FileValidator {

    public static final int MAGIC_BYTES_LENGTH = 12;

    private FileValidator() {}

    // ── Public API ────────────────────────────────────────────────────────────

    public static boolean isAllowedImage(byte[] header) {
        return isJpeg(header) || isPng(header) || isWebp(header) || isGif(header);
    }

    public static boolean isAllowedVideo(byte[] header) {
        return isMp4OrMov(header) || isWebm(header) || isAvi(header);
    }

    /** Returns the canonical extension for the detected image type, or null if not recognised. */
    public static String imageExtension(byte[] header) {
        if (isJpeg(header)) return ".jpg";
        if (isPng(header))  return ".png";
        if (isWebp(header)) return ".webp";
        if (isGif(header))  return ".gif";
        return null;
    }

    /** Returns the canonical extension for the detected video type, or null if not recognised. */
    public static String videoExtension(byte[] header) {
        if (isMp4OrMov(header)) return ".mp4";
        if (isWebm(header))     return ".webm";
        if (isAvi(header))      return ".avi";
        return null;
    }

    // ── Magic bytes ───────────────────────────────────────────────────────────

    // FF D8 FF
    private static boolean isJpeg(byte[] h) {
        return h.length >= 3
                && b(h, 0) == 0xFF && b(h, 1) == 0xD8 && b(h, 2) == 0xFF;
    }

    // 89 50 4E 47 (‰PNG)
    private static boolean isPng(byte[] h) {
        return h.length >= 4
                && b(h, 0) == 0x89 && b(h, 1) == 0x50 && b(h, 2) == 0x4E && b(h, 3) == 0x47;
    }

    // RIFF....WEBP
    private static boolean isWebp(byte[] h) {
        return h.length >= 12
                && b(h, 0) == 0x52 && b(h, 1) == 0x49 && b(h, 2) == 0x46 && b(h, 3) == 0x46
                && b(h, 8) == 0x57 && b(h, 9) == 0x45 && b(h, 10) == 0x42 && b(h, 11) == 0x50;
    }

    // GIF8
    private static boolean isGif(byte[] h) {
        return h.length >= 4
                && b(h, 0) == 0x47 && b(h, 1) == 0x49 && b(h, 2) == 0x46 && b(h, 3) == 0x38;
    }

    // MP4/MOV: ISO Base Media (ftyp box at offset 4 → bytes 4-7 = 66 74 79 70)
    private static boolean isMp4OrMov(byte[] h) {
        return h.length >= 8
                && b(h, 4) == 0x66 && b(h, 5) == 0x74 && b(h, 6) == 0x79 && b(h, 7) == 0x70;
    }

    // WebM: EBML header 1A 45 DF A3
    private static boolean isWebm(byte[] h) {
        return h.length >= 4
                && b(h, 0) == 0x1A && b(h, 1) == 0x45 && b(h, 2) == 0xDF && b(h, 3) == 0xA3;
    }

    // RIFF....AVI (space = 0x20)
    private static boolean isAvi(byte[] h) {
        return h.length >= 12
                && b(h, 0) == 0x52 && b(h, 1) == 0x49 && b(h, 2) == 0x46 && b(h, 3) == 0x46
                && b(h, 8) == 0x41 && b(h, 9) == 0x56 && b(h, 10) == 0x49 && b(h, 11) == 0x20;
    }

    private static int b(byte[] arr, int i) {
        return arr[i] & 0xFF;
    }
}