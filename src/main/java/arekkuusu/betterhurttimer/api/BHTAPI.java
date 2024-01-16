package arekkuusu.betterhurttimer.api;

import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceData;
import arekkuusu.betterhurttimer.api.capability.data.HurtType;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class BHTAPI {

    public static final Function<HurtSourceInfo, Function<CharSequence, HurtSourceData>> HURT_SOURCE_DATA_FUNCTION = i -> s -> new HurtSourceData(i);
    public static final Object2ObjectMap<CharSequence, HurtSourceInfo> DAMAGE_SOURCE_INFO_MAP = new Object2ObjectArrayMap<>();
    public static final Map<ResourceLocation, Double> ATTACK_THRESHOLD_MAP = new LinkedHashMap<>();
    public static final Map<ResourceLocation, Double> ATTACK_ITEM_THRESHOLD_MAP = new LinkedHashMap<>();
    public static final Function<Entity, AttackInfo> INFO_FUNCTION = u -> new AttackInfo();

    public static Field field;
    static {
        field = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_184617_aD");
        field.setAccessible(true);
    }

    public static boolean isCustom(@Nullable Entity entity) {
        ResourceLocation location = null;
        if (entity != null) {
            EntityEntry entry = EntityRegistry.getEntry(entity.getClass());
            if (entry != null) {
                location = entry.getRegistryName();
            }
        }
        return BHTAPI.ATTACK_THRESHOLD_MAP.containsKey(location);
    }

    public static synchronized void addSource(HurtSourceInfo info) {
        BHTAPI.DAMAGE_SOURCE_INFO_MAP.put(new HurtType(info.sourceName), info);
    }

    public static synchronized void addAttacker(ResourceLocation location, double threshold) {
        BHTAPI.ATTACK_THRESHOLD_MAP.put(location, threshold);
    }

    public static void addItem(ResourceLocation location, double threshold) {
        BHTAPI.ATTACK_ITEM_THRESHOLD_MAP.put(location, threshold);
    }

    public static Optional<HurtSourceData> get(EntityLivingBase entity, DamageSource source) {
        HurtSourceInfo info = BHTAPI.DAMAGE_SOURCE_INFO_MAP.get(source.getDamageType());
        if (info == null) return Optional.empty();
        return Capabilities.hurt(entity).map(c ->
                c.hurtMap.computeIfAbsent(info.sourceName, BHTAPI.HURT_SOURCE_DATA_FUNCTION.apply(info))
        );
    }
}
