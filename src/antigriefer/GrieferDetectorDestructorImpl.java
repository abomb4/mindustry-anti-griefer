package antigriefer;

import arc.util.Log;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.net.Administration;
import mindustry.world.Tiles;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.storage.CoreBlock;

/**
 * Idiot who destruct everything
 *
 * @author abomb4 2023-09-05
 */
public class GrieferDetectorDestructorImpl implements GrieferDetector {

    /** single thread program, maybe I can reuse this */
    private static final DetectResult result1 = new DetectResult();

    @Override
    public DetectResult detect(Administration.PlayerAction action, PlayerBehaviorContext context) {
        if (context.blockBuilt > 16) {
            result1.level = EnumDetectResult.NOPE;
            result1.reason = "";
            return result1;
        }

        if (action.type == Administration.ActionType.breakBlock) {
            context.blockDestructed += 1;
        } else if (action.type == Administration.ActionType.removePlanned) {
            context.blockDestructed += action.plans.length;
        } else {
            result1.level = EnumDetectResult.NOPE;
            result1.reason = "";
            return result1;
        }

        if (context.blockDestructed < (context.blockBuilt + 1) * 2) {
            result1.level = EnumDetectResult.DANGER;
            result1.reason = Log.format(
                "Player @ already destucted @ blocks, but built @ only, danger",
                action.player.name, context.blockDestructed, context.blockBuilt);
            return result1;
        } else {
            result1.level = EnumDetectResult.CONFIRMED;
            result1.reason = Log.format(
                "Player @ already destucted @ blocks, but built @ only, confirmed as griefer",
                action.player.name, context.blockDestructed, context.blockBuilt);
            return result1;
        }
    }
}
