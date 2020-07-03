package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class HurtTimeMixin extends Entity {

    @Shadow public int hurtTime;
    @Shadow public int maxHurtTime;
    @Shadow public float attackedAtYaw;
    public float preAttackedAtYaw;
    public int preHurtTime;

    public HurtTimeMixin(World p_i1582_1_) {
        super(p_i1582_1_);
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
    }

    @Inject(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;hurtTime:I", shift = At.Shift.AFTER))
    public void hurtResistantTime(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        this.hurtResistantTime = BHTConfig.CONFIG.damageFrames.hurtResistantTime;
    }

    @Redirect(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;playHurtSound(Lnet/minecraft/util/DamageSource;)V"))
    public void playHurtSound(EntityLivingBase that, DamageSource source) {
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
