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

        // Prevent retries from accidentally overwriting other runtime changes.
        reloaded = true;
        try {
            minecraft.options.load();
            LOGGER.info("Reapplied saved key mappings after mod registration");
        } catch (RuntimeException ex) {
            LOGGER.error("Unable to reload options.txt; check the settings file", ex);
        }
    }
}
