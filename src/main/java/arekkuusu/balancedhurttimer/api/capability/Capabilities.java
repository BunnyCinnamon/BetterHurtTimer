package arekkuusu.balancedhurttimer.api.capability;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import javax.annotation.Nullable;
import java.util.Optional;

public final class Capabilities {
    @CapabilityInject(HurtCapability.class)
    public static final Capability<HurtCapability> HURT_LIMITER = null;

    public static Optional<HurtCapability> hurt(@Nullable Entity entity) {
        return entity != null ? Optional.ofNullable(entity.getCapability(HURT_LIMITER, null)) : Optional.empty();
    }
}
