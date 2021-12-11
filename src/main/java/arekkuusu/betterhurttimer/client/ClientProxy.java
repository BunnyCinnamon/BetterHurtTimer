package arekkuusu.betterhurttimer.client;

import arekkuusu.betterhurttimer.common.proxy.IProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {
    // holi :)
    // a que hora sales por el pan? :3
    public static int preHurtRender;

    @Override
    public void setPreHurtTime(EntityLivingBase entity) {
        if(entity == Minecraft.getMinecraft().getRenderViewEntity()) {
            ClientProxy.preHurtRender = entity.hurtTime;
        }
    }
}