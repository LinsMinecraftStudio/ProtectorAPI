package io.github.lijinhong11.protector.api.flag;

import io.github.lijinhong11.protector.api.annotations.WeakImplementation;
import org.jetbrains.annotations.Nullable;

@WeakImplementation(reason = "some plugins' didn't have enough flags")
public enum CommonFlags {
    BUILD("build", "place", "build", "build", "place-blocks", "PLACE_BLOCKS"),
    PVP("pvp", "player_damage", "pvp", "pvp", "pvp", "PVP_OVERWORLD"),
    ANIMAL_SPAWN("animal_spawn", "animal_spawn", "spawn-animals", "mob-spawning", "mob-spawn", "ANIMAL_SPAWNERS_SPAWN"),
    FIRESPREAD("firespread", "fire_spread", "fire", "fire-spread", "fire-spread", "FIRE_SPREAD"),
    FLY("fly", "fly", "allow-fly", "fly", "fly", "ISLAND_FLY_PROTECTION"),
    CONTAINER("container", "container", "chest", "chest-access", "container-access", "CHEST"),
    ENDERPEARL("enderpearl", "ender_pearl", "teleport", "enderpearl", "enderpearl", "ENDER_PEARL"),
    TNT("tnt", "tnt_explode", "block-transform", "tnt", "tnt", "TNT_PRIMING"),
    ITEM_DROP("itemdrop", "can-drop", "can-drop", "item-drop", "item-drop", "ITEM_DROP"),
    ITEM_PICKUP("itempickup", "can-pickup", "can-pickup", "item-pickup", "item-pickup", "ITEM_PICKUP"),
    INTERACT("use", "place", "build", "use", "use", "REDSTONE"),
    KEEP_INV("keepinv", "keep-inventory", "keep-inventory", "keep-inventory", "keep-inventory", null),
    KEEP_EXP("keepexp", "keep-levels", "keep-levels", "keep-exp", "keep-exp", null),
    CREEPER("creeper", "creeper_explode", "mob-loot", "creeper-explosion", "creeper-explosion", null),
    MOB_ITEM_DROP("mobitemdrop", "mob_drop_item", "mob-loot", "exp-drops", "exp-drop", null),
    MONSTER_SPAWN("monsters", "monster_spawn", "spawn-monsters", "mob-spawning", "mob-spawn", "MONSTER_SPAWNERS_SPAWN"),
    MOVE("move", "move", "can-move", "entry", "entry", "MOVE_BOX"),
    BREAK("break", "break", "allow-break", "block-break", "break-blocks", "BREAK_BLOCKS"),
    PLACE("place", "place", "allow-place", "block-place", "place-blocks", "PLACE_BLOCKS");

    private final String residence;
    private final @Nullable String dominion;
    private final @Nullable String redProtect;
    private final @Nullable String worldGuard;
    private final @Nullable String plotSquared;
    private final @Nullable String bentoBox;

    CommonFlags(
            String residence,
            @Nullable String dominion,
            @Nullable String redProtect,
            @Nullable String worldGuard,
            @Nullable String plotSquared,
            @Nullable String bentoBox) {
        this.residence = residence;
        this.dominion = dominion;
        this.redProtect = redProtect;
        this.worldGuard = worldGuard;
        this.plotSquared = plotSquared;
        this.bentoBox = bentoBox;
    }

    public static CommonFlags fromResidence(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.residence.equalsIgnoreCase(flag)) return cf;
        }
        return null;
    }

    public static CommonFlags fromDominion(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.dominion != null && cf.dominion.equalsIgnoreCase(flag)) return cf;
        }
        return null;
    }

    public static CommonFlags fromRedProtect(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.redProtect != null && cf.redProtect.equalsIgnoreCase(flag)) return cf;
        }
        return null;
    }

    public static CommonFlags fromWorldGuard(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.worldGuard != null && cf.worldGuard.equalsIgnoreCase(flag)) return cf;
        }
        return null;
    }

    public static CommonFlags fromPlotSquared(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.plotSquared != null && cf.plotSquared.equalsIgnoreCase(flag)) return cf;
        }
        return null;
    }

    public static CommonFlags fromBentoBox(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.bentoBox != null && cf.bentoBox.equalsIgnoreCase(flag)) return cf;
        }
        return null;
    }

    public static CommonFlags fromAny(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.dominion != null && cf.dominion.equalsIgnoreCase(flag)) return cf;
            if (cf.residence != null && cf.residence.equalsIgnoreCase(flag)) return cf;
            if (cf.plotSquared != null && cf.plotSquared.equalsIgnoreCase(flag)) return cf;
            if (cf.bentoBox != null && cf.bentoBox.equalsIgnoreCase(flag)) return cf;
            if (cf.redProtect != null && cf.redProtect.equalsIgnoreCase(flag)) return cf;
            if (cf.worldGuard != null && cf.worldGuard.equalsIgnoreCase(flag)) return cf;
        }

        return null;
    }

    public String getForResidence() {
        return residence;
    }

    public @Nullable String getForDominion() {
        return dominion;
    }

    public @Nullable String getForRedProtect() {
        return redProtect;
    }

    public @Nullable String getForWorldGuard() {
        return worldGuard;
    }

    public @Nullable String getForPlotSquared() {
        return plotSquared;
    }

    public @Nullable String getForBentoBox() {
        return bentoBox;
    }
}
