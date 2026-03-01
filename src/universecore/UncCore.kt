package universecore

import arc.Core
import arc.Events
import arc.scene.Group
import arc.scene.ui.layout.Table
import arc.util.Log
import arc.util.OS
import arc.util.Time
import ice.library.world.Load
import mindustry.Vars
import mindustry.game.EventType
import mindustry.game.EventType.UnlockEvent
import mindustry.game.EventType.WorldLoadEvent
import mindustry.world.Block
import singularity.Sgl
import universecore.android.AndroidImpl
import universecore.desktopcore.desktop.AccessibleHelper
import universecore.desktopcore.desktop.ClassHelper
import universecore.desktopcore.desktop.DesktopImpl
import universecore.desktopcore.desktop.FieldAccessHelper
import universecore.desktopcore.desktop.MethodInvokeHelper
import universecore.ui.fragments.SecondaryConfigureFragment
import universecore.util.handler.CategoryHandler
import universecore.util.handler.FieldHandler

/**UniverseCore主类,同时也是调用核心类,这里会保存各种可能会用到的默认实例以及许多必要实例
 * @author EBwilson
 */
object UncCore : Load {
  const val version = "2.0.0"

  lateinit var fieldAccessHelper: FieldAccessHelper
  lateinit var methodInvokeHelper: MethodInvokeHelper
  lateinit var classHelper: ClassHelper
  lateinit var accessibleHelper: AccessibleHelper

  private val dwd= if (OS.isAndroid)AndroidImpl() else  DesktopImpl()

  /**方块类别处理工具实例 */
  var categories = CategoryHandler()
  lateinit var secConfig: SecondaryConfigureFragment

  override fun setup() {
    Log.info("[Universe Core] core loading")

    Time.run(0f) {
      Events.on(UnlockEvent::class.java) { event ->
        if (event.content is Block) {
          categories.handleBlockFrag()
        }
      }
      Events.on(WorldLoadEvent::class.java) { _ ->
        Core.app.post { categories.handleBlockFrag() }
      }
    }
  }

  override fun init() {
    var toggler = FieldHandler.getValueDefault<Table>(Vars.ui.hudfrag.blockfrag, "toggler")

    Events.run(EventType.Trigger.update) {
      if (toggler.parent == null) {
        toggler = FieldHandler.getValueDefault(Vars.ui.hudfrag.blockfrag, "toggler")
        Core.app.post {
          Sgl.ui.toolBar.build()
        }
      }
    }

    if (!Vars.net.server()) {
      val overlay = FieldHandler.getValueDefault<Group>(Vars.control.input, "group")
      FieldHandler.decache(Vars.control.input.javaClass)
      secConfig = SecondaryConfigureFragment()
      secConfig.build(overlay)
    }
    categories.init()
  }
}