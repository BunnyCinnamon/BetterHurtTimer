package arekkuusu.balancedhurttimer.client;

import arekkuusu.balancedhurttimer.BHT;
import arekkuusu.balancedhurttimer.BHTConfig;
import arekkuusu.balancedhurttimer.client.render.effect.DamageParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID, value = Side.CLIENT)
public class Events {

    public static final String HEALTH_TAG = BHT.MOD_ID + ".health";

    @SubscribeEvent
    public static void displayDamage(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!entity.world.isRemote || !BHTConfig.RENDER_CONFIG.rendering.showDamageParticles) return;

        int currentHealth = (int) Math.ceil(entity.getHealth());
        if (entity.getEntityData().hasKey(HEALTH_TAG)) {
            int entityHealth = ((NBTTagInt) entity.getEntityData().getTag(HEALTH_TAG)).getInt();
            if (entityHealth != currentHealth) {
                displayParticle(entity, entityHealth - currentHealth);
            }
        }

        entity.getEntityData().setInteger(HEALTH_TAG, currentHealth);
    }

    public static void displayParticle(Entity entity, int damage) {
        if (damage == 0) return;

        World world = entity.world;
        double motionX = world.rand.nextGaussian() * 0.02;
        double motionY = 0.5f;
        double motionZ = world.rand.nextGaussian() * 0.02;
        Particle damageIndicator = new DamageParticle(damage, world, entity.posX, entity.posY + entity.height, entity.posZ, motionX, motionY, motionZ);
        Minecraft.getMinecraft().effectRenderer.addEffect(damageIndicator);
    }
}
