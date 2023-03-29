package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.client.ClientProxy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class HurtCameraEffectMixin {

    @Inject(method = "bobHurt", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I", value = "FIELD", ordinal = 0), cancellable = true)
    private void hurtCameraEffect(PoseStack poseStack, float partialTicks, CallbackInfo info) {
        if (!BHTConfig.Runtime.Rendering.doHurtCameraEffect || (ClientProxy.preHurtRender > 0)) {
            info.cancel();
        }
    }
}
