package ice

import ice.content.blocks.IceBlocks
import ice.library.drawUpdate.DrawUpdates
import ice.music.IceMusics
import ice.content.*
import ice.parse.JTContents
import ice.ui.Ui
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.world.meta.BuildVisibility

/**
 * 用于辅助加载kotlin内容防止主类紊乱
 */
object ContentLoad {
    /**来点单例*/
    fun init() {
        //  Vars.asyncCore.processes.clear().add(CeProcess())
        /*  Events.on(EventType.WorldLoadEndEvent::class.java) {
              object : DrawUpdate() {
                  lateinit var seq: Seq<Unit>
                  override fun update() {
                      seq = Groups.unit.copy()
                      super.update()
                  }

                  override var overall = false

                  override fun draw() {
                      seq.forEach {
                          if (it.type.name == UnitTypes.omura.name) {
                              for (i in polyVecs.indices) {
                                  val vec2 = Vec2(polyVecs[i].x, polyVecs[i].y)
                                  val vec22: Vec2 = if (i + 1 < polyVecs.size) {
                                      Vec2(polyVecs[i + 1].x, polyVecs[i + 1].y)
                                  } else {
                                      Vec2(polyVecs[0].x, polyVecs[0].y)
                                  }
                                  vec2.rotate(it.rotation)
                                  vec22.rotate(it.rotation)
                                  Draw.z(100f)
                                  Lines.line(it.x + vec2.x, it.y + vec2.y, it.x + vec22.x, it.y + vec22.y)
                              }
                          }

                      }
                      super.draw()
                  }
              }
          }*/
        Ui.load()
        DrawUpdates.load()
        Vars.content.each {
            if (it is UnlockableContent) {
                it.unlock()
            }
        }
    }

    fun load() {
        IceMusics.load()
        IceItems.load()
        IceLiquids.load()
        IceStatus.load()
        IceBlocks.load()
        IceUnitTypes.load()
        IcePlanets.load()
        IceWeathers.load()
        JTContents.load()
        text()
    }

    private fun text() {
        //TemperatureBlock("1111111111111111")
        // Noise2dBlock("1223")
        Vars.content.blocks().select { it.minfo.mod === Ice.ice }.forEach {
            it.buildVisibility = BuildVisibility.shown
        }
    }
}

