package ice.audio

import arc.Core
import arc.audio.Sound
import ice.library.IFiles

object ISounds {
    val beamLoop = getSound("beamLoop")
    val chizovegeta = getSound("chizovegeta")
    val flblSquirt = getSound("flblSquirt")
    val foldJump = getSound("foldJump")
    val forceHoldingLaser2 = getSound("forceHoldingLaser2")
    val highExplosiveShell = getSound("highExplosiveShell")
    val laser1 = getSound("laser1")
    val laser2 = getSound("laser2")
    val laserGun = getSound("laserGun")
    val minimalist3 = getSound("minimalist3")
    val moonhidelaunched = getSound("moonhidelaunched")
    val remainInstall = getSound("remainInstall")
    val remainUninstall = getSound("remainUninstall")
    val 激射 = getSound("shotFiercely")
    val 聚爆=getSound("聚爆")
    val 速射=getSound("速射")

    private fun getSound(name: String): Sound {
        val file = IFiles.findSound("$name.ogg")
        return Core.audio.newSound(file)
    }
}