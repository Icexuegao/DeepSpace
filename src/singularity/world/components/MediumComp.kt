package singularity.world.components

import arc.Core
import arc.func.Floatp
import arc.func.Func
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Strings
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.Block
import singularity.Singularity

interface MediumComp {
  companion object {
    val mediumRegiom: Array<TextureRegion?> = arrayOfNulls(1)
  }

  //@Annotations.BindField("mediumCapacity")
  var mediumCapacity: Float

  //  @Annotations.BindField("lossRate")
  var lossRate: Float

  //  @Annotations.BindField("mediumMoveRate")
  var mediumMoveRate: Float

  // @Annotations.BindField("outputMedium")
  var outputMedium: Boolean

  //@Annotations.MethodEntry(entryMethod = "setBars")
  fun setHeatBars() {
    if (this is Block) {
      this.addBar("medium", Func { entity: Building ->
        val ent = entity as MediumBuildComp
        object : Bar(Prov { Core.bundle.get("misc.medium") + ":" + Strings.autoFixed(ent.mediumContains, 2) }, Prov { Pal.reactorPurple }, Floatp { ent.mediumContains / mediumCapacity }) {
          override fun draw() {
            super.draw()
            if (mediumRegiom[0] == null) mediumRegiom[0] = Singularity.getModAtlas("medium")
            Draw.rect(mediumRegiom[0], x + height, y + height / 2, height, height)
          }
        }
      })
    }
  }
}