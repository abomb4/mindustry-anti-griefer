package antigriefer;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.scene.Group;
import arc.scene.event.ClickListener;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;

import static mindustry.Vars.net;
import static mindustry.Vars.ui;

/**
 * Someone dangerous
 *
 * @author abomb4 2023-09-09
 */
public class InGamePlayerListFragment {

    /** Store every joined players including exited */
    public final SettingsModel settings;

    public Table content = new Table().marginRight(13f).marginLeft(13f);
    private final Interval timer = new Interval();

    private boolean visible = false;

    public InGamePlayerListFragment(SettingsModel settings) {
        this.settings = settings;
    }

    public void build(Group parent) {
        content.name = "anti-griefer";
        parent.fill(cont -> {
            cont.name = "anti-griefer";
            cont.visible(() -> visible);
            cont.update(() -> {
                if (visible && timer.get(20)) {
                    rebuild();
                    content.pack();
                    content.act(Core.graphics.getDeltaTime());
                    //hacky
                    Core.scene.act(0f);
                }
            });

            cont.right().marginRight(10).table(Tex.buttonTrans, pane -> {
                pane.label(() -> Core.bundle.format(Groups.player.size() == 1 ? "players.single" : "players",
                    Groups.player.size()));
                pane.row();

                pane.row();
                pane.pane(content).grow().scrollX(false);
                pane.row();

                pane.table(menu -> {
                    menu.defaults().growX().height(50f).fillY();
                    menu.name = "menu";

                    // menu.button("@server.bans", ui.bans::show).disabled(b -> net.client());
                    // menu.button("@server.admins", ui.admins::show).disabled(b -> net.client());
                    menu.button("@close", this::toggle);
                }).margin(0f).pad(10f).growX();

            }).touchable(Touchable.childrenOnly).minWidth(360f);
        });

        rebuild();

        parent.fill(full -> {
            full.center().right().button("X", this::toggle).width(80).width(80);
        });

        // Events.on(EventType.WorldLoadEvent.class, (event -> {
        //     Core.app.post(this::rebuild);
        // }));
    }

    public void rebuild() {
        content.clear();

        float h = 50f;
        boolean found = false;

        // players.clear();
        // Groups.player.copy(players);
        //
        // players.sort(Structs.comps(Structs.comparing(Player::team), Structs.comparingBool(p -> !p.admin)));

        for (var user : settings.players.values()) {
            found = true;

            Table button = new Table();
            button.left();
            button.margin(5).marginBottom(10);

            ClickListener listener = new ClickListener();

            Table iconTable = new Table() {
                @Override
                public void draw() {
                    super.draw();
                    if (user.online) {
                        Draw.colorMul(Pal.accent, listener.isOver() ? 1.3f : 1f);
                    } else {
                        Draw.colorMul(Pal.gray, 0.5f);
                    }
                    Draw.alpha(parentAlpha);
                    Lines.stroke(Scl.scl(4f));
                    Lines.rect(x, y, width, height);
                    Draw.reset();
                }
            };

            iconTable.margin(8);
            iconTable.add(new Image(Icon.trello).setScaling(Scaling.bounded)).grow();
            iconTable.name = user.name;

            button.add(iconTable).size(h);
            button.labelWrap("[#ffffff]" + user.name)
                .style(Styles.outlineLabel).width(170f).pad(10);
            button.add().grow();

            button.background(Tex.underline);

            var noStyle = new ImageButton.ImageButtonStyle() {{
                down = Styles.none;
                up = Styles.none;
                imageDownColor = Color.lightGray;
                imageUpColor = Color.white;
                imageOverColor = Color.lightGray;
            }};

            var yesStyle = new ImageButton.ImageButtonStyle() {{
                down = Styles.none;
                up = Styles.none;
                imageUpColor = Pal.accent;
                imageDownColor = Pal.accent;
                imageOverColor = Pal.accent.cpy().lerp(Color.white, 0.3f);
            }};

            button.add().growY();
            button.button(Icon.trash, settings.inBlackList(user.id) ? yesStyle : noStyle, () -> {
                Log.info("Toggle black list");
                settings.toggleBlackList(user.id);
                rebuild();
            }).marginRight(5);
            button.button(Icon.admin, settings.inWhiteList(user.id) ? yesStyle : noStyle, () -> {
                Log.info("Toggle white list");
                settings.toggleWhiteList(user.id);
                rebuild();
            }).marginRight(5);

            content.add(button).width(350f).height(h + 14);
            content.row();
        }

        if (!found) {
            content.add(Core.bundle.format("players.notfound")).padBottom(6).width(350f).maxHeight(h + 14);
        }

        content.marginBottom(5);
    }

    public void toggle() {
        visible = !visible;
        if (visible) {
            rebuild();
        } else {
            Core.scene.setKeyboardFocus(null);
        }
    }
}
