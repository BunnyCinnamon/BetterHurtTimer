package arekkuusu.betterhurttimer;

import net.minecraftforge.common.config.Config;

@Config(modid = BHT.MOD_ID, name = BHT.MOD_ID)
public class BHTConfig {

    @Config.Comment("Global Values")
    @Config.LangKey(BHT.MOD_ID + ".config.global")
    public static Values CONFIG = new Values();

    @Config.Comment("Client Values")
    @Config.LangKey(BHT.MOD_ID + ".config.render")
    public static RenderValues RENDER_CONFIG = new RenderValues();

    public static class Values {
        @Config.Comment("Indirect Sources independent of the attacker's attack speed")
        public final DamageFrames damageFrames = new DamageFrames();
        @Config.Comment("Direct Sources dependent on the attacker's attack speed and the vanilla iFrame time of the entity")
        public final AttackFrames attackFrames = new AttackFrames();
        @Config.Comment("Knockback Sources filter")
        public final KnockbackFrames knockbackFrames = new KnockbackFrames();

        public static class DamageFrames {
            @Config.Comment("Default value replacing vanilla's iFrame after being hurt")
            @Config.RangeInt(min = 0)
            public int hurtResistantTime = 0;
            @Config.Comment("Default value replacing vanilla's iFrame after armor damage." +
                    "\n# Used mainly to prevent armor from wearing down too fast" +
                    "\n# If the next attack deals more than the previous the difference is applied")
            @Config.RangeInt(min = 0)
            public int armorResistantTime = 5;
            @Config.Comment("Default value replacing vanilla's iFrame after shield damage." +
                    "\n# Used mainly to prevent shield from wearing down too fast" +
                    "\n# If the next attack deals more than the previous the difference is applied")
            @Config.RangeInt(min = 0)
            public int shieldResistantTime = 5;
            @Config.Comment("Damage sources that need a specific iFrame." +
                    "\n\nFormat: [*Damage Source name (Regex)]:[*Should damage stack between iFrames]:[*iFrame time]" +
                    "\n* Damage Source name -> Used to identify the type of damage you're receiving." +
                    "\n* Should damage stack between iFrames -> 'true' or 'false', when set to 'true' damage will always stack regardless of the iFrame, but it will only apply the damage every iFrame." +
                    "\n* iFrame time -> How often you can receive damage from this damage source." +
                    "\n\n\nExamples:" +
                    "\n- inFire:false:10 -> Source 'inFire' does not stack and only allows hits every 10 game ticks." +
                    "\n- inFire|lava:false:10 -> Sources 'inFire' or 'lava' do not stack and only allows hits every 10 game ticks (lava and fire will share the same iFrame)." +
                    "\n- arrow:true:10 - > Source 'arrow' does stack and hits the accumulated damage every 10 game ticks." +
                    "\n\n# If the next attack deals more than the previous the difference is applied" +
                    "\n")
            public String[] damageSource = {
                    "^inFire$:false:10",
                    "^lightningBolt$:false:10",
                    "^lava$:false:10",
                    "^hotFloor$:false:10",
                    "^inWall$:false:10",
                    "^cramming$:false:10",
                    "^cactus$:false:10",
                    "^fall$:false:0",
                    "^flyIntoWall$:false:0",
                    "^outOfWorld$:false:10",
                    "^generic$:false:5",
                    "^magic$:false:10",
                    "^wither$:false:10",
                    "^anvil$:false:10",
                    "^fallingBlock$:false:10",
                    "^dragonBreath$:false:10",
                    "^arrow$:true:10",
                    "^thrown$:true:10",
                    "^indirectMagic$:false:10",
                    "^thorns$:false:5",
                    "^explosion\\.player$:false:5",
                    "^skill$:true:20",
                    "^indirectSkill$:true:20"
            };
            @Config.RangeDouble(min = 0)
            public double nextAttackDamageDifference = 0.5D;
        }

        public static class AttackFrames {
            @Config.Comment("Attack reload speed before the attack is canceled (Players only)")
            @Config.RangeDouble(min = 0, max = 1)
            public double attackThresholdPlayer = 0.5;
            @Config.Comment("Default attack speed before the attack is canceled (Mobs only)")
            @Config.RangeDouble(min = 0, max = 1)
            public double attackThresholdDefault = 1;
            @Config.Comment("Mobs that need a specific attack threshold." +
                    "\n\nFormat: [*mod:entity]:[*Attack threshold]" +
                    "\n* mod:entity -> Id of the entity in-game." +
                    "\n* Attack threshold -> Attack reload speed before the attack is canceled." +
                    "\n\n\nExample:" +
                    "\n- minecraft:slime:1 -> 'Slime' from mod 'Minecraft' will only be able to attack when its attack reload time is 100%." +
                    "\n")
            public String[] attackThreshold = {
                    "minecraft:slime:1",
                    "tconstruct:blueslime:1",
                    "thaumcraft:thaumslime:1",
            };
            @Config.Comment("Damage Sources from direct hits." +
                    "\n\nExample: Players and Mobs melee Damage Source.\n")
            public String[] attackSources = {
                    "player",
                    "mob"
            };
        }

        public static class KnockbackFrames {
            @Config.Comment("Set this to false to activate 1.16+ knockback mechanics.")
            public boolean knockbackAsAChance = false;
            @Config.Comment("Damage Sources will not apply knockback when on this list.")
            public String[] knockbackExemptSource = {
                    "indirectSkill"
            };
        }
    }

    public static class RenderValues {
        public final Rendering rendering = new Rendering();

        public static class Rendering {
            public boolean doHurtCameraEffect = true;
            public boolean showDamageParticles = true;
            public int damageColor = 0xFF0000;
            public int healColor = 0x00FF00;
        }
    }
}