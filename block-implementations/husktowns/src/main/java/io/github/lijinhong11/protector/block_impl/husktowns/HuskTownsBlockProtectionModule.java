package io.github.lijinhong11.protector.block_impl.husktowns;

import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import io.github.lijinhong11.protector.api.flag.CustomFlag;
import io.github.lijinhong11.protector.api.flag.FlagRegisterable;
import net.kyori.adventure.key.Key;
import net.william278.husktowns.api.BukkitHuskTownsAPI;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HuskTownsBlockProtectionModule implements IBlockProtectionModule, FlagRegisterable {
    private final BukkitHuskTownsAPI api = BukkitHuskTownsAPI.getInstance();

    @Override
    public @NotNull String getPluginName() {
        return "HuskTowns";
    }

    @Override
    public boolean isProtected(Player player, Location block) {
        return api.isClaimAt(api.getPosition(block));
    }

    @Override
    public boolean allowBreak(Player player, Location block) {
        return api.isOperationAllowed(api.getOnlineUser(player), OperationType.BLOCK_BREAK, api.getPosition(block));
    }

    @Override
    public boolean allowPlace(Player player, Location block) {
        return api.isOperationAllowed(api.getOnlineUser(player), OperationType.BLOCK_PLACE, api.getPosition(block));
    }

    @Override
    public boolean allowInteract(Player player, Location block) {
        return api.isOperationAllowed(api.getOnlineUser(player), OperationType.BLOCK_INTERACT, api.getPosition(block));
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        Key key = Key.key(flag.namespace(), flag.id());
        OperationType ot = api.getOperationTypeRegistry().createOperationType(key);

        api.getOperationTypeRegistry().registerOperationType(ot);
    }
}
