package ice.library.universecore

import arc.Core
import arc.Events
import arc.util.Log
import arc.util.OS
import arc.util.Time
import ice.library.universecore.androidcore.AndroidFieldAccessHelper
import ice.library.universecore.androidcore.AndroidMethodInvokeHelper
import ice.library.universecore.desktop9core.DesktopFieldAccessHelper9
import ice.library.universecore.desktopcore.DesktopMethodInvokeHelper
import ice.library.universecore.util.FieldAccessHelper
import ice.library.universecore.util.MethodInvokeHelper
import ice.library.universecore.util.handler.CategoryHandler
import ice.library.world.Load
import mindustry.game.EventType.UnlockEvent
import mindustry.game.EventType.WorldLoadEvent
import mindustry.world.Block

object UncCore : Load {
    var fieldAccessHelper: FieldAccessHelper
    var methodInvokeHelper: MethodInvokeHelper
    var categories: CategoryHandler = CategoryHandler()

    init {
        Log.info("[Universe Core] core loading")
        if (OS.isAndroid) {
            fieldAccessHelper = AndroidFieldAccessHelper()
            methodInvokeHelper = AndroidMethodInvokeHelper()
        } else {
            fieldAccessHelper = DesktopFieldAccessHelper9()
            methodInvokeHelper = DesktopMethodInvokeHelper()
        }

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
        // if (!Vars.net.server()) {
        //  Vars.ui.database = UncDatabaseDialog.make();
        //   Group overlay = FieldHandler.getValueDefault(Vars.control.input, "group");
        //   FieldHandler.decache(Vars.control.input.getClass());
        //   secConfig = new SecondaryConfigureFragment();
        //   secConfig.build(overlay);
        //  }
        categories.init()
        //  Time.run(2, classes::finishGenerate);
    }
}

