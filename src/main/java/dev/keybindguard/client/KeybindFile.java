package dev.keybindguard.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Pure-Java persistence helper. Does not know about Minecraft classes.
 *
 * Minecraft writes keybinds as lines whose names start with "key_".
 * This helper only changes those lines and preserves every other option line
 * verbatim.
 */
public final class KeybindFile {
    private KeybindFile() {}

    public static Map<String, String> read(Path path) throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        if (!Files.isRegularFile(path)) return entries;
        for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
            String name = keyName(line);
            if (name != null) entries.put(name, line);
        }
        return entries;
    }

    /**
     * Restore a startup snapshot after an early save has discarded or replaced
     * mappings which were already present in {@code options.txt}.
     *
     * <p>This deliberately restores existing key lines as well as missing ones:
     * an early {@code Options.save()} can write a mod key's default binding in
     * place of the user's stored binding. It is intended only for the short
     * startup protection window; callers must disable it before the player can
     * edit key bindings.</p>
     *
     * @return the number of key lines restored or appended
     */
    public static int restoreStartupSnapshot(Path path, Map<String, String> preserved) throws IOException {
        if (preserved.isEmpty() || !Files.isRegularFile(path)) return 0;

        String current = Files.readString(path, StandardCharsets.UTF_8);
        StringBuilder output = new StringBuilder(current.length());
        Set<String> present = new HashSet<>();
        int restoredCount = 0;

        int offset = 0;
        while (offset < current.length()) {
            int lineEnd = offset;
            while (lineEnd < current.length()) {
                char character = current.charAt(lineEnd);
                if (character == '\n' || character == '\r') break;
                lineEnd++;
            }
            int lineBreakEnd = lineEnd;
            if (lineBreakEnd < current.length() && current.charAt(lineBreakEnd) == '\r') {
                lineBreakEnd++;
                if (lineBreakEnd < current.length() && current.charAt(lineBreakEnd) == '\n') lineBreakEnd++;
            } else if (lineBreakEnd < current.length()) {
                lineBreakEnd++;
            }

            String line = current.substring(offset, lineEnd);
            String key = keyName(line);
            String savedLine = key == null ? null : validSavedLine(key, preserved.get(key));
            if (savedLine != null) {
                present.add(key);
                if (!line.equals(savedLine)) {
                    output.append(savedLine);
                    restoredCount++;
                } else {
                    output.append(line);
                }
            } else {
                output.append(line);
            }
            output.append(current, lineEnd, lineBreakEnd);
            offset = lineBreakEnd;
        }

        for (Map.Entry<String, String> entry : preserved.entrySet()) {
            String key = entry.getKey();
            String savedLine = validSavedLine(key, entry.getValue());
            if (savedLine == null || present.contains(key)) continue;
            if (output.length() > 0 && !endsWithLineBreak(output)) output.append('\n');
            output.append(savedLine).append('\n');
            restoredCount++;
        }

        if (restoredCount > 0) Files.writeString(path, output.toString(), StandardCharsets.UTF_8);
        return restoredCount;
    }

    private static String keyName(String line) {
        int colon = line.indexOf(':');
        if (colon <= 0) return null;
        String name = line.substring(0, colon);
        return name.startsWith("key_") && !name.isBlank() ? name : null;
    }

    private static String validSavedLine(String key, String line) {
        if (key == null || !key.startsWith("key_") || line == null
                || line.contains("\n") || line.contains("\r") || !line.startsWith(key + ":")) {
            return null;
        }
        return line;
    }

    private static boolean endsWithLineBreak(StringBuilder text) {
        char last = text.charAt(text.length() - 1);
        return last == '\n' || last == '\r';
    }
}
