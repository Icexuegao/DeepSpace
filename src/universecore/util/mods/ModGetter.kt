package universecore.util.mods

import arc.Core
import arc.files.Fi
import arc.files.ZipFi
import arc.func.Boolf2
import arc.struct.Seq
import arc.util.serialization.Jval
import mindustry.mod.Mod
import universecore.util.IllegalModHandleException

object ModGetter {
  /** 模组文件夹位置  */
  val modDirectory: Fi = Core.settings.getDataDirectory().child("mods")

  /**
   * 传入一个文件，检查此文件是否是一个mod文件，若此文件是一个mod，若是，则返回mod的meta文件，若不是则抛出[IllegalModHandleException]
   * @param modFile 检查文件，可以是一个目录
   * @return 这个mod的main meta文件
   * @throws IllegalModHandleException 如果这个文件不是一个mod
   */
  fun checkModFormat(modFile: Fi): Fi {
    var modFile = modFile
    try {
      if (modFile !is ZipFi && !modFile.isDirectory()) modFile = ZipFi(modFile)
    } catch (_: Throwable) {
      throw IllegalModHandleException("文件不是有效的压缩文件")
    }

    val modJson = listOf("mod.json", "mod.hjson", "plugin.json", "plugin.hjson").find { fileName -> modFile.child(fileName).exists() } ?: throw IllegalModHandleException("模组格式错误: 未找到模组元内容")
    return modFile.child(modJson)
  }

  /**
   * 判断传入的文件是否是一个mod
   * @param modFile 检查的文件
   * @return 布尔值表示的结果
   */
  fun isMod(modFile: Fi): Boolean {
    try {
      checkModFormat(modFile)
      return true
    } catch (_: IllegalModHandleException) {
      return false
    }
  }

  fun getModsWithFilter(filter: Boolf2<Fi, Jval>): Seq<ModInfo> {
    val result = Seq<ModInfo>()

    for (file in modDirectory.list()) {
      if (!isMod(file)) continue
      val info = Jval.read(checkModFormat(file).reader())

      try {
        if (filter.get(file, info)) {
          result.add(ModInfo(ZipFi(file)))
        }
      } catch (ignored: IllegalModHandleException) {
        throw Exception("ModGetter: json解析错误 $info", ignored)
      }
    }

    return result
  }

  fun getModsWithName(name: String): Seq<ModInfo> {
    return getModsWithFilter { _: Fi, i: Jval -> i.getString("name") == name }
  }

  fun getModsWithClass(mainClass: Class<out Mod>): Seq<ModInfo> {
    return getModsWithFilter { _: Fi, i: Jval -> i.getString("main") == mainClass.getCanonicalName() }
  }

  fun getModWithName(name: String): ModInfo? {
    val seq = getModsWithName(name)
    return if (seq.isEmpty) null else seq.first()
  }

  fun getModWithClass(mainClass: Class<out Mod>): ModInfo? {
    val seq = getModsWithClass(mainClass)
    return if (seq.isEmpty) null else seq.first()
  }
}