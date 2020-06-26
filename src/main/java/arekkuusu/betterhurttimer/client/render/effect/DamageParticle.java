package arekkuusu.betterhurttimer.client.render.effect;

import arekkuusu.betterhurttimer.BHTConfig;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class DamageParticle extends Particle {

    public boolean grow = true;
    public float scale;
    public String text;
    public int color;

    public DamageParticle(int damage, ClientWorld world, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ) {
        super(world, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
        this.particleGravity = 0.1F;
        this.scale = 1F;
        this.maxAge = 12;
        this.color = damage > 0 ? BHTConfig.Runtime.Rendering.damageColor : BHTConfig.Runtime.Rendering.healColor;
        this.text = Integer.toString(Math.abs(damage));
    }

    @Override
    public void renderParticle(@Nonnull IVertexBuilder buffer, @Nonnull ActiveRenderInfo renderInfo, float partialTicks) {
        Vector3d vec3d = renderInfo.getProjectedView();
        float rotationYaw = -renderInfo.getRenderViewEntity().rotationYaw;
        float rotationPitch = renderInfo.getRenderViewEntity().rotationPitch;

        float locX = (float)(MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - vec3d.getX());
        float locY = (float)(MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - vec3d.getY());
        float locZ = (float)(MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - vec3d.getZ());

        GL11.glPushMatrix();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glTranslatef(locX, locY, locZ);
        GL11.glRotatef(rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotationPitch, 1.0F, 0.0F, 0.0F);

        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glScaled(this.scale * 0.008D, this.scale * 0.008D, this.scale * 0.008D);
        GL11.glScaled(this.scale, this.scale, this.scale);

        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(true);

        final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        //fontRenderer.renderString(this.text, -MathHelper.floor(fontRenderer.getStringWidth(this.text) / 2.0F) + 1, -MathHelper.floor(fontRenderer.FONT_HEIGHT / 2.0F) + 1, this.color, );

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glPopMatrix();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.grow) {
            this.scale *= 1.08F;
            if (this.scale > 2F * 2.0D) {
                this.grow = false;
            }
        } else {
            this.scale *= 0.96F;
        }
    }

    @Override
    @Nonnull
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }
}
