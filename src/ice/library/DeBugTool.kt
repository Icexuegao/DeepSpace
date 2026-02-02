package ice.library

import arc.files.Fi
import arc.graphics.Pixmap
import ice.DeepSpace
import ice.library.IFiles.replaceModName
import ice.library.struct.log
import mindustry.Vars
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.Turret
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.environment.StaticWall

object DeBugTool {
  /**输出不完整方块贴图名称*/
  fun sdw() {
    val spritesDir: Fi = DeepSpace.mod.root.child("sprites")
    val spriteMap = HashMap<String, Fi>()
    fun processDirectory(directory: Fi) {
      directory.list().forEach {
        if (it.isDirectory) {
          processDirectory(it)
        }
        if (it.extension().equals("png")) {
          spriteMap[it.nameWithoutExtension()] = it
        }
      }
    }
    processDirectory(spritesDir)
    Vars.content.blocks().forEach { block ->
      if (block !is Block || !block.squareSprite || block is Floor) return@forEach
      if (!spriteMap.contains(block.name.replaceModName())) return@forEach
      if (block is Turret) return@forEach
      if (block is StaticWall) return@forEach
      if (block.minfo.mod != DeepSpace.mod) return@forEach
      val spriteFile = spriteMap[block.name.replaceModName()]
      val pixmap = Pixmap(spriteFile)
      check@ for (x in 0 until pixmap.width) {
        for (y in 0 until pixmap.height) {
          val pixelColor: Int = pixmap.get(x, 0)
          if (pixelColor == 0) {
            log { block.localizedName }
            break@check
          }
        }
      }
    }
  }
}