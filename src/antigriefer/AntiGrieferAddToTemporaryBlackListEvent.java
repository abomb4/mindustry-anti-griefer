package antigriefer;

import mindustry.gen.Player;

/**
 * Someone temporary added to black list
 *
 * @author abomb4 2023-09-09
 */
public class AntiGrieferAddToTemporaryBlackListEvent {

    private final Player player;

    public AntiGrieferAddToTemporaryBlackListEvent(Player player) {
        this.player = player;
    }
}
