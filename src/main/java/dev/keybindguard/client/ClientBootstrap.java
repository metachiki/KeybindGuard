package dev.keybindguard.client;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Reload once, after mod key mappings have been registered. */
public final class ClientBootstrap {
    private static final Logger LOGGER = LogManager.getLogger("KeybindGuard");
    private static boolean reloaded;

    private ClientBootstrap() {}

    public static void install() {
        NeoForge.EVENT_BUS.addListener(ClientBootstrap::onFirstTick);
    }

    private static void onFirstTick(ClientTickEvent.Post ignored) {
        if (reloaded) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options == null) return;

        try {
            // The mixin still holds the pre-startup snapshot while this reload
            // applies it after mod key mappings have been registered.
            minecraft.options.load();
            LOGGER.info("Reapplied saved key mappings after mod registration");
        } catch (RuntimeException ex) {
            LOGGER.error("Unable to reload options.txt; check the settings file", ex);
        } finally {
            // Do not intercept ordinary saves or intentional player changes.
            StartupKeybindGuard.finishStartupProtection();
            reloaded = true;
        }
    }
}
