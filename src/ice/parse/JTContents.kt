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
    fun load() {
        class LoadRun(var type: ContentType, var file: Fi)

        val runs = Seq<LoadRun>()
        val contentRoot = IFiles.findDirectory("IceContent") ?: return
        for (type in ContentType.all) {
            val folder = contentRoot.child("${type.name}s") //units,items....
            if (folder.exists()) {
                for (file in folder.findAll { f -> f.extension() == "json" || f.extension() == "toml" }) {
                    runs.add(LoadRun(type, file))
                }
            }
        }
        //确保mod内容是在适当的顺序
        for (l in runs) {
            try {
                //这将绑定内容，但不会完全加载
                val loaded = parse(
                    l.file.nameWithoutExtension(), if (l.file.extension() == "toml") {
                        Toml.parse(l.file.read()).toJson()
                    } else {
                        l.file.readString("UTF-8")
                    }, l.file, l.type
                )
                Log.info(
                    "[green][Load][${Ice.NAME}]-[${Core.bundle.get("content.${l.type.name}.name")}]-[@]-<${l.file.name()}>[]",
                    if (loaded is UnlockableContent) loaded.localizedName else loaded
                )
            } catch (e: Throwable) {
                markError(ErrorContent(), l.file, e)
            }

        }
        finishParsing()
    }
}
