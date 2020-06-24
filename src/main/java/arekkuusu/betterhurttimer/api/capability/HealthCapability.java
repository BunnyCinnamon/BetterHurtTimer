package arekkuusu.betterhurttimer.api.capability;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class HealthCapability implements ICapabilitySerializable<CompoundNBT>, Capability.IStorage<HealthCapability> {

    public int health = -1;

    public static void init() {
        CapabilityManager.INSTANCE.register(HealthCapability.class, new HealthCapability(), HealthCapability::new);
        MinecraftForge.EVENT_BUS.register(new HealthCapability.Handler());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.HEALTH.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.HEALTH.getStorage().writeNBT(Capabilities.HEALTH, this, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.HEALTH.getStorage().readNBT(Capabilities.HEALTH, this, null, nbt);
    }

    @Nullable
    @Override
    public INBT writeNBT(Capability<HealthCapability> capability, HealthCapability instance, Direction side) {
        return new CompoundNBT();
    }

    @Override
    public void readNBT(Capability<HealthCapability> capability, HealthCapability instance, Direction side, INBT nbt) {
    }

    public static class Handler {
        private static final ResourceLocation KEY = new ResourceLocation(BHT.MOD_ID, "health");

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity)
                event.addCapability(KEY, Capabilities.HEALTH.getDefaultInstance());
        }
    }
}
