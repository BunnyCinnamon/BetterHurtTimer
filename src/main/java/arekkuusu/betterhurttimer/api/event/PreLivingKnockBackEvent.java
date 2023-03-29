package arekkuusu.betterhurttimer.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class PreLivingKnockBackEvent extends Event {

    private final LivingEntity entityLiving;
    private final DamageSource source;

    public PreLivingKnockBackEvent(LivingEntity entityLiving, DamageSource source) {
        this.entityLiving = entityLiving;
        this.source = source;
    }

    public LivingEntity getEntityLiving() {
        return entityLiving;
    }

    public DamageSource getSource() {
        return source;
    }
}
