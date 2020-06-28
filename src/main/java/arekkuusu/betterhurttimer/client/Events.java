package arekkuusu.betterhurttimer.client;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.client.render.effect.DamageParticle;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID, value = Dist.CLIENT)
public class Events {

    /*@SubscribeEvent
    public static void renderAfterWorld(RenderWorldLastEvent event) {
        IRenderTypeBuffer.Impl bufferIn = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        MatrixStack stack = event.getMatrixStack();
        stack.push();
        stack.translate(0.0D, 0.0D, 0.0D);
        stack.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
        stack.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = stack.getLast().getMatrix();
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
        int j = (int)(f1 * 255.0F) << 24;
        float f2 = (float)(-fontRenderer.func_238414_a_(new TranslationTextComponent("10")) / 2);
        fontRenderer.func_238416_a_(new TranslationTextComponent("10"), f2, 0, 553648127, false, matrix4f, bufferIn, true, j, 255);
        stack.pop();
    }*/

    @SubscribeEvent
    public static void displayDamage(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.world.isRemote || !BHTConfig.Runtime.Rendering.showDamageParticles) return;

        Capabilities.health(entity).ifPresent(cap -> {
        int currentHealth = (int) Math.ceil(entity.getHealth());
            if (cap.health != -1 && cap.health != currentHealth) {
                displayParticle(entity, cap.health - currentHealth);
            }
            cap.health = currentHealth;
        });
    }

    public static void displayParticle(Entity entity, int damage) {
        if (damage == 0) return;

        ClientWorld world = (ClientWorld) entity.world;
        double motionX = world.rand.nextGaussian() * 0.02;
        double motionY = 0.5f;
        double motionZ = world.rand.nextGaussian() * 0.02;
        Particle damageIndicator = new DamageParticle(damage, world, entity.getPosX(), entity.getPosY() + entity.getHeight(), entity.getPosZ(), motionX, motionY, motionZ);
        Minecraft.getInstance().particles.addEffect(damageIndicator);
    }
}
