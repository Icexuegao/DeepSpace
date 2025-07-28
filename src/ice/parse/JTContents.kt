package ice.parse

import arc.files.Fi
import arc.struct.Seq
import arc.util.Log
import ice.library.IFiles
import ice.parse.ContentParser.finishParsing
import ice.parse.ContentParser.markError
import ice.parse.ContentParser.parse
import mindustry.ctype.ContentType
import mindustry.mod.Mods.LoadedMod
import mindustry.type.ErrorContent
import org.tomlj.Toml


object JTContents {
    private class LoadRun(var type: ContentType, var file: Fi)

    lateinit var currentMod: LoadedMod

    fun load(fileName: String, mod: LoadedMod) {
        currentMod = mod
        val runs = Seq<LoadRun>()
        val contentRoot = IFiles.findDirectory(fileName) ?: return
        for (type in ContentType.all) {
            val folder = contentRoot.child("${type.name}s") //units,items....
            if (folder.exists()) {
                for (file in folder.findAll { it.extension() == "toml" || it.extension() == "json" }) {
                    runs.add(LoadRun(type, file))
                }
            }
        }
        runs.forEach {
            try {
                val isToml = it.file.extension().equals("toml")
                parse(
                    it.file.nameWithoutExtension(),
                    if (isToml) Toml.parse(it.file.read()).toJson() else it.file.readString(),
                    it.file,
                    it.type
                )
                Log.info("已加载${it.file.name()}")
            } catch (e: Throwable) {
                markError(ErrorContent(), it.file, e)
            }
        }
        finishParsing()
    }
}
