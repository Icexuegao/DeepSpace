package ice.parse

import arc.Core
import arc.files.Fi
import arc.struct.Seq
import arc.util.Log
import ice.Ice
import ice.library.IFiles
import ice.parse.IceContentParser.*
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.type.ErrorContent
import org.tomlj.Toml


object JTContents {
    private class LoadRun(var type: ContentType, var file: Fi)

    fun load() {
        val runs = Seq<LoadRun>()
        val contentRoot = IFiles.findDirectory("IceContent") ?: return
        for (type in ContentType.all) {
            val folder = contentRoot.child("${type.name}s") //units,items....
            if (folder.exists()) {
                for (file in folder.findAll { it.extension() == "toml" }) {
                    runs.add(LoadRun(type, file))
                }
            }
        }
        runs.forEach { fi ->
            try {
                val loaded =
                    parse(fi.file.nameWithoutExtension(), Toml.parse(fi.file.read()).toJson(), fi.file, fi.type)
                Log.info("[green][Load][${Ice.NAME}]-[${
                    Core.bundle.get("content.${fi.type.name}.name")
                }]-[@]-<${fi.file.name()}>[]", if (loaded is UnlockableContent) loaded.localizedName else loaded)
            } catch (e: Throwable) {
                markError(ErrorContent(), fi.file, e)
            }
        }
        finishParsing()
    }
}
