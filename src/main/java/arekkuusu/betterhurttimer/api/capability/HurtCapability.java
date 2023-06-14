package arekkuusu.betterhurttimer.api.capability;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
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
import java.util.WeakHashMap;

public class HurtCapability implements ICapabilitySerializable<CompoundNBT>, Capability.IStorage<HurtCapability> {

    public Object2ObjectMap<CharSequence, HurtSourceData> hurtMap = new Object2ObjectArrayMap<>();
    public WeakHashMap<Entity, AttackInfo> meleeMap = new WeakHashMap<>();
    public int ticksToArmorDamage;
    public int ticksToShieldDamage;
    public double lastArmorDamage;
    public double lastShieldDamage;

    public static void init() {
        CapabilityManager.INSTANCE.register(HurtCapability.class, new HurtCapability(), HurtCapability::new);
        MinecraftForge.EVENT_BUS.register(new HurtCapability.Handler());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.HURT_LIMITER.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) Capabilities.HURT_LIMITER.getStorage().writeNBT(Capabilities.HURT_LIMITER, this, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        Capabilities.HURT_LIMITER.getStorage().readNBT(Capabilities.HURT_LIMITER, this, null, nbt);
    }

    //** NBT **//
    public static final String LAST_ARMOR_TIMER_NBT = "ticksToArmorDamage";
    public static final String LAST_SHIELD_TIMER_NBT = "ticksToShieldDamage";

    @Nullable
    @Override
    public INBT writeNBT(Capability<HurtCapability> capability, HurtCapability instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt(LAST_ARMOR_TIMER_NBT, instance.ticksToArmorDamage);
        tag.putInt(LAST_SHIELD_TIMER_NBT, instance.ticksToShieldDamage);
        return tag;
    }

    @Override
    public void readNBT(Capability<HurtCapability> capability, HurtCapability instance, Direction side, INBT nbt) {
        CompoundNBT tag = (CompoundNBT) nbt;
        instance.ticksToArmorDamage = tag.getInt(LAST_ARMOR_TIMER_NBT);
        instance.ticksToShieldDamage = tag.getInt(LAST_SHIELD_TIMER_NBT);
    }
    //** NBT **//

    public static class Handler {
        private static final ResourceLocation KEY = new ResourceLocation(BHT.MOD_ID, "hurt");

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity || BHTAPI.isCustom(event.getObject())) {
                event.addCapability(KEY, Capabilities.HURT_LIMITER.getDefaultInstance());
                if (event.getObject() instanceof LivingEntity) {
                    ((LivingEntity) event.getObject()).ticksSinceLastSwing = -1;
                }
            }
        }

        @SubscribeEvent
        public void clonePlayer(PlayerEvent.Clone event) {
            event.getEntityLiving().getCapability(Capabilities.HURT_LIMITER, null).ifPresent(cap -> {
                event.getOriginal().getCapability(Capabilities.HURT_LIMITER, null).ifPresent(sub -> {
                    cap.deserializeNBT(sub.serializeNBT());
                });
            });
        }
    }
}
