package antigriefer;

import arc.math.Mathf;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.net.Administration;
import mindustry.world.Tiles;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.storage.CoreBlock;

/**
 * Detect who build nuke around core
 *
 * @author abomb4 2023-09-05
 */
public class GrieferDetectorNukeCoreImpl implements GrieferDetector {

    /** single thread program, maybe I can reuse this */
    private static final DetectResult result1 = new DetectResult();



    @Override
    public DetectResult detect(Administration.PlayerAction action, PlayerBehaviorContext context) {
        if (action.type == Administration.ActionType.placeBlock
            && action.block instanceof PowerGenerator pgblock
            && pgblock.explosionRadius > 0
            && pgblock.explosionDamage > 0
        ) {
            // is a core around this explosion block?
            final short x = action.tile.x, y = action.tile.y;
            // i'm lazy, just calculate a square instead of circle
            final Team team = action.player.team();

            int radius = Math.max((int) (pgblock.explosionRadius * 0.9f), pgblock.size / 2);

            int i, j;
            final Tiles tiles = Vars.world.tiles;
            for (i = Math.max(0, x - radius);
                 i < Math.min(tiles.width, x + radius);
                 i++) {
                for (j = Math.max(0, y - radius);
                     j < Math.min(tiles.height, y + radius);
                     j++) {
                    final Building build = tiles.get(i, j).build;
                    if (build instanceof CoreBlock.CoreBuild cb && cb.team == team) {
                        result1.level = EnumDetectResult.CONFIRMED;
                        result1.reason = Log.format(
                            "Player @ trying to build a @ at (@, @), close to core at (@, @), treat as griefer",
                            action.player.name, pgblock.localizedName, x, y, cb.tileX(), cb.tileY());
                        return result1;
                    }
                }
            }
        }

        result1.level = EnumDetectResult.NOPE;
        result1.reason = "";
        return result1;

    }
}
