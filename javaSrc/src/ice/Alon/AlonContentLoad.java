package ice.Alon;

import ice.Alon.content.IceBlocks;
import ice.Alon.content.IceLiquids;
import ice.Alon.content.IceStatus;
import ice.Alon.content.items.IceItems;
import ice.Alon.ui.ContentInfoDialog;
import ice.Alon.ui.MenusDialogKt;
import ice.Alon.content.items.KtItems;

import static mindustry.Vars.ui;

/**
 * 用于辅助加载content内容防止主类紊乱
 */
public class AlonContentLoad {

    public static void init() {
        ui.content = new ContentInfoDialog();
        MenusDialogKt.init();
    }

    public static void load() {
        KtItems.Companion.load();
        IceItems.load();
        IceLiquids.load();
        IceStatus.load();
        IceBlocks.load();


       /* Events.on(EventType.ClientLoadEvent.class, (e) -> {
            IceDialog j = new IceDialog();
            j.addListener(new ToolUi.MyInputListener(j));
            j.margin(10, 20, 10, 20);
            j.defaults().pad(3).expand().fill();
            j.cont.defaults();
            j.table((t) -> t.image(Items.coal.fullIcon)).fillX().pad(20).row();
            Drawable flatDown = createFlatDown("ice-blood-over");
            Cell<TextButton> button = j.button("@back", Icon.left, new TextButton.TextButtonStyle(flatDown, flatDown, flatDown, Fonts.def), j::hide);
            button.height(64f).pad(10).row();
            button.marginLeft(27);
            j.cont.table((t) -> {
                t.image(Items.coal.fullIcon).size(500, 500).row();
                t.button("123", () -> {
                }).fillX().getTable().addListener(new ToolUi.MyInputListener(t));
            }).left();
            j.setStyle(new Dialog.DialogStyle() {{

                background = createFlatDown("ice-blood-over");
                 *//*   down = up = ToolUi.createFlatDown("ice-flat-down-base1");
                    font = Fonts.def;
                    fontColor = Color.valueOf("97abb7");
                    disabledFontColor = Color.gray;*//*
            }});
            j.cont.table((t) -> {
                int i = 0;
                for (Item item : Vars.content.items()) {
                    if (item instanceof IceItem) {
                        if (i <= 5) {
                            i++;
                            t.table((t1) -> {
                                t1.image(item.fullIcon).getTable().addListener(new ToolUi.MyInputListener(t1));
                                t1.row();
                                t1.setBackground(createFlatDown("ice-blood-over"));
                                t1.add(item.localizedName);
                            }).left().pad(10);
                        } else {
                            i = 0;
                            t.table((t1) -> {
                                t1.image(item.fullIcon);
                                t1.row();
                                t1.setBackground(createFlatDown("ice-blood-over"));
                                t1.add(item.localizedName);
                            }).pad(10).row();
                        }

                    }
                }
                var cells = t.getCells();
                for (int g = 0; g < cells.size; g++) {
                    cells.get(g).size(86);
                }
            }).fillY().fillX();
            j.show();
        });*/

        /* IcePlanets.load();
        Tool.load();
        stone();
        DisplayName.displayName();
        Game.load();
        TreeTech.load();*/
    }


    /*protected void stone() {
        Properties properties = FileTool.getJarFileProperties("mod.hjson");
        Display_Name = properties.getProperty("displayName");
    }*/
}
