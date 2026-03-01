package singularity.ui.fragments.override

import arc.Core
import arc.Events
import arc.func.Boolp
import arc.scene.Group
import arc.scene.event.Touchable
import arc.scene.ui.Image
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.scene.ui.layout.WidgetGroup
import ice.DeepSpace
import ice.graphics.IceColor
import ice.library.IFiles
import ice.library.IFiles.appendModName
import mindustry.Vars
import mindustry.core.Version
import mindustry.game.EventType.ResizeEvent
import mindustry.ui.fragments.MenuFragment
import singularity.Sgl
import singularity.Singularity
import universecore.UncCore
import universecore.util.handler.FieldHandler
import universecore.util.handler.MethodHandler
import kotlin.math.min

class SglMenuFrag : MenuFragment() {
  val spacea = IFiles.findPng("spacea".appendModName())

  var shown: Boolean = true
  val group = WidgetGroup()
  override fun build(parent: Group) {
    parent.clear()
    group.setFillParent(true)
    group.touchable = Touchable.childrenOnly
    group.visible { !Vars.ui.editor.isShown }
    parent.addChild(group)
    //
    group.fill {
      it.image(spacea).grow()
    }
    //info icon
    /* if (mobile) {
      group.fill(c -> c.bottom().left().button("", new TextButton.TextButtonStyle() {{
        font = Fonts.def;
        fontColor = Color.white;
        up = infoBanner;
      }}, ui.about::show).size(84, 45).name("info"));
      group.fill(c -> c.bottom().right().button(Icon.discord, new ImageButton.ImageButtonStyle() {{
        up = discordBanner;
      }}, ui.discord::show).size(84, 45).name("discord"));
    } else if (becontrol.active()) {
      group.fill(c -> c.bottom().right().button("@be.check", Icon.refresh, () -> {
        ui.loadfrag.show();
        becontrol.checkUpdate(result -> {
          ui.loadfrag.hide();
          if (!result) {
            ui.showInfo("@be.noupdates");
          }
        });
      }).size(200, 60).name("becheck").update(t -> {
        t.getLabel().setColor(becontrol.isUpdateAvailable() ? Tmp.c1.set(Color.white).lerp(Pal.accent, Mathf.absin(5f, 1f)) : Color.white);
      }));
    }*/
    val versionText = (if (Version.build == -1) "[#fc8140aa]" else "[#ffffffba]") + Version.combined()
    val modVersionText = "[#${IceColor.b4}]UniverseCore:${UncCore.version} ${DeepSpace.displayName}:${DeepSpace.version}"

    group.fill { t: Table? ->
      t!!.defaults().top()
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

    group.fill { c: Table? ->
      c!!.visibility = Boolp { shown }
      FieldHandler.setValueDefault(Vars.ui.menufrag, "container", c)
      FieldHandler.decache(Vars.ui.menufrag.javaClass)
      c.name = "menu container"
      MethodHandler.invokeTemp<SglMenuFrag?, Any?>(this, if (Vars.mobile) "buildMobile" else "buildDesktop")
      Events.on(ResizeEvent::class.java) { event: ResizeEvent? -> MethodHandler.invokeTemp<SglMenuFrag?, Any?>(this, if (Vars.mobile) "buildMobile" else "buildDesktop") }
    }
  }
}