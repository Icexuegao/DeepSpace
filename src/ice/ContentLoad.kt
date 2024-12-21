package ice

import arc.Core
import arc.scene.ui.layout.Table
import ice.content.*
import ice.content.blocks.IceBlocks
import ice.parse.JTContents
import ice.scene.MyInputListener
import ice.type.Incident
import ice.ui.Ui
import ice.ui.menus.MenusDialog
import ice.ui.menus.SettingValue
import mindustry.Vars
import mindustry.ctype.UnlockableContent
import mindustry.world.meta.BuildVisibility

object ContentLoad {
    fun init() {
        SettingValue.init()


        val t1 = Table()
        t1.x += 500
        t1.y +=100
        t1.button("传教") {
            Incident.announce("[red]<<传教>> 你的信仰疑似有点动摇[]", 9f)
        }
        Vars.ui.hudGroup.addChild(t1)
        val table = Table()
        table.table {
            it.add("窗口大小设置")
            it.addListener(MyInputListener.DragInputListener(table))
        }.row()
        table.x = 500f
        table.y = 500f
        table.slider(0f, Core.graphics.width.toFloat() - 10, 1f) {
            if (Vars.mobile) {
                MenusDialog.deepSpace.tableCell.height(it)
            } else {
                MenusDialog.deepSpace.tableCell.width(it)
            }
        }.with { it.name = "宽度" }.size(500f, 60f).row()
        table.slider(0f, Core.graphics.height.toFloat() - 10, 1f) {
            if (Vars.mobile) {
                MenusDialog.deepSpace.tableCell.width(it)
            } else {
                MenusDialog.deepSpace.tableCell.height(it)
            }

        }.with { it.name = "长度" }.size(500f, 60f).row()
        Vars.ui.menuGroup.addChild(table)
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
        Vars.content.each {
            if (it is UnlockableContent) {
                it.unlock()
            }
        }

    }

    fun load() {
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

