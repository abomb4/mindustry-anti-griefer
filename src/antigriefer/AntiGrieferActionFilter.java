package antigriefer;

import arc.Core;
import arc.Events;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.net.Administration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Anti-Griefer MOD main logic
 *
 * @author abomb4 2023-09-04
 */
public class AntiGrieferActionFilter implements Administration.ActionFilter {

    /** settings for this mod */
    private final AntiGrieferSettings settings;

    private final List<GrieferDetector> grieferDetectors = new ArrayList<>();

    /**
     * Construct
     *
     * @param settings settings for this mod
     */
    public AntiGrieferActionFilter(AntiGrieferSettings settings) {
        this.settings = settings;

        Events.on(EventType.PlayerJoin.class, playerJoin -> {
            String id = playerJoin.player.uuid();

        });
    }

    @Override
    public boolean allow(Administration.PlayerAction action) {

        if (action.player == Vars.player) {
            return true;
        }

        final String id = action.player.uuid();
        Log.info("@ Player @ (@) doing @", new Date(), action.player.name, id, action.type);
        // whitelist first
        if (settings.inWhiteList(id)) {
            return true;
        }

        // blacklist cannot do anything
        if (settings.inBlackList(id)) {
            return false;
        }

        GrieferDetector.EnumDetectResult topLevel = GrieferDetector.EnumDetectResult.NOPE;

        for (final GrieferDetector detector : this.grieferDetectors) {
            GrieferDetector.EnumDetectResult level = detector.detect(action);
            if (level.ordinal() > topLevel.ordinal()) {
                topLevel = level;
            }
        }

        final int mode = settings.getStrictMode();

        // 根据设置严格级别，识别可能的破坏者。
        // 如果可能为破坏者，暂时禁用他的动作，并提示主机‘本局允许’和‘加入白名单’
        // 破坏者可能有几种特征：
        // - 进来啥也不干上来就拆
        // - 没有制作任何东西

        if (mode == AntiGrieferSettings.MODE_LOOSE) {
            return switch (topLevel) {
                case NOPE -> {
                    yield true;
                }
                case CONFIRMED -> {
                    // Here is strict mode, go hell forever
                    Log.info("@ > Player @ (@) trying to @, griefer confirmed, add to black list.",
                        now(), action.player.name, id, action);
                    settings.addBlackList(id);
                    Events.fire(new AntiGrieferAddToBlackListEvent(action.player));
                    yield false;
                }
                case DANGER -> {
                    Log.info("@ > Player @ (@) trying to @, danger, popup.",
                        now(), action.player.name, id, action);
                    Events.fire(new AntiGrieferDangerousPlayerEvent(action.player));
                    yield true;
                }
            };
        } else {
            // AntiGrieferSettings.MODE_STRICT or others
            return switch (topLevel) {
                case NOPE ->{
                    // those action is not allowed, send a popup and avoid
                    boolean forbid = action.type == Administration.ActionType.breakBlock
                        || action.type == Administration.ActionType.configure
                        || action.type == Administration.ActionType.removePlanned
                        || action.type == Administration.ActionType.withdrawItem;
                    if (forbid) {
                        Log.info("@ > Player @ (@) trying to @, it's not allowed in STRICT mode",
                            now(), action.player.name, id, action);
                    }

                    yield !forbid;
                }
                case DANGER -> {
                    // Here is strict mode, go hell
                    Log.info("@ > Player @ (@) trying to @, danger, add to temp black list.",
                        now(), action.player.name, id, action);
                    settings.addTemporaryBlackList(id);
                    Events.fire(new AntiGrieferAddToTemporaryBlackListEvent(action.player));
                    yield false;
                }
                case CONFIRMED -> {
                    // Here is strict mode, go hell forever
                    Log.info("@ > Player @ (@) trying to @, griefer confirmed, add to black list.",
                        now(), action.player.name, id, action);
                    settings.addBlackList(id);
                    Events.fire(new AntiGrieferAddToBlackListEvent(action.player));
                    yield false;
                }
            };
        }
    }

    private static String now() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
}
