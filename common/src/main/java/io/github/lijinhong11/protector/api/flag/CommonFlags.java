package io.github.lijinhong11.protector.api.flag;

import org.jetbrains.annotations.Nullable;

public enum CommonFlags {
    ANVIL("anvil", "anvil", null),
    ANIMAL_KILLING("animal_killing", "animal_killing", "passives"),
    ANIMAL_SPAWN("animal_spawn", "animal_spawn", "spawn-animals"),
    BED("bed", "bed", null),
    BEACON("beacon", "beacon", null),
    BREAK("break", "break", "allow-break"),
    BREW("brew", "brew", null),
    BUTTON("button", "button", "button"),
    CAKE("cake", "cake", null),
    CONTAINER("container", "container", "chest"),
    CREEPER("creeper", "creeper_explode", "mob-loot"),
    DOOR("door", "door", "door"),
    DYE("dye", "dye", null),
    EGG("egg", "egg", null),
    ENCHANT("enchant", "enchant", null),
    ENDERPEARL("enderpearl", "ender_pearl", "teleport"),
    FALLINPROTECTION("fallinprotection", "gravity_block", null),
    FEED("feed", "feed", null),
    FIRESPREAD("firespread", "fire_spread", "fire"),
    FLY("fly", "fly", "allow-fly"),
    GLOW("glow", "glow", null),
    HOOK("hook", "hook", null),
    IGNITE("ignite", "ignite", null),
    ITEMDROP("itemdrop", "can-drop", "can-drop"),
    ITEMPICKUP("itempickup", "can-pickup", "can-pickup"),
    KEEPINV("keepinv", "keep-inventory", "keep-inventory"),
    KEEPEXP("keepexp", "keep-levels", "keep-levels"),
    LEVER("lever", "lever", "lever"),
    MOBITEMDROP("mobitemdrop", "mob_drop_item", "mob-loot"),
    MOBKILLING("mobkilling", "monster_killing", null),
    MONSTERS("monsters", "monster_spawn", "spawn-monsters"),
    MOVE("move", "move", "can-move"),
    NOTE("note", "note_block", null),
    PISTONPROTECTION("pistonprotection", "piston_outside", null),
    PLACE("place", "place", "allow-place"),
    PRESSURE("pressure", "pressure", "pressure"),
    DIODE("diode", "repeater", "redstone"),
    RIDING("riding", "riding", "minecart"),
    SHEAR("shear", "shear", null),
    SHOOT("shoot", "shoot", "can-projectiles"),
    TP("tp", "teleport", "teleport"),
    TRADE("trade", "trade", null),
    TRAMPLE("trample", "trample", "cropsfarm"),
    TNT("tnt", "tnt_explode", "block-transform"),
    VEHICLEDESTROY("vehicledestroy", "vehicle_destroy", null),
    VILLAGER_KILLING("villager_killing", "villager_killing", null),
    WITHERDESTRUCTION("witherdestruction", "witherdestruction", "spawn-wither"),
    TITLE("title", "show_border", null),
    USE("use", "use", null),
    BUILD("build", "place", "build"),
    PVP("pvp", "player_damage", "pvp"),
    ENDER_CHEST("ender-chest", null, "ender-chest"),
    REDSTONE("redstone", "redstone", "redstone"),
    CAN_GROW("can-grow", "harvest", "can-grow");

    private final String residence;
    private final @Nullable String dominion;
    private final @Nullable String redProtect;

    CommonFlags(String residence, @Nullable String dominion, @Nullable String redProtect) {
        this.residence = residence;
        this.dominion = dominion;
        this.redProtect = redProtect;
    }

    public String getForResidence() {
        return residence;
    }

    @Nullable
    public String getForDominion() {
        return dominion;
    }

    @Nullable
    public String getForRedProtect() {
        return redProtect;
    }
}
