package ice.core

import arc.Core
import arc.struct.Seq
import arc.util.serialization.Jval
import ice.DeepSpace
import ice.audio.IMusics
import ice.entities.ModeDifficulty
import ice.library.world.Load
import mindustry.Vars
import mindustry.ui.dialogs.PlanetDialog
import kotlin.properties.Delegates
import kotlin.reflect.*
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

object SettingValue : Load {
  private var configs = SettingValue::class.memberProperties.filter { prop ->
    // 检查属性的可见性，只保留非私有属性
    prop.visibility != KVisibility.PRIVATE
  }.apply {
    forEach {
      it.isAccessible = true
    }
  }
  private var saveScheduled = false
  private fun scheduleSave() {
    if (!saveScheduled) {
      saveScheduled = true
      Core.app.post {
        save()
        saveScheduled = false
      }
    }
  }

  private val deBugRunSeq = Seq<(Boolean) -> Unit>()
  fun addDeBugRun(block: (Boolean) -> Unit) {
    deBugRunSeq.add(block)
  }

  var 启用主菜单音乐 by observable(true) { _, _, new ->
    if (!new) IMusics.title.stop()
  }
  var difficulty by observable(ModeDifficulty.General)
  var menuMusicVolume by observable(1f)
  var 视野最大缩放限制 by observable(6f) { _, _, new ->
    Vars.renderer.maxZoom = new
  }
  var 视野最小缩放限制 by observable(1.5f) { _, _, new ->
    Vars.renderer.minZoom = new
  }
  var 启用星球区块ID by observable(false)
  var 启用调试模式 by observable(false) { _, _, new ->
    deBugRunSeq.forEach { it.invoke(new) }
  }
  var 星球区块调试 by observable(false) { _, _, new ->
    PlanetDialog.debugSelect = new
  }
  var 启用多合成角标常显 by observable(false)

  var 启用包裹物品绘制 by observable(false)
  var 启用QQ头像获取 by observable(true)
  var 进入游戏自动弹出mod主菜单 by observable(false)
  var 禁用mod主界面背景 by observable(false)

  //游戏目标内信息显示
  var 显示实体信息 by observable(true)
  var 状态指示器不透明度 by observable(0.3f)
  var 信息显示刷新间隔 by observable(0.5f)
  var 最多信息显示数目 by observable(4f)
  var 信息面板缩放 by observable(1f)
  var 范围显示模式选中半径 by observable(64f)
  var 状态指示器尺寸 by observable(4f)
  var 显示状态效果的剩余时间 by observable(false)

  @delegate:Order var godpod by observable(0)

  @Retention(AnnotationRetention.RUNTIME)
  @Target(AnnotationTarget.FIELD)
  private annotation class Order

  private fun <T> observable(initialValue: T, onChange: (property: KProperty<*>, old: T, new: T) -> Unit = { _, _, _ -> }) =
    Delegates.observable(initialValue) { property, old, new ->
      onChange(property, old, new)
      scheduleSave()
    }

  override fun setup() {
    val get = DeepSpace.globals.getString("ice-SettingValue", write())
    val config: Jval = Jval.read(get)
    for (cfg in configs) {
      val name = cfg.name
      if (!config.has(name)) continue
      val temp = config.get(name).toString()
      if (cfg is KMutableProperty<*>) {
        // 使用setter方法设置值
        cfg.setter.call(this, warp(cfg.returnType, temp))
      }
    }
  }

  fun save() {
    DeepSpace.globals.put("ice-SettingValue", write())
  }

  private fun write(): String {
    val tree = Jval.newObject()
    val map = tree.asObject()
    try {
      for (cfg in configs) {
        val key = cfg.name
        val obj = cfg.get(this) ?: continue
        map.put(key, pack(obj))
      }
    } catch (e: Exception) {
      throw Exception("设置写入错误${this::class.simpleName}", e)
    }

    return tree.toString(Jval.Jformat.formatted)
  }

  fun clear() {
    DeepSpace.globals.put("ice-SettingValue", "")
  }

  private inline fun <reified T> warp(type: KType, value: String): T {
    val classifier = type.classifier as KClass<*>
    val obj = when {
      classifier == Int::class -> value.toInt() as T
      classifier == Byte::class -> value.toByte() as T
      classifier == Short::class -> value.toShort() as T
      classifier == Boolean::class -> value.toBoolean() as T
      classifier == Long::class -> value.toLong() as T
      classifier == Char::class -> value.first() as T
      classifier == Float::class -> value.toFloat() as T
      classifier == Double::class -> value.toDouble() as T
      classifier == String::class -> value as T
      classifier.isSubclassOf(CharSequence::class) -> value as T
      classifier.isSubclassOf(Array::class) -> toArray(type, value)
      classifier.isSubclassOf(Enum::class) -> {
        classifier.java.enumConstants?.find { (it as Enum<*>).name == value } as T
      }

      else -> throw IllegalArgumentException("Invalid type: $type")
    }
    return obj
  }

  private fun <T> toArray(type: KType, value: String?): T {
    val classifier = type.classifier as? KClass<*> ?: throw IllegalArgumentException("Invalid array type: $type")

    if (!classifier.java.isArray) throw IllegalArgumentException("Class $classifier was not an array")
    val a = Jval.read(value).asArray()
    val elementType = classifier.java.componentType
    val res = java.lang.reflect.Array.newInstance(elementType, a.size)

    for (i in 0 until a.size) {
      java.lang.reflect.Array.set(res, i, warp(elementType.kotlin.createType(), a[i].toString()))
    }

    @Suppress("UNCHECKED_CAST") return res as T
  }

  private fun pack(value: Any): Jval {
    return when (value) {
      is Int -> Jval.valueOf(value)
      is Byte -> Jval.valueOf(value.toInt())
      is Short -> Jval.valueOf(value.toInt())
      is Boolean -> Jval.valueOf(value)
      is Long -> Jval.valueOf(value)
      is Char -> Jval.valueOf(value.code)
      is Float -> Jval.valueOf(value)
      is Double -> Jval.valueOf(value)
      is String -> Jval.valueOf(value)
      is Array<*> -> packArray(value)
      is Enum<*> -> Jval.valueOf(value.name)
      else -> throw IllegalArgumentException("无支撑类型: ${value::class.qualifiedName}")
    }
  }

  private fun packArray(array: Array<*>): Jval {
    val len = java.lang.reflect.Array.getLength(array)
    val res = Jval.newArray()
    val arr = res.asArray()
    for (i in 0..<len) {
      arr.add(pack(java.lang.reflect.Array.get(array, i)))
    }
    return res
  }
}