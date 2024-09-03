package ice;

import ice.Alon.AlonContentLoad;
import mindustry.mod.Mod;

public class Ice extends Mod {
    /**
     * 模组代码使用的name,切勿更改!!
     */
    public static final String NAME = "ice";
    /**
     * 游戏内显示的模组介绍
     */
    public static String displayName;
    /**
     * <老婆们可爱捏>模组主要参与负责人</老婆们可爱捏>
     */
    private static final String[] author = {"Alon,洋葱,Elegy"};

    public Ice() {
    }

    /**
     * 初始化mod内容 一般来说你们不用动
     */
    @Override
    public void init() {
        AlonContentLoad.init();
    }

    /**
     * 用于加载content内容
     */
    @Override
    public void loadContent() {
        AlonContentLoad.load();
    }


}
