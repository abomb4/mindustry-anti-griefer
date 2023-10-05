package antigriefer;

/**
 * Context of detection
 *
 * @author abomb4 2023-10-04
 */
public class PlayerBehaviorContext {

    public final String id;

    public int blockBuilt = 0;

    public long temporatyBlacklistUntil = 0;

    public long temporatyWhitelistUntil = 0;

    public PlayerBehaviorContext(String id) {
        this.id = id;
    }
}
