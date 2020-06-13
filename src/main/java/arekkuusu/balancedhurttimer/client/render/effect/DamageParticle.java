package arekkuusu.balancedhurttimer.client.render.effect;

import arekkuusu.balancedhurttimer.BHTConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class DamageParticle extends Particle {

    public boolean grow = true;
    public float scale = 1.0F;
    public String text;
    public int damage;
    public int color;

    public DamageParticle(int damage, World world, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ) {
        super(world, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
        this.particleTextureJitterX = 0F;
        this.particleTextureJitterY = 0F;
        this.particleGravity = 0.1F;
        this.particleScale = 3F;
        this.particleMaxAge = 12;
        this.color = damage > 0 ? BHTConfig.RENDER_CONFIG.rendering.damageColor : BHTConfig.RENDER_CONFIG.rendering.healColor;
        this.text = Integer.toString(Math.abs(damage));
        this.damage = damage;
    }

    @Override
    public void renderParticle(BufferBuilder renderer, final Entity entity, final float x, final float y, final float z, final float dX, final float dY, final float dZ) {
        float rotationYaw = (-Minecraft.getMinecraft().player.rotationYaw);
        float rotationPitch = Minecraft.getMinecraft().player.rotationPitch;

        final float locX = ((float) (this.prevPosX + (this.posX - this.prevPosX) * x - interpPosX));
        final float locY = ((float) (this.prevPosY + (this.posY - this.prevPosY) * y - interpPosY));
        final float locZ = ((float) (this.prevPosZ + (this.posZ - this.prevPosZ) * z - interpPosZ));

        GL11.glPushMatrix();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glTranslatef(locX, locY, locZ);
        GL11.glRotatef(rotationYaw, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotationPitch, 1.0F, 0.0F, 0.0F);

        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glScaled(this.particleScale * 0.008D, this.particleScale * 0.008D, this.particleScale * 0.008D);
        GL11.glScaled(this.scale, this.scale, this.scale);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(true);

        final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        fontRenderer.drawStringWithShadow(this.text, -MathHelper.floor(fontRenderer.getStringWidth(this.text) / 2.0F) + 1, -MathHelper.floor(fontRenderer.FONT_HEIGHT / 2.0F) + 1, this.color);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glPopMatrix();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.grow) {
            this.particleScale *= 1.08F;
            if (this.particleScale > 3F * 2.0D) {
                this.grow = false;
            }
        } else {
            this.particleScale *= 0.96F;
        }
    }

    public int getFXLayer() {
        return 3;
    }
}
