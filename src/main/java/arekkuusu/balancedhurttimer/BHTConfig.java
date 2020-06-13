package arekkuusu.balancedhurttimer;

import net.minecraftforge.common.config.Config;

@Config(modid = BHT.MOD_ID, name = BHT.MOD_ID)
public class BHTConfig {

    @Config.Comment("Global Values")
    @Config.LangKey(BHT.MOD_ID + ".config.global")
    public static Values CONFIG = new Values();

    @Config.LangKey(BHT.MOD_ID + ".config.render")
    public static RenderValues RENDER_CONFIG = new RenderValues();

    public static class Values {
        public final AttackFrames attackFrames = new AttackFrames();

        public static class AttackFrames {
            public String[] filterSources = {
                    "source:skill|frames:true|timer:20"
            };
        }
    }

    public static class RenderValues {
        public final Rendering rendering = new Rendering();

        public static class Rendering {
            public boolean showDamageParticles = true;
            public int damageColor = 0xFF0000;
            public int healColor = 0x00FF00;
        }
    }
}