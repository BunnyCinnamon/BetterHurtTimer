package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class HurtCameraEffectMixin {

    int oldHurt;
    int oldTick;

    @Inject(method = "bobHurt", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;hurtTime:I", value = "FIELD", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    private void hurtCameraEffect(PoseStack poseStack, float partialTicks, CallbackInfo info) {
        if (!BHTConfig.Runtime.Rendering.doHurtCameraEffect) {
            info.cancel();
        }

        LivingEntity cameraEntity = (LivingEntity) Minecraft.getInstance().getCameraEntity();
        if (cameraEntity != null) {
            if (this.oldHurt == 0) {
                this.oldHurt = cameraEntity.hurtTime;
            }
            if (this.oldHurt > cameraEntity.hurtTime) {
                this.oldHurt = cameraEntity.hurtTime;
            }
            if (this.oldHurt < cameraEntity.hurtTime) {
                cameraEntity.hurtTime = this.oldHurt;
                if (this.oldTick != cameraEntity.tickCount) {
                    --this.oldHurt;
                }
                this.oldTick = cameraEntity.tickCount;
            }
        }
    }
}
