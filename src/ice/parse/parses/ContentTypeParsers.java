package ice.parse.parses;

import arc.func.Prov;
import arc.struct.ObjectMap;
import arc.util.Log;
import arc.util.serialization.JsonValue;
import ice.type.content.IceItem;
import ice.type.content.IceLiquid;
import ice.type.content.IceStatusEffect;
import ice.type.content.IceUnitType;
import ice.Ice;
import mindustry.Vars;
import mindustry.ai.types.FlyingAI;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.game.SpawnGroup;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.Block;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.*;
import mindustry.world.meta.BuildVisibility;

import static ice.parse.IceContentParser.*;
import static mindustry.Vars.maxBlockSize;

public class ContentTypeParsers {
    public interface TypeParser<T extends Content> {
        T parse(String name, JsonValue value) throws Exception;
    }

    public static final ObjectMap<ContentType, TypeParser<?>> contentParser = ObjectMap.of(

            ContentType.block, (TypeParser<Block>) (name, value) -> {
                readBundle(ContentType.block, name, value);

                Block block;
                if (locate(ContentType.block, name) != null) {
                    if (value.has("type")) {
                        Log.warn("Warning: '" + Ice.ice.name + "-" + name + "' re-declares a type. This will be interpreted as a new block. If you wish to override a vanilla block, omit the 'type' section, as vanilla block `type`s cannot be changed.");
                        block = make(resolve(value.getString("type", ""), Block.class), name);
                    } else {
                        block = locate(ContentType.block, name);
                    }
                } else {
                    block = make(resolve(value.getString("type", ""), Block.class), name);
                }

                currentContent = block;
                /**设置默认可见,之后可能会被替换*/
                block.buildVisibility = BuildVisibility.shown;
                read(() -> {
                    if (value.has("consumes") && value.get("consumes").isObject()) {
                        for (JsonValue child : value.get("consumes")) {
                            switch (child.name) {
                                case "item" -> block.consumeItem(find(ContentType.item, child.asString()));
                                case "itemCharged" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeItemCharged.class, child));
                                case "itemFlammable" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeItemFlammable.class, child));
                                case "itemRadioactive" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeItemRadioactive.class, child));
                                case "itemExplosive" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeItemExplosive.class, child));
                                case "itemExplode" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeItemExplode.class, child));
                                case "items" ->
                                        block.consume(child.isArray() ? new ConsumeItems(parserJson.readValue(ItemStack[].class, child)) : parserJson.readValue(ConsumeItems.class, child));
                                case "liquidFlammable" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeLiquidFlammable.class, child));
                                case "liquid" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeLiquid.class, child));
                                case "liquids" ->
                                        block.consume(child.isArray() ? new ConsumeLiquids(parserJson.readValue(LiquidStack[].class, child)) : parserJson.readValue(ConsumeLiquids.class, child));

                                case "coolant" ->
                                        block.consume((Consume) parserJson.readValue(ConsumeCoolant.class, child));
                                case "power" -> {
                                    if (child.isNumber()) {
                                        block.consumePower(child.asFloat());
                                    } else {
                                        block.consume((Consume) parserJson.readValue(ConsumePower.class, child));
                                    }
                                }
                                case "powerBuffered" -> block.consumePowerBuffered(child.asFloat());
                                default ->
                                        throw new IllegalArgumentException("未知消费类型: [" + child.name + "] 在block:[" + block.name + "].");
                            }
                        }
                        value.remove("consumes");
                    }

                    readFields(block, value, true);

                    if (block.size > maxBlockSize) {
                        throw new IllegalArgumentException("Blocks cannot be larger than " + maxBlockSize);
                    }


                });

                return block;
            },

            ContentType.unit, (TypeParser<IceUnitType>) (name, value) -> {
                readBundle(ContentType.unit, name, value);

                IceUnitType unit;
                if (locate(ContentType.unit, name) == null) {

                    unit = make(resolve(value.getString("template", ""), IceUnitType.class), name);

                    if (value.has("template")) {
                        value.remove("template");
                    }

                    var typeVal = value.get("type");
                    if (unit.constructor == null || typeVal != null) {
                        if (typeVal != null && !typeVal.isString()) {
                            throw new RuntimeException("Unit '" + name + "' has an incorrect type. Types must be strings.");
                        }

                        unit.constructor = unitType(typeVal);
                    }
                } else {
                    unit = locate(ContentType.unit, name);
                }

                currentContent = unit;
                //TODO test this!
                read(() -> {
                    //添加 reconstructor 类型
                    if (value.has("requirements")) {
                        JsonValue rec = value.remove("requirements");

                        UnitReq req = parserJson.readValue(UnitReq.class, rec);

                        if (req.block instanceof Reconstructor r) {
                            if (req.previous != null) {
                                r.upgrades.add(new UnitType[]{req.previous, unit});
                            }
                        } else if (req.block instanceof UnitFactory f) {
                            f.plans.add(new UnitFactory.UnitPlan(unit, req.time, req.requirements));
                        } else {
                            throw new IllegalArgumentException("Missing a valid 'block' in 'requirements'");
                        }

                    }

                    if (value.has("controller") || value.has("aiController")) {
                        unit.aiController = supply(resolve(value.getString("controller", value.getString("aiController", "")), FlyingAI.class));
                        value.remove("controller");
                    }

                    if (value.has("defaultController")) {
                        var sup = supply(resolve(value.getString("defaultController"), FlyingAI.class));
                        unit.controller = u -> sup.get();
                        value.remove("defaultController");
                    }

                    //读取额外的默认波次
                    if (value.has("waves")) {
                        JsonValue waves = value.remove("waves");
                        SpawnGroup[] groups = parserJson.readValue(SpawnGroup[].class, waves);
                        for (SpawnGroup group : groups) {
                            group.type = unit;
                        }

                        Vars.waves.get().addAll(groups);
                    }

                    readFields(unit, value, true);
                });

                return unit;
            },

            ContentType.weather, (TypeParser<Weather>) (name, value) -> {
                Weather weather;
                if (locate(ContentType.weather, name) != null) {
                    weather = locate(ContentType.weather, name);
                    readBundle(ContentType.weather, name, value);
                } else {
                    readBundle(ContentType.weather, name, value);
                    weather = make(resolve(getType(value), ParticleWeather.class), name);
                    value.remove("type");
                }
                currentContent = weather;
                read(() -> readFields(weather, value));
                return weather;
            },

            ContentType.item, parser(ContentType.item, IceItem::new),

            ContentType.liquid, (TypeParser<IceLiquid>) (name, value) -> {
                IceLiquid liquid;
                if (locate(ContentType.liquid, name) != null) {
                    liquid = locate(ContentType.liquid, name);
                    readBundle(ContentType.liquid, name, value);
                } else {
                    readBundle(ContentType.liquid, name, value);
                    liquid = make(resolve(value.getString("type", null), IceLiquid.class), name);
                    value.remove("type");
                }
                currentContent = liquid;
                read(() -> readFields(liquid, value));
                return liquid;
            },

            ContentType.status, parser(ContentType.status, IceStatusEffect::new),

            ContentType.sector, (TypeParser<SectorPreset>) (name, value) -> {
                if (value.isString()) {
                    return locate(ContentType.sector, name);
                }

                if (!value.has("sector") || !value.get("sector").isNumber())
                    throw new RuntimeException("SectorPresets must have a sector number.");

                SectorPreset out = new SectorPreset(name, currentMod);

                currentContent = out;
                read(() -> {
                    Planet planet = locate(ContentType.planet, value.getString("planet", "serpulo"));

                    if (planet == null)
                        throw new RuntimeException("Planet '" + value.getString("planet") + "' not found.");

                    out.initialize(planet, value.getInt("sector", 0));

                    value.remove("sector");
                    value.remove("planet");

                    readFields(out, value);
                });
                return out;
            });

    //用于解析的 intermediate 类
    static class UnitReq {
        public Block block;
        public ItemStack[] requirements = {};
        public UnitType previous;
        public float time = 60f * 10f;
    }

    public static Prov<Unit> unitType(JsonValue value) {
        if (value == null) return UnitEntity::create;
        return switch (value.asString()) {
            case "flying" -> UnitEntity::create;
            case "mech" -> MechUnit::create;
            case "legs" -> LegsUnit::create;
            case "naval" -> UnitWaterMove::create;
            case "payload" -> PayloadUnit::create;
            case "missile" -> TimedKillUnit::create;
            case "tank" -> TankUnit::create;
            case "hover" -> ElevationMoveUnit::create;
            case "tether" -> BuildingTetherPayloadUnit::create;
            case "crawl" -> CrawlUnit::create;
            default ->
                    throw new RuntimeException("Invalid unit type: '" + value + "'. Must be 'flying/mech/legs/naval/payload/missile/tether/crawl'.");
        };
    }
}


