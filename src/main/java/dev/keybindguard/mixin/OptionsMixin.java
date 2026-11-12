package dev.keybindguard.mixin;

import dev.keybindguard.client.StartupKeybindGuard;
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
/**
 * Early saves can happen before NeoForge mod keys are registered. Keep one
 * startup snapshot, restore it across those saves, then reload it once after
 * registration. The protection is released before a player can edit controls.
 */
@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow private File optionsFile;

    @Unique private static final Logger KEYBINDGUARD_LOGGER = LogManager.getLogger("KeybindGuard");
    @Inject(method = "load", at = @At("HEAD"))
    private void keybindguard$captureOnLoad(CallbackInfo ci) {
        if (optionsFile == null) return;
        try {
            StartupKeybindGuard.capture(optionsFile.toPath());
        } catch (IOException e) {
            KEYBINDGUARD_LOGGER.warn("Could not snapshot options.txt key mappings", e);
        }
    }

    @Inject(method = "save", at = @At("TAIL"))
    private void keybindguard$restoreAfterSave(CallbackInfo ci) {
        if (optionsFile == null || !optionsFile.isFile()) return;
        try {
            int restored = StartupKeybindGuard.restore(optionsFile.toPath());
            if (restored > 0) {
                KEYBINDGUARD_LOGGER.debug("Restored {} startup key mapping(s) after an early save", restored);
            }
        } catch (IOException e) {
            KEYBINDGUARD_LOGGER.warn("Could not protect options.txt key mappings", e);
        }
    }
}
