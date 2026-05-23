package singularity.ui.fragments.override

import arc.Core
import arc.Events
import arc.func.Boolp
import arc.graphics.g2d.Draw
import arc.scene.Group
import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import ice.DeepSpace
import ice.core.IFiles
import ice.graphics.IceColor
import mindustry.Vars
import mindustry.core.Version
import mindustry.game.EventType.ResizeEvent
import mindustry.ui.fragments.MenuFragment
import singularity.Sgl
import singularity.Singularity
import universecore.UncCore
import universecore.scene.ui.BloomGroup
import universecore.util.handler.FieldHandler
import universecore.util.handler.MethodHandler
import kotlin.math.min

class SglMenuFrag :MenuFragment() {
  val spacea = IFiles.findModPng("spacea")

  var shown: Boolean = true
  val group = WidgetGroup()

  override fun build(parent: Group) {
    parent.clear()
    group.setFillParent(true)
    group.touchable = Touchable.childrenOnly
    group.visible { !Vars.ui.editor.isShown }
    parent.addChild(group)
    group.fill { x, y, w, h ->
      Draw.rect(spacea, w/2f,  h/2f, w, h)
    }
    val versionText = (if (Version.build == -1) "[#fc8140aa]" else "[#ffffffba]") + Version.combined()
    val modVersionText = "[#${IceColor.b4}]UniverseCore:${UncCore.version} ${DeepSpace.modDisplayName}:${DeepSpace.modVersion}"

    val bloomGroup = BloomGroup().apply {
      bloomIntensity = 0.7f
    }
    bloomGroup.setFillParent(true)

    group.fill { df: Table ->
      df.touchable = Touchable.disabled
      df.add(bloomGroup).grow()

      bloomGroup.fill { t ->

        t.defaults().top()
        t.visibility = Boolp { shown }
        val button = Image(Singularity.getModAtlas("logo"))
        button.clicked { Sgl.ui.mainMenu.show() }
        val i = t.top().add(button).size(940f, 270f)
        t.row()
        t.add(versionText).padTop(4f)
        t.row()
        t.add(modVersionText).padTop(2f)

        val r = Runnable {
          var scl = min(Core.graphics.height / 3.4f / Scl.scl(270f), 1f)
          scl = min(scl, min(Core.graphics.width / Scl.scl(940f), 1f))
          i.size(940 * scl, 270 * scl)
          t.invalidateHierarchy()
        }
        r.run()
        Events.on(ResizeEvent::class.java) { e: ResizeEvent? ->
          r.run()
        }
      }
    }

    group.fill { c->
      c.visibility = Boolp { shown }
      FieldHandler.setValueDefault(Vars.ui.menufrag, "container", c)
      FieldHandler.decache(Vars.ui.menufrag.javaClass)
      c.name = "menu container"
      MethodHandler.invokeTemp<SglMenuFrag, Any?>(this, if (Vars.mobile) "buildMobile" else "buildDesktop")
      Events.on(ResizeEvent::class.java) {
        MethodHandler.invokeTemp<SglMenuFrag, Any?>(
          this, if (Vars.mobile) "buildMobile" else "buildDesktop"
        )
      }
    }

  }
}