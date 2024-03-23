package arekkuusu.betterhurttimer.api.capability.data;

public class HurtSourceData {

    public final HurtSourceInfo info;
    public double lastHurtAmount;
    public int lastHurtTick = -1;

    public HurtSourceData(HurtSourceInfo info) {
        this.info = info;
    }

    public void update() {
        if (this.lastHurtTick <= this.info.waitTime) {
            ++this.lastHurtTick;
        }
    }

    public boolean canApply() {
        return this.lastHurtTick > this.info.waitTime;
    }

    public void trigger() {
        if (this.canApply()) {
            this.lastHurtTick = 0;
            this.lastHurtAmount = 0;
        }
    }
}
