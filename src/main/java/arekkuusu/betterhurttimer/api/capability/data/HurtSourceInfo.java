package arekkuusu.betterhurttimer.api.capability.data;

import arekkuusu.betterhurttimer.BHTConfig;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

public class HurtSourceInfo {

    public final String sourceName;
    public final boolean doFrames;
    public final int waitTime;

    public HurtSourceInfo(String sourceName, boolean doFrames, int waitTime) {
        this.sourceName = sourceName;
        this.doFrames = doFrames;
        this.waitTime = waitTime;
    }

    public static class HurtSourceData {

        public final HurtSourceInfo info;
        public DamageSource damageSource;
        public boolean canApply;
        public float amount;
        public float lastHurtAmount;
        public int lastHurtTick;
        public int tick;

        public HurtSourceData(HurtSourceInfo info) {
            this.info = info;
            this.canApply = true;
            this.lastHurtTick = info.waitTime + 1;
        }

        public void trigger() {
            this.tick = this.info.waitTime;
            this.canApply = false;
            if(lastHurtTick > info.waitTime) {
                this.lastHurtAmount = Integer.MIN_VALUE;
            }
        }

        public void accumulate(float damage) {
            this.amount += damage;
        }

        public void apply(Entity entity) {
            entity.hurtResistantTime = 0;
            entity.attackEntityFrom(this.damageSource, this.amount);
            entity.hurtResistantTime = 0;
            this.canApply = true;
            this.amount = 0;
        }
    }
}
