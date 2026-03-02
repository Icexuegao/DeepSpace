package ice.library

import arc.Core
import arc.Events
import arc.graphics.Texture
import arc.input.KeyCode
import arc.struct.Seq
import arc.util.Tmp
import ice.graphics.TextureDelegate
import ice.library.world.Load
import ice.ui.menusDialog.AchievementDialog
import ice.world.content.blocks.distribution.conveyor.PackStack
import mindustry.Vars
import mindustry.game.EventType
import mindustry.gen.Groups

object EventType : Load {
  class AchievementUnlockEvent(var achievement: AchievementDialog.Achievement)
  class LogisticsHubFire

  private val contentInitEvent = Seq<()->Unit>()
  private val clientLoadEvent = Seq<()->Unit>()
  private val atlasPackEvent = Seq<()->Unit>()
  override fun setup() {
    //字体缩放模糊问题
    addClientLoadEvent {
      Core.atlas.textures.forEach {
        val fid = Texture.TextureFilter.nearest
        it.setFilter(fid, fid)
      }
    }
    //替换原版音轨
    Events.on(EventType.MusicRegisterEvent::class.java) {
      Vars.control.sound.bossMusic.clear()
      Vars.control.sound.ambientMusic.clear()
      Vars.control.sound.darkMusic.clear()
      //即使替换掉原版音轨,游戏内也会播放以前的音乐,我不知道这是为什么,所以我只能将他们clear()
      Vars.control.sound = ice.audio.SoundControl()
    }
    Events.on(EventType.AtlasPackEvent::class.java) {
      TextureDelegate.delegate.forEach {it()}
    }
    Events.on(EventType.AtlasPackEvent::class.java) {
      atlasPackEvent.forEach {it()}
    }
    Events.on(EventType.ContentInitEvent::class.java) {
      contentInitEvent.forEach {it()}
    }
    Events.on(EventType.ClientLoadEvent::class.java) {
      clientLoadEvent.forEach {it()}
    }

    var df: PackStack? = null
    Events.run(EventType.Trigger.update) {
      if (Core.input.isTouched) {
        if (!Core.input.keyDown(KeyCode.mouseLeft)) return@run
        val mouseWorld = Core.input.mouseWorld()
        val find = Groups.draw.find {entityc ->
          entityc is PackStack && entityc.dst2(mouseWorld.x, mouseWorld.y) <= 5 * 5
        } as? PackStack
        if (df == null) {
          df = find
        }
      } else {
        df = null
      }
      df?.let {
        if (!it.added) {
          return@run
        }
        val mouseWorld = Core.input.mouseWorld()
        val sub = Tmp.v1.set(mouseWorld).sub(df).scl(0.1f)
        if (!Vars.state.isPaused) it.move(sub)
      }
    }
  }

  fun addContentInitEvent(run: ()->Unit) {
    contentInitEvent.add(run)
  }

  fun addClientLoadEvent(run: ()->Unit) {
    clientLoadEvent.add(run)
  }

  fun addAtlasPackEvent(run: ()->Unit) {
    atlasPackEvent.add(run)
  }
}