package ice.music

import arc.audio.Sound
import arc.files.Fi
import ice.library.IFiles

object IceSounds {
    val foldJump = Sound(getSound("foldJump"))
    private fun getSound(name: String): Fi {
        return IFiles.find("$name.ogg")
    }
}