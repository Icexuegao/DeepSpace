package ice.Alon.world.meta.stat;

import arc.Core;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class IceStat extends Stat {
    /**
     * 营养浓度
     */
    public static Stat

            nutrientConcentration = new IceStat("nutrientConcentration") {{
        color = "[red]";
        Flable = true;
        defaultToken = "{shake}";
    }}, /**
     * 配方
     */
    formulas = new IceStat("formulas"), /**
     * 建造时间花费
     */
    cost = new IceStat("cost"),

    /**
     * 建筑血量系数
     */
    healthScaling = new IceStat("healthScaling"),

    /**
     * 硬度
     */
    hardness = new IceStat("hardness"),

    /**
     * 是否用于建造
     */
    buildable = new IceStat("buildable"),

    /**
     * 状态重载时间
     */
    effectTime = new IceStat("effectTime", IceStatCat.special),

    /**
     * 状态效果
     */
    effect = new IceStat("effect", IceStatCat.special),

    /** 破甲 */
    armorBreak = new IceStat("armorBreak"),

    /**
     * 状态持续时间
     */
    statusTime = new IceStat("statusTime", IceStatCat.special),

    /**
     * 范围
     */
    radius = new IceStat("radius", IceStatCat.special);


    /**
     * 字体颜色
     */
    public String color = "[lightgray]";
    /**
     * 特殊效果,如果Flable为false则无意义
     */
    public String defaultToken = "";
    /**
     * 是否启用Flable
     */
    public boolean Flable = false;

    public IceStat(String name, StatCat category) {
        super(name, category);
    }

    public IceStat(String name) {
        this(name, StatCat.general);
    }

    @Override
    public String localized() {
        String s = Core.bundle.get("IceStat." + name);
        if (s.equals("???" + name + "???")) return Core.bundle.get("stat." + name);
        return s;
    }
}
