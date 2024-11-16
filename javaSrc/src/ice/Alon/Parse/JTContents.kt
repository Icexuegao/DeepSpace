package ice.Alon.Parse

import arc.Core
import arc.files.Fi
import arc.struct.Seq
import arc.util.Log
import ice.Alon.File.IceFiles
import ice.Ice
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.ctype.UnlockableContent
import mindustry.type.ErrorContent
import org.tomlj.Toml

class JTContents {
    companion object {
        private val parser = IceContentParser()
        fun load() {
            class LoadRun(val type: ContentType, val file: Fi)

            val runs = Seq<LoadRun>()
            val contentRoot = IceFiles.find("IceContent")
            for (type in ContentType.all) {
                val lower = type.name.lowercase()
                val folder = contentRoot.child(lower + if (lower.endsWith("s")) "" else "s") //units,items....
                if (folder.exists()) {
                    for (file in folder.findAll { f: Fi -> f.extension() == "json" || f.extension() == "hjson" || f.extension() == "toml" }) {
                        runs.add(LoadRun(type, file!!))
                    }
                }
            }
            //确保mod内容是在适当的顺序
            for (l in runs) {
                val current = Vars.content.lastAdded
                try {
                    //这将绑定内容，但不会完全加载
                    val loaded: Content = if (l.file.extension() == "toml") {
                        val parse = Toml.parse(l.file.read())
                        val json = parse.toJson()
                        /**解析toml */
                        parser.parse(l.file.nameWithoutExtension(), json, l.file, l.type)
                    } else {
                        /**解析json */
                        parser.parse(
                            l.file.nameWithoutExtension(), l.file.readString("UTF-8"), l.file, l.type
                        )
                    }
                    Log.info(
                        "[green][Loaded][${Ice.NAME}]-[${Core.bundle.get("content.${l.type.name}.name")}]-[@]-<${l.file.name()}>[]",
                        if (loaded is UnlockableContent) loaded.localizedName else loaded
                    )
                } catch (e: Throwable) {
                    if (current !== Vars.content.lastAdded && Vars.content.lastAdded != null) {
                        parser.markError(Vars.content.lastAdded, l.file, e)
                    } else {
                        val error = ErrorContent()
                        parser.markError(error, l.file, e)
                    }
                }
            }
            //这样就完成了对内容字段的解析
            parser.finishParsing()
        }
    }
}