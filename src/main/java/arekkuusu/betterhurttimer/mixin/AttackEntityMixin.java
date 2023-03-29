package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.api.event.PreLivingAttackEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class AttackEntityMixin {

    private static float amountTemp;

    @Inject(method = "onLivingAttack", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private static void onLivingAttack(LivingEntity entity, DamageSource src, float amount, CallbackInfoReturnable<Boolean> info) {
        PreLivingAttackEvent event = new PreLivingAttackEvent(entity, src, amount);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            info.setReturnValue(false);
            info.cancel();
        } else {
            amountTemp = event.getAmount();
        }
    }

    @ModifyVariable(method = "onLivingAttack", at = @At(target = "Lnet/minecraftforge/common/MinecraftForge;EVENT_BUS:Lnet/minecraftforge/eventbus/api/IEventBus;", value = "FIELD", shift = At.Shift.BEFORE), remap = false)
    private static float onLivingAttackAmountSet(float amount) {
        return amountTemp;
    }
}
