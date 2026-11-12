package dev.keybindguard.client;

import java.io.IOException;
import java.nio.file.Path;

/** Shared client-session startup guard used by the Options mixin and bootstrap. */
public final class StartupKeybindGuard {
    private static final StartupKeybindProtection PROTECTION = new StartupKeybindProtection();

    private StartupKeybindGuard() {}

    public static void capture(Path optionsFile) throws IOException {
        PROTECTION.capture(optionsFile);
    }

    public static int restore(Path optionsFile) throws IOException {
        return PROTECTION.restore(optionsFile);
    }

    public static void finishStartupProtection() {
        PROTECTION.release();
    }
}
