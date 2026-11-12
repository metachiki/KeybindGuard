package dev.keybindguard.mixin;

import dev.keybindguard.client.KeybindFile;
import net.minecraft.client.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Early saves can happen before NeoForge mod keys are registered. Preserve
 * their raw values across saves, then the client bootstrap reloads once.
 */
@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow private File optionsFile;

    @Unique private static final Logger KEYBINDGUARD_LOGGER = LogManager.getLogger("KeybindGuard");
    @Unique private final Map<String, String> keybindguard$previous = new LinkedHashMap<>();

    @Inject(method = "load", at = @At("HEAD"))
    private void keybindguard$captureOnLoad(CallbackInfo ci) {
        keybindguard$capture();
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void keybindguard$captureBeforeSave(CallbackInfo ci) {
        keybindguard$capture();
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void keybindguard$restoreAfterSave(CallbackInfo ci) {
        if (optionsFile == null || !optionsFile.isFile()) return;
        try {
            int restored = KeybindFile.restoreMissing(optionsFile.toPath(), keybindguard$previous);
            if (restored > 0) {
                KEYBINDGUARD_LOGGER.debug("Protected {} key mapping(s) during early save", restored);
            }
            // Refresh the snapshot: changes saved by Minecraft now take precedence.
            keybindguard$previous.clear();
            keybindguard$previous.putAll(KeybindFile.read(optionsFile.toPath()));
        } catch (IOException e) {
            KEYBINDGUARD_LOGGER.warn("Could not protect options.txt key mappings", e);
        }
    }

    @Unique
    private void keybindguard$capture() {
        if (optionsFile == null || !optionsFile.isFile()) return;
        try {
            keybindguard$previous.putAll(KeybindFile.read(optionsFile.toPath()));
        } catch (IOException e) {
            KEYBINDGUARD_LOGGER.warn("Could not read options.txt key mappings", e);
        }
    }
}
