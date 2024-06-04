package antigriefer;

import mindustry.net.Administration;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.liquid.Conduit;

/**
 * Context of detection
 *
 * @author abomb4 2023-10-04
 */
public class PlayerBehaviorContext {

    public final String id;

    /** Block built, but not Conveyor or Conduit */
    public int blockBuilt = 0;

    /** Destructed {@link GrieferDetectorDestructorImpl} */
    public int blockDestructed = 0;

    public long temporaryBlacklistUntil = 0;

    public long temporaryWhitelistUntil = 0;

    public PlayerBehaviorContext(String id) {
        this.id = id;
    }

    /**
     * Invoke on action
     *
     * @param action Mindustry action
     */
    public void onPlayerAction(Administration.PlayerAction action) {
        if (action.type == Administration.ActionType.placeBlock
            && !(action.block instanceof Conveyor)
            && !(action.block instanceof Conduit)
        ) {
            blockBuilt += 1;
        }
    }
}
