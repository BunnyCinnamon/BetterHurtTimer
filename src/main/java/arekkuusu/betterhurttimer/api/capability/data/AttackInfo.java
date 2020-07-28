package arekkuusu.betterhurttimer.api.capability.data;

public class AttackInfo {

    public int ticksSinceLastMelee;
    public boolean override;

    public AttackInfo(int ticksSinceLastMelee) {
        this.ticksSinceLastMelee = ticksSinceLastMelee;
    }
}
