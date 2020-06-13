package arekkuusu.balancedhurttimer;

import arekkuusu.balancedhurttimer.api.BHTAPI;
import arekkuusu.balancedhurttimer.api.capability.HurtLimiterCapability;
import arekkuusu.balancedhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.balancedhurttimer.common.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = BHT.MOD_ID,
        name = BHT.MOD_NAME,
        version = BHT.MOD_VERSION,
        acceptedMinecraftVersions = "[1.12.2]",
        certificateFingerprint = "72cd337644e68ff7257f69b2927894048793e577"
)
public class BHT {

    //Useful names
    public static final String MOD_ID = "balancedhurttimer";
    public static final String MOD_NAME = "Balanced Hurt Timer";
    public static final String MOD_VERSION = "GRADLE:VERSION";
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
        HurtLimiterCapability.init();
        for (String filterSource : BHTConfig.CONFIG.attackFrames.filterSources) {

        }

        BHTAPI.add(new HurtSourceInfo("skill", true, 20));
    }

    @EventHandler
    public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
        LOG.warn("Invalid fingerprint detected!");
    }
}