package ice.content.unit


import ice.world.content.unit.IceUnitType
import mindustry.gen.CrawlUnit

class Barb: IceUnitType("unit_barb", CrawlUnit::class.java) {
  init {
    localization {
      zh_CN {
        this.localizedName = "绒刺"
        description = "请输入文本"
      }
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