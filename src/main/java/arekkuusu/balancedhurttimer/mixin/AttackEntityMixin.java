package arekkuusu.balancedhurttimer.mixin;

import arekkuusu.balancedhurttimer.api.event.PreLivingAttackEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class AttackEntityMixin {

    @Inject(method = "onLivingAttack(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void onLivingAttack(EntityLivingBase entity, DamageSource src, float amount, CallbackInfoReturnable<Boolean> info) {
        if (MinecraftForge.EVENT_BUS.post(new PreLivingAttackEvent(entity, src, amount))) {
            info.setReturnValue(false);
            info.cancel();
        }
    }
}
