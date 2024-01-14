package Iceconent.World.Block;

import Iceconent.content.IceItems;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Nullable;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.graphics.Drawf;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.state;

public class chaixiehexin extends CoreBlock {
    public static final Stat yuming, kaile, xinling;

    static {
        xinling = new Stat("kongzhi", StatCat.function);
        yuming = new Stat("yuming", StatCat.items);
        kaile = new Stat("kaile", StatCat.general);
    }


    public float PowerProduction = 1;
    public float range = 40;
    public float craftTime = 80;
    public @Nullable ItemStack outputItem;
    public @Nullable ItemStack[] outputItems = null;
    private float progress;

    public chaixiehexin(String name) {
        super(name);
        consumePowerBuffered(4000.0F);
        buildType = chaixiehexinbuild::new; /**buildType来指定build*/
    }

    @Override
    public boolean canBreak(Tile tile) {
        /**  为true可拆卸 */
        /** state.teams.cores(tile.team()).size 判断单个队伍全局核心数量  */
        return state.teams.cores(tile.team()).size > 1;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.timePeriod = craftTime;
        if ((hasItems && itemCapacity > 0) || outputItems != null) {
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        }
        if (outputItems != null) {
            stats.add(Stat.output, StatValues.items(craftTime, outputItems));
        }
        /** 颜色代码格式为[#******] 不加#号默认为文本，加了为颜色代码 */
        stats.add(xinling, "控制范围[red]" + (int) range / 8 + "[]的单位");
        stats.add(kaile, "[#00F2FF]菜就多练，输不起就别玩[]");
        stats.add(kaile, "[#FB00FF]以前是以前，现在是现在。如果你一直拿以前当现在的话，哥们，你怎么不拿你刚出生时做对比啊[]");
        stats.add(yuming, true);
        stats.add(Stat.ammo, IceItems.redIce);
        stats.add(Stat.basePowerGeneration, 360f, StatUnit.powerSecond);
    }

    @Override
    public void init() {
        if (outputItems == null && outputItem != null) {
            outputItems = new ItemStack[]{outputItem};
        }
        if (outputItems != null) hasItems = true;
        super.init();
    }

    @Override
    /** 以替换  */ public boolean canReplace(Block other) {
        return other.alwaysReplace;
    }

    @Override
    /**可以放置*/ public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return state.teams.cores(team).size < 3;
    }

    public class chaixiehexinbuild extends CoreBuild {
        /**
         * Build代表实体一般不用调用Aunken用反射调用，所以Build一般是类名加Build
         */
        public int i;
        public int v = 0;

        @Override
        /** 鼠标放上去显示  */ public void drawSelect() {
            Drawf.dashRect(Color.white, x, y, 80, 160);
        }

        public int xunhuan() {
            if (v <= 29) {
                v++;
            } else {
                v = 1;
            }
            return v;
        }


        /**
         * 一直显示
         * Drawf.dashCircle(x, y, 40, colors[i]);//绘制圆
         * Fill.rect(x, y, 40, 40);绘制白色方块
         * Draw.rect(TR, x, y, 48, 48); 绘制方块框
         * Drawf.dashSquare(c, x, y, 40);
         * Drawf.dashSquare和Drawf.dashRect都行
         */
        @Override
        public void draw() {
            Drawf.dashRect(Color.white, x, y, 80, 160);
            Drawf.dashCircle(x, y, range, Color.valueOf("ffffff"));
            if (timer.get(7.5f)) {
                i = xunhuan();
                Draw.rect(new TextureRegion(Core.atlas.find("ice-java-mod-chaofeng" + i)), x, y);
            } else {
                Draw.rect(new TextureRegion(Core.atlas.find("ice-java-mod-chaofeng" + i)), x, y);
            }
        }

        /**
         * 存数据和读取数据都是用write和read  用于保存building实体里面的数据就是实体的值 ，不写的话，这个值下次会进入默认值。
         */
        public void outitem() {
            progress %= 1f;/**  重新定义加工时间，保证在[0-1]中 */
            /** getMaximumAccepted 获取最大接受数 */
            for (ItemStack output : outputItems) {
                if (acceptItem(this, output.item)) {
                    items.add(output.item, output.amount);
                }
            }
        }

        @Override
        /** updateTile在update里执行 写机制的地方   */ public void updateTile() {
            /** 检测单位并索引  */
            Units.nearbyEnemies(team, x, y, range, unit -> {
                /** 范围圆  */
                unit.team = this.team;
            });
            //更新图块
            super.updateTile();
            progress += getProgressIncrease(craftTime);/**获取加工时进度  */
            if (progress >= 1f) {
                outitem();
            }
            /**items,add给这个实体建筑添加物品
             * tile是地块
             * team队伍
             * rotation方块的旋转*/
        }

        @Override
        public float getPowerProduction() {
            return PowerProduction * 60;
        }


    }
}
