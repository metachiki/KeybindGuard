package dev.keybindguard.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds the one immutable keybinding snapshot taken before Minecraft begins
 * startup saves. The protection ends after the post-registration reload, so
 * later player edits are never rewritten by this class.
 */
public final class StartupKeybindProtection {
    private Path protectedFile;
    private Map<String, String> snapshot = Map.of();
    private boolean captured;
    private boolean released;

    /** Capture only the first existing options file seen during startup. */
    public synchronized void capture(Path optionsFile) throws IOException {
        if (released || captured || optionsFile == null || !Files.isRegularFile(optionsFile)) return;
        protectedFile = canonicalize(optionsFile);
        snapshot = new LinkedHashMap<>(KeybindFile.read(protectedFile));
        captured = true;
    }

    /** Restore only the file which supplied the startup snapshot. */
    public synchronized int restore(Path optionsFile) throws IOException {
        if (released || !captured || protectedFile == null || optionsFile == null
                || !protectedFile.equals(canonicalize(optionsFile))) return 0;
        return KeybindFile.restoreStartupSnapshot(protectedFile, snapshot);
    }

    /** End startup protection permanently for this client session. */
    public synchronized void release() {
        released = true;
        snapshot = Map.of();
        protectedFile = null;
    }

    private static Path canonicalize(Path path) {
        return path.toAbsolutePath().normalize();
    }
}
