package arekkuusu.betterhurttimer.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class HurtCameraEffectMixin {

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    private void hurtCameraEffect(float partialTicks, CallbackInfo info) {
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).hurtTime > 0) {
            info.cancel();
        }
    }
}
