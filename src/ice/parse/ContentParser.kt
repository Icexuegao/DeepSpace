@file:Suppress("UNCHECKED_CAST")

package ice.parse

import arc.assets.AssetDescriptor
import arc.files.Fi
import arc.func.Func
import arc.func.Prov
import arc.graphics.g2d.TextureRegion
import arc.math.Interp
import arc.struct.ObjectMap
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Log
import arc.util.Nullable
import arc.util.Reflect
import arc.util.Strings
import arc.util.serialization.Json
import arc.util.serialization.Json.FieldMetadata
import arc.util.serialization.JsonValue
import arc.util.serialization.Jval
import arc.util.serialization.Jval.Jformat
import arc.util.serialization.SerializationException
import ice.library.struct.isNotEmpty
import ice.parse.JTContents.currentMod
import ice.parse.parses.ClassFieldParsers.classPar
import ice.parse.parses.ClassMap
import ice.parse.parses.ClassTypeParsers
import mindustry.Vars
import mindustry.content.TechTree
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.ctype.MappableContent
import mindustry.ctype.UnlockableContent
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.game.Objectives
import mindustry.game.Objectives.Objective
import mindustry.io.SaveVersion
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.meta.Env
import java.lang.reflect.Modifier

object ContentParser {
    /**忽略未知字段?*/
    var ignoreUnknownFields: Boolean = true
    var currentContent: Content? = null


    /**
     * 存储需要完全解析的内容，例如读取内容字段。
     * 这样做是为了首先适应内容名称的绑定。
     */
    val reads: Seq<Runnable> = Seq()
    val postreads: Seq<Runnable> = Seq()
    val toBeParsed: ObjectSet<Any> = ObjectSet()

    var implicitNullable: ObjectSet<Class<*>> = ObjectSet<Class<*>>().apply {
        addAll(TextureRegion::class.java, Array<TextureRegion>::class.java, Array<Array<TextureRegion>>::class.java,
            Array<Array<Array<TextureRegion>>>::class.java)
    }
    var sounds: ObjectMap<String, AssetDescriptor<*>> = ObjectMap()
    val parserContentJson: ObjectMap<ContentType, ClassTypeParsers.TypeParser<*>> = ClassTypeParsers.contentParser
    var contentTypes: ObjectMap<Class<*>, ContentType> = object : ObjectMap<Class<*>, ContentType>() {}

    val parserFieldJson: Json = object : Json() {
        override fun <T> readValue(
            type: Class<T>?,
            elementType: Class<*>?,
            jsonData: JsonValue,
            keyType: Class<*>?,
        ): T {
            val t = internalRead(type, elementType, jsonData, keyType)
            /**t不等于null 不是包装器       type==null或者不是基元类型 */
            if (t != null && !Reflect.isWrapper(t.javaClass) && (type == null || !type.isPrimitive)) {
                /**检查对象是否有为null的属性 */
                checkNullFields(t)
            }
            return t
        }

        fun <T> internalRead(type: Class<T>?, elementType: Class<*>?, jsonData: JsonValue, keyType: Class<*>?): T {
            if (type != null) {
                /**从classmap解析对象 */
                if (classPar.containsKey(type)) {
                    try {
                        return classPar[type].parse(type, jsonData) as T
                    } catch (e: Exception) {
                        throw RuntimeException(e)
                    }
                }

                //尝试解析 env 位
                if ((type == Int::class.javaPrimitiveType || type == Int::class.java) && jsonData.isArray) {
                    var value = 0
                    for (str in jsonData) {
                        if (!str.isString) throw SerializationException("整数位域值必须全部为字符串。发现： $str")
                        val field = str.asString()
                        value = value or Reflect.get(Env::class.java, field)
                    }

                    return value as T
                }
                //解析基本类型
                if (Content::class.java.isAssignableFrom(type)) {
                    val ctype = contentTypes.getThrow(type) {
                        IllegalArgumentException("没有该内容Type的Class解析器: " + type.simpleName)
                    }
                    val prefix = currentMod.name + "-"
                    val one: T? = Vars.content.getByName<MappableContent>(ctype, prefix + jsonData.asString()) as T
                    if (one != null) return one
                    val two: T? = Vars.content.getByName<MappableContent>(ctype, jsonData.asString()) as T
                    if (two != null) return two

                    throw IllegalArgumentException(
                        """"${jsonData.name}":没有找到类型为${ctype}的'${jsonData.asString()}'.确保'${jsonData.asString()}'存在""")
                }
            }
            return super.readValue(type, elementType, jsonData, keyType)
        }
    }


    init {
        for (type in ContentType.all) {
            val arr = Vars.content.getBy<Content>(type)
            if (arr.isNotEmpty()) {
                var c: Class<*> = arr.first().javaClass
                //获取 Base Content 类，跳过中间体
                while (!(c.superclass == Content::class.java || c.superclass == UnlockableContent::class.java || Modifier.isAbstract(
                        c.superclass.modifiers))
                ) {
                    c = c.superclass
                }
                contentTypes.put(c, type)
            }
        }
    }

    @Throws(Exception::class)
    fun parse(name: String, json: String, file: Fi, type: ContentType): Content {
        val value = parserFieldJson.fromJson<JsonValue>(null, Jval.read(json).toString(Jformat.plain))
        if (!parserContentJson.containsKey(type)) {
            throw SerializationException("内容类型没有解析器 '$type'")
        }


        val c = parserContentJson[type].parse(name, value)
        c.minfo.sourceFile = file
        toBeParsed.add(c)
        /**把原版设置mod */
        c.minfo.mod = currentMod
        return c
    }

    /**稍后致电以阅读内容的附加信息。*/
    fun read(run: Runnable) {
        val cont = currentContent
        reads.add {
            currentContent = cont
            run.run()

            //解析后检查 Null
            if (cont != null) {
                toBeParsed.remove(cont)
                checkNullFields(cont)
            }
        }
    }

    fun attempt(run: Runnable) {
        try {
            run.run()
        } catch (t: Throwable) {
            Log.err(t)
            //不要覆盖双重错误
            markError(currentContent!!, t)
        }
    }

    fun finishParsing() {
        reads.forEach(::attempt)
        postreads.forEach(::attempt)
        reads.clear()
        postreads.clear()
        toBeParsed.clear()
    }

    fun markError(content: Content, file: Fi, error: Throwable) {
        Log.err("[red]Error for [@] / [@][]:\n@\n", content, file, Strings.getStackTrace(error))
        content.minfo.mod = currentMod
        content.minfo.sourceFile = file
        content.minfo.error = makeError(error, file)
        content.minfo.baseError = error
    }

    fun markError(content: Content, error: Throwable) {
        if (content.minfo != null && !content.hasErrored()) {
            markError(content, content.minfo.sourceFile, error)
        }
    }

    fun makeError(t: Throwable, file: Fi): String {
        val builder = StringBuilder()
        builder.append("[lightgray]").append("File: ").append(file.name()).append("[]\n\n")

        if (t.message != null && t is Jval.JsonParseException) {
            builder.append("[accent][[JsonParse][] ").append(":\n").append(t.message)
        } else if (t is NullPointerException) {
            builder.append(Strings.neatError(t))
        } else {
            val causes = Strings.getCauses(t)
            for (e in causes) {
                builder.append("[accent][[").append(e.javaClass.simpleName.replace("Exception", "")).append("][] ")
                    .append(if (e.message != null) e.message!!.replace("mindustry.", "").replace("arc.", "") else "")
                    .append("\n")
            }
        }
        return builder.toString()
    }

    fun <T : MappableContent> locate(type: ContentType, name: String): T? {
        val first = Vars.content.getByName<T>(type, name) //尝试替换原版
        return first ?: Vars.content.getByName(type, currentMod.name + "-" + name)
    }

    fun <T : MappableContent> locateAny(name: String): T? {
        for (t in ContentType.all) {
            val out = locate<T>(t, name)
            if (out != null) {
                return out
            }
        }
        return null
    }

    fun <T : Content> parser(type: ContentType, constructor: Func<String, T>): ClassTypeParsers.TypeParser<T> {
        return ClassTypeParsers.TypeParser { name: String, value: JsonValue ->
            val item: T = if (locate<MappableContent>(type, name) != null) {
                locate<MappableContent>(type, name) as T
            } else {
                constructor[name]
            }
            currentContent = item
            read { readFields(item, value) }
            item
        }
    }

    fun <T : Content> find(type: ContentType, name: String): T {
        var c: Content? = Vars.content.getByName(type, name)
        if (c == null) c = Vars.content.getByName(type, currentMod.name + "-" + name)
        requireNotNull(c) { "No $type found with name '$name'" }
        return c as T
    }

    fun getString(value: JsonValue, key: String = "type"): String {
        if (value.has(key)) {
            return value.getString(key)
        } else {
            throw IllegalArgumentException("您缺少一个$key,必须先添加它,然后才能解析文件")
        }
    }

    fun parseProgressOp(base: PartProgress, op: String, data: JsonValue): PartProgress {
        //我必须对此进行硬编码，不幸的是，这不是获取参数名称的简单方法
        return when (op) {
            "inv" -> base.inv()
            "slope" -> base.slope()
            "clamp" -> base.clamp()
            "delay" -> base.delay(data.getFloat("amount"))
            "sustain" -> base.sustain(data.getFloat("offset", 0f), data.getFloat("grow", 0f), data.getFloat("sustain"))
            "shorten" -> base.shorten(data.getFloat("amount"))
            "compress" -> base.compress(data.getFloat("start"), data.getFloat("end"))
            "add" -> if (data.has("amount")) base.add(data.getFloat("amount")) else base.add(
                parserFieldJson.readValue(PartProgress::class.java, data["other"]))

            "blend" -> base.blend(parserFieldJson.readValue(PartProgress::class.java, data["other"]),
                data.getFloat("amount"))

            "mul" -> if (data.has("amount")) base.mul(data.getFloat("amount")) else base.mul(
                parserFieldJson.readValue(PartProgress::class.java, data["other"]))

            "min" -> base.min(parserFieldJson.readValue(PartProgress::class.java, data["other"]))
            "sin" -> base.sin(if (data.has("offset")) data.getFloat("offset") else 0f, data.getFloat("scl"),
                data.getFloat("mag"))

            "absin" -> base.absin(data.getFloat("scl"), data.getFloat("mag"))
            "curve" -> if (data.has("interp")) base.curve(
                parserFieldJson.readValue(Interp::class.java, data["interp"])) else base.curve(data.getFloat("offset"),
                data.getFloat("duration"))

            else -> throw java.lang.RuntimeException(
                "Unknown operation '$op', check PartProgress class for a list of methods.")
        }
    }

    /** 反射创建对象  */
    fun <T> make(type: Class<*>, name: String = "null"): T {
        try {
            if (name == "null") {
                val cons = type.getDeclaredConstructor()
                cons.isAccessible = true
                return cons.newInstance() as T
            } else {
                val cons = type.getDeclaredConstructor(String::class.java)
                cons.isAccessible = true
                return cons.newInstance(name) as T
            }

        } catch (e: Exception) {
            throw RuntimeException("获取构造函数错误: ${type.name}")
        }
    }

    /** 尝试从类类型 map 解析类。  */
    fun <T> resolve(base: String?, def: Class<T>?): Class<T> {
        //未指定基类
        if (base.isNullOrEmpty()) return def!!
        //如果在全局映射中找到，则返回 Map 类
        val s = if (Character.isLowerCase(base[0])) Strings.capitalize(base) else base
        val out = ClassMap.classmap[s]
        if (out != null) return out as Class<T>

        //尝试将其解析为 Raw 类名
        if (base.indexOf('.') != -1) {
            try {
                return Class.forName(base) as (Class<T>)
            } catch (ignored: Exception) {
                //尝试使用 Mod 类加载器
                try {
                    return Class.forName(base, true, Vars.mods.mainLoader()) as (Class<T>)
                } catch (ignore: Exception) {
                }
            }
        }
        throw RuntimeException("ClassMap未找到改类型:$base")
    }

    fun <T> supply(type: Class<T>): Prov<T> {
        try {
            val cons = type.getDeclaredConstructor()
            return Prov {
                try {
                    return@Prov cons.newInstance()
                } catch (e: Exception) {
                    throw RuntimeException(e.toString() + "构造函数不可访问!!!")
                }
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun field(type: Class<*>, value: JsonValue): Any {
        return field(type, value.asString())
    }

    /** 按名称从静态类中获取字段，如果未找到，则引发描述性异常。  */
    fun field(type: Class<*>, name: String): Any {
        try {
            val b = type.getField(name)[null]
            requireNotNull(b) { type.simpleName + ": 没有找到: " + name + " 属性" }
            return b
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException(e)
        }
    }

    fun fieldOpt(type: Class<*>, value: JsonValue): Any? {
        return try {
            type.getField(value.asString())[null]
        } catch (e: java.lang.Exception) {
            null
        }
    }

    fun checkNullFields(objects: Any?) {
        if (objects == null || objects is Number || objects is String || toBeParsed.contains(
                objects) || objects.javaClass.name.startsWith("arc.")
        ) return

        parserFieldJson.getFields(objects.javaClass).values().toSeq().forEach { field ->
            try {
                if (field.field.type.isPrimitive) return@forEach

                if (!field.field.isAnnotationPresent(
                        Nullable::class.java) && field.field[objects] == null && !implicitNullable.contains(
                        field.field.type)
                ) {
                    throw java.lang.RuntimeException(
                        ((("'" + field.field.name) + "' in " + ((if (objects.javaClass.isAnonymousClass) objects.javaClass.superclass else objects.javaClass).simpleName) + " is missing! Object = " + objects + ", field = (" + field.field.name) + " = " + field.field[objects]) + ")")
                }
            } catch (e: java.lang.Exception) {
                throw java.lang.RuntimeException(e)
            }
        }
    }

    fun readFields(objects: Any, jsonMap: JsonValue, stripType: Boolean = false) {
        if (stripType) jsonMap.remove("type")
        val research = jsonMap.remove("research")

        toBeParsed.remove(objects)
        val type = objects.javaClass
        val fields = parserFieldJson.getFields(type)


        var child = jsonMap.child
        while (child != null) {
            val metadata: FieldMetadata = fields.get(child.name()) ?: if (ignoreUnknownFields) {
                Log.warn("[@]:不可识别字段: @ (@)", currentContent!!.minfo.sourceFile.name(), child.name,
                    type.simpleName)
                child = child.next
                continue
            } else {
                val ex = SerializationException("未找到字段: " + child.name + " (" + type.name + ")")
                ex.addTrace(child.trace())
                throw ex
            }

            val field = metadata.field
            try {
                field.set(objects, parserFieldJson.readValue(field.type, metadata.elementType, child, metadata.keyType))
            } catch (ex: IllegalAccessException) {
                throw SerializationException("Error accessing field: " + field.name + " (" + type.name + ")", ex)
            } catch (ex: SerializationException) {
                ex.addTrace(field.name + " (" + type.name + ")")
                throw ex
            } catch (runtimeEx: RuntimeException) {
                val ex = SerializationException(runtimeEx)
                ex.addTrace(child.trace())
                ex.addTrace(field.name + " (" + type.name + ")")
                throw ex
            }
            child = child.next
        }


        if (objects is UnlockableContent && research != null) {

            //添加研究技术节点
            val researchName: String?
            val customRequirements: Array<ItemStack>?

            //research 可以是单个字符串，也可以是具有 parent 和 requirements 的对象
            if (research.isString) {
                researchName = research.asString()
                customRequirements = null
            } else {
                researchName = research.getString("parent", null)
                customRequirements =
                    if (research.has("requirements")) parserFieldJson.readValue(Array<ItemStack>::class.java,
                        research["requirements"])
                    else null
            }

            //删除旧节点
            TechTree.all.find { t ->
                t.content == objects
            }?.remove()


            val node = TechTree.TechNode(null, objects, customRequirements ?: ItemStack.empty)


            postreads.add {
                currentContent = objects

                //添加自定义目标
                if (research.has("objectives")) {
                    node.objectives.addAll(
                        *parserFieldJson.readValue(Array<Objective>::class.java, research["objectives"]))
                }

                //除非已指定，否则所有物料都有 produce 要求
                if (objects is Item && !node.objectives.contains { o -> o is Objectives.Produce && o.content == objects }) {
                    node.objectives.add(Objectives.Produce(objects))
                }

                //remove old node from parent
                if (node.parent != null) {
                    node.parent.children.remove(node)
                }

                if (customRequirements == null) {
                    node.setupRequirements(objects.researchRequirements())
                }

                if (research.has("planet")) {
                    node.planet = find(ContentType.planet, research.getString("planet"))
                }

                if (research.getBoolean("root", false)) {
                    node.name = research.getString("name", objects.name)
                    node.requiresUnlock = research.getBoolean("requiresUnlock", false)
                    TechTree.roots.add(node)
                } else {
                    if (researchName != null) {
                        //find parent node.
                        val parent = TechTree.all.find { t ->
                            t.content.name.equals(researchName) || t.content.name.equals(
                                currentMod.name + "-" + researchName) || t.content.name.equals(
                                SaveVersion.mapFallback(researchName))
                        }

                        if (parent == null) {
                            Log.warn(
                                "Content '" + researchName + "' isn't in the tech tree, but '" + objects.name + "' requires it to be researched.")
                        } else {
                            //add this node to the parent
                            if (!parent.children.contains(node)) {
                                parent.children.add(node)
                            }
                            //reparent the node
                            node.parent = parent
                        }
                    } else {
                        Log.warn(
                            objects.name + " is not a root node, and does not have a `parent: ` property. Ignoring.")
                    }
                }
            }
        }
    }


}