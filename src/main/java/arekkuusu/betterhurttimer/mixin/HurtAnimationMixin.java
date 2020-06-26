package arekkuusu.betterhurttimer.mixin;

import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public class HurtAnimationMixin {

    @Shadow
    public int hurtTime;

    @Inject(method = "performHurtAnimation()V", at = @At("HEAD"), cancellable = true)
    public void performHurtAnimation(CallbackInfo info) {
        if (this.hurtTime > 0) {
            info.cancel();
        }
    }

    @Inject(method = "handleStatusUpdate(B)V", at = @At("HEAD"), cancellable = true)
    public void handleStatusUpdate(byte id, CallbackInfo info) {
        if (id == 2 || id == 33 || id == 36 || id == 37) {
            if (this.hurtTime > 0) {
                info.cancel();
            }
        }
    }
}
