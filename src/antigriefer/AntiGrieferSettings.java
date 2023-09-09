package antigriefer;

import arc.Core;
import arc.struct.Seq;

/**
 * Settings model
 *
 * @author abomb4 2023-09-04
 */
public class AntiGrieferSettings {

    /** Loose mode, no restrictions by default */
    public static final int MODE_LOOSE = 1;
    /** Strict mode, everyone cannot destruct or configure blocks by default */
    public static final int MODE_STRICT = 3;

    /** Internal setting key */
    private static final String SETTING_KEY = "anti-griefer-mod-settings";

    /** Permanent black list */
    private final Seq<String> blacklistIds = new Seq<>();

    /** Permanent white list */
    private final Seq<String> whitelistIds = new Seq<>();

    /** Players temporary add to white list, dropped if game exit */
    private final Seq<String> temporaryWhitelistIds = new Seq<>();

    /** Players temporary add to black list, dropped if game exit */
    private final Seq<String> temporaryBlacklistIds = new Seq<>();

    /** Strict mode, see {@link #MODE_STRICT} and {@link #MODE_LOOSE}, default is STRICT. */
    private int strictMode = MODE_STRICT;

    // region black list
    public void addBlackList(String id) {
        blacklistIds.add(id);
        save();
    }

    public void addTemporaryBlackList(String id) {
        temporaryBlacklistIds.add(id);
    }

    public void removeBlackList(String id) {
        blacklistIds.remove(id);
        save();
    }

    public void clearBlackList() {
        blacklistIds.clear();
        save();
    }

    public boolean inBlackList(String id) {
        return blacklistIds.contains(id) || temporaryBlacklistIds.contains(id);
    }
    // endregion black list

    // region white list
    public void addWhiteList(String id) {
        whitelistIds.add(id);
        save();
    }

    public void addTemporaryWhiteList(String id) {
        temporaryWhitelistIds.add(id);
    }

    public void removeWhiteList(String id) {
        whitelistIds.remove(id);
        save();
    }

    public void clearWhiteList() {
        whitelistIds.clear();
        save();
    }

    public boolean inWhiteList(String id) {
        return whitelistIds.contains(id) || temporaryWhitelistIds.contains(id);
    }
    // endregion black list

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
        Core.settings.putJson(SETTING_KEY, AntiGrieferSettings.class, this);
    }

    /**
     * Load from settings, create new instance if not saved.
     *
     * @return Settings instance
     */
    static AntiGrieferSettings load() {
        return Core.settings.getJson(SETTING_KEY, AntiGrieferSettings.class, AntiGrieferSettings::new);
    }
}
