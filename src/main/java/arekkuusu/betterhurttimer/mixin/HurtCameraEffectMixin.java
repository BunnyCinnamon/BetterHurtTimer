package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class HurtCameraEffectMixin {

    @Inject(method = "hurtCameraEffect", at = @At(target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I", value = "FIELD", ordinal = 0), cancellable = true)
    private void hurtCameraEffect(MatrixStack matrixStackIn, float partialTicks, CallbackInfo info) {
        if (!BHTConfig.Runtime.Rendering.doHurtCameraEffect) {
            info.cancel();
        }
    }
}
