package ice.content.unit

import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.content.block.EnvironmentBlocks
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.entity.base.Entity
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.content.Fx
import mindustry.gen.Sounds

class RichTumor : IceUnitType("richTumor", RichTumorUnit::class.java) {
  init {
    speed = 0f
    accel = -3f
    range = 0f
    health = 30f
    hitSize = 4f
    drawCell = false
    targetAir = false
    useUnitCap = false
    targetable = false
    itemCapacity = 0
    targetGround = false
    createScorch = false
    outlineRadius = 1
    playerControllable = false
    deathSound = Sounds.plantBreak
    deathExplosionEffect = IceEffects.bloodNeoplasma
    localization {
      zh_CN {
        name = "丰瘤"
        description = "小型陆行污染生物.无法移动与攻击,不会被任何单位视为目标.落地后进入短暂的潜伏期,随后将下方地表同化为活性肿瘤地"
      }
    }
  }

  class RichTumorUnit : Entity() {
    var time = run {
      Fx.rand.setSeed(id.toLong())
      Fx.rand.random(6f, 10f) * 60
    }
    var timer = 0f
    override fun update() {
      super.update()
      timer += Time.delta
      if (timer > time) {
        Vars.world.tileWorld(x, y)?.setFloor(EnvironmentBlocks.肿瘤地)
        kill()
      }
    }

    override fun write(write: Writes) {
      super.write(write)
      write.f(time)
      write.f(timer)
    }

    override fun read(read: Reads) {
      super.read(read)
      time = read.f()
      timer = read.f()
    }
  }
}