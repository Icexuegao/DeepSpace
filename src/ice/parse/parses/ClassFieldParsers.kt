package ice.parse.parses

import arc.Core
import arc.assets.AssetDescriptor
import arc.assets.loaders.SoundLoader.SoundParameter
import arc.audio.Sound
import arc.func.Cons
import arc.graphics.Blending
import arc.graphics.Color
import arc.math.Interp
import arc.math.geom.Mat3D
import arc.math.geom.Vec3
import arc.struct.ObjectMap
import arc.util.Log
import arc.util.Strings
import arc.util.serialization.JsonValue
import ice.parse.ContentParser
import ice.parse.ContentParser.parserFieldJson
import ice.parse.ContentParser.readFields
import ice.parse.JTContents.currentMod
import mindustry.Vars
import mindustry.content.*
import mindustry.ctype.ContentType
import mindustry.ctype.MappableContent
import mindustry.ctype.UnlockableContent
import mindustry.entities.Effect
import mindustry.entities.UnitSorts
import mindustry.entities.Units.Sortf
import mindustry.entities.abilities.Ability
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.part.DrawPart
import mindustry.entities.part.DrawPart.PartProgress
import mindustry.entities.part.RegionPart
import mindustry.entities.pattern.ShootPattern
import mindustry.game.Objectives.*
import mindustry.game.Schematic
import mindustry.game.Schematics
import mindustry.gen.Sounds
import mindustry.graphics.CacheLayer
import mindustry.maps.generators.PlanetGenerator
import mindustry.maps.planet.AsteroidGenerator
import mindustry.type.*
import mindustry.type.ammo.ItemAmmoType
import mindustry.type.ammo.PowerAmmoType
import mindustry.world.consumers.Consume
import mindustry.world.consumers.ConsumeLiquid
import mindustry.world.consumers.ConsumeLiquidBase
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.Attribute
import mindustry.world.meta.BuildVisibility

object ClassFieldParsers {
    fun interface FieldParser {
        @Throws(Exception::class)
        fun parse(type: Class<*>, value: JsonValue): Any
    }

    val classPar: ObjectMap<Class<*>, FieldParser> = ObjectMap<Class<*>, FieldParser>().apply {
        put(Effect::class.java) { _: Class<*>, data: JsonValue ->
            if (data.isString) {
                return@put ContentParser.field(Fx::class.java, data)
            }
            if (data.isArray) {
                return@put MultiEffect(*parserFieldJson.readValue<Array<Effect>>(Array<Effect>::class.java, data))
            }
            val bc: Class<*> = ContentParser.resolve(data.getString("type", ""), ParticleEffect::class.java)
            data.remove("type")
            val result = ContentParser.make<Effect>(bc)
            readFields(result, data)
            return@put result
        }
        put(Sortf::class.java) { _: Class<*>, data: JsonValue ->
            return@put ContentParser.field(UnitSorts::class.java, data)
        }
        put(Interp::class.java) { _: Class<*>, data: JsonValue ->
            ContentParser.field(Interp::class.java, data)
        }
        put(Blending::class.java) { _: Class<*>, data: JsonValue ->
            ContentParser.field(Blending::class.java, data)
        }
        put(CacheLayer::class.java) { _: Class<*>, data: JsonValue ->
            return@put ContentParser.field(CacheLayer::class.java, data)
        }
        put(Attribute::class.java) { _: Class<*>, data: JsonValue ->
            val attr = data.asString()
            if (Attribute.exists(attr)) return@put Attribute.get(attr)
            return@put Attribute.add(attr)
        }
        put(BuildVisibility::class.java) { _: Class<*>, data: JsonValue ->
            ContentParser.field(BuildVisibility::class.java, data)
        }
        put(Schematic::class.java) { _: Class<*>, data: JsonValue ->
            val result = ContentParser.fieldOpt(Loadouts::class.java, data)
            if (result != null) {
                return@put result
            } else {
                val str = data.asString()
                if (str.startsWith(Vars.schematicBaseStart)) {
                    return@put Schematics.readBase64(str)
                } else {
                    return@put Schematics.read(Vars.tree["schematics/" + str + "." + Vars.schematicExtension])
                }
            }
        }
        put(Color::class.java) { _: Class<*>, data: JsonValue ->
            val string = data.asString()
            val contains = string.contains("Color.")
            if (contains) {
                return@put ContentParser.field(Color::class.java, string.replace("Color.", ""))
            }
            Color.valueOf(data.asString())
        }
        put(StatusEffect::class.java) { _: Class<*>, data: JsonValue ->
            if (data.isString) {
                val result = ContentParser.locate<StatusEffect>(ContentType.status, data.asString())
                if (result != null) return@put result
                throw IllegalArgumentException("未知状态效果: '" + data.asString() + "'")
            }
            val effect = StatusEffect(data.getString("name"))
            effect.minfo.mod = currentMod
            readFields(effect, data)
            effect
        }

        put(BulletType::class.java) { _: Class<*>, data: JsonValue ->
            if (data.isString) {
                return@put ContentParser.field(Bullets::class.java, data)
            }
            val bc: Class<*> = ContentParser.resolve(data.getString("type", ""), BasicBulletType::class.java)
            data.remove("type")
            val result = ContentParser.make(bc) as BulletType
            readFields(result, data)
            result
        }
        put(AmmoType::class.java) { _: Class<*>, data: JsonValue ->
            //string -> item
            //if liquid ammo support is added, this should scan for liquids as well
            if (data.isString) return@put ItemAmmoType(ContentParser.find<Item>(ContentType.item, data.asString()))
            //number -> power
            if (data.isNumber) return@put PowerAmmoType(data.asFloat())

            val bc = ContentParser.resolve(data.getString("type", ""), ItemAmmoType::class.java)
            data.remove("type")
            val result: AmmoType = ContentParser.make(bc)
            readFields(result, data)
            result
        }
        put(DrawBlock::class.java) { c: Class<*>, data: JsonValue ->
            if (data.isString) {
                //try to instantiate
                return@put ContentParser.make<Any>(ContentParser.resolve(data.asString(), c))
            }
            //array is shorthand for DrawMulti
            if (data.isArray) {
                return@put DrawMulti(*parserFieldJson.readValue<Array<DrawBlock>>(Array<DrawBlock>::class.java, data))
            }
            val bc = ContentParser.resolve(data.getString("type", ""), DrawDefault::class.java)
            data.remove("type")
            val result: DrawBlock = ContentParser.make(bc)
            readFields(result, data)
            result
        }
        put(ShootPattern::class.java) { _: Class<*>, data: JsonValue ->
            val bc = ContentParser.resolve(data.getString("type", ""), ShootPattern::class.java)
            data.remove("type")
            val result = ContentParser.make<ShootPattern>(bc)
            readFields(result, data)
            result
        }
        put(DrawPart::class.java) { _: Class<*>, data: JsonValue ->
            val bc: Class<*> = ContentParser.resolve(data.getString("type", ""), RegionPart::class.java)
            data.remove("type")
            val result = ContentParser.make<DrawPart>(bc)
            readFields(result, data)
            result
        }
        put(PartProgress::class.java) { _: Class<*>, data: JsonValue ->
            //简单情况：它是一个字符串或数字常量
            if (data.isString) return@put ContentParser.field(PartProgress::class.java, data.asString())
            if (data.isNumber) return@put PartProgress.constant(data.asFloat())

            if (!data.has("type")) {
                throw RuntimeException(
                    "PartProgress object need a 'type' string field. Check the PartProgress class for a list of constants."
                )
            }

            var base: PartProgress =
                ContentParser.field(PartProgress::class.java, data.getString("type")) as PartProgress

            val opval = if (data.has("operation")) data["operation"] else if (data.has("op")) data["op"] else null

            //无单一操作，检查多操作
            if (opval == null) {
                val opsVal =
                    if (data.has("operations")) data["operations"] else if (data.has("ops")) data["ops"] else null

                if (opsVal != null) {
                    if (!opsVal.isArray) throw RuntimeException("链接的 PartProgress 操作必须是一个数组。")
                    var i = 0
                    while (true) {
                        val `val` = opsVal[i]
                        val op = if (`val`.has("operation")) `val`["operation"] else if (`val`.has(
                                "op"
                            )
                        ) `val`["op"] else null

                        if (op != null) base = ContentParser.parseProgressOp(base, op.asString(), `val`)
                        i++
                    }
                }

                return@put base
            }

            //这是要调用的方法的名称
            val op = opval.asString()
            ContentParser.parseProgressOp(base, op, data)
        }
        put(PlanetGenerator::class.java) { _: Class<*>, data: JsonValue ->
            val result = AsteroidGenerator() //目前只有一种类型
            readFields(result, data)
            result
        }
        put(Mat3D::class.java) { _: Class<*>, data: JsonValue ->
            //transform x y z format
            if (data.has("x") && data.has("y") && data.has("z")) {
                return@put Mat3D().translate(data.getFloat("x", 0f), data.getFloat("y", 0f), data.getFloat("z", 0f))
            }

            //transform array format
            if (data.isArray && data.size == 3) {
                return@put Mat3D().setToTranslation(Vec3(data.asFloatArray()))
            }

            val mat = Mat3D()

            //TODO this is kinda bad
            for (`val` in data) {
                when (`val`.name) {
                    "translate", "trans" -> mat.translate(parserFieldJson.readValue(Vec3::class.java, data))

                    "scale", "scl" -> mat.scale(parserFieldJson.readValue(Vec3::class.java, data))
                    "rotate", "rot" -> mat.rotate(
                        parserFieldJson.readValue(Vec3::class.java, data), data.getFloat("degrees", 0f)
                    )

                    "multiply", "mul" -> mat.mul(parserFieldJson.readValue(Mat3D::class.java, data))
                    "x", "y", "z" -> {
                    }

                    else -> throw RuntimeException("Unknown matrix transformation: '" + `val`.name + "'")
                }
            }
            mat
        }
        put(Vec3::class.java) { _: Class<*>, data: JsonValue ->
            if (data.isArray) return@put Vec3(data.asFloatArray())
            Vec3(data.getFloat("x", 0f), data.getFloat("y", 0f), data.getFloat("z", 0f))
        }
        put(Sound::class.java) { _: Class<*>, data: JsonValue ->
            val fieldOpt = ContentParser.fieldOpt(Sounds::class.java, data)
            if (fieldOpt != null) return@put fieldOpt

            if (Vars.headless) return@put Sound()

            val name = "sounds/" + data.asString()
            val path = if (Vars.tree["$name.ogg"].exists()) "$name.ogg" else "$name.mp3"

            if (ContentParser.sounds.containsKey(
                    path
                )
            ) return@put (ContentParser.sounds[path].params as SoundParameter).sound
            val sound = Sound()
            val desc: AssetDescriptor<*> = Core.assets.load(path, Sound::class.java, SoundParameter(sound))
            desc.errored = Cons { obj: Throwable -> obj.printStackTrace() }
            ContentParser.sounds.put(path, desc)
            sound
        }
        put(Objective::class.java) { _: Class<*>, data: JsonValue ->
            if (data.isString) {
                val cont = ContentParser.locateAny<MappableContent>(data.asString())
                requireNotNull(cont) { "Unknown objective content: " + data.asString() }
                return@put Research(cont as UnlockableContent)
            }
            val oc = ContentParser.resolve(data.getString("type", ""), SectorComplete::class.java)
            data.remove("type")
            val obj: Objective = ContentParser.make(oc)
            readFields(obj, data)
            obj
        }
        put(Ability::class.java) { c: Class<*>, data: JsonValue ->
            val oc: Class<*> = ContentParser.resolve(data.getString("type", ""), c)
            data.remove("type")
            val obj = ContentParser.make<Ability>(oc)
            readFields(obj, data)
            obj
        }
        put(Weapon::class.java) { _: Class<*>, data: JsonValue ->
            val oc = ContentParser.resolve(data.getString("type", ""), Weapon::class.java)
            data.remove("type")
            val weapon: Weapon = ContentParser.make(oc)
            readFields(weapon, data)
            weapon.name = currentMod.name + "-" + weapon.name
            weapon
        }
        put(Consume::class.java) { _: Class<*>, data: JsonValue ->
            val oc = ContentParser.resolve(data.getString("type", ""), Consume::class.java)
            data.remove("type")
            val consume: Consume = ContentParser.make(oc)
            readFields(consume, data)
            consume
        }
        put(ConsumeLiquidBase::class.java) { _: Class<*>, data: JsonValue ->
            val oc = ContentParser.resolve(data.getString("type", ""), ConsumeLiquidBase::class.java)
            data.remove("type")
            val consume = ContentParser.make<ConsumeLiquidBase>(oc)
            readFields(consume, data)
            consume
        }
        put(Item::class.java) { _, jsonData ->
            val item =
                Vars.content.item(jsonData.asString()) ?: Vars.content.item(currentMod.name + "-" + jsonData.asString())
            return@put if (item == null) {
                Log.warn("${currentMod.name}:没有找到物品${jsonData.asString()},默认返回${Items.copper.name}")
                Items.copper
            } else {
                item
            }

        }
        put(Liquid::class.java) { _, jsonData ->
            val liquid = Vars.content.liquid(jsonData.asString()) ?: Vars.content.liquid(
                currentMod.name + "-" + jsonData.asString()
            )
            return@put if (liquid == null) {
                Log.warn("${currentMod.name}:没有找到物品${jsonData.asString()},默认返回${Liquids.water.name}")
                Liquids.water
            } else {
                liquid
            }

        }
        put(ItemStack::class.java) { _, jsonData ->
            if (jsonData.isString && jsonData.asString().contains("/")) {
                val split: Array<String> = jsonData.asString().split("/").toTypedArray()
                return@put parserFieldJson.fromJson(
                    ItemStack::class.java, "{item: " + split[0] + ", amount: " + split[1] + "}"
                )
            }
            val itemStack = ItemStack()
            readFields(itemStack, jsonData)
            return@put itemStack
        }
        put(LiquidStack::class.java) { _, jsonData ->
            if (jsonData.isString && jsonData.asString().contains("/")) {
                val split: Array<String> = jsonData.asString().split("/").toTypedArray()
                return@put parserFieldJson.fromJson(
                    LiquidStack::class.java, "{liquid: " + split[0] + ", amount: " + split[1] + "}"
                )
            }
            val lIquidStack = LiquidStack(Liquids.water, 1f)
            readFields(lIquidStack, jsonData)
            return@put lIquidStack
        }
        put(ConsumeLiquid::class.java) { _, jsonData ->
            val liquids = parserFieldJson.fromJson(LiquidStack::class.java, jsonData.asString())
            return@put ConsumeLiquid(liquids.liquid, liquids.amount)
        }
        put(PayloadStack::class.java) { _, jsonData ->
            //try to parse "payloaditem/amount" syntax
            if (jsonData.isString && jsonData.asString().contains("/")) {
                val split = jsonData.asString().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val number = Strings.parseInt(split[1], 1)
                val cont: UnlockableContent? =
                    if (Vars.content.unit(split[0]) == null) Vars.content.block(split[0]) else Vars.content.unit(
                        split[0]
                    )
                return@put PayloadStack(cont ?: Blocks.router, number)
            } else {
                val payloadStack = PayloadStack()
                readFields(payloadStack, jsonData)
                return@put payloadStack
            }

        }
    }
}