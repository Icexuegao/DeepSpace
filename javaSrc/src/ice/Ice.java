package ice;

import ice.Alon.AlonContentLoad;
import mindustry.Vars;
import mindustry.mod.Mod;
import mindustry.mod.Mods;

public class Ice extends Mod {
    /**
     * Mods的LoadedMod 直接使用
     */
    public static Mods.LoadedMod ice;
    /**
     * 模组代码使用的name,切勿更改!!
     */
    public static final String NAME = "ice";
    /**
     * 模组主要参与负责人 ps:老婆们可爱捏
     */
    public static final String[] author = {"Alon,洋葱,Elegy"};

    public Ice() {
    }

    /**
     * 初始化mod内容
     */
    @Override
    public void init() {
        AlonContentLoad.INSTANCE.init();
    }

    /**
     * 用于加载content内容
     */
    @Override
    public void loadContent() {
        ice = Vars.mods.getMod(this.getClass());
        AlonContentLoad.INSTANCE.load();
    }


}
