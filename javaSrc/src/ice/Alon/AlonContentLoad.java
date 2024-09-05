package ice.Alon;

import ice.Alon.File.settings.SerializationStore;
import ice.Alon.asundry.BaseTool.Tool;
import ice.Alon.content.IceLiquids;
import ice.Alon.content.IceStatus;
import ice.Alon.content.blocks.IceBlocks;
import ice.Alon.content.items.IceItems;
import ice.Alon.content.items.KtItems;
import ice.Alon.music.IceMusics;
import ice.Alon.ui.dialogs.ContentInfoDialog;
import ice.Alon.ui.dialogs.MenusDialogKt;
import mindustry.Vars;

/**
 * 用于辅助加载content内容防止主类紊乱
 */

public class AlonContentLoad {
    public static void init() {
        SerializationStore.serializationStore.load();
        Vars.ui.content = new ContentInfoDialog();
        MenusDialogKt.init();
    }

    public static void load() {
        IceMusics.IceMusics.load();
        KtItems.Companion.load();
        IceItems.load();
        IceLiquids.load();
        IceStatus.load();
        IceBlocks.load();
        Tool.load();
    }
}
