package dev.keybindguard;

import dev.keybindguard.client.ClientBootstrap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/** Client utility. Dedicated servers do not initialize any client code. */
@Mod("keybindguard")
public final class KeybindGuard {
    public KeybindGuard(IEventBus modBus, Dist dist) {
        if (dist.isClient()) {
            ClientBootstrap.install();
        }
    }
}
