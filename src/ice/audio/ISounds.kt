package ice.audio

import arc.Core
import arc.audio.Sound
import ice.library.IFiles

object ISounds {
  val radar= getSound("radar")
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
  val remainInstall = getSound("remainInstall")
  val remainUninstall = getSound("remainUninstall")
  val 激射 = getSound("shotFiercely")
  val 聚爆 = getSound("聚爆")
  val 速射 = getSound("速射")
  val 棱镜 = getSound("棱镜")
  val 月隐发射 = getSound("moonhidelaunched")
  val 月隐蓄力 = getSound("月隐蓄力")
  val 灼烧=getSound("灼烧")

  val 进入模组界面=getUISound("进入模组界面")
  val 模组界面左侧按钮反馈=getUISound("模组界面左侧按钮反馈")
  val 数据板块顶部选择按钮反馈 = getUISound("数据板块顶部选择按钮反馈")
  val 数据板块内个体反馈 = getUISound("数据板块内个体反馈")
  val 科技树内个体已激活 = getUISound("科技树内个体已激活")
  val 进入数据界面 = getUISound("进入数据界面")
  private fun getSound(name: String): Sound {
    val file = IFiles.findSound("$name.ogg")
    return Core.audio.newSound(file)
  }
  private fun getUISound(name: String): Sound {
    val file = IFiles.findSound("ui-$name.ogg")
    return Core.audio.newSound(file)
  }
}