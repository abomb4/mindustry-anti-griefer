package antigriefer;

import arc.Core;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.Time;
import mindustry.gen.Player;
import mindustry.net.Administration;

import java.util.Date;

import static mindustry.net.Administration.ActionType.*;

/**
 * Settings model
 *
 * @author abomb4 2023-09-04
 */
public class SettingsModel {

    /** Loose mode, no restrictions by default */
    public static final int MODE_LOOSE = 1;
    /** Strict mode, everyone cannot destruct or configure blocks by default */
    public static final int MODE_STRICT = 3;

    /** Internal setting key */
    private static final String SETTING_KEY = "anti-griefer-mod-settings";

    /** Permanent black list */
    public ObjectMap<String, PlayerInfo> players = new ObjectMap<>();

    /** Allowd actions for normal players */
    public ObjectSet<Administration.ActionType> allowedActions = new ObjectSet<>();

    /** Strict mode, see {@link #MODE_STRICT} and {@link #MODE_LOOSE}, default is STRICT. */
    public int strictMode = MODE_STRICT;

    public SettingsModel() {
        allowedActions.addAll(
            breakBlock, placeBlock, rotate, configure, withdrawItem, depositItem, control, buildSelect, command,
            removePlanned, commandUnits, commandBuilding, respawn, pickupBlock, dropPayload
        );
    }

    /**
     * Host can disable some action for normal players
     *
     * @return action is allowed
     */
    public boolean actionAllowed(Administration.ActionType type) {
        return allowedActions.contains(type);
    }

    /**
     * Set strict mode, using {@link #MODE_STRICT} or {@link #MODE_LOOSE}
     *
     * @param strictMode mode
     */
    public void setStrictMode(int strictMode) {
        this.strictMode = strictMode;
        this.save();
    }

    /**
     * Strict mode:
     * <p>
     * {@link #MODE_LOOSE}: No restrictions by default
     * <p>
     * {@link #MODE_STRICT}: Everyone cannot destruct or configure blocks by default
     *
     * @return Strict mode
     */
    public int getStrictMode() {
        return strictMode;
    }


    /**
     * Persist settings
     */
    private void save() {
        Core.settings.putJson(SETTING_KEY, SettingsModel.class, this);
    }

    /**
     * Load from settings, create new instance if not saved.
     *
     * @return Settings instance
     */
    static SettingsModel load() {
        return Core.settings.getJson(SETTING_KEY, SettingsModel.class, SettingsModel::new);
    }

    public boolean inWhiteList(String id) {
        PlayerInfo i = players.get(id);
        if (i == null) {
            return true;
        }
        return i.permanentWhite || Time.millis() <= i.whitUntilMillis;
    }

    public boolean inBlackList(String id) {
        PlayerInfo i = players.get(id);
        if (i == null) {
            return true;
        }
        return i.permanentBlack || Time.millis() <= i.whitUntilMillis;
    }

    public void addBlackList(Player player, String reason) {
        PlayerInfo i = players.get(player.uuid());
        if (i == null) {
            return;
        }
        i.blackReason = reason;
        i.permanentBlack = true;
        save();
    }

    public void addWhiteList(Player player) {
        PlayerInfo i = players.get(player.uuid());
        if (i == null) {
            return;
        }
        i.permanentWhite = true;
        save();
    }

    public void toggleBlackList(String id) {
        PlayerInfo i = players.get(id);
        if (i == null) {
            return;
        }
        i.permanentBlack = !i.permanentBlack;
        if (i.permanentBlack) {
            i.permanentWhite = false;
        }
        save();
    }

    public void toggleWhiteList(String id) {
        PlayerInfo i = players.get(id);
        if (i == null) {
            return;
        }
        i.permanentWhite = !i.permanentWhite;
        if (i.permanentWhite) {
            i.permanentBlack = false;
        }
        save();
    }

    public void playerJoin(Player player) {
        String id = player.uuid();
        PlayerInfo i = players.get(id);
        if (i == null) {
            players.put(id, new PlayerInfo(player.getInfo(), true));
        } else {
            i.online = true;
            i.lastJoin = new Date();
        }
        save();
    }

    public void playerLeave(Player player) {
        PlayerInfo i = players.get(player.uuid());
        if (i == null) {
            return;
        }
        i.online = false;
    }
}
