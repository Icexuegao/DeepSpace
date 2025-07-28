package ice.parse.parses

import arc.func.Func
import arc.func.Prov
import arc.struct.ObjectMap
import arc.util.Log
import arc.util.serialization.JsonValue
import ice.parse.ContentParser
import ice.parse.ContentParser.currentContent
import ice.parse.ContentParser.locate
import ice.parse.ContentParser.read
import ice.parse.ContentParser.readFields
import ice.parse.JTContents.currentMod
import ice.parse.parses.ClassTypeParsers.TypeParser
import ice.library.type.baseContent.item.IceItem
import ice.library.type.baseContent.liquid.IceLiquid
import ice.library.type.baseContent.status.IceStatusEffect
import ice.library.type.baseContent.unit.type.IceUnitType
import mindustry.Vars
import mindustry.ai.types.FlyingAI
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.ctype.MappableContent
import mindustry.entities.units.UnitController
import mindustry.game.SpawnGroup
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.type.*
import mindustry.type.weather.ParticleWeather
import mindustry.world.Block
import mindustry.world.blocks.units.Reconstructor
import mindustry.world.blocks.units.UnitFactory
import mindustry.world.blocks.units.UnitFactory.UnitPlan
import mindustry.world.consumers.*
import mindustry.world.meta.BuildVisibility

object ClassTypeParsers {
    fun interface TypeParser<T : Content> {
        @Throws(Exception::class)
        fun parse(name: String, value: JsonValue): T
    }

    val contentParser: ObjectMap<ContentType, TypeParser<*>> = ObjectMap.of(
        ContentType.item,
        TypeParser { name: String, value: JsonValue ->
            val item: Item = if (locate<MappableContent>(ContentType.item, name) != null) {
                locate<MappableContent>(ContentType.item, name) as Item
            } else {
                IceItem(name, "ffffff")
            }
            currentContent = item
            read { readFields(item, value) }
            item
        },
        ContentType.liquid,
        TypeParser { name: String, value: JsonValue ->
            val liquid: IceLiquid
            if (locate<MappableContent>(ContentType.liquid, name) != null) {
                liquid = locate(ContentType.liquid, name)!!
            } else {
                liquid = ContentParser.make(
                    ContentParser.resolve(value.getString("type", null), IceLiquid::class.java), name
                )
                value.remove("type")
            }
            currentContent = liquid
            read { readFields(liquid, value) }
            liquid
        },
        ContentType.block,
        TypeParser { name: String, value: JsonValue ->
            val block: Block
            if (locate<MappableContent>(ContentType.block, name) != null) {
                if (value.has("type")) {
                    Log.warn(
                        "警告:'$currentMod-$name' re-declares a type. This will be interpreted as a new block. If you wish to override a vanilla block, omit the 'type' section, as vanilla block `type`s cannot be changed."
                    )
                    block =
                        ContentParser.make(ContentParser.resolve(value.getString("type", ""), Block::class.java), name)
                } else {
                    block = locate(ContentType.block, name)!!
                }
            } else {
                block = ContentParser.make(ContentParser.resolve(value.getString("type", ""), Block::class.java), name)
            }

            currentContent = block
            /**设置默认可见,之后可能会被替换 */
            block.buildVisibility = BuildVisibility.shown
            read {
                if (value.has("consumes") && value["consumes"].isObject) {
                    for (child in value["consumes"]) {
                        when (child.name) {
                            "item" -> block.consumeItem(ContentParser.find(ContentType.item, child.asString()))
                            "itemCharged" -> block.consume(
                                ContentParser.parserFieldJson.readValue(
                                    ConsumeItemCharged::class.java, child
                                ) as Consume
                            )

                            "itemFlammable" -> block.consume(
                                ContentParser.parserFieldJson.readValue(
                                    ConsumeItemFlammable::class.java, child
                                ) as Consume
                            )

                            "itemRadioactive" -> block.consume(
                                ContentParser.parserFieldJson.readValue(
                                    ConsumeItemRadioactive::class.java, child
                                ) as Consume
                            )

                            "itemExplosive" -> block.consume(
                                ContentParser.parserFieldJson.readValue(
                                    ConsumeItemExplosive::class.java, child
                                ) as Consume
                            )

                            "itemExplode" -> block.consume(
                                ContentParser.parserFieldJson.readValue(
                                    ConsumeItemExplode::class.java, child
                                ) as Consume
                            )

                            "items" -> block.consume(
                                if (child.isArray) ConsumeItems(
                                    ContentParser.parserFieldJson.readValue(
                                        Array<ItemStack>::class.java, child
                                    )
                                ) else ContentParser.parserFieldJson.readValue(
                                    ConsumeItems::class.java, child
                                )
                            )

                            "liquidFlammable" -> block.consume(
                                ContentParser.parserFieldJson.readValue(
                                    ConsumeLiquidFlammable::class.java, child
                                ) as Consume
                            )

                            "liquid" -> block.consume(
                                ContentParser.parserFieldJson.readValue(ConsumeLiquid::class.java, child) as Consume
                            )

                            "liquids" -> block.consume(
                                if (child.isArray) ConsumeLiquids(
                                    ContentParser.parserFieldJson.readValue(
                                        Array<LiquidStack>::class.java, child
                                    )
                                ) else ContentParser.parserFieldJson.readValue(
                                    ConsumeLiquids::class.java, child
                                )
                            )

                            "coolant" -> block.consume(
                                ContentParser.parserFieldJson.readValue(ConsumeCoolant::class.java, child) as Consume
                            )

                            "power" -> {
                                if (child.isNumber) {
                                    block.consumePower(child.asFloat())
                                } else {
                                    block.consume(
                                        ContentParser.parserFieldJson.readValue(
                                            ConsumePower::class.java, child
                                        ) as Consume
                                    )
                                }
                            }

                            "powerBuffered" -> block.consumePowerBuffered(child.asFloat())
                            else -> throw IllegalArgumentException(
                                "未知消费类型: [" + child.name + "] 在block:[" + block.name + "]."
                            )
                        }
                    }
                    value.remove("consumes")
                }
                readFields(block, value, true)
                require(block.size <= Vars.maxBlockSize) { "Blocks cannot be larger than " + Vars.maxBlockSize }
            }
            block
        },
        ContentType.status,
        ContentParser.parser(ContentType.status) { name: String ->
            IceStatusEffect(name)
        },
        ContentType.weather,
        TypeParser { name: String, value: JsonValue ->
            val weather: Weather
            if (locate<MappableContent>(ContentType.weather, name) != null) {
                weather = locate(ContentType.weather, name)!!
            } else {
                weather = ContentParser.make(
                    ContentParser.resolve(ContentParser.getString(value), ParticleWeather::class.java), name
                )
                value.remove("type")
            }
            currentContent = weather
            read { readFields(weather, value) }
            weather
        },
        ContentType.sector,
        TypeParser { name: String, value: JsonValue ->
            if (value.isString) {
                return@TypeParser locate<SectorPreset>(ContentType.sector, name)!!
            }
            if (!value.has("sector") || !value["sector"].isNumber) throw RuntimeException(
                "SectorPresets必须具有数字编号。"
            )
            val out = SectorPreset(name, currentMod)

            currentContent = out
            read {
                val planet = locate<Planet>(ContentType.planet, value.getString("planet", "serpulo"))
                    ?: throw java.lang.RuntimeException("Planet '" + value.getString("planet") + "' not found.")
                out.initialize(planet, value.getInt("sector", 0))

                value.remove("sector")
                value.remove("planet")
                readFields(out, value)
            }
            out
        },
        ContentType.unit,
        TypeParser { name: String, value: JsonValue ->
            val unit: IceUnitType
            if (locate<MappableContent>(ContentType.unit, name) == null) {
                unit = ContentParser.make(
                    ContentParser.resolve(value.getString("template", ""), IceUnitType::class.java), name
                )

                if (value.has("template")) {
                    value.remove("template")
                }
                val typeVal = value["type"]
                if (unit.constructor == null || typeVal != null) {
                    if (typeVal != null && !typeVal.isString) {
                        throw java.lang.RuntimeException("Unit '$name' has an incorrect type. Types must be strings.")
                    }

                    unit.constructor = unitType(typeVal)
                }
            } else {
                unit = locate(ContentType.unit, name)!!
            }

            currentContent = unit
            //TODO test this!
            read {
                //添加 reconstructor 类型
                if (value.has("requirements")) {
                    val rec = value.remove("requirements")
                    val req = ContentParser.parserFieldJson.readValue(UnitReq::class.java, rec)
                    val block = req.block
                    if (block is Reconstructor) {
                        if (req.previous != null) {
                            block.upgrades.add(arrayOf(req.previous, unit))
                        }
                    } else if (block is UnitFactory) {
                        block.plans.add(UnitPlan(unit, req.time, req.requirements))
                    } else {
                        throw IllegalArgumentException("在 'requirements' 中缺少有效的 'block''")
                    }
                }

                if (value.has("controller") || value.has("aiController")) {
                    val resolve = ContentParser.resolve(
                        value.getString("controller", value.getString("aiController", "")), FlyingAI::class.java
                    )

                    unit.aiController = ContentParser.supply(resolve)
                    value.remove("controller")

                }

                if (value.has("defaultController")) {
                    val sup = ContentParser.supply(
                        ContentParser.resolve(value.getString("defaultController"), FlyingAI::class.java)
                    )
                    unit.controller = Func { sup.get() as UnitController }
                    value.remove("defaultController")
                }
                //读取额外的默认波次
                if (value.has("waves")) {
                    val waves = value.remove("waves")
                    val groups = ContentParser.parserFieldJson.readValue(Array<SpawnGroup>::class.java, waves)
                    for (group in groups) {
                        group.type = unit
                    }

                    Vars.waves.get().addAll(*groups)
                }
                readFields(unit, value, true)
            }
            unit
        },
    )

    class UnitReq {
        var block: Block? = null
        var requirements: Array<ItemStack> = arrayOf()
        var previous: UnitType? = null
        var time: Float = 60f * 10f
    }

    fun unitType(value: JsonValue?): Prov<Unit> {
        if (value == null) return Prov { UnitEntity.create() }
        return when (value.asString()) {
            "flying" -> Prov { UnitEntity.create() }
            "mech" -> Prov { MechUnit.create() }
            "legs" -> Prov { LegsUnit.create() }
            "naval" -> Prov { UnitWaterMove.create() }
            "payload" -> Prov { PayloadUnit.create() }
            "missile" -> Prov { TimedKillUnit.create() }
            "tank" -> Prov { TankUnit.create() }
            "hover" -> Prov { ElevationMoveUnit.create() }
            "tether" -> Prov { BuildingTetherPayloadUnit.create() }
            "crawl" -> Prov { CrawlUnit.create() }
            else -> throw RuntimeException(
                "Invalid unit type: '$value'. Must be 'flying/mech/legs/naval/payload/missile/tether/crawl'."
            )
        }
    }
}