package antigriefer;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.Group;
import arc.scene.event.ClickListener;
import arc.scene.event.InputEvent;
import arc.scene.event.Touchable;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.Label;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;

/**
 * Someone dangerous
 *
 * @author abomb4 2023-09-09
 */
public class InGamePlayerListFragment {

    public static final float WIDTH = 360f;
    public static final float HEIGHT = 500f;
    public static final float DEFAULT_X = 0f;
    public static final float DEFAULT_Y = 60f;
    /** Store every joined players including exited */
    public final SettingsModel settings;

    private static final Label.LabelStyle labelStyle = new Label.LabelStyle(Fonts.def, Color.white);

    public Table content = new Table().marginRight(13f).marginLeft(13f);
    private final Interval timer = new Interval();

    private boolean visible = false;
    private Table table;

    private boolean dragging = false;

    public float x = DEFAULT_X;
    public float y = DEFAULT_Y;
    public float touchBeginX = 0;
    public float touchBeginY = 0;

    public InGamePlayerListFragment(SettingsModel settings) {
        this.settings = settings;
    }

    public void build(Group parent) {
        content.name = "anti-griefer";
        table = new Table(cont -> {
            // }));
            // parent.fill(cont -> {
            cont.name = "anti-griefer-window";
            cont.top();
            cont.row().table(Tex.pane, t -> {
                Label label = new Label("AG", labelStyle);
                ClickListener cl = new ClickListener() {
                    @Override
                    public void touchDragged(InputEvent event, float x, float y, int pointer) {
                        if (pointer != pressedPointer || cancelled) {
                            return;
                        }
                        // pressed = isOver(event.listenerActor, x, y);
                        if (pressed && pointer == 0 && button != null && !Core.input.keyDown(button)) {
                            pressed = false;
                        }
                        if (!pressed) {
                            // Once outside the tap square, don't use the tap square anymore.
                            invalidateTapSquare();
                        }
                    }
                };
                label.addListener(cl);
                label.update(() -> {
                    if (cl.isPressed()) {
                        if (!dragging) {
                            dragging = true;
                            touchBeginX = Vars.control.input.getMouseX();
                            touchBeginY = Vars.control.input.getMouseY();
                        } else {
                            var tx = Vars.control.input.getMouseX() - touchBeginX;
                            var ty = Vars.control.input.getMouseY() - touchBeginY;
                            table.x = x + tx;
                            table.y = y + ty;
                        }
                    } else if (dragging) {
                        dragging = false;
                        if (Mathf.within(table.x, table.y, x, y, 5)) {
                            toggle();
                        } else {
                            x = table.x;
                            y = table.y;
                        }
                    }
                });
                t.add(label).top().left().minWidth(80f).height(40);

            }).left().top();

            cont.row().table(Tex.pane, listTable -> {

                listTable.visible(() -> visible);
                listTable.update(() -> {
                    if (visible && timer.get(20)) {
                        rebuild();
                        content.pack();
                        content.act(Core.graphics.getDeltaTime());
                        //hacky
                        Core.scene.act(0f);
                    }
                });

                listTable.right().marginRight(10).table(pane -> {
                    pane.label(() -> Core.bundle.format("players", Groups.player.size() - 1));
                    pane.row();

                    pane.row();
                    pane.pane(content).grow().scrollX(false);
                    pane.row();

                    pane.table(menu -> {
                        menu.defaults().growX().height(50f).fillY();
                        menu.name = "menu";

                        // menu.button("@server.bans", ui.bans::show).disabled(b -> net.client());
                        menu.button("@settings", Vars.ui.admins::show);
                        menu.button("@close", this::toggle);
                    }).margin(0f).pad(10f).growX();

                }).left().touchable(Touchable.childrenOnly).growX();
            });
        });
        table.left().bottom();
        table.setWidth(WIDTH);
        table.setHeight(HEIGHT);
        table.x = DEFAULT_X;
        table.y = DEFAULT_Y;
        parent.addChild(table);

        rebuild();
        Events.on(EventType.WorldLoadEvent.class, (event -> {
            visible = false;
        }));
        Events.on(EventType.ResizeEvent.class, (event -> {
            if (table.x + 80 > Core.scene.getWidth()) {
                table.x = x = DEFAULT_X;
                table.y = y = DEFAULT_Y;
            }
            if (table.y + 150 > Core.scene.getHeight()) {
                table.x = x = DEFAULT_X;
                table.y = y = DEFAULT_Y;
            }
        }));
    }

    public void rebuild() {
        content.clear();

        float h = 50f;
        boolean found = false;

        // players.clear();
        // Groups.player.copy(players);
        //
        // players.sort(Structs.comps(Structs.comparing(Player::team), Structs.comparingBool(p -> !p.admin)));

        for (var user : settings.players.values().toSeq().sort((o1, o2) -> {
            if (o1.online && !o2.online) {
                return 1;
            }
            if (!o1.online && o2.online) {
                return -1;
            }
            return o1.lastJoin.compareTo(o2.lastJoin);
        })) {
            found = true;

            Table buttonTable = new Table();
            buttonTable.left();
            buttonTable.margin(5).marginBottom(10);

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

            buttonTable.add(iconTable).size(h);
            buttonTable.labelWrap("[#ffffff]" + user.name)
                .style(Styles.outlineLabel).width(170f).pad(10);
            buttonTable.add().grow();

            buttonTable.background(Tex.underline);

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

            buttonTable.add().growY();
            buttonTable.button(Icon.trash, settings.inBlackList(user.id) ? yesStyle : noStyle, () -> {
                Log.info("Toggle black list");
                settings.toggleBlackList(user.id);
                rebuild();
            }).marginLeft(20).size(40, 40);
            buttonTable.button(Icon.admin, settings.inWhiteList(user.id) ? yesStyle : noStyle, () -> {
                Log.info("Toggle white list");
                settings.toggleWhiteList(user.id);
                rebuild();
            }).marginLeft(20).size(40, 40);

            content.add(buttonTable).width(350f).height(h + 14);
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
            table.setWidth(WIDTH);
            rebuild();
        } else {
            table.setWidth(80f);
            Core.scene.setKeyboardFocus(null);
        }
    }
}
