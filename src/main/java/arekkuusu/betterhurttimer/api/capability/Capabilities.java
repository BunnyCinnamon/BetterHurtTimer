package arekkuusu.betterhurttimer.api.capability;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Capabilities {

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    public static <T> Capability<T> empty() {
        return null;
    }

    public static final Capability<HurtCapability> HURT_LIMITER = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static final Capability<HealthCapability> HEALTH = CapabilityManager.get(new CapabilityToken<>() {
    });

    public static LazyOptional<HurtCapability> hurt(@Nullable Entity entity) {
        return entity != null ? entity.getCapability(HURT_LIMITER, null) : LazyOptional.empty();
    }

    public static LazyOptional<HealthCapability> health(@Nullable Entity entity) {
        return entity != null ? entity.getCapability(HEALTH, null) : LazyOptional.empty();
    }
}
