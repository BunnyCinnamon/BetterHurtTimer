package arekkuusu.betterhurttimer;

import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.HurtCapability;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.betterhurttimer.common.command.CommandExport;
import arekkuusu.betterhurttimer.common.proxy.IProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(
        modid = BHT.MOD_ID,
        name = BHT.MOD_NAME,
        version = BHT.MOD_VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        certificateFingerprint = "72cd337644e68ff7257f69b2927894048793e577"
)
public class BHT {

    //Useful names
    public static final String MOD_ID = "betterhurttimer";
    public static final String MOD_NAME = "Better Hurt Timer";
    public static final String MOD_VERSION = "1.12.2-1.0.0.0";
    public static final String SERVER_PROXY = "arekkuusu." + MOD_ID + ".common.ServerProxy";
    public static final String CLIENT_PROXY = "arekkuusu." + MOD_ID + ".client.ClientProxy";

    @SidedProxy(serverSide = SERVER_PROXY, clientSide = CLIENT_PROXY)
    private static IProxy proxy;
    private static final BHT INSTANCE = new BHT();
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static IProxy getProxy() {
        return proxy;
    }

    @Mod.InstanceFactory
    public static BHT getInstance() {
        return INSTANCE;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        HurtCapability.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.initAttackFrames();
        this.initDamageFrames();
    }

    public void initAttackFrames() {
        String patternAttackFrames = "^(.*:.*):((\\d*\\.)?\\d+)$";
        Pattern r = Pattern.compile(patternAttackFrames);
        for (String s : BHTConfig.CONFIG.attackFrames.attackThreshold) {
            Matcher m = r.matcher(s);
            if (m.matches()) {
                BHTAPI.addAttacker(new ResourceLocation(m.group(1)), Double.parseDouble(m.group(2)));
            } else {
                BHT.LOG.warn("[Attack Frames Config] - String " + s + " is not a valid format");
            }
        }
    }

    public void initDamageFrames() {
        String patternAttackFrames = "^(.*):(true|false):?(\\d*)";
        Pattern r = Pattern.compile(patternAttackFrames);
        for (String s : BHTConfig.CONFIG.damageFrames.damageSource) {
            Matcher m = r.matcher(s);
            if (m.matches()) {
                BHTAPI.addSource(new HurtSourceInfo(m.group(1), Boolean.parseBoolean(m.group(2)), Integer.parseInt(m.group(3))));
            } else {
                BHT.LOG.warn("[Damage Frames Config] - String " + s + " is not a valid format");
            }
        }
    }

    @EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandExport());
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOG.warn("Invalid fingerprint detected!");
    }
}