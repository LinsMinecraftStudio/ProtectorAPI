package io.github.lijinhong11.protectorapi.flag;

import org.jetbrains.annotations.Nullable;

/**
 * The enum class to store common flags
 */
public enum CommonFlags {
    BUILD("build", "place", "build", "build", "place-blocks", "PLACE_BLOCKS", "block_place", "BLOCK_PLACE", "block-place"),
    PVP("pvp", "player_damage", "pvp", "pvp", "pvp", "PVP_OVERWORLD", "player_damage_player", null, null),
    ANIMAL_SPAWN(
            "animal_spawn",
            "animal_spawn",
            "spawn-animals",
            "mob-spawning",
            "mob-spawn",
            "ANIMAL_SPAWNERS_SPAWN",
            "passive_mob_spawn",
            null,
            "entity-spawn"),
    FIRESPREAD(
            "firespread",
            "fire_spread",
            "fire",
            "fire-spread",
            "fire-spread",
            "FIRE_SPREAD",
            "fire_spread",
            null,
            "block-spread"),
    FLY("fly", "fly", "allow-fly", "fly", "fly", "ISLAND_FLY_PROTECTION", null, null, null),
    CONTAINER(
            "container",
            "container",
            "chest",
            "chest-access",
            "container-access",
            "CHEST",
            "container_open",
            "OPEN_CONTAINERS",
            "interact-inventory"),
    ENDERPEARL(
            "enderpearl",
            "ender_pearl",
            "teleport",
            "enderpearl",
            "enderpearl",
            "ENDER_PEARL",
            "ender_pearl_teleport",
            null,
            "entity-teleport-from"),
    TNT("tnt", "tnt_explode", "block-transform", "tnt", "tnt", "TNT_PRIMING", "explosion_damage_terrain", null, "explosion-block"),
    ITEM_DROP("itemdrop", "can-drop", "can-drop", "item-drop", "item-drop", "ITEM_DROP", null, null, "item-drop"),
    ITEM_PICKUP(
            "itempickup",
            "can-pickup",
            "can-pickup",
            "item-pickup",
            "item-pickup",
            "ITEM_PICKUP",
            null,
            null,
            "item-pickup"),
    INTERACT("use", "place", "build", "use", "use", null, "block_interact", "INTERACT", "interact-block-secondary"),
    KEEP_INV("keepinv", "keep-inventory", "keep-inventory", "keep-inventory", "keep-inventory", null, null, null, null),
    KEEP_EXP("keepexp", "keep-levels", "keep-levels", "keep-exp", "keep-exp", null, null, null, null),
    CREEPER("creeper", "creeper_explode", "mob-loot", "creeper-explosion", "creeper-explosion", null, null, null, "explosion-block"),
    MOB_ITEM_DROP("mobitemdrop", "mob_drop_item", "mob-loot", "exp-drops", "exp-drop", null, null, null, "item-drop"),
    MONSTER_SPAWN(
            "monsters",
            "monster_spawn",
            "spawn-monsters",
            "mob-spawning",
            "mob-spawn",
            "MONSTER_SPAWNERS_SPAWN",
            "monster_spawn",
            null,
            "entity-spawn"),
    MOVE("move", "move", "can-move", "entry", "entry", "MOVE_BOX", null, null, "enter-claim"),
    BREAK(
            "break",
            "break",
            "allow-break",
            "block-break",
            "break-blocks",
            "BREAK_BLOCKS",
            "block_break",
            "BLOCK_BREAK",
            "block-break"),
    PLACE(
            "place",
            "place",
            "allow-place",
            "block-place",
            "place-blocks",
            "PLACE_BLOCKS",
            "block_place",
            "BLOCK_PLACE",
            "block-place"),
    RIDING(
            "riding",
            "riding",
            null,
            "ride",
            null,
            "RIDING",
            null,
            null,
            "entity_riding"),
    LEAF_DECAY(
            "decay",
            null,
            "leaves-decay",
            "leaf-decay",
            "leaf-decay",
            "LEAF_DECAY",
            null,
            null,
            "leaf_decay"),
    CROP_TRAMPLE(
            "trample",
            "trample",
            null,
            "block-trampling",
            null,
            "CROP_TRAMPLE",
            null,
            null,
            null),
    FISHING(
            "hook",
            "hook",
            "fishing",
            null,
            "fishing",
            null,
            null,
            null,
            null),
    ANVIL(
            "anvil",
            "anvil",
            null,
            "use-anvil",
            null,
            "ANVIL",
            null,
            null,
            null);

    private final String residence;
    private final @Nullable String dominion;
    private final @Nullable String redProtect;
    private final @Nullable String worldGuard;
    private final @Nullable String plotSquared;
    private final @Nullable String bentoBox;
    private final @Nullable String huskClaims;
    private final @Nullable String iridiumSkyblock;
    private final @Nullable String griefDefender;

    CommonFlags(
            String residence,
            @Nullable String dominion,
            @Nullable String redProtect,
            @Nullable String worldGuard,
            @Nullable String plotSquared,
            @Nullable String bentoBox,
            @Nullable String huskClaims,
            @Nullable String iridiumSkyblock,
            @Nullable String griefDefender) {
        this.residence = residence;
        this.dominion = dominion;
        this.redProtect = redProtect;
        this.worldGuard = worldGuard;
        this.plotSquared = plotSquared;
        this.bentoBox = bentoBox;
        this.huskClaims = huskClaims;
        this.iridiumSkyblock = iridiumSkyblock;
        this.griefDefender = griefDefender;
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

    public static CommonFlags fromHuskClaims(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.huskClaims != null && cf.huskClaims.equalsIgnoreCase(flag)) return cf;
        }

        return null;
    }

    public static CommonFlags fromIridiumSkyblock(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.iridiumSkyblock != null && cf.iridiumSkyblock.equalsIgnoreCase(flag)) return cf;
        }

        return null;
    }

    public static CommonFlags fromGriefDefender(String flag) {
        for (CommonFlags cf : values()) {
            if (cf.griefDefender != null && cf.griefDefender.equalsIgnoreCase(flag)) return cf;
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
            if (cf.huskClaims != null && cf.huskClaims.equalsIgnoreCase(flag)) return cf;
            if (cf.iridiumSkyblock != null && cf.iridiumSkyblock.equalsIgnoreCase(flag)) return cf;
            if (cf.griefDefender != null && cf.griefDefender.equalsIgnoreCase(flag)) return cf;
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

    public @Nullable String getForHuskClaims() {
        return huskClaims;
    }

    public @Nullable String getForIridiumSkyblock() {
        return iridiumSkyblock;
    }

    public @Nullable String getForGriefDefender() {
        return griefDefender;
    }
}
