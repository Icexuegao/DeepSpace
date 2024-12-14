package ice.world.meta.stat;

import arc.Core;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public class IceStat extends Stat {
    /**
     * 营养浓度
     */
    public static final Stat 营养浓度 = new IceStat("nutrientConcentration") {{
        color = "[red]";
        Flable = true;
        defaultToken = "{shake}";
    }}, 配方 = new IceStat("formulas", StatCat.crafting) {
    }, 建造时间花费 = new IceStat("cost") {
    }, 建筑血量系数 = new IceStat("healthScaling") {
    }, 硬度 = new IceStat("hardness") {
    }, 是否用于建造 = new IceStat("buildable") {
    }, 状态重载时间 = new IceStat("effectTime", IceStatCat.special) {
    }, 状态效果 = new IceStat("effect", IceStatCat.special) {
    }, 破甲 = new IceStat("armorBreak") {
    }, 状态持续时间 = new IceStat("statusTime", IceStatCat.special) {
    }, 范围 = new IceStat("radius", IceStatCat.special);
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
        String s = Core.bundle.get("iceStat." + name);
        if (s.equals("???" + name + "???")) return Core.bundle.get("stat." + name);
        return s;
    }
}
