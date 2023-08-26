package arekkuusu.betterhurttimer;

import net.minecraftforge.common.config.Config;

@Config(modid = BHT.MOD_ID, name = BHT.MOD_ID)
public class BHTConfig {

    @Config.Comment("Global Values")
    @Config.LangKey(BHT.MOD_ID + ".config.global")
    @Config.RequiresMcRestart
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
                    "^inFire$:10",
                    "^onFire:10",
                    "^lightningBolt$:10",
                    "^lava$:10",
                    "^hotFloor$:10",
                    "^inWall$:10",
                    "^cramming$:10",
                    "^cactus$:10",
                    "^fall$:0",
                    "^flyIntoWall$:0",
                    "^outOfWorld$:10",
                    "^generic$:5",
                    "^magic$:10",
                    "^wither$:10",
                    "^anvil$:10",
                    "^fallingBlock$:10",
                    "^dragonBreath$:10",
                    "^indirectMagic$:10",
                    "^thorns$:5",
                    "^explosion\\.player$:5"
            };
            @Config.RangeDouble(min = 0)
            public double nextAttackDamageDifference = 0.5D;
            @Config.RangeDouble(min = 0)
            public boolean nextAttackDamageDifferenceApply = true;
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
            @Config.Comment("Items that need a specific attack reload speed. [Overwrites mob specific attack threshold]" +
                    "\n\nFormat: [*mod:item)]:[*attack reload speed]" +
                    "\n* Item Source name -> Used to identify the item used." +
                    "\n* attack reload speed -> Attack reload speed before the attack is canceled." +
                    "\n\n\nExamples:  (when attack threshold is 1)" +
                    "\n- minecraft:iron_axe:2 -> Iron Axe can never attack." +
                    "\n- minecraft:iron_axe:1 -> Iron Axe can only attack when fully up." +
                    "\n- minecraft:iron_axe:0.5 -> Iron Axe can only attack when more than halfway up." +
                    "\n- minecraft:iron_axe:0 -> Iron Axe can always attack." +
                    "\n")
            public String[] itemSource = {
            };
            @Config.Comment("Damage Sources from direct hits." +
                    "\n\nExample: Players and Mobs melee Damage Source.\n")
            public String[] attackSources = {
                    "player",
                    "mob"
            };
            @Config.Comment("Compatability mode." +
                    "\n\nMixins will not apply when this is true.\n")
            public Boolean turnOffMixins = false;
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