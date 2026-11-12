package dev.keybindguard.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Standalone regression tests (no NeoForge dependency required). */
public final class KeybindFileTest {
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("keybindguard-test-");
        try {
            Path path = directory.resolve("options.txt");
            Files.writeString(path, "fullscreen:false\nkey_key.mod.sample:key.keyboard.k\n"
                    + "key_key.jump:key.keyboard.space\n");
            Map<String, String> original = KeybindFile.read(path);
            assertEquals(2, original.size(), "read key mappings only");

            // Simulate an early vanilla save which only knows the vanilla jump key.
            Files.writeString(path, "fullscreen:false\nkey_key.jump:key.keyboard.l\n");
            assertEquals(1, KeybindFile.restoreMissing(path, original), "restore unknown mod key");
            String after = Files.readString(path);
            assertTrue(after.contains("key_key.mod.sample:key.keyboard.k"), "custom key retained");
            assertTrue(after.contains("key_key.jump:key.keyboard.l"), "new vanilla value retained");
            assertTrue(!after.contains("key_key.jump:key.keyboard.space"), "old value not reinstated");
            assertEquals(0, KeybindFile.restoreMissing(path, original), "restore idempotent");

            // No trailing newline: restore must create a line break.
            Files.writeString(path, "fullscreen:false");
            assertEquals(2, KeybindFile.restoreMissing(path, original), "missing terminal newline");
            assertTrue(Files.readString(path).startsWith("fullscreen:false\nkey_"), "line separation");

            // Missing or unrelated options must not be invented.
            assertEquals(0, KeybindFile.read(directory.resolve("missing.txt")).size(), "missing file");
            assertEquals(0, KeybindFile.restoreMissing(directory.resolve("missing.txt"), original), "missing file restore");
            System.out.println("KeybindFile regression tests passed");
        } finally {
            try (var files = Files.walk(directory)) {
                files.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                    try { Files.deleteIfExists(p); }
                    catch (Exception e) { throw new RuntimeException(e); }
                });
            }
        }
    }

    private static void assertTrue(boolean value, String reason) {
        if (!value) throw new AssertionError(reason);
    }
    private static void assertEquals(int expected, int actual, String reason) {
        if (expected != actual) throw new AssertionError(reason + ": expected " + expected + ", got " + actual);
    }
}
