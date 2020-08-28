package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.client.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class HurtCameraEffectMixin {

    @Inject(method = "hurtCameraEffect", at = @At(target = "Lnet/minecraft/entity/EntityLivingBase;hurtTime:I", value = "FIELD", ordinal = 0), cancellable = true)
    private void hurtCameraEffect(float partialTicks, CallbackInfo info) {
        if (!BHTConfig.RENDER_CONFIG.rendering.doHurtCameraEffect || (ClientProxy.preHurtRender > 0)) {
            info.cancel();
        }
    }
}
