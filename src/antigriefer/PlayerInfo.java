package antigriefer;

import arc.struct.Seq;
import mindustry.net.Administration;

import java.util.Date;

/**
 * The player information that this mod focuses on
 *
 * @author abomb4 2023-10-04
 */
public class PlayerInfo {
    public String id;
    public String name;
    public transient boolean online;
    public Seq<String> usedNames = new Seq<>();

    public boolean permanentBlack;
    public boolean permanentWhite;
    public long blackUntilMillis;
    public long whitUntilMillis;

    public String blackReason;
    public Date lastJoin;

    public PlayerInfo() {
    }

    public PlayerInfo(Administration.PlayerInfo info, boolean online) {
        this.id = info.id;
        this.name = info.lastName;
        this.online = online;
    }

    public void useName(String name) {
        this.name = name;
        if (!this.usedNames.contains(name)) {
            this.usedNames.add(name);
        }
    }
}

