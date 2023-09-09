package antigriefer;

import arc.Core;
import arc.Events;
import arc.scene.Group;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.input.Binding;
import mindustry.net.Administration;

/**
 * Someone dangerous
 *
 * @author abomb4 2023-09-09
 */
public class AntiGrieferUI {

    /** Store every joined players including exited */
    private final ObjectMap<String, Administration.PlayerInfo> allJoinedPlayers = new ObjectMap<>();

    /**
     * copy PlayerListFragment
     */
    void rebuild() {

    }

    public void load(Group parent) {
        parent.fill(full -> {
            full.center().right().button("X", () -> {

            }).width(80).width(80);
        });

        Events.on(EventType.ClientLoadEvent.class, (event -> {
            rebuild();
        }));

        Events.on(EventType.WorldLoadEvent.class, (event -> {
            Core.app.post((this::rebuild));
        }));

        Events.on(EventType.PlayerJoin.class, (event -> {
            if (event.player.isLocal()) {
                return;
            }
            String id = event.player.uuid();
            Administration.PlayerInfo info = event.player.getInfo();
            allJoinedPlayers.put(id, info);
        }));

        // Core.input.keyTap(Binding.player_list)
    }
}
