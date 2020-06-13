package arekkuusu.balancedhurttimer.api.capability;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.Optional;

public final class Capabilities {
    @CapabilityInject(HurtLimiterCapability.class)
    public static final Capability<HurtLimiterCapability> HURT_LIMITER = null;

    public static Optional<HurtLimiterCapability> hurt(@Nullable Entity entity) {
        return entity != null ? Optional.ofNullable(entity.getCapability(HURT_LIMITER, null)) : Optional.empty();
    }
}
