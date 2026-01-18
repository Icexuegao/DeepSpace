package singularity.world.blocks.nuclear

import arc.func.Prov
import arc.math.Interp
import arc.math.Mathf
import arc.scene.actions.Actions
import arc.scene.ui.layout.Table
import arc.util.Align
import arc.util.Scaling
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.gen.Tex
import singularity.world.meta.SglStat
import singularity.world.meta.SglStatUnit
import kotlin.math.min

open class EnergyBuffer(name: String) : NuclearNode(name) {
    var minPotential: Float = 128f
    var maxPotential: Float = 1024f

  init {
    buildType= Prov(::EnergyBufferBuild)
  }
    override fun setStats() {
        super.setStats()
        stats.add(SglStat.minEnergyPotential, minPotential, SglStatUnit.neutronFlux)
        stats.add(SglStat.maxEnergyPotential, maxPotential, SglStatUnit.neutronFlux)
    }

    override fun appliedConfig() {
        super.appliedConfig()
        config(Array<Any>::class.java) { b: EnergyBufferBuild, arr: Array<Any> ->
          if (arr[0] is Boolean) {
            if (arr[0] as Boolean) b.output = arr[1] as Float
            else b.input = arr[1] as Float
          }
        }
    }

    inner class EnergyBufferBuild : NuclearNodeBuild() {
        var input: Float = minPotential
        var output: Float = minPotential
        var showing: Boolean = false
        var show: Runnable = Runnable {}
        var close: Runnable = Runnable {}

        override fun onConfigureBuildTapped(other: Building): Boolean {
            if (other === this) {
                if (!showing) {
                    show.run()
                    showing = true
                } else {
                    close.run()
                    showing = false
                }
                return false
            } else return super.onConfigureBuildTapped(other)
        }

        override fun buildConfiguration(table: Table) {
            showing = false
            table.table { t: Table? ->
              t!!.visible = false
              t.setOrigin(Align.center)

              t.table(Tex.pane) { ta: Table? ->
                ta!!.image(Icon.download).size(40f).get().setScaling(Scaling.fit)
                buildEnergySlider(ta, minPotential, maxPotential, { input }, { f: Float -> configure(arrayOf<Any>(false, f)) })
                ta.row()
                ta.image(Icon.upload).size(40f).get().setScaling(Scaling.fit)
                buildEnergySlider(ta, minPotential, maxPotential, { output }, { f: Float -> configure(arrayOf<Any>(true, f)) })
              }

              show = Runnable {
                t.visible = true
                t.pack()
                t.isTransform = true
                t.actions(
                  Actions.scaleTo(0f, 1f),
                  Actions.visible(true),
                  Actions.scaleTo(1f, 1f, 0.07f, Interp.pow3Out)
                )
              }
              close = Runnable {
                t.actions(
                  Actions.scaleTo(1f, 1f),
                  Actions.scaleTo(0f, 1f, 0.07f, Interp.pow3Out),
                  Actions.visible(false)
                )
              }
            }.fillY()
        }

        override fun updateTile() {
            super.updateTile()

            input = Mathf.clamp(input, minPotential, maxPotential)
            output = Mathf.clamp(output, minPotential, maxPotential)
        }

        override var inputPotential
           get() = min(input, getEnergy())
          set(value){}
        override var outputPotential
           get() =  min(output, getEnergy())
          private set(value) {}
        override fun write(write: Writes) {
            super.write(write)
            write.f(input)
            write.f(output)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            input = read.f()
            output = read.f()
        }
    }
}