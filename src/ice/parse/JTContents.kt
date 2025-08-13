package ice.parse

import arc.files.Fi
import mindustry.ctype.ContentType
import mindustry.mod.Mods

object JTContents {
    private class LoadRun(var type: ContentType, var file: Fi)

    lateinit var currentMod: Mods.LoadedMod

    fun load(fileName: String, mod: Mods.LoadedMod) {
        currentMod = mod
       /* val runs = Seq<LoadRun>()
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
    }*/
}}
