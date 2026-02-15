package ice

import ice.library.IFiles
import mindustry.Vars
import mindustry.mod.Mods

object DeepSpace {
  val mod: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
  val name = IFiles.modWithClass.displayName
  val displayName = IFiles.modWithClass.displayName
  val version = IFiles.modWithClass.version
  val author: String = mod.meta.author
  const val githubProjectUrl = "https://github.com/Icexuegao/DeepSpace"
  const val repo="Icexuegao/DeepSpace"
  const val githubProjReleaseApi = "https://api.github.com/repos/Icexuegao/DeepSpace/releases/latest"
  const val qqGropsUrl = "https://qm.qq.com/q/3CR3cn2Wc8"
}