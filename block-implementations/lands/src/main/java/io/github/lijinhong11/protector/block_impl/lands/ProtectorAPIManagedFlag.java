package io.github.lijinhong11.protector.block_impl.lands;

import io.github.lijinhong11.protector.api.flag.CustomFlag;
import me.angeschossen.lands.api.flags.DefaultStateFlag;
import me.angeschossen.lands.api.flags.enums.FlagModule;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ProtectorAPIManagedFlag extends DefaultStateFlag<ProtectorAPIManagedFlag> {
    private final CustomFlag object;

    public ProtectorAPIManagedFlag(CustomFlag flag) {
        super(flag.plugin(), Target.PLAYER, Objects.requireNonNullElse(flag.displayName(), flag.id()), true, true);

        object = flag;
    }

    @Override
    protected ProtectorAPIManagedFlag self() {
        return this;
    }

    @Override
    public @NotNull String getTogglePerm() {
        return object.plugin().getName().toLowerCase() + ".flag_setting." + object.id();
    }

    @Override
    public @NotNull String getTogglePermission() {
        return getTogglePerm();
    }

    @Override
    public @NotNull FlagModule getModule() {
        return FlagModule.PLAYER;
    }
}
