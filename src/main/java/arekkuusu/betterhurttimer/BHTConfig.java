package arekkuusu.betterhurttimer;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public final class BHTConfig {

    public static class Common {

        public final ForgeConfigSpec.IntValue hurtResistantTime;
        public final ForgeConfigSpec.IntValue armorResistantTime;
        public final ForgeConfigSpec.IntValue shieldResistantTime;
        public ForgeConfigSpec.ConfigValue<List<? extends String>> damageSource;
        public final ForgeConfigSpec.DoubleValue nextAttackDamageDifference;
        public final ForgeConfigSpec.BooleanValue nextAttackDamageDifferenceApply;
        public final ForgeConfigSpec.DoubleValue attackThresholdPlayer;
        public final ForgeConfigSpec.DoubleValue attackThresholdDefault;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> attackThreshold;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> attackSources;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> knockbackExemptSource;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings")
                    .push("server");
            // damageFrames
            builder.comment("Indirect Sources independent of the attacker's attack speed")
                    .push("damageFrames");
            hurtResistantTime = builder
                    .comment("Default value replacing vanilla's iFrame after being hurt.")
                    .defineInRange("hurtResistantTime", 0, 0, Integer.MAX_VALUE);
            armorResistantTime = builder
                    .comment("Default value replacing vanilla's iFrame after armor damage." +
                            "\n# Used mainly to prevent armor from wearing down too fast" +
                            "\n# If the next attack deals more than the previous the difference is applied")
                    .defineInRange("armorResistantTime", 5, 0, Integer.MAX_VALUE);
            shieldResistantTime = builder
                    .comment("Default value replacing vanilla's iFrame after shield damage." +
                            "\n# Used mainly to prevent shield from wearing down too fast" +
                            "\n# If the next attack deals more than the previous the difference is applied")
                    .defineInRange("shieldResistantTime", 5, 0, Integer.MAX_VALUE);
            damageSource = builder
                    .comment("Damage sources that need a specific iFrame." +
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
                    .defineList("damageSources", () -> Arrays.asList(
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
                            "^mob$:true:5",
                            "^skill$:true:20",
                            "^indirectSkill$:true:20"
                    ), o -> true);
            nextAttackDamageDifference = builder
                    .comment("How much more damage the next attack must have to be accepted within the i-Frame")
                    .defineInRange("nextAttackDamageDifference", 0.5D, Double.MIN_VALUE, Double.MAX_VALUE);
            nextAttackDamageDifferenceApply = builder
                    .comment("If false damage difference won't be reduced")
                    .define("nextAttackDamageDifferenceApply", true);
            builder.pop();
            // attackFrames
            builder.comment("Direct Sources dependent on the attacker's attack speed and the vanilla iFrame time of the entity")
                    .push("attackFrames");
            attackThresholdPlayer = builder
                    .comment("Attack reload speed before the attack is canceled (Players only).")
                    .defineInRange("attackThreshold.player", 0.5D, 0D, 1D);
            attackThresholdDefault = builder
                    .comment("Default attack speed before the attack is canceled (Mobs only).")
                    .defineInRange("attackThreshold.mob", 1D, 0D, 1D);
            attackThreshold = builder
                    .comment("Mobs that need a specific attack threshold." +
                            "\n\nFormat: [*mod:entity]:[*Attack threshold]" +
                            "\n* mod:entity -> Id of the entity in-game." +
                            "\n* Attack threshold -> Attack reload speed before the attack is canceled." +
                            "\n\n\nExample:" +
                            "\n- minecraft:slime:1 -> 'Slime' from mod 'Minecraft' will only be able to attack when its attack reload time is 100%." +
                            "\n")
                    .defineList("attackThreshold.customs", () -> Arrays.asList("minecraft:slime:1", "tconstruct:blueslime:1", "thaumcraft:thaumslime:1"), o -> true);
            attackSources = builder
                    .comment("Damage Sources from direct hits." +
                            "\n\nExample: Players and Mobs melee Damage Source.\n")
                    .define("attackSources", () -> Arrays.asList("player", "mob"), o -> true);
            builder.pop();
            // knockbackFrames
            builder.comment("Knockback Sources filter")
                    .push("knockbackFrames");
            //noinspection ArraysAsListWithZeroOrOneArgument
            knockbackExemptSource = builder
                    .comment("Damage Sources will not apply knockback when on this list.")
                    .defineList("exemptSources", () -> Arrays.asList("indirectSkill"), o -> true);
            builder.pop();
            builder.pop();
        }
    }

    public static class Client {

        public final ForgeConfigSpec.BooleanValue doHurtCameraEffect;
        public final ForgeConfigSpec.BooleanValue showDamageParticles;
        public final ForgeConfigSpec.IntValue damageColor;
        public final ForgeConfigSpec.IntValue healColor;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering")
                    .push("client");
            doHurtCameraEffect = builder
                    .comment("Set this to false to disable hurt camera animations.")
                    .define("hurtCameraAnimation.enabled", true);
            showDamageParticles = builder
                    .comment("Set this to false to disable health particles.")
                    .define("particles.enabled", true);
            damageColor = builder
                    .comment("The color of damage particles.")
                    .defineInRange("particles.color.damage", 0xFF0000, 0, Integer.MAX_VALUE);
            healColor = builder
                    .comment("The color of heal particles.")
                    .defineInRange("particles.color.heal", 0x00FF00, 0, Integer.MAX_VALUE);
            builder.pop();
        }
    }

    public static final class Holder {

        public static final Common COMMON;
        public static final ForgeConfigSpec COMMON_SPEC;

        public static final Client CLIENT;
        public static final ForgeConfigSpec CLIENT_SPEC;

        static {
            final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
            COMMON_SPEC = specPair.getRight();
            COMMON = specPair.getLeft();
        }

        static {
            final Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
            CLIENT_SPEC = specPair.getRight();
            CLIENT = specPair.getLeft();
        }
    }

    public static final class Setup {

        public static void client(final ModConfig config) {
            Runtime.Rendering.doHurtCameraEffect = Holder.CLIENT.doHurtCameraEffect.get();
            Runtime.Rendering.showDamageParticles = Holder.CLIENT.showDamageParticles.get();
            Runtime.Rendering.damageColor = Holder.CLIENT.damageColor.get();
            Runtime.Rendering.healColor = Holder.CLIENT.healColor.get();
        }

        public static void server(final ModConfig config) {
            // DamageFrames
            Runtime.DamageFrames.hurtResistantTime = Holder.COMMON.hurtResistantTime.get();
            Runtime.DamageFrames.armorResistantTime = Holder.COMMON.armorResistantTime.get();
            Runtime.DamageFrames.shieldResistantTime = Holder.COMMON.shieldResistantTime.get();
            Runtime.DamageFrames.shieldResistantTime = Holder.COMMON.shieldResistantTime.get();
            Runtime.DamageFrames.damageSource = Holder.COMMON.damageSource.get();
            Runtime.DamageFrames.nextAttackDamageDifference = Holder.COMMON.nextAttackDamageDifference.get();
            Runtime.DamageFrames.nextAttackDamageDifferenceApply = Holder.COMMON.nextAttackDamageDifferenceApply.get();
            // AttackFrames
            Runtime.AttackFrames.attackThresholdPlayer = Holder.COMMON.attackThresholdPlayer.get();
            Runtime.AttackFrames.attackThresholdDefault = Holder.COMMON.attackThresholdDefault.get();
            Runtime.AttackFrames.attackThreshold = Holder.COMMON.attackThreshold.get();
            Runtime.AttackFrames.attackSources = Holder.COMMON.attackSources.get();
            // KnockbackFrames
            Runtime.KnockbackFrames.knockbackExemptSource = Holder.COMMON.knockbackExemptSource.get();
        }
    }

    public static final class Runtime {

        public static class DamageFrames {
            public static int hurtResistantTime;
            public static int armorResistantTime;
            public static int shieldResistantTime;
            public static List<? extends String> damageSource;
            public static double nextAttackDamageDifference;
            public static boolean nextAttackDamageDifferenceApply;
        }

        public static class AttackFrames {
            public static double attackThresholdPlayer;
            public static double attackThresholdDefault;
            public static List<? extends String> attackThreshold;
            public static List<? extends String> attackSources;
        }

        public static class KnockbackFrames {
            public static List<? extends String> knockbackExemptSource;
        }

        public static class Rendering {
            public static boolean doHurtCameraEffect;
            public static boolean showDamageParticles;
            public static int damageColor;
            public static int healColor;
        }
    }
}