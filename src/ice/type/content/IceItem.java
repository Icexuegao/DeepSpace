package ice.type.content;

import arc.Core;
import arc.graphics.Color;
import arc.struct.Seq;
import ice.world.meta.stat.IceStat;
import ice.world.meta.stat.IceStats;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;

public class IceItem extends Item {
    /** 物品合集 */
    public static Seq<IceItem> items = new Seq<>();
    /** 营养浓度 */
    public float 营养浓度 = 0;

    public IceItem(String name) {
        this(name, Color.blue);
    }

    public IceItem(String name, Color color) {
        super(name, color);
        items.add(this);
        localizedName = Core.bundle.get(getContentType() + "." + name + ".name", name);
        description = Core.bundle.getOrNull(getContentType() + "." + name + ".description");
        details = Core.bundle.getOrNull(getContentType() + "." + name + ".details");
    }

    public IceItem(String name, String color) {
        this(name, Color.valueOf(color));
    }

    public IceItem(String name, Color color, int hardness) {
        this(name, color);
        this.hardness = hardness;
    }

    @Override
    public void init() {
        stats = new IceStats();
        super.init();
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setStats() {
        IceStats stats1 = (IceStats) stats;
        stats1.addPercent(IceStat.explosiveness, explosiveness);
        stats1.addPercent(IceStat.flammability, flammability);
        stats1.addPercent(IceStat.radioactivity, radioactivity);
        stats1.addPercent(IceStat.charge, charge);
        if (营养浓度 != 0) stats1.addPercentThrob(IceStat.营养浓度, 营养浓度, "[red]");
        stats1.add(IceStat.建造时间花费, cost);
        stats1.add(IceStat.硬度, hardness);
        stats1.add(IceStat.建筑血量系数, healthScaling);
        stats1.add(IceStat.是否用于建造, buildable);
    }

    /** 矿物物品类会自动创建对应的OreBlock */
    public static class OreItem extends IceItem {
        public static Seq<IceOreBlock> oreBlocks = new Seq<>();

        public OreItem(String name, Color color, int hardness) {
            super(name, color, hardness);
            new IceOreBlock("ORE" + name, this) {{
                oreBlocks.add(this);
                useColor = true;
                variants = 3;
                mapColor = itemDrop.color;
            }};
        }

        public OreItem(String name, String color, int hardness) {
            this(name, Color.valueOf(color), hardness);
        }
    }

    public static class IceOreBlock extends OreBlock {
        public IceOreBlock(String name, Item ore) {
            super(name, ore);
        }

        @Override
        public void drawBase(Tile tile) {
            super.drawBase(tile);
        }
    }
}
