package antigriefer;

import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.net.Administration;
import mindustry.ui.dialogs.BaseDialog;

import static mindustry.Vars.netServer;
import static mindustry.Vars.ui;

/**
 * @author abomb4 2023-10-06
 */
public class SettingsDialog extends BaseDialog {

    public SettingsDialog(String title) {
        super("@mode.custom");

        addCloseButton();

        setup();
        shown(this::setup);
    }

    private void setup() {
        cont.clear();

        float w = 400f, h = 80f;

        Table table = new Table();

        ScrollPane pane = new ScrollPane(table);
        pane.setFadeScrollBars(false);

        if (netServer.admins.getAdmins().size == 0) {
            table.add("@server.admins.none");
        }

        for (Administration.PlayerInfo info : netServer.admins.getAdmins()) {
            Table res = new Table(Tex.button);
            res.margin(14f);

            res.labelWrap("[lightgray]" + info.lastName).width(w - h - 24f);
            res.add().growX();
            res.button(Icon.cancel, () -> {
                ui.showConfirm("@confirm", "@confirmunadmin", () -> {
                    netServer.admins.unAdminPlayer(info.id);
                    Groups.player.each(player -> {
                        if (player != null && !player.isLocal() && player.uuid().equals(info.id)) {
                            player.admin(false);
                        }
                    });
                    setup();
                });
            }).size(h).pad(-14f);

            table.add(res).width(w).height(h);
            table.row();
        }

        cont.add(pane);
    }
}
