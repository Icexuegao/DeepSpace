package ice.Alon;

import arc.Events;
import ice.Alon.File.settings.SerializationStore;
import ice.Alon.Text.Text;
import ice.Alon.asundry.BaseTool.Tool;
import ice.Alon.content.IceLiquids;
import ice.Alon.content.IceStatus;
import ice.Alon.content.blocks.IceBlocks;
import ice.Alon.content.items.IceItems;
import ice.Alon.content.items.KtItems;
import ice.Alon.music.IceMusics;
import ice.Alon.ui.DisplayNameKt;
import ice.Alon.ui.dialogs.ContentInfoDialog;
import ice.Alon.ui.dialogs.MenusDialogKt;
import ice.Ice;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.world.meta.BuildVisibility;
import universecore.world.lightnings.LightningContainer;

/**
 * 用于辅助加载content内容防止主类紊乱
 */

public class AlonContentLoad {

    public static void init() {
        DisplayNameKt.load();
        SerializationStore.serializationStore.load();
        Vars.ui.content = new ContentInfoDialog();
        MenusDialogKt.load();
        Events.run(EventType.Trigger.drawOver, ()->{
        });
    }

    public static void load() {
        IceMusics.IceMusics.load();
        KtItems.Companion.load();
        IceItems.load();
        IceLiquids.load();
        IceStatus.load();
        IceBlocks.load();
        Tool.load();
        ice.Alon.asundry.Content.IceBlocks.load();
        new Text("12345");
       Vars.content.blocks().each((g)->{
          if (g.minfo.mod==Ice.ice){
              g.buildVisibility= BuildVisibility.shown;
          }
       });
      /* Vars.ui.hudGroup.update(()->{
           for(Tile tile : world.tiles){
               tile.overlay().drawBase(tile);
           }
       });*/
    }
}
