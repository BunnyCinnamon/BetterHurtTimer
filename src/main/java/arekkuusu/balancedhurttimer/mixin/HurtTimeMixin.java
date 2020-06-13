package arekkuusu.balancedhurttimer.mixin;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class HurtTimeMixin {

    @Shadow
    public int hurtTime;
    @Shadow
    public int maxHurtTime;
    public int preHurtTime;

    @Inject(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At("HEAD"), cancellable = true)
    public void attackEntityFromBefore(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (this.hurtTime > 0) {
            this.preHurtTime = this.hurtTime;
        } else {
            this.preHurtTime = 0;
        }
        Object o = this;
        ((EntityLivingBase) o).hurtResistantTime = 0;
    }

    @Inject(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At("TAIL"), cancellable = true)
    public void attackEntityFromAfter(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (this.preHurtTime > 0) {
            this.hurtTime = this.preHurtTime;
        }
        Object o = this;
        ((EntityLivingBase) o).hurtResistantTime = 0;
    }

    @Inject(method = "playHurtSound(Lnet/minecraft/util/DamageSource;)V", at = @At("HEAD"), cancellable = true)
    public void playHurtSound(DamageSource source, CallbackInfo info) {
        if (this.preHurtTime > this.maxHurtTime / 2) {
            info.cancel();
        }
    }
}
