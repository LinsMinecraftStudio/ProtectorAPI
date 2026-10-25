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
package io.github.lijinhong11.protector.impl.griefdefender;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.ClaimManager;
import com.griefdefender.api.claim.ClaimType;
import com.griefdefender.api.event.CreateClaimEvent;
import com.griefdefender.api.event.RemoveClaimEvent;
import com.griefdefender.api.permission.flag.Flag;
import com.griefdefender.lib.kyori.adventure.text.Component;
import com.griefdefender.lib.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import io.github.lijinhong11.protectorapi.ProtectorAPI;
import io.github.lijinhong11.protectorapi.flag.CommonFlags;
import io.github.lijinhong11.protectorapi.flag.CustomFlag;
import io.github.lijinhong11.protectorapi.flag.FlagRegisterable;
import io.github.lijinhong11.protectorapi.flag.FlagState;
import io.github.lijinhong11.protectorapi.handlers.RangeCreateHandler;
import io.github.lijinhong11.protectorapi.handlers.RangeDeleteHandler;
import io.github.lijinhong11.protectorapi.protection.IProtectionModule;
import io.github.lijinhong11.protectorapi.protection.IProtectionRange;
import io.github.lijinhong11.protectorapi.protection.ProtectionCheck;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GriefDefenderProtectionModule implements IProtectionModule, FlagRegisterable {
    private final Core api = GriefDefender.getCore();

    public GriefDefenderProtectionModule() {
        GriefDefender.getEventManager().getBus().subscribe(CreateClaimEvent.Post.class, event -> {
            ProtectorAPI.getHandlers(RangeCreateHandler.class)
                    .forEach(h -> h.onCreate(this, new GriefDefenderClaimInfo(event.getClaim())));
        });

        GriefDefender.getEventManager().getBus().subscribe(RemoveClaimEvent.class, event -> {
            ProtectorAPI.getHandlers(RangeDeleteHandler.class)
                    .forEach(h -> h.onDelete(this, new GriefDefenderClaimInfo(event.getClaim())));
        });
    }

    @Override
    public @NotNull String getPluginName() {
        return "GriefDefender";
    }

    @Override
    public boolean supportsAsync(@NotNull ProtectionCheck check) {
        return false;
    }

    @Override
    public boolean isProtected(@NotNull World world, int chunkX, int chunkZ) {
        if (!GriefDefender.getCore().isEnabled(world.getUID())) {
            return false;
        }
        ClaimManager claimManager = GriefDefender.getCore().getClaimManager(world.getUID());
        if (claimManager == null) {
            return false;
        }
        // Match BlockUtil.asLong(x, z), used by GDClaim.getChunkHashes(boolean).
        long chunkHash = ((long) chunkX & 0xffffffffL) | (((long) chunkZ & 0xffffffffL) << 32);
        Set<Claim> claims = claimManager.getChunksToClaimsMap().get(chunkHash);
        return claims != null && !claims.isEmpty();
    }

    @Override
    public List<? extends IProtectionRange> getProtectionRangeInfos(@NotNull OfflinePlayer player) {
        return api.getAllPlayerClaims(player.getUniqueId()).stream()
                .map(GriefDefenderClaimInfo::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isInProtectionRange(@NotNull Location location) {
        Claim cl = api.getClaimAt(location);
        return cl != null && !cl.isWilderness();
    }

    @Override
    public @Nullable IProtectionRange getProtectionRangeInfo(@NotNull Location location) {
        return api.getClaimAt(location) == null ? null : new GriefDefenderClaimInfo(api.getClaimAt(location));
    }

    @Override
    public boolean isSupportGlobalFlags() {
        return false;
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull String flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FlagState<?> getGlobalFlag(@NotNull CommonFlags flag, @NotNull String world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull String flag, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setGlobalFlag(@NotNull String world, @NotNull CommonFlags flag, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void registerFlag(CustomFlag flag) {
        GriefDefenderFlag griefDefenderFlag = new GriefDefenderFlag(flag);

        GriefDefender.getRegistry().registerBuilderSupplier(Flag.class, () -> griefDefenderFlag);
    }

    public static class GriefDefenderFlag implements Flag {
        private final CustomFlag flag;

        public GriefDefenderFlag(CustomFlag flag) {
            this.flag = flag;
        }

        @Override
        public boolean getDefaultClaimTypeValue(ClaimType claimType) {
            return flag.defaultValue();
        }

        @Override
        public String getPermission() {
            return "griefdefender.flag." + this.flag.id().toLowerCase();
        }

        @Override
        public Component getDescription() {
            return this.flag.description() == null
                    ? Component.text("Not defined.") // use default text from GriefDefender
                    : LegacyComponentSerializer.legacyAmpersand().deserialize(this.flag.description());
        }

        @Override
        public String getId() {
            return flag.id();
        }

        @Override
        public String getName() {
            return flag.displayName() == null ? flag.id() : flag.displayName();
        }
    }
}
