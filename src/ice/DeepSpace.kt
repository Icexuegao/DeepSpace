package ice

import arc.Core
import arc.Settings
import arc.files.Fi
import arc.func.Prov
import arc.struct.ObjectMap
import arc.util.Log
import arc.util.Strings
import arc.util.serialization.JsonReader
import ice.core.IFiles
import mindustry.Vars
import mindustry.mod.Mods
import singularity.Singularity
import singularity.core.UpdatePool
import java.util.*

object DeepSpace {
  val mod: Mods.LoadedMod by lazy { Vars.mods.getMod(Ice::class.java) }
  /** 此模组的压缩包对象  */
  val modFile: Fi = IFiles.modWithClass.file
  /** 此mod内部名称  */
  val modName = IFiles.modWithClass.name
  /** 此mod显示名称  */
  val modDisplayName = IFiles.modWithClass.displayName
  /** 此mod的模组版本  */
  val modVersion = IFiles.modWithClass.version
  /** 此mod的模组作者  */
  val modAuthor = IFiles.modWithClass.author
  /** 此mod的最近模组更新时间  */
  val modUpdateDate: String by lazy {
    JsonReader().parse(mod.root.child("mod.json").read()).get("updateDate").asString()
  }
  /** 此mod的github仓库  */
  const val githubProjectUrl = "https://github.com/Icexuegao/DeepSpace"
  const val REPO = "Icexuegao/DeepSpace"
  const val githubProjReleaseApi = "https://api.github.com/repos/Icexuegao/DeepSpace/releases/latest"
  /** 此mod的QQ群组 */
  const val qqGropsUrl = "https://qm.qq.com/q/3CR3cn2Wc8"
  /** 模组数据文件夹 */
  val modDirectory: Fi = Core.settings.getDataDirectory().child(modName).apply {
    if (!exists()) mkdirs()
  }
  /** 模组持久全局变量存储文件  */
  val globalVars: Fi = modDirectory.child("global_vars.bin")
  /** 模组持久全局变量备份文件  */
  val globalVarsBackup: Fi = modDirectory.child("global_vars.bin.bak")
  /** 持久保存的全局变量集  */
  var globals: Settings = object :Settings() {
    init {
      setAutosave(true)
      setDataDirectory(modDirectory)
      UpdatePool.receive("autosaveGlobal", ::autosave)
    }

    override fun getSettingsFile(): Fi {
      return globalVars
    }

    override fun getBackupFolder(): Fi {
      return modDirectory.child("global_backups")
    }

    override fun getBackupSettingsFile(): Fi {
      return globalVarsBackup
    }

    @Synchronized
    override fun load() {
      try {
        loadValues()
      } catch(error: Throwable) {
        Log.err("Error in load: " + Strings.getStackTrace(error))
        if (errorHandler != null) {
          if (!hasErrored) errorHandler.get(error)
        } else {
          throw error
        }
        hasErrored = true
      }
      loaded = true
    }

    @Synchronized
    override fun forceSave() {
      if (!loaded) return
      try {
        saveValues()
      } catch(error: Throwable) {
        Log.err("Error in forceSave to " + settingsFile + ":\n" + Strings.getStackTrace(error))
        if (errorHandler != null) {
          if (!hasErrored) errorHandler.get(error)
        } else {
          throw error
        }
        hasErrored = true
      }
      modified = false
    }
  }
  val docCache: ObjectMap<Fi, String> = ObjectMap<Fi, String>()
  fun getDocumentFile(name: String?): Fi {
    return Singularity.getInternalFile("documents").child(Core.bundle.locale.toString()).child(name)
  }

  fun getDocumentFile(locale: Locale, name: String?): Fi? {
    val docs = Singularity.getInternalFile("documents").child(locale.toString())
    return if (docs.exists()) docs.child(name) else Singularity.getInternalFile("documents").child("zh_CN")
  }

  fun getDocument(name: String?, cache: Boolean): String? {
    val fi = getDocumentFile(name)
    return if (cache) docCache.get(fi, Prov { fi.readString() }) else fi.readString()
  }

  fun getDocument(locale: Locale, name: String?): String? {
    return getDocumentFile(locale, name)!!.readString()
  }
}