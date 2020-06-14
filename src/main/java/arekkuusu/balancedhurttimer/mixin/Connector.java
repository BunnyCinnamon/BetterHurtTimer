package arekkuusu.balancedhurttimer.mixin;

import arekkuusu.balancedhurttimer.BHT;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class Connector implements IMixinConnector {

    @Override
    public void connect() {
        BHT.LOG.log(Level.INFO, "[Connector] - Loaded Mixin classes for mod " + BHT.MOD_NAME);
        Mixins.addConfiguration("assets/" + BHT.MOD_ID + "/" + BHT.MOD_ID + ".mixins.json");
    }
}
