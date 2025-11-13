package ice.music

import arc.Core
import arc.audio.Sound
import ice.library.IFiles

object ISounds {
    val minimalist3=getSound("minimalist3")
    val foldJump = getSound("foldJump")
    val laser1 = getSound("laser1")
    val highExplosiveShell = getSound("highExplosiveShell")
    val laser2 = getSound("laser2")
    val chizovegeta=getSound("chizovegeta")
    val forceHoldingLaser2 = getSound("forceHoldingLaser2")
    val remainInstall=getSound("remainInstall")
    val remainUninstall=getSound("remainUninstall")
    val laserGun=getSound("laserGun")
    val flblSquirt=getSound("flblSquirt")
    private fun getSound(name: String): Sound {
        val file = IFiles.findSound("$name.ogg")
        return Core.audio.newSound(file)
    }
}