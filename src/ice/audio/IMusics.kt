package ice.audio

import arc.audio.Music
import ice.core.IFiles

object IMusics {
  val title: Music = Music(IFiles.findMusic("title.ogg"))
  val 核心过载仪式: Music = Music(IFiles.findMusic("Core_Overload_Ritual.ogg"))
  val 异端核心: Music = Music(IFiles.findMusic("Heretic_Core.ogg"))
  val 静态立场: Music = Music(IFiles.findMusic("Stasis_Field.ogg"))
}



