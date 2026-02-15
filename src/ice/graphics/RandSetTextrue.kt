package ice.graphics

import arc.Events
import arc.scene.style.ScaledNinePatchDrawable
import arc.struct.Seq
import arc.util.Interval
import ice.library.IFiles
import ice.world.meta.IceEffects

class RandSetTextrue(def: String, size: Int){
  val def = IFiles.createNinePatch(def) as ScaledNinePatchDrawable
  val textrue = Array(size) { "$def-$it" }

  init {
    Events.run(mindustry.game.EventType.Trigger.update) {
      upfate()
    }
  }

  val seq = Seq(textrue)
  var blink = false
  val blinktime = 6f
  var blinkTempTex = seq.first()
  val interval = Interval(1)
  var iterator = seq.iterator()


  open fun upfate() {
    if (!blink) return
    if (interval[blinktime]) {
      if (iterator.hasNext()) {
        blinkTempTex = iterator.next()
        def.patch= (IFiles.createNinePatch(blinkTempTex) as ScaledNinePatchDrawable).patch
      } else endBlink()
    }
  }


  open fun endBlink() {
    blink = false
    iterator = seq.iterator()
  }
}