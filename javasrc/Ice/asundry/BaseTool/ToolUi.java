package Ice.asundry.BaseTool;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.NinePatch;
import arc.graphics.g2d.TextureAtlas;
import arc.input.KeyCode;
import arc.scene.Element;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.style.Drawable;
import arc.scene.style.ScaledNinePatchDrawable;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.content.TechTree;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.ui.Fonts;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;

public class ToolUi {
    public static final TestTool tool = new TestTool();
    public static final Table menu = new Table();
    public static boolean menuFirst = false;
    public static boolean modeFirst = false;
    public static boolean contentFirst = false;
    public static boolean treeFirst = false;
    public static boolean sundryFirst = false;
    public static boolean unitFirst = false;
    public static boolean teamFirst = false;
    public static final float tableSize = 40;

    public static void toolUi() {
        Events.on(EventType.ClientLoadEvent.class, (e) -> {
            TextButton.TextButtonStyle text = new TextButton.TextButtonStyle() {{
                over = createFlatDown("ice-flat-down-base");
                down = up = createFlatDown("ice-flat-down-base1");
                font = Fonts.def;
                fontColor = Color.valueOf("97abb7");
                disabledFontColor = Color.gray;
            }};/** 按一次不显示*/
            TextButton.TextButtonStyle textBS = new TextButton.TextButtonStyle() {{/**按一次显示 */
                font = Fonts.def;
                over = checked = createFlatDown("ice-flat-down-base");
                down = up = createFlatDown("ice-flat-down-base1");
                checkedOverFontColor = checkedFontColor = overFontColor = Color.valueOf("#cbebff");
                fontColor = Color.valueOf("97abb7");
            }};
            menu.x = 1200;
            menu.y = 600;
            Vars.ui.hudGroup.addChild(menu);
            menu.image().color(Color.valueOf("#cbebff")).size(tableSize).get().addListener(new MyInputListener(menu));
            menu.row();
            menu.button("\ue811", textBS, () -> menuFirst = !menuFirst).size(tableSize).tooltip("工具箱").checked(menuFirst);
            menu.row();
            menu.button("\ue87c", textBS, () -> modeFirst = !modeFirst).visible(() -> menuFirst).size(tableSize).checked(b -> modeFirst).tooltip("游戏模式");
            menu.button("\ue873", textBS, tool::editor).visible(() -> menuFirst && modeFirst).size(tableSize).checked(b -> state.rules.editor).tooltip("编辑模式");
            menu.button("\ue85b", textBS, tool::resources).visible(() -> menuFirst && modeFirst).size(tableSize).checked(b -> state.rules.infiniteResources).tooltip("沙盒模式");
            // menu.button("\ue88d", textBS, tool::liftProhibition).visible(() -> menuFirst && modeFirst).size(tableSize).checked(b -> true).tooltip("解禁");
            menu.row();
            menu.button(Items.copper.emoji(), textBS, () -> contentFirst = !contentFirst).visible(() -> menuFirst).size(tableSize).checked(b -> contentFirst).tooltip("内容");
            menu.button("[acid]+[]" + Items.copper.emoji(), text, tool::coreAddItems).visible(() -> menuFirst && contentFirst).size(tableSize).tooltip("添加物品");
            menu.button("[red]×[]" + Items.copper.emoji(), text, tool::clearCoreItems).visible(() -> menuFirst && contentFirst).size(tableSize).tooltip("删除物品");
            menu.row();
            menu.button("\ue875", textBS, () -> treeFirst = !treeFirst).visible(() -> menuFirst).size(tableSize).checked(treeFirst).tooltip("科技");
            menu.button("[acid]+[]\ue875", text, tool::techTreeUnlock).visible(() -> menuFirst && treeFirst).size(tableSize).tooltip("解锁科技树");
            menu.button("[red]×[]\ue875", text, tool::techTreeLock).visible(() -> menuFirst && treeFirst).size(tableSize).tooltip("锁定科技树");
            menu.row();
            menu.button("\ue816", textBS, () -> sundryFirst = !sundryFirst).visible(() -> menuFirst).size(tableSize).checked(sundryFirst).tooltip("杂项");
            menu.button("\ue852", text, tool::clearLog).visible(() -> menuFirst && sundryFirst).size(tableSize).tooltip("清除运行日志");
            menu.button("\uf15b", text, tool::Blueprints).visible(() -> menuFirst && sundryFirst).size(tableSize).tooltip("蓝图大小限制解锁");
            menu.button("\ue864", text, tool::gameOver).visible(() -> menuFirst && sundryFirst).size(tableSize).tooltip("游戏胜利");
            menu.row();
            menu.button("\ue86d", textBS, () -> unitFirst = !unitFirst).visible(() -> menuFirst).checked(unitFirst).size(tableSize).tooltip("单位");
            menu.button("[red]x[]" + UnitTypes.alpha.emoji(), text, tool::killUnits).visible(() -> menuFirst && unitFirst).size(tableSize).tooltip("非玩家单位死亡");
            menu.button("[red]x[]" + UnitTypes.beta.emoji(), text, tool::killPlayerUnit).visible(() -> menuFirst && unitFirst).size(tableSize).tooltip("玩家单位死亡");
            menu.button("[red]x[]" + UnitTypes.gamma.emoji(), text, tool::killHeath).visible(() -> menuFirst && unitFirst).size(tableSize).tooltip("玩家单位扣除生命");
            menu.row();
            menu.button("\ue82c", textBS, () -> teamFirst = !teamFirst).visible(() -> menuFirst).checked(teamFirst).size(tableSize).tooltip("队伍");
            menu.button("[#" + Team.crux.color + "]队[]", text, () -> Vars.player.team(Team.crux)).visible(() -> menuFirst && teamFirst).size(tableSize);
            menu.button("[#" + Team.sharded.color + "]队[]", text, () -> Vars.player.team(Team.sharded)).visible(() -> menuFirst && teamFirst).size(tableSize);
            menu.button("[#" + Team.blue.color + "]队[]", text, () -> Vars.player.team(Team.blue)).visible(() -> menuFirst && teamFirst).size(tableSize);
            menu.button("[#" + Team.green.color + "]队[]", text, () -> Vars.player.team(Team.green)).visible(() -> menuFirst && teamFirst).size(tableSize);
            menu.button("[#" + Team.derelict.color + "]队[]", text, () -> Vars.player.team(Team.derelict)).visible(() -> menuFirst && teamFirst).size(tableSize);
            menu.button("[#" + Team.malis.color + "]队[]", text, () -> Vars.player.team(Team.malis)).visible(() -> menuFirst && teamFirst).size(tableSize);

        });
    }


    public static final class TestTool {
        public void gameOver() {
            for (Team team : Team.all) {
                if (team != Vars.player.team()) {
                    Vars.state.teams.get(team).cores.each(Building::kill);
                }
            }
            Vars.state.rules.waves = false;
        }

        /**
         * public void liftProhibition() {
         * Vars.content.blocks().each((b) -> b.placeableOn = !b.placeableOn);
         * }
         */

        public void Blueprints() {
            Vars.maxSchematicSize = 128;
        }

        public void resources() {
            Vars.state.rules.infiniteResources = !Vars.state.rules.infiniteResources;
        }

        public void clearCoreItems() {
            Vars.player.core().items.clear();
        }

        public void coreAddItems() {
            Vars.content.items().each((i) -> {
                CoreBlock.CoreBuild core = Vars.player.team().core();
                if (core != null) {
                    core.items.add(i, Vars.player.core().storageCapacity);
                }
            });
        }

        public void editor() {
            Vars.state.rules.editor = !Vars.state.rules.editor;
        }

        public void techTreeUnlock() {
            TechTree.all.each((t) -> t.content.unlock());
        }

        public void techTreeLock() {
            TechTree.all.each((t) -> t.content.clearUnlock());
        }

        public void killPlayerUnit() {
            Vars.player.unit().kill();
        }

        public void killHeath() {
            Vars.player.unit().health -= (float) 10;
        }

        public void killUnits() {
            Groups.unit.each((unit) -> {
                if (unit != Vars.player.unit()) {
                    unit.kill();
                }
            });
        }
        public void clearLog() {
            Vars.ui.consolefrag.clearMessages();
        }
    }


    public static final class MyInputListener extends InputListener {
        public final Element[] e;
        float statX;
        float statY;

        public MyInputListener(Element... e) {
            this.e = e;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
            statX = x;
            statY = y;
            return true;
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            for (Element e : e) {
                e.x += x - statX;
                e.y += y - statY;
            }

        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {

        }
    }

    public static Drawable createFlatDown(String name) {
        TextureAtlas.AtlasRegion region = Core.atlas.find(name);
        int[] splits = region.splits;

        ScaledNinePatchDrawable copy = new ScaledNinePatchDrawable(new NinePatch(region, splits[0], splits[1], splits[2], splits[3])) {
            public float getLeftWidth() {
                return 0;
            }

            public float getRightWidth() {
                return 0;
            }

            public float getTopHeight() {
                return 0;
            }

            public float getBottomHeight() {
                return 0;
            }
        };
        copy.setMinWidth(0);
        copy.setMinHeight(0);
        copy.setTopHeight(0);
        copy.setRightWidth(0);
        copy.setBottomHeight(0);
        copy.setLeftWidth(0);
        return copy;
    }
}
