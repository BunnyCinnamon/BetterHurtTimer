package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.HurtCapability;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.common.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class HurtTimeMixin extends Entity {

    @Shadow
    public int hurtTime;
    @Shadow
    public int maxHurtTime;
    @Shadow
    public float attackedAtYaw;
    public float preAttackedAtYaw;
    public int preHurtTime;
    public DamageSource preDamageSource;

    public HurtTimeMixin(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At("HEAD"))
    public void attackEntityFromBefore(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (this.hurtTime > 0) {
            this.preHurtTime = this.hurtTime;
        } else {
            this.preHurtTime = 0;
        }
        if (this.attackedAtYaw > 0) {
            this.preAttackedAtYaw = this.attackedAtYaw;
        } else {
            this.preAttackedAtYaw = 0;
        }
        //noinspection ConstantConditions
        BHT.getProxy().setPreHurtTime((LivingEntity) ((Object) this));
        this.preDamageSource = source;
    }

    @Redirect(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;hurtResistantTime:I", ordinal = 0))
    public int attackResistantOverride(LivingEntity target, DamageSource source) {
        if (Events.isAttack(this.preDamageSource)) {
            Entity attacker = this.preDamageSource.getTrueSource();
            LazyOptional<HurtCapability> optional = Capabilities.hurt(attacker);
            if (optional.isPresent()) {
                HurtCapability capability = optional.orElseThrow(UnsupportedOperationException::new);
                final AttackInfo attackInfo = capability.meleeMap.computeIfAbsent(target, Events.INFO_FUNCTION);
                if (attackInfo.override) {
                    attackInfo.override = false;
                    return target.maxHurtResistantTime;
                }
            }
        }
        return target.hurtResistantTime;
    }

    @Inject(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I", shift = At.Shift.AFTER))
    public void hurtResistantTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        this.hurtResistantTime = BHTConfig.Runtime.DamageFrames.hurtResistantTime;
    }

    @Redirect(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setEntityState(Lnet/minecraft/entity/Entity;B)V", ordinal = 2))
    public void turnOffSound(World world, Entity entity, byte b) {
        if (b == 2 || b == 33 || b == 36 || b == 37) {
            if (this.preHurtTime == 0) {
                world.setEntityState(entity, b);
            }
        }
    }

    @Redirect(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;playHurtSound(Lnet/minecraft/util/DamageSource;)V"))
    public void playHurtSound(LivingEntity that, DamageSource source) {
        if (this.preHurtTime == 0) {
            this.playHurtSound(source);
        }
    }

    @Inject(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At("TAIL"))
    public void attackEntityFromAfter(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (this.preHurtTime > 0) {
            this.hurtTime = this.preHurtTime;
        }
        if (this.preAttackedAtYaw > 0) {
            this.attackedAtYaw = this.preAttackedAtYaw;
        }
    }

    @Inject(method = "playHurtSound(Lnet/minecraft/util/DamageSource;)V", at = @At("HEAD"), cancellable = true)
    public void playHurtSound(DamageSource source, CallbackInfo info) {
        if (this.preHurtTime > 0) {
            info.cancel();
        }
    }

    @Shadow
    protected abstract void playHurtSound(DamageSource source);
}
