package me.mmmjjkx.protectorapi;

import io.github.lijinhong11.protector.api.ProtectorAPI;
import io.github.lijinhong11.protector.api.block.IBlockProtectionModule;
import io.github.lijinhong11.protector.api.protection.IProtectionModule;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class DebugCommand implements TabExecutor {
    private final ProtectorAPIPluginImpl plugin;

    public DebugCommand(ProtectorAPIPluginImpl plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (commandSender.isOp()) {
            if (args.length == 0) {
                commandSender.sendMessage("ProtectorAPI version: " + plugin.getDescription().getVersion());
                commandSender.sendMessage("Usage: /protectorapi-debug <check | checkAllows | listProtectionModules | listBlockProtectionModules>");
                return false;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("check")) {
                    if (!(commandSender instanceof Player p)) {
                        commandSender.sendMessage("You must be a player to do that.");
                        return false;
                    }

                    Location loc = p.getLocation();

                    String protectionModule = "None";
                    boolean hasRange = false;
                    IProtectionModule module = ProtectorAPI.findModule(loc);
                    if (module != null) {
                        protectionModule = module.getPluginName();
                        hasRange = module.getProtectionRangeInfo(loc) != null;
                    }

                    commandSender.sendMessage("Protection Module: " + protectionModule);
                    commandSender.sendMessage("Has Protection Range: " + hasRange);
                } else if (args[0].equalsIgnoreCase("checkAllows")) {
                    if (!(commandSender instanceof Player p)) {
                        commandSender.sendMessage("You must be a player to do that.");
                        return false;
                    }

                    commandSender.sendMessage("Allow Place: " + ProtectorAPI.allowPlace(p));
                    commandSender.sendMessage("Allow Break: " + ProtectorAPI.allowBreak(p));
                    commandSender.sendMessage("Allow Interact: " + ProtectorAPI.allowInteract(p));
                } else if (args[0].equalsIgnoreCase("listProtectionModules")) {
                    List<String> plugins = ProtectorAPI.getAllAvailableProtectionModules().stream().map(IProtectionModule::getPluginName).toList();
                    commandSender.sendMessage("All Protection Modules: " + String.join(",", plugins));
                } else if (args[0].equalsIgnoreCase("listBlockProtectionModules")) {
                    List<String> plugins = ProtectorAPI.getAllAvailableBlockProtectionModules().stream().map(IBlockProtectionModule::getPluginName).toList();
                    commandSender.sendMessage("All Block Protection Modules: " + String.join(",", plugins));
                }
            }
        } else {
            commandSender.sendMessage("You don't have permission to do that.");
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return StringUtil.copyPartialMatches(strings[0], List.of("check", "checkAllows", "listProtectionModules", "listBlockProtectionModules"), new ArrayList<>());
        }

        return List.of();
    }
}
