package ice.content.unit

import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.gen.CrawlUnit

class Barb: IceUnitType("unit_barb", CrawlUnit::class.java) {
  init {
    BaseBundle.bundle {
      desc(zh_CN,"绒刺")
    }
    health = 120f
    armor = 2f
    hitSize = 8f
    speed = 1.3f
    rotateSpeed = 2.4f
    omniMovement = false
    segments = 8
    segmentScl = 3f
    segmentPhase = 5f
    segmentMag = 0.5f
    drawBody = false
    drawCell = false
    hidden = false
    crushDamage = 0.2f
    hovering = true
  }
}