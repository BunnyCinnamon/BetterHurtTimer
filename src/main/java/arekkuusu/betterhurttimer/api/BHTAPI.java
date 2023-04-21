package arekkuusu.betterhurttimer.api;

import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class BHTAPI {

    public static final Function<LivingEntity, Function<CharSequence, HurtSourceInfo>> HURT_SOURCE_INFO_FUNCTION = e -> s -> new HurtSourceInfo(s, false, e.invulnerableDuration);
    public static final Function<HurtSourceInfo, Function<CharSequence, HurtSourceData>> HURT_SOURCE_DATA_FUNCTION = i -> s -> new HurtSourceData(i);
    public static final Object2ObjectMap<CharSequence, HurtSourceInfo> DAMAGE_SOURCE_INFO_MAP = new Object2ObjectArrayMap<>();
    public static final Map<ResourceLocation, Double> ATTACK_THRESHOLD_MAP = new LinkedHashMap<>();
    public static final Function<Entity, AttackInfo> INFO_FUNCTION = u -> new AttackInfo();

    public static boolean isCustom(@Nullable Entity entity) {
        ResourceLocation location = null;
        if (entity != null) {
            location = EntityType.getKey(entity.getType());
        }
        return BHTAPI.ATTACK_THRESHOLD_MAP.containsKey(location);
    }

    public static synchronized void addSource(HurtSourceInfo info) {
        BHTAPI.DAMAGE_SOURCE_INFO_MAP.put(new HurtSourceInfo.HurtType(info.sourceName), info);
    }

    public static synchronized void addAttacker(ResourceLocation location, double threshold) {
        BHTAPI.ATTACK_THRESHOLD_MAP.put(location, threshold);
    }

    public static LazyOptional<HurtSourceData> get(LivingEntity entity, DamageSource source) {
        HurtSourceInfo info = BHTAPI.DAMAGE_SOURCE_INFO_MAP.computeIfAbsent(source.getMsgId(), BHTAPI.HURT_SOURCE_INFO_FUNCTION.apply(entity));
        return Capabilities.hurt(entity).lazyMap(c ->
                c.hurtMap.computeIfAbsent(info.sourceName, BHTAPI.HURT_SOURCE_DATA_FUNCTION.apply(info))
        );
    }
}
