package universecore

import arc.Core
import arc.Events
import arc.scene.Group
import arc.util.Log
import arc.util.OS
import arc.util.Time
import ice.library.universecore.androidcore.AndroidFieldAccessHelper
import ice.library.universecore.androidcore.AndroidMethodInvokeHelper
import ice.library.universecore.desktop9core.DesktopFieldAccessHelper9
import ice.library.universecore.desktopcore.DesktopMethodInvokeHelper
import ice.library.world.Load
import mindustry.Vars
import mindustry.game.EventType.UnlockEvent
import mindustry.game.EventType.WorldLoadEvent
import mindustry.world.Block
import universecore.ui.fragments.SecondaryConfigureFragment
import universecore.ui.styles.UncStyles
import universecore.util.FieldAccessHelper
import universecore.util.MethodInvokeHelper
import universecore.util.handler.CategoryHandler
import universecore.util.handler.FieldHandler

/**UniverseCore主类,同时也是调用核心类,这里会保存各种可能会用到的默认实例以及许多必要实例
 * @author EBwilson
 */
object UncCore : Load {
  const val version = "2.0.0"
  var fieldAccessHelper: FieldAccessHelper = if (OS.isAndroid) AndroidFieldAccessHelper() else DesktopFieldAccessHelper9()
  var methodInvokeHelper: MethodInvokeHelper = if (OS.isAndroid) AndroidMethodInvokeHelper() else DesktopMethodInvokeHelper()

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

    UncStyles.load()
  }

  override fun init() {
    if (!Vars.net.server()) {
      //  Vars.ui.database = UncDatabaseDialog.make();
      val overlay = FieldHandler.getValueDefault<Group>(Vars.control.input, "group")
      FieldHandler.decache(Vars.control.input.javaClass)
      secConfig = SecondaryConfigureFragment()
      secConfig.build(overlay)
    }
    categories.init()
    //  Time.run(2, classes::finishGenerate);
  }
}