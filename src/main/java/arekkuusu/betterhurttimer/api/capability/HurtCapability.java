package arekkuusu.betterhurttimer.api.capability;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.WeakHashMap;

@SuppressWarnings("ConstantConditions")
public class HurtCapability implements ICapabilitySerializable<NBTTagCompound>, Capability.IStorage<HurtCapability> {

    public Object2ObjectMap<CharSequence, HurtSourceData> hurtMap = new Object2ObjectArrayMap<>();
    public WeakHashMap<Entity, AttackInfo> meleeMap = new WeakHashMap<>();
    public int ticksToArmorDamage;
    public int ticksToShieldDamage;
    public double lastArmorDamage;
    public double lastShieldDamage;

    public static void init() {
        CapabilityManager.INSTANCE.register(HurtCapability.class, new HurtCapability(), HurtCapability::new);
        MinecraftForge.EVENT_BUS.register(new Handler());
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return getCapability(capability, facing) != null;
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == Capabilities.HURT_LIMITER ? Capabilities.HURT_LIMITER.cast(this) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) Capabilities.HURT_LIMITER.getStorage().writeNBT(Capabilities.HURT_LIMITER, this, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        Capabilities.HURT_LIMITER.getStorage().readNBT(Capabilities.HURT_LIMITER, this, null, nbt);
    }

    //** NBT **//
    public static final String LAST_ARMOR_TIMER_NBT = "ticksToArmorDamage";
    public static final String LAST_SHIELD_TIMER_NBT = "ticksToShieldDamage";

    @Override
    @Nullable
    public NBTBase writeNBT(Capability<HurtCapability> capability, HurtCapability instance, EnumFacing side) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger(LAST_ARMOR_TIMER_NBT, instance.ticksToArmorDamage);
        tag.setInteger(LAST_SHIELD_TIMER_NBT, instance.ticksToShieldDamage);
        return tag;
    }

    @Override
    public void readNBT(Capability<HurtCapability> capability, HurtCapability instance, EnumFacing side, NBTBase nbt) {
        NBTTagCompound tag = (NBTTagCompound) nbt;
        instance.ticksToArmorDamage = tag.getInteger(LAST_ARMOR_TIMER_NBT);
        instance.ticksToShieldDamage = tag.getInteger(LAST_SHIELD_TIMER_NBT);
    }
    //** NBT **//

    public static class Handler {
        private static final ResourceLocation KEY = new ResourceLocation(BHT.MOD_ID, "HURT");

        @SubscribeEvent
        public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof EntityLivingBase) {
                event.addCapability(KEY, Capabilities.HURT_LIMITER.getDefaultInstance());
                try {
                    BHTAPI.field.setInt(((EntityLivingBase) event.getObject()), -1);
                } catch(Exception ignored) {
                }
            }
        }

        @SubscribeEvent
        public void clonePlayer(PlayerEvent.Clone event) {
            event.getEntityPlayer().getCapability(Capabilities.HURT_LIMITER, null)
                    .deserializeNBT(event.getOriginal().getCapability(Capabilities.HURT_LIMITER, null).serializeNBT());
        }
    }
}
