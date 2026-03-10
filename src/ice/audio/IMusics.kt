package ice.audio

import arc.Core
import arc.audio.Music
import arc.func.Cons
import ice.library.IFiles
import mindustry.gen.Musics

object IMusics {
  val title: Music = Core.audio.newMusic(IFiles.findMusic("title.ogg"))
  val 核心过载仪式: Music = Core.audio.newMusic(IFiles.findMusic("Core_Overload_Ritual.ogg"))
  val 异端核心: Music = Core.audio.newMusic(IFiles.findMusic("Heretic_Core.ogg"))
  val 静态立场: Music = Core.audio.newMusic(IFiles.findMusic("Stasis_Field.ogg"))
}



