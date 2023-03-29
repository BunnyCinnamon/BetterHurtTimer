package arekkuusu.betterhurttimer.client;

import arekkuusu.betterhurttimer.common.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientProxy implements IProxy {
    // holi :)
    // a que hora sales por el pan? :3
    public static int preHurtRender;

    @Override
    public void setPreHurtTime(LivingEntity entity) {
        if(entity == Minecraft.getInstance().cameraEntity) {
            ClientProxy.preHurtRender = entity.hurtTime;
        }
    }
}