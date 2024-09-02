package ice;

import ice.Alon.AlonContentLoad;
import mindustry.mod.Mod;

public class Ice extends Mod {
    /**
     * 模组代码使用的name
     */
    public static final String NAME = "ice";
    /**
     * 游戏内显示的模组介绍
     */
    public static String Display_Name;
    /**
     * 模组主要参与负责人
     */
    public static final String[] Author = {"Alon,洋葱,Elegy"};


    public Ice() {
    }

    /**
     * 初始化mod内容 一般来说你们不用动
     */
    @Override
    public void init() {
        AlonContentLoad.init();
        super.init();
    }

    /**
     * 用于加载content内容
     */
    @Override
    public void loadContent() {
        AlonContentLoad.load();
    }


}
