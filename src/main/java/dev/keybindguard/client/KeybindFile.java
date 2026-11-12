package dev.keybindguard.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pure-Java persistence helper. Does not know about Minecraft classes.
 *
 * Minecraft writes keybinds as lines whose names start with "key_".
 * Keybind Guard only preserves missing key lines. Existing values are never
 * overwritten, so intentional user changes take precedence.
 */
public final class KeybindFile {
    private KeybindFile() {}

    public static Map<String, String> read(Path path) throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        if (!Files.isRegularFile(path)) return entries;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            int colon = line.indexOf(':');
            if (colon < 0) continue;
            String name = line.substring(0, colon);
            if (name.startsWith("key_") && !name.isBlank()) {
                entries.put(name, line);
            }
        }
        return entries;
    }

    /** Append only key names missing from the rewritten file. */
    public static int restoreMissing(Path path, Map<String, String> preserved) throws IOException {
        if (preserved.isEmpty() || !Files.isRegularFile(path)) return 0;
        Map<String, String> current = read(path);
        StringBuilder missing = new StringBuilder();
        int restored = 0;
        for (Map.Entry<String, String> entry : preserved.entrySet()) {
            String key = entry.getKey();
            if (!key.startsWith("key_") || current.containsKey(key)) continue;
            String rawLine = entry.getValue();
            if (rawLine == null || rawLine.contains("\n") || rawLine.contains("\r")
                    || !rawLine.startsWith(key + ":")) continue;
            missing.append(rawLine).append('\n');
            restored++;
        }
        if (restored > 0) {
            byte[] existing = Files.readAllBytes(path);
            boolean addNewline = existing.length > 0
                    && existing[existing.length - 1] != '\n'
                    && existing[existing.length - 1] != '\r';
            if (addNewline) missing.insert(0, '\n');
            Files.writeString(path, missing.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        }
        return restored;
    }
}
