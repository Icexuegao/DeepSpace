package ice.content.block.product

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.util.Tmp
import ice.content.IItems
import ice.content.block.EnvironmentBlocks
import ice.library.struct.texture.LazyTextureSingleDelegate
import ice.ui.bundle.bundle
import ice.world.draw.DrawMulti
import mindustry.content.Fx
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawSideRegion
import mindustry.world.meta.BlockStatus
import singularity.world.blocks.product.NormalCrafter

class CrystalMiner : NormalCrafter("crystal_miner") {
  val rotatorBottomRegion: TextureRegion by LazyTextureSingleDelegate("$name-rotator-bottom")
  val rotatorRegion: TextureRegion by LazyTextureSingleDelegate("$name-rotator")
  val rotatorRegionHeat: TextureRegion by LazyTextureSingleDelegate("$name-rotator-heat")

  init {
    bundle {
      desc(zh_CN, "晶簇粉碎器")
    }
    hasItems = true
    size = 2
    rotate = true
    drawers = DrawMulti(DrawDefault(), object :DrawSideRegion(){
      override fun icons(block: Block?): Array<out TextureRegion?> {
        return arrayOf()
      }
    })
    buildType = Prov(::CrystalMinerBuild)
    rotateDraw = false
    updateEffect =Fx.mine
    updateEffectColor = IItems.燃素水晶.color
    newConsume().apply {
      power(120f / 60f)
      time(130f)
    }
    newProduce().apply {
      item(IItems.燃素水晶, 1)
    }
    ambientSound = Sounds.loopDrill
    ambientSoundVolume = 0.1f
    requirements(Category.production, IItems.铬锭, 50, IItems.单晶硅, 20, IItems.高碳钢, 50, IItems.锌锭, 30)
  }

  inner class CrystalMinerBuild : NormalCrafterBuild() {

    var k = false
    override fun consEfficiency(): Float {
      return super.consEfficiency() * if (k) 1 else 0
    }

    override fun shouldConsume(): Boolean {
      return super.shouldConsume() && k
    }

    override fun status(): BlockStatus? {
      if (autoSelect && !canSelect && recipeCurrent == -1) return BlockStatus.noInput
      if (!k) return BlockStatus.noInput
      return super.status()
    }

    override fun updateTile() {
      super.updateTile()
      k = false
      tile.nearby(rotation).nearby(rotation)?.block()?.let {
        if (it == EnvironmentBlocks.燃素晶簇) {
          k = true
        }
      }
      tile.nearby(rotation).nearby(rotation).nearby(rotation - 1)?.block()?.let {
        if (it == EnvironmentBlocks.燃素晶簇) {
          k = true
        }
      }
      tile.nearby(rotation).nearby(rotation).nearby(rotation)?.block()?.let {
        if (it == EnvironmentBlocks.燃素晶簇) {
          k = true
        }
      }

      tile.nearby(rotation).nearby(rotation).nearby(rotation).nearby(rotation - 1)?.block()?.let {
        if (it == EnvironmentBlocks.燃素晶簇) {
          k = true
        }
      }
    }

    override fun draw() {
      super.draw()

      //    Fill.rect(.drawx(), tile.nearby(rotation).nearby( rotation).drawy(), 8f, 8f)

      Tmp.v1.set(0f, size / 2f * 8f).rotate(rotation * 90f - 90f)

      val vx = x + Tmp.v1.x
      val vy = y + Tmp.v1.y
      Draw.z(Layer.blockOver)
      Draw.rect(rotatorBottomRegion, vx, vy)
      Drawf.spinSprite(rotatorRegion, vx, vy, totalProgress * 2)

      Draw.color(Pal.redLight)
      Draw.alpha(warmup * if (k) 1 else 0)
      Draw.rect(rotatorRegionHeat, vx, vy, totalProgress * 2)
    }
  }
}