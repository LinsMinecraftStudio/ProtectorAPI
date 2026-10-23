/*
 * ProtectorAPI
 * Copyright (C) 2026 lijinhong11(mmmjjkx)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package me.mmmjjkx.protectorapi;

import io.github.lijinhong11.protector.block_impl.blocklocker.BlockLockerBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.bolt.BoltBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.chestprotection.ChestProtectionBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.chestshop.ChestShopBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.factionsuuid.FactionsUUIDBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.funnyguilds.FunnyGuildsBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.griefprevention.GriefPreventionBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.husktowns.HuskTownsBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.landclaimplugin.LandClaimPluginBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.lands.LandsBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.lockettepro.LocketteProBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.lwcx.LWCXBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.nobuildplus.NoBuildPlusBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.quickshop.QuickShopHikariProtection;
import io.github.lijinhong11.protector.block_impl.shopchest.ShopChestBlockProtectionModule;
import io.github.lijinhong11.protector.block_impl.towny.TownyBlockProtectionModule;
import io.github.lijinhong11.protector.impl.askyblock.ASkyBlockProtectionModule;
import io.github.lijinhong11.protector.impl.bentobox.BentoBoxProtectionModule;
import io.github.lijinhong11.protector.impl.dominion.DominionProtectionModule;
import io.github.lijinhong11.protector.impl.excellentclaims.ExcellentClaimsBlockProtectionModule;
import io.github.lijinhong11.protector.impl.griefdefender.GriefDefenderProtectionModule;
import io.github.lijinhong11.protector.impl.huskclaims.HuskClaimsProtectionModule;
import io.github.lijinhong11.protector.impl.iridiumskyblock.IridiumSkyblockProtectionModule;
import io.github.lijinhong11.protector.impl.plotsquared.PlotSquaredProtectionModule;
import io.github.lijinhong11.protector.impl.redprotect.RedProtectProtectionModule;
import io.github.lijinhong11.protector.impl.residence.ResidenceProtectionModule;
import io.github.lijinhong11.protector.impl.worldguard.WorldGuardProtectionModule;
import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.protection.IBlockProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import java.util.stream.Collectors;
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

        Metrics metrics = new Metrics(this, 26519);
        metrics.addCustomChart(new Metrics.AdvancedPie(
                "protection_plugins",
                () -> ProtectionPluginMetrics.countPluginNames(
                        ProtectorAPI.getAllAvailableProtectionModules().stream()
                                .map(IProtectionModule::getPluginName)
                                .collect(Collectors.toList()),
                        ProtectorAPI.getAllAvailableBlockProtectionModules().stream()
                                .map(IBlockProtectionModule::getPluginName)
                                .collect(Collectors.toList()))));

        getCommand("protectorapi-debug").setExecutor(new DebugCommand());

        getLogger().info("Successfully loaded ProtectorAPI!");

        if (getConfig().getBoolean("update-check")) {
            new ModrinthUpdateChecker(this, "protectorapi").check();
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Successfully disabled ProtectorAPI!");
    }

    private void registerModules() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.isPluginEnabled("WorldGuard")) {
            WorldGuardProtectionModule module = new WorldGuardProtectionModule();
            ProtectorAPI.registerProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("Dominion")) {
            ProtectorAPI.registerProtectionModule(new DominionProtectionModule());
        }

        if (pm.isPluginEnabled("HuskClaims")) {
            ProtectorAPI.registerProtectionModule(new HuskClaimsProtectionModule());
        }

        if (pm.isPluginEnabled("IridiumSkyblock")) {
            ProtectorAPI.registerProtectionModule(new IridiumSkyblockProtectionModule());
        }

        if (pm.isPluginEnabled("RedProtect")) {
            RedProtectProtectionModule module = new RedProtectProtectionModule();
            ProtectorAPI.registerProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("Residence")) {
            ResidenceProtectionModule module = new ResidenceProtectionModule();
            ProtectorAPI.registerProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("BentoBox")) {
            BentoBoxProtectionModule module = new BentoBoxProtectionModule();
            ProtectorAPI.registerProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("PlotSquared")) {
            PlotSquaredProtectionModule module = new PlotSquaredProtectionModule();
            ProtectorAPI.registerProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("GriefDefender")) {
            GriefDefenderProtectionModule module = new GriefDefenderProtectionModule();
            ProtectorAPI.registerProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("ASkyBlock")) {
            ProtectorAPI.registerProtectionModule(new ASkyBlockProtectionModule());
        }
    }

    private void registerBlockModules() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.isPluginEnabled("BlockLocker")) {
            ProtectorAPI.registerBlockProtectionModule(new BlockLockerBlockProtectionModule());
        }

        if (pm.isPluginEnabled("Bolt")) {
            ProtectorAPI.registerBlockProtectionModule(new BoltBlockProtectionModule());
        }

        if (pm.isPluginEnabled("ChestProtection")) {
            ProtectorAPI.registerBlockProtectionModule(new ChestProtectionBlockProtectionModule());
        }

        if (pm.isPluginEnabled("ChestShop")) {
            ProtectorAPI.registerBlockProtectionModule(new ChestShopBlockProtectionModule());
        }

        if (pm.isPluginEnabled("ExcellentClaims")) {
            ProtectorAPI.registerBlockProtectionModule(new ExcellentClaimsBlockProtectionModule());
        }

        if (pm.isPluginEnabled("FactionsUUID")) {
            FactionsUUIDBlockProtectionModule module = new FactionsUUIDBlockProtectionModule();
            ProtectorAPI.registerBlockProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("FunnyGuilds")) {
            ProtectorAPI.registerBlockProtectionModule(new FunnyGuildsBlockProtectionModule());
        }

        if (pm.isPluginEnabled("GriefPrevention")) {
            GriefPreventionBlockProtectionModule module = new GriefPreventionBlockProtectionModule();
            ProtectorAPI.registerBlockProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("HuskTowns")) {
            ProtectorAPI.registerBlockProtectionModule(new HuskTownsBlockProtectionModule());
        }

        if (pm.isPluginEnabled("LandClaimPlugin")) {
            LandClaimPluginBlockProtectionModule module = new LandClaimPluginBlockProtectionModule();
            ProtectorAPI.registerBlockProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("LockettePro")) {
            ProtectorAPI.registerBlockProtectionModule(new LocketteProBlockProtectionModule());
        }

        if (pm.isPluginEnabled("LWC")) {
            ProtectorAPI.registerBlockProtectionModule(new LWCXBlockProtectionModule());
        }

        if (pm.isPluginEnabled("QuickShop-Hikari")) {
            ProtectorAPI.registerBlockProtectionModule(new QuickShopHikariProtection());
        }

        if (pm.isPluginEnabled("ShopChest")) {
            ProtectorAPI.registerBlockProtectionModule(new ShopChestBlockProtectionModule());
        }

        if (pm.isPluginEnabled("Towny")) {
            TownyBlockProtectionModule module = new TownyBlockProtectionModule();
            ProtectorAPI.registerBlockProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("Lands")) {
            LandsBlockProtectionModule module = new LandsBlockProtectionModule();
            ProtectorAPI.registerBlockProtectionModule(module);
            ProtectorAPI.registerChunkProtectionModule(module);
        }

        if (pm.isPluginEnabled("NoBuildPlus")) {
            ProtectorAPI.registerBlockProtectionModule(new NoBuildPlusBlockProtectionModule());
        }
    }
}
