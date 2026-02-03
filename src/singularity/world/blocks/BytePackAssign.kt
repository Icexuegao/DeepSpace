package singularity.world.blocks

import arc.util.Log
import arc.util.pooling.Pools
import ice.ui.menusDialog.AchievementDialog.AchievementNotification.Companion.assign
import singularity.ui.fragments.notification.Notification
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.blocks.distribute.matrixGrid.MatrixGridBlock
import singularity.world.blocks.distribute.matrixGrid.MatrixGridBlock.PosCfgPair
import singularity.world.blocks.distribute.matrixGrid.MatrixGridCore.LinkPair
import universecore.util.DataPackable

object BytePackAssign {
  fun assignAll() {
    try {
      DataPackable.assignType(TargetConfigure.typeID) { _: Array<Any?>? -> TargetConfigure() }
      DataPackable.assignType(LinkPair.typeID) { _: Array<Any?>? -> Pools.obtain(LinkPair::class.java) { LinkPair() } }
      DataPackable.assignType(MatrixGridBlock.typeID) { _: Array<Any?>? -> Pools.obtain(PosCfgPair::class.java) { PosCfgPair() } }

      assign()
      Notification.Note.assign()
      Notification.Warning.assign()
      Notification.ResearchCompleted.assign()
      Notification.Inspired.assign()
    } catch (e: Throwable) {
      Log.err("some error happened, may fatal, details: ")
      Log.err(e)
    }
  }
}