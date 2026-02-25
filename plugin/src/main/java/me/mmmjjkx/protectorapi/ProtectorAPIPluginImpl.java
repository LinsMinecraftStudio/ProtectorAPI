package me.mmmjjkx.protectorapi;

import io.github.lijinhong11.protector.block_impl.blocklocker.BlockLockerBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.bolt.BoltBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.chestprotection.ChestProtectionBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.chestshop.ChestShopBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.factionsuuid.FactionsUUIDBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.funnyguilds.FunnyGuildsBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.griefprevention.GriefPreventionBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.husktowns.HuskTownsBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.lands.LandsBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.lockettepro.LocketteProBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.lwcx.LWCXBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.nobuildplus.NoBuildPlusBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.quickshop.QuickShopHikariProtection;
import io.github.lijinhong11.protector.block_impl.quickshop.QuickShopReremakeProtection;
import io.github.lijinhong11.protector.block_impl.shopchest.ShopChestBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.towny.TownyBlockProtectionModule;
import io.github.lijinhong11.protector.impl.bentobox.BentoBoxProtectionModule;
import io.github.lijinhong11.protector.impl.dominion.DominionProtectionModule;
import io.github.lijinhong11.protector.impl.excellentclaims.ExcellentClaimsBlockProtectionModule;
import io.github.lijinhong11.protector.impl.huskclaims.HuskClaimsProtectionModule;
import io.github.lijinhong11.protector.impl.plotsquared.PlotSquaredProtectionModule;
import io.github.lijinhong11.protector.impl.redprotect.RedProtectProtectionModule;
import io.github.lijinhong11.protector.impl.residence.ResidenceProtectionModule;
import io.github.lijinhong11.protector.impl.worldguard.WorldGuardProtectionModule;
import io.github.lijinhong11.protectorapi.ProtectorAPI;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtectorAPIPluginImpl extends JavaPlugin {
    @Override
    public void onLoad() {
        ProtectorAPI.setPluginHost(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveConfig();

        registerModules();
        registerBlockModules();

        new Metrics(this, 26519);

        getCommand("protectorapi-debug").setExecutor(new DebugCommand());

        getLogger().info("Successfully loaded ProtectorAPI!");

        if (getConfig().getBoolean("update-check")) {
            UpdateChecker.check();
        }
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

        if (pm.isPluginEnabled("HuskClaims")) {
            ProtectorAPI.register(new HuskClaimsProtectionModule());
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

        if (pm.isPluginEnabled("PlotSquared")) {
            ProtectorAPI.register(new PlotSquaredProtectionModule());
        }
    }

    private void registerBlockModules() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.isPluginEnabled("BlockLocker")) {
            ProtectorAPI.register(new BlockLockerBlockProtectionModule());
        }

        if (pm.isPluginEnabled("Bolt")) {
            ProtectorAPI.register(new BoltBlockProtectionModule());
        }

        if (pm.isPluginEnabled("ChestProtection")) {
            ProtectorAPI.register(new ChestProtectionBlockProtectionModule());
        }

        if (pm.isPluginEnabled("ChestShop")) {
            ProtectorAPI.register(new ChestShopBlockProtectionModule());
        }

        if (pm.isPluginEnabled("ExcellentClaims")) {
            ProtectorAPI.register(new ExcellentClaimsBlockProtectionModule());
        }

        if (pm.isPluginEnabled("FactionsUUID")) {
            ProtectorAPI.register(new FactionsUUIDBlockProtectionModule());
        }

        if (pm.isPluginEnabled("FunnyGuilds")) {
            ProtectorAPI.register(new FunnyGuildsBlockProtectionModule());
        }

        if (pm.isPluginEnabled("GriefPrevention")) {
            ProtectorAPI.register(new GriefPreventionBlockProtectionModule());
        }

        if (pm.isPluginEnabled("HuskTowns")) {
            ProtectorAPI.register(new HuskTownsBlockProtectionModule());
        }

        if (pm.isPluginEnabled("LockettePro")) {
            ProtectorAPI.register(new LocketteProBlockProtectionModule());
        }

        if (pm.isPluginEnabled("LWC")) {
            ProtectorAPI.register(new LWCXBlockProtectionModule());
        }

        if (pm.isPluginEnabled("QuickShop-Hikari")) {
            ProtectorAPI.register(new QuickShopHikariProtection());
        }

        if (pm.isPluginEnabled("QuickShop-Reremake")) {
            ProtectorAPI.register(new QuickShopReremakeProtection());
        }

        if (pm.isPluginEnabled("ShopChest")) {
            ProtectorAPI.register(new ShopChestBlockProtectionModule());
        }

        if (pm.isPluginEnabled("Towny")) {
            ProtectorAPI.register(new TownyBlockProtectionModule());
        }

        if (pm.isPluginEnabled("Lands")) {
            ProtectorAPI.register(new LandsBlockProtectionModule());
        }

        if (pm.isPluginEnabled("NoBuildPlus")) {
            ProtectorAPI.register(new NoBuildPlusBlockProtectionModule());
        }
    }
}
