package ice.music

import arc.Core
import arc.audio.Sound
import ice.library.IFiles

object IceSounds {
    val foldJump = getSound("foldJump")
    val laser1 = getSound("laser1")
    val highExplosiveShell = getSound("highExplosiveShell")
    val laser2 = getSound("laser2")
    val forceHoldingLaser2 = getSound("forceHoldingLaser2")
    private fun getSound(name: String): Sound {
        val file = IFiles.findSound("$name.ogg")
        return Core.audio.newSound(file)
    }
}