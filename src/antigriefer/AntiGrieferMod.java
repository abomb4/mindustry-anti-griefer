package antigriefer;

import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;
import mindustry.net.Administration;

/**
 * Entrance
 *
 * @author abomb4 2023-09-04
 */
public class AntiGrieferMod extends Mod {

    public static AntiGrieferActionFilter filter;

    public InGamePlayerListFragment frag;

    @Override
    public void init() {
        Log.info("Initializing Anti-Griefer Mod.");

        SettingsModel settings = SettingsModel.load();
        filter = new AntiGrieferActionFilter(settings);

        Vars.netServer.admins.addActionFilter(filter);

        frag = new InGamePlayerListFragment(settings);

        Events.on(EventType.ClientLoadEvent.class, clientLoadEvent -> {
            frag.build(Vars.ui.hudGroup);
        });

        Events.on(EventType.PlayerJoin.class, (event -> {
            if (event.player.isLocal()) {
                return;
            }
            settings.playerJoin(event.player);
        }));

        Events.on(EventType.PlayerLeave.class, (event -> {
            if (event.player.isLocal()) {
                return;
            }
            settings.playerLeave(event.player);
        }));

        Log.info("Initializing Anti-Griefer Mod done.");
    }

}
