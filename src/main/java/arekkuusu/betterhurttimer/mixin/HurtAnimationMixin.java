package arekkuusu.betterhurttimer.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class HurtAnimationMixin {

    @Shadow
    public int hurtTime;

    @Inject(method = "animateHurt", at = @At("HEAD"), cancellable = true)
    public void performHurtAnimation(CallbackInfo info) {
        if (this.hurtTime > 0) {
            info.cancel();
        }
    }
}
