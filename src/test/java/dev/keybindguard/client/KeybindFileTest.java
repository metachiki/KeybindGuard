package dev.keybindguard.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Standalone regression tests (no NeoForge dependency required). */
public final class KeybindFileTest {
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("keybindguard-test-");
        try {
            restoresMissingAndOverwrittenStartupMappings(directory.resolve("options.txt"));
            startupSnapshotIsFixedAndDoesNotTouchOtherProfiles(directory);
            missingFilesAreIgnored(directory.resolve("missing.txt"));
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

    private static void restoresMissingAndOverwrittenStartupMappings(Path path) throws Exception {
        Files.writeString(path, "fullscreen:false\nkey_key.mod.sample:key.keyboard.k\n"
                + "key_key.jump:key.keyboard.l\nresourcePacks:[\\\"vanilla\\\"]\n");
        Map<String, String> original = KeybindFile.read(path);
            assertEquals(2, original.size(), "read key mappings only");

        // Simulate an early save which deletes one mod key and writes defaults over another.
        Files.writeString(path, "fullscreen:true\nkey_key.jump:key.keyboard.space\n"
                + "key_key.new.mod:key.keyboard.g\nresourcePacks:[\\\"custom\\\"]\n");
        assertEquals(2, KeybindFile.restoreStartupSnapshot(path, original),
                "restore missing and overwritten startup mappings");
        String after = Files.readString(path);
        assertTrue(after.contains("key_key.mod.sample:key.keyboard.k"), "deleted custom key restored");
        assertTrue(after.contains("key_key.jump:key.keyboard.l"), "overwritten custom key restored");
        assertTrue(after.contains("key_key.new.mod:key.keyboard.g"), "new key left untouched");
        assertTrue(after.contains("fullscreen:true"), "unrelated option left untouched");
        assertTrue(after.contains("resourcePacks:[\\\"custom\\\"]"), "unrelated profile data left untouched");
        assertEquals(0, KeybindFile.restoreStartupSnapshot(path, original), "restore idempotent");

        // No trailing newline: appending missing mappings must create a line break.
        Files.writeString(path, "fullscreen:false");
        assertEquals(2, KeybindFile.restoreStartupSnapshot(path, original), "missing terminal newline");
        assertTrue(Files.readString(path).startsWith("fullscreen:false\nkey_"), "line separation");
    }

    private static void startupSnapshotIsFixedAndDoesNotTouchOtherProfiles(Path directory) throws Exception {
        Path protectedOptions = directory.resolve("options.txt");
        Path otherProfile = directory.resolve("other-options.txt");
        Files.writeString(protectedOptions, "key_key.mod.sample:key.keyboard.k\n");
        Files.writeString(otherProfile, "key_key.mod.sample:key.keyboard.p\n");

        StartupKeybindProtection protection = new StartupKeybindProtection();
        protection.capture(protectedOptions);
        Files.writeString(protectedOptions, "key_key.mod.sample:key.keyboard.space\n");
        protection.capture(protectedOptions);
        assertEquals(1, protection.restore(protectedOptions), "fixed snapshot restores original value");
        assertEquals(0, protection.restore(otherProfile), "other profile is never modified");
        assertTrue(Files.readString(otherProfile).contains("key.keyboard.p"), "other profile unchanged");

        protection.release();
        Files.writeString(protectedOptions, "key_key.mod.sample:key.keyboard.q\n");
        assertEquals(0, protection.restore(protectedOptions), "released protection allows player changes");
        assertTrue(Files.readString(protectedOptions).contains("key.keyboard.q"), "intentional change retained");
    }

    private static void missingFilesAreIgnored(Path missing) throws Exception {
        assertEquals(0, KeybindFile.read(missing).size(), "missing file");
        assertEquals(0, KeybindFile.restoreStartupSnapshot(missing,
                Map.of("key_key.mod.sample", "key_key.mod.sample:key.keyboard.k")), "missing file restore");
    }

    private static void assertTrue(boolean value, String reason) {
        if (!value) throw new AssertionError(reason);
    }
    private static void assertEquals(int expected, int actual, String reason) {
        if (expected != actual) throw new AssertionError(reason + ": expected " + expected + ", got " + actual);
    }
}
