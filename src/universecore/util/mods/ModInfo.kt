package universecore.util.mods

import arc.files.Fi
import arc.util.Log
import arc.util.serialization.Jval
import universecore.util.mods.ModGetter.checkModFormat

class ModInfo(modFile: Fi) {
  val name: String
  val version: String
  val displayName: String
  val file: Fi

  init {
    val modMeta = checkModFormat(modFile)
    val info = Jval.read(modMeta.reader())
    file = modFile
    name = info.get("name").asString()
    version = info.getNull("version")
    displayName = info.getNull("displayName")
  }

  fun Jval.getNull(key: String): String {
    try {
      return get(key).asString()
    } catch (e: Exception) {
      Log.err(Exception("ModInfo: $key is null", e))
    }
    return "null"
  }
}