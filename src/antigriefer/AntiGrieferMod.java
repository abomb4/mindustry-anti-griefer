package antigriefer;

import arc.util.Log;
import mindustry.Vars;
import mindustry.mod.Mod;

/**
 * Entrance
 *
 * @author abomb4 2023-09-04
 */
public class AntiGrieferMod extends Mod {

    public static AntiGrieferActionFilter filter;

    @Override
    public void loadContent() {
        Log.info("Loading Anti-Griefer Mod.");

        AntiGrieferSettings settings = AntiGrieferSettings.load();
        filter = new AntiGrieferActionFilter(settings);

        Vars.netServer.admins.addActionFilter(filter);
    }

}
