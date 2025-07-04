package me.mmmjjkx.protectorapi;

import io.github.lijinhong11.protector.api.ProtectorAPI;
import io.github.lijinhong11.protector.block_impl.blocklocker.BlockLockerBlockProtectionModule;
import io.github.lijinhong11.protector.impl.WorldGuardProtectionModule;
import io.github.lijinhong11.protector.impl.bentobox.BentoBoxProtectionModule;
import io.github.lijinhong11.protector.impl.dominion.DominionProtectionModule;
import io.github.lijinhong11.protector.impl.redprotect.RedProtectProtectionModule;
import io.github.lijinhong11.protector.impl.residence.ResidenceProtectionModule;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectorAPIPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        registerModules();
        registerBlockModules();

        getLogger().info("Successfully loaded ProtectorAPI!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Successfully disabled ProtectorAPI!");
    }

    private void registerModules() {
        PluginManager pm = getServer().getPluginManager();
        if (pm.isPluginEnabled("WorldGuard")) {
            ProtectorAPI.register(new WorldGuardProtectionModule());
        }

        if (pm.isPluginEnabled("Dominion")) {
            ProtectorAPI.register(new DominionProtectionModule());
        }

        if (pm.isPluginEnabled("RedProtect")) {
            ProtectorAPI.register(new RedProtectProtectionModule());
        }

        if (pm.isPluginEnabled("Residence")) {
            ProtectorAPI.register(new ResidenceProtectionModule());
        }

        if (pm.isPluginEnabled("BentoBox")) {
            ProtectorAPI.register(new BentoBoxProtectionModule());
        }
    }

    private void registerBlockModules() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.isPluginEnabled("BlockLocker")) {
            ProtectorAPI.register(new BlockLockerBlockProtectionModule());
        }
    }
}
