package arekkuusu.betterhurttimer.common.core;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class Plugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if("arekkuusu.betterhurttimer.mixin.DamageArmorMixin".equals(mixinClassName) && (hasClass("org.bukkit.plugin.Plugin") || hasClass("com.obscuria.obscureapi.ObscureAPI"))) {
            return false;
        }
        if("arekkuusu.betterhurttimer.mixin.DamageArmorMixinBukkit".equals(mixinClassName) && !hasClass("org.bukkit.plugin.Plugin")) {
            return false;
        }
        if("arekkuusu.betterhurttimer.mixin.DamageArmorMixinOverride".equals(mixinClassName) && !hasClass("com.robertx22.mine_and_slash.mixins.LivingEntityMixin")) {
            return false;
        }
        if("arekkuusu.betterhurttimer.mixin.DamageArmorMixinObscureApi".equals(mixinClassName) && !hasClass("com.obscuria.obscureapi.ObscureAPI")) {
            return false;
        }
        return true;
    }

    public boolean hasClass(String name)  {
        try {
            Class.forName(name, false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
