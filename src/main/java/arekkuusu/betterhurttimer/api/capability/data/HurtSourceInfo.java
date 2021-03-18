package arekkuusu.betterhurttimer.api.capability.data;

import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HurtSourceInfo {

    public final CharSequence sourceName;
    public final boolean doFrames;
    public final int waitTime;

    public HurtSourceInfo(CharSequence sourceName, boolean doFrames, int waitTime) {
        this.sourceName = sourceName;
        this.doFrames = doFrames;
        this.waitTime = waitTime;
    }

    public static class HurtType implements CharSequence {

        public final CharSequence type;
        public final Pattern pattern;

        public HurtType(CharSequence type) {
            this.type = type;
            this.pattern = Pattern.compile(type.toString());
        }

        @Override
        public int length() {
            return type.length();
        }

        @Override
        public char charAt(int index) {
            return type.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return type.subSequence(start, end);
        }

        @Override
        @Nonnull
        public String toString() {
            return type.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CharSequence)) return false;
            CharSequence charSequence = (CharSequence) o;
            return type.equals(o) || pattern.matcher(charSequence).matches();
        }

        @Override
        public int hashCode() {
            return type.hashCode();
        }
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
            if (lastHurtTick > info.waitTime) {
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
