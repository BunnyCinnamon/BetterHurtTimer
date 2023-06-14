package arekkuusu.betterhurttimer.api.capability;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.WeakHashMap;

public class HurtCapability implements ICapabilitySerializable<CompoundTag> {

    public Object2ObjectMap<CharSequence, HurtSourceData> hurtMap = new Object2ObjectArrayMap<>();
    public WeakHashMap<Entity, AttackInfo> meleeMap = new WeakHashMap<>();
    public int ticksToArmorDamage;
    public int ticksToShieldDamage;
    public double lastArmorDamage;
    public double lastShieldDamage;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return Capabilities.HURT_LIMITER.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(LAST_ARMOR_TIMER_NBT, ticksToArmorDamage);
        tag.putInt(LAST_SHIELD_TIMER_NBT, ticksToShieldDamage);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ticksToArmorDamage = tag.getInt(LAST_ARMOR_TIMER_NBT);
        ticksToShieldDamage = tag.getInt(LAST_SHIELD_TIMER_NBT);
    }

    //** NBT **//
    public static final String LAST_ARMOR_TIMER_NBT = "ticksToArmorDamage";
    public static final String LAST_SHIELD_TIMER_NBT = "ticksToShieldDamage";
    //** NBT **//

    public static class Handler {
        private static final ResourceLocation KEY = new ResourceLocation(BHT.MOD_ID, "hurt");

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof LivingEntity || BHTAPI.isCustom(event.getObject())) {
                event.addCapability(KEY, new HurtCapability());
                if (event.getObject() instanceof LivingEntity)
                    ((LivingEntity) event.getObject()).attackStrengthTicker = -1;
            }
        }

        @SubscribeEvent
        public void clonePlayer(PlayerEvent.Clone event) {
            event.getEntity().getCapability(Capabilities.HURT_LIMITER, null).ifPresent(cap -> {
                event.getOriginal().getCapability(Capabilities.HURT_LIMITER, null).ifPresent(sub -> {
                    cap.deserializeNBT(sub.serializeNBT());
                });
            });
        }
    }
}
