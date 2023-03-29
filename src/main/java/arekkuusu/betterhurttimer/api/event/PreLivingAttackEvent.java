package arekkuusu.betterhurttimer.api.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class PreLivingAttackEvent extends Event {

    private final LivingEntity entityLiving;
    private final DamageSource source;
    private float amount;

    public PreLivingAttackEvent(LivingEntity entityLiving, DamageSource source, float amount) {
        this.entityLiving = entityLiving;
        this.source = source;
        this.amount = amount;
    }

    public LivingEntity getEntityLiving() {
        return entityLiving;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
