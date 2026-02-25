package me.mmmjjkx.protectorapi;

import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.block.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.flag.FlagStates;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DebugCommand implements TabExecutor {
    DebugCommand() {}

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.isOp()) {
            sender.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cYou must be a player to use this command.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("here")) {
            debugHere(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("modules")) {
            debugModules(sender);
            return true;
        }

        sender.sendMessage("§7Usage: /protectorapi-debug <here | modules>");
        return true;
    }

    /* ========================================= */

    private void debugHere(Player player) {
        Location loc = player.getLocation();

        player.sendMessage("§8==============================");
        player.sendMessage("§6[ProtectorAPI Debug]");
        player.sendMessage("§7Location: §f" + loc.getWorld().getName()
                + " (" + loc.getBlockX()
                + ", " + loc.getBlockY()
                + ", " + loc.getBlockZ()
                + ")");

        IProtectionModule module = ProtectorAPI.findModule(loc);

        if (module == null) {
            player.sendMessage("§7Protection Module: §cNone");
        } else {
            player.sendMessage("§7Protection Module: §a" + module.getPluginName());

            boolean inRange = module.isInProtectionRange(loc);
            player.sendMessage("§7In Protection Range: §f" + inRange);

            IProtectionRangeInfo info = module.getProtectionRangeInfo(player);

            if (info != null) {
                player.sendMessage("§7Flags:");

                printFlag(player, info, CommonFlags.BREAK);
                printFlag(player, info, CommonFlags.PLACE);
                printFlag(player, info, CommonFlags.INTERACT);
            }
        }

        IBlockProtectionModule blockModule = ProtectorAPI.findBlockModule(player, loc);

        if (blockModule == null) {
            player.sendMessage("§7Block Module: §cNone");
        } else {
            player.sendMessage("§7Block Module: §a" + blockModule.getPluginName());

            player.sendMessage("  §fallowBreak: " + formatBool(blockModule.allowBreak(player, loc)));
            player.sendMessage("  §fallowPlace: " + formatBool(blockModule.allowPlace(player, loc)));
            player.sendMessage("  §fallowInteract: " + formatBool(blockModule.allowInteract(player, loc)));
        }

        player.sendMessage("§7Final Decision (Protection Module + Block Module):");
        player.sendMessage("  §fallowBreak(): " + formatBool(ProtectorAPI.allowBreak(player, loc)));
        player.sendMessage("  §fallowPlace(): " + formatBool(ProtectorAPI.allowPlace(player, loc)));
        player.sendMessage("  §fallowInteract(): " + formatBool(ProtectorAPI.allowInteract(player, loc)));

        player.sendMessage("§8==============================");
    }

    private void debugModules(CommandSender sender) {
        sender.sendMessage("§8==============================");
        sender.sendMessage("§6[ProtectorAPI Modules]");

        ProtectorAPI.getAllAvailableProtectionModules()
                .forEach(m -> sender.sendMessage("§a- " + m.getPluginName() + " §7(GlobalFlags: "
                        + (m.isSupportGlobalFlags() ? "SUPPORTED" : "UNSUPPORTED")
                        + ")"));

        ProtectorAPI.getAllAvailableBlockProtectionModules()
                .forEach(m -> sender.sendMessage("§b- " + m.getPluginName() + " §7(Block Protection)"));

        sender.sendMessage("§8==============================");
    }

    private String formatBool(boolean value) {
        return value ? "§aALLOW" : "§cDENY";
    }

    private void printFlag(Player player, IProtectionRangeInfo info, CommonFlags flag) {
        try {
            FlagState<?> state = info.getFlagState(flag, player);

            if (state == null || state instanceof FlagStates.UnsupportedFlagState) {
                player.sendMessage("  §f" + flag.name() + ": §7UNSUPPORTED (Module or API is unsupported)");
                return;
            }

            Object value = info.getFlagState(flag, player).value();

            if (!(value instanceof Boolean bool)) {
                player.sendMessage("  §f" + flag.name() + ": §7NON-BOOLEAN (" + value.toString() + ")");
                return;
            }

            player.sendMessage("  §f" + flag.name() + ": " + formatBool(bool));
        } catch (Exception e) {
            player.sendMessage("  §f" + flag.name() + ": §cERROR (See console for details)");
            ProtectorAPI.getPluginHost().getLogger().log(Level.SEVERE, "Failed to get flag state: ", e);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("here", "modules"), new ArrayList<>());
        }

        return List.of();
    }
}
