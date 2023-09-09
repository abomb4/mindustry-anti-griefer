package antigriefer;

import mindustry.gen.Player;

/**
 * Someone dangerous
 *
 * @author abomb4 2023-09-09
 */
public class AntiGrieferDangerousPlayerEvent {

    public final Player player;

    public AntiGrieferDangerousPlayerEvent(Player player) {
        this.player = player;
    }
}
