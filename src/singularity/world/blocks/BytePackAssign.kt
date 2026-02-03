package singularity.world.blocks;

import arc.util.Log;
import arc.util.pooling.Pools;
import ice.ui.menusDialog.AchievementDialog;
import singularity.ui.fragments.notification.Notification;
import singularity.world.blocks.distribute.TargetConfigure;
import singularity.world.blocks.distribute.matrixGrid.MatrixGridBlock;
import singularity.world.blocks.distribute.matrixGrid.MatrixGridCore;
import universecore.util.DataPackable;

public class BytePackAssign {
  public static void assignAll() {
    try{
      DataPackable.assignType(TargetConfigure.typeID, param -> new TargetConfigure());
      DataPackable.assignType(MatrixGridCore.LinkPair.typeID, param -> Pools.obtain(MatrixGridCore.LinkPair.class, MatrixGridCore.LinkPair::new));
      DataPackable.assignType(MatrixGridBlock.typeID, param -> Pools.obtain(MatrixGridBlock.PosCfgPair.class, MatrixGridBlock.PosCfgPair::new));

      AchievementDialog.AchievementNotification.Companion.assign();
      Notification.Note.assign();
      Notification.Warning.assign();
      Notification.ResearchCompleted.assign();
      Notification.Inspired.assign();
    }catch(Throwable e){
      Log.err("some error happened, may fatal, details: ");
      Log.err(e);
    }
  }
}
