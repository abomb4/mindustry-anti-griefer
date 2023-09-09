package antigriefer;

import mindustry.gen.Player;

/**
 * Someone added to black list
 *
 * @author abomb4 2023-09-09
 */
public class AntiGrieferAddToBlackListEvent {

    public final Player player;

    public AntiGrieferAddToBlackListEvent(Player player) {
        this.player = player;
    }
}
