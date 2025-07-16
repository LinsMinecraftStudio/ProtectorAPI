package io.github.lijinhong11.protector.block_impl.funnyguilds;

import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import net.dzikoysk.funnyguilds.feature.protection.ProtectionSystem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FunnyGuildsBlockProtectionModule implements IBlockProtectionModule {
    @Override
    public @NotNull String getPluginName() {
        return "FunnyGuilds";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return !ProtectionSystem.isProtected(player, block, false).isEmpty();
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return !isProtected(player, block);
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return ProtectionSystem.isProtected(player, block, true).isEmpty();
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return !isProtected(player, block);
    }
}
