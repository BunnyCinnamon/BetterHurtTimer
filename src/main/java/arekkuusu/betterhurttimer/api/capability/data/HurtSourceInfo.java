package arekkuusu.betterhurttimer.api.capability.data;

public class HurtSourceInfo {

    public final CharSequence sourceName;
    public final int waitTime;

    public HurtSourceInfo(CharSequence sourceName, int waitTime) {
        this.sourceName = sourceName;
        this.waitTime = waitTime;
    }
}
