package io.github.lijinhong11.protector.impl.plotsquared;

import com.plotsquared.core.configuration.caption.StaticCaption;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import io.github.lijinhong11.protector.api.flag.CustomFlag;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PSCustomFlag extends BooleanFlag<PSCustomFlag> {
    private final CustomFlag flag;

    public PSCustomFlag(CustomFlag flag) {
        super(flag.defaultValue(), StaticCaption.of(flag.description() == null ? "" : flag.description()));

        this.flag = flag;
    }

    protected PSCustomFlag(boolean v, CustomFlag flag) {
        super(v, StaticCaption.of(flag.description() == null ? "" : flag.description()));

        this.flag = flag;
    }

    protected PSCustomFlag get(boolean v) {
        return new PSCustomFlag(v, flag);
    }

    @Override
    protected PSCustomFlag flagOf(@NonNull Boolean value) {
        return get(value);
    }
}
