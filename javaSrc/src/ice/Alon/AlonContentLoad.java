package ice.Alon;

import ice.Alon.content.IceBlocks;
import ice.Alon.content.IceItems;
import ice.Alon.content.IceLiquids;
import ice.Alon.content.IceStatus;
import ice.Alon.ui.IceContentInfoDialog;
import iceKotlin.Alon.KtItems;
import mindustry.Vars;

/**
 * 用于辅助加载content内容防止主类紊乱
 */
public class AlonContentLoad {
    public static void init() {
        Vars.ui.content = new IceContentInfoDialog();
    }

    public static void load() {
        KtItems.KtItems.INSTANCE.load();
        IceItems.load();
        IceLiquids.load();
        IceStatus.load();
        IceBlocks.load();
        /* IcePlanets.load();
        Tool.load();
        stone();
        DisplayName.displayName();
        Game.load();
        TreeTech.load();*/
    }
}
