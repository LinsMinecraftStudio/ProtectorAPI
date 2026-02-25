package io.github.lijinhong11.protector.impl.redprotect;

import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Core.config.Category.GlobalFlagsCategory;
import io.github.lijinhong11.protectorapi.flag.*;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRangeInfo;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedProtectProtectionModule implements IProtectionModule, FlagRegisterable {
    private final RedProtectAPI api;

    public RedProtectProtectionModule() {
        api = RedProtect.get().getAPI();
    }

    @Override
    public @NotNull String getPluginName() {
        return "RedProtect";
    }

    @Override
    public List<? extends IProtectionRangeInfo> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        List<RedProtectRegionInfo> protectionRangeInfos = new ArrayList<>();
        for (Region region : api.getPlayerRegions(String.valueOf(player.getUniqueId()))) {
            protectionRangeInfos.add(new RedProtectRegionInfo(region));
        }

        return protectionRangeInfos;
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        return api.getRegion(location) != null;
    }

    @Override
    public @Nullable IProtectionRangeInfo getProtectionRangeInfo(@NotNull Location location) {
        Region region = api.getRegion(location);
        if (region == null) {
            return null;
        }

        return new RedProtectRegionInfo(region);
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        RedProtect.get()
                .getRegionManager()
                .getAllRegions()
                .forEach(r -> r.setFlag(null, flag.id(), flag.defaultValue()));
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return true;
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        GlobalFlagsCategory category = RedProtect.get().getConfigManager().globalFlagsRoot();
        GlobalFlagsCategory.WorldProperties properties = category.worlds.get(world);
        if (flag.equals(CommonFlags.PLACE.getForRedProtect())
                || flag.equals(CommonFlags.BREAK.getForRedProtect())
                || flag.equals(CommonFlags.BUILD.getForRedProtect())) {
            return FlagStates.fromBoolean(properties.build);
        }

        Field[] field = GlobalFlagsCategory.WorldProperties.class.getDeclaredFields();
        for (Field f : field) {
            if (f.getName().equals(flag)) {
                try {
                    Object value = f.get(properties);
                    if (value instanceof Boolean b) {
                        return FlagStates.fromNullableBoolean(b);
                    } else {
                        return FlagStates.of(value);
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
        }

        return FlagStates.UNSUPPORTED;
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        if (flag.getForRedProtect() == null) {
            return FlagStates.UNSUPPORTED;
        }

        return getGlobalFlag(flag.getForRedProtect(), world);
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        GlobalFlagsCategory category = RedProtect.get().getConfigManager().globalFlagsRoot();
        GlobalFlagsCategory.WorldProperties properties = category.worlds.get(world);
        Field[] field = GlobalFlagsCategory.WorldProperties.class.getDeclaredFields();
        for (Field f : field) {
            if (f.getName().equals(flag)) {
                try {
                    if (!f.getType().isInstance(value)) {
                        throw new IllegalArgumentException("Value type mismatch");
                    }

                    f.set(properties, value);
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        if (flag.getForRedProtect() == null) {
            return;
        }

        setGlobalFlag(world, flag.getForRedProtect(), value);
    }
}
