package ice.Alon.Parse;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader.SoundParameter;
import arc.audio.Sound;
import arc.files.Fi;
import arc.func.Func;
import arc.func.Prov;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.geom.Mat3D;
import arc.math.geom.Rect;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.*;
import arc.util.serialization.Json;
import arc.util.serialization.Json.FieldMetadata;
import arc.util.serialization.JsonValue;
import arc.util.serialization.Jval;
import arc.util.serialization.Jval.Jformat;
import arc.util.serialization.Jval.JsonParseException;
import arc.util.serialization.SerializationException;
import ice.Ice;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.FlyingAI;
import mindustry.content.*;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units.Sortf;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.DrawPart.PartProgress;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootPattern;
import mindustry.game.Objectives.Objective;
import mindustry.game.Objectives.Produce;
import mindustry.game.Objectives.Research;
import mindustry.game.Objectives.SectorComplete;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.game.SpawnGroup;
import mindustry.gen.*;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.Shaders;
import mindustry.graphics.g3d.*;
import mindustry.graphics.g3d.PlanetGrid.Ptile;
import mindustry.io.JsonIO;
import mindustry.io.SaveVersion;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.maps.planet.AsteroidGenerator;
import mindustry.mod.ClassMap;
import mindustry.mod.Mods.LoadedMod;
import mindustry.type.*;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.Block;
import mindustry.world.blocks.units.Reconstructor;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.blocks.units.UnitFactory.UnitPlan;
import mindustry.world.consumers.*;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.meta.Env;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static mindustry.Vars.*;

@SuppressWarnings("unchecked")
public class IceContentParser {
    public Content parse(String name, String json, Fi file, ContentType type) throws Exception {
        if (contentTypes.isEmpty()) {
            init();
        }
        //删除额外的 # 字符以使其有效 JSON...显然有些人的 JSON 中有 *unquoted* # 字符
        if (file.extension().equals("json")) {
            json = json.replace("#", "\\#");
        }
        JsonValue value = parserJson.fromJson(null, Jval.read(json).toString(Jformat.plain));
        if (!parsers.containsKey(type)) {
            throw new SerializationException("No parsers for content type '" + type + "'");
        }

        boolean located = locate(type, name) == null;

        Content c = parsers.get(type).parse(name, value);
        c.minfo.sourceFile = file;
        toBeParsed.add(c);
        /**判断是否是新content然后设置mod*/
        if (located) {
            c.minfo.mod = currentMod;
        }
        return c;
    }

    /**
     * 忽略未知字段?
     */
    private static final boolean ignoreUnknownFields = true;
    ObjectMap<Class<?>, ContentType> contentTypes = new ObjectMap<>();
    ObjectSet<Class<?>> implicitNullable = ObjectSet.with(TextureRegion.class, TextureRegion[].class, TextureRegion[][].class, TextureRegion[][][].class);
    ObjectMap<String, AssetDescriptor<?>> sounds = new ObjectMap<>();

    ObjectMap<Class<?>, FieldParser> classParsers = new ObjectMap<>() {{
        put(Effect.class, (type, data)->{
            if (data.isString()) {
                return field(Fx.class, data);
            }
            if (data.isArray()) {
                return new MultiEffect(parserJson.readValue(Effect[].class, data));
            }
            Class<? extends Effect> bc = resolve(data.getString("type", ""), ParticleEffect.class);
            data.remove("type");
            Effect result = make(bc);
            readFields(result, data);
            return result;
        });
        put(Sortf.class, (type, data)->field(UnitSorts.class, data));
        put(Interp.class, (type, data)->field(Interp.class, data));
        put(Blending.class, (type, data)->field(Blending.class, data));
        put(CacheLayer.class, (type, data)->field(CacheLayer.class, data));
        put(Attribute.class, (type, data)->{
            String attr = data.asString();
            if (Attribute.exists(attr)) return Attribute.get(attr);
            return Attribute.add(attr);
        });
        put(BuildVisibility.class, (type, data)->field(BuildVisibility.class, data));
        put(Schematic.class, (type, data)->{
            Object result = fieldOpt(Loadouts.class, data);
            if (result != null) {
                return result;
            } else {
                String str = data.asString();
                if (str.startsWith(Vars.schematicBaseStart)) {
                    return Schematics.readBase64(str);
                } else {
                    return Schematics.read(Vars.tree.get("schematics/" + str + "." + Vars.schematicExtension));
                }
            }
        });
        put(Color.class, (type, data)->Color.valueOf(data.asString()));
        put(StatusEffect.class, (type, data)->{
            if (data.isString()) {
                StatusEffect result = locate(ContentType.status, data.asString());
                if (result != null) return result;
                throw new IllegalArgumentException("Unknown status effect: '" + data.asString() + "'");
            }
            StatusEffect effect = new StatusEffect(currentMod.name + "-" + data.getString("name"));
            effect.minfo.mod = currentMod;
            readFields(effect, data);
            return effect;
        });
        put(UnitCommand.class, (type, data)->{
            if (data.isString()) {
                var cmd = UnitCommand.all.find(u->u.name.equals(data.asString()));
                if (cmd != null) {
                    return cmd;
                } else {
                    throw new IllegalArgumentException("Unknown unit command name: " + data.asString());
                }
            } else {
                throw new IllegalArgumentException("Unit commands must be strings.");
            }
        });
        put(BulletType.class, (type, data)->{
            if (data.isString()) {
                return field(Bullets.class, data);
            }
            Class<?> bc = resolve(data.getString("type", ""), BasicBulletType.class);
            data.remove("type");
            BulletType result = (BulletType) make(bc);
            readFields(result, data);
            return result;
        });
        put(AmmoType.class, (type, data)->{
            //string -> item
            //if liquid ammo support is added, this should scan for liquids as well
            if (data.isString()) return new ItemAmmoType(find(ContentType.item, data.asString()));
            //number -> power
            if (data.isNumber()) return new PowerAmmoType(data.asFloat());

            var bc = resolve(data.getString("type", ""), ItemAmmoType.class);
            data.remove("type");
            AmmoType result = make(bc);
            readFields(result, data);
            return result;
        });
        put(DrawBlock.class, (type, data)->{
            if (data.isString()) {
                //try to instantiate
                return make(resolve(data.asString()));
            }
            //array is shorthand for DrawMulti
            if (data.isArray()) {
                return new DrawMulti(parserJson.readValue(DrawBlock[].class, data));
            }
            var bc = resolve(data.getString("type", ""), DrawDefault.class);
            data.remove("type");
            DrawBlock result = make(bc);
            readFields(result, data);
            return result;
        });
        put(ShootPattern.class, (type, data)->{
            var bc = resolve(data.getString("type", ""), ShootPattern.class);
            data.remove("type");
            var result = make(bc);
            readFields(result, data);
            return result;
        });
        put(DrawPart.class, (type, data)->{
            Class<?> bc = resolve(data.getString("type", ""), RegionPart.class);
            data.remove("type");
            var result = make(bc);
            readFields(result, data);
            return result;
        });
        //TODO this is untested
        put(PartProgress.class, (type, data)->{
            //simple case: it's a string or number constant
            if (data.isString()) return field(PartProgress.class, data.asString());
            if (data.isNumber()) return PartProgress.constant(data.asFloat());

            if (!data.has("type")) {
                throw new RuntimeException("PartProgress object need a 'type' string field. Check the PartProgress class for a list of constants.");
            }

            PartProgress base = (PartProgress) field(PartProgress.class, data.getString("type"));

            JsonValue opval = data.has("operation") ? data.get("operation") : data.has("op") ? data.get("op") : null;

            //no singular operation, check for multi-operation
            if (opval == null) {
                JsonValue opsVal = data.has("operations") ? data.get("operations") : data.has("ops") ? data.get("ops") : null;

                if (opsVal != null) {
                    if (!opsVal.isArray())
                        throw new RuntimeException("Chained PartProgress operations must be an array.");
                    int i = 0;
                    while (true) {
                        JsonValue val = opsVal.get(i);
                        if (val == null) break;
                        JsonValue op = val.has("operation") ? val.get("operation") : val.has("op") ? val.get("op") : null;

                        base = parseProgressOp(base, op.asString(), val);
                        i++;
                    }
                }

                return base;
            }

            //this is the name of the method to call
            String op = opval.asString();

            return parseProgressOp(base, op, data);
        });
        put(PlanetGenerator.class, (type, data)->{
            var result = new AsteroidGenerator(); //only one type for now
            readFields(result, data);
            return result;
        });
        put(Mat3D.class, (type, data)->{
            if (data == null) return new Mat3D();

            //transform x y z format
            if (data.has("x") && data.has("y") && data.has("z")) {
                return new Mat3D().translate(data.getFloat("x", 0f), data.getFloat("y", 0f), data.getFloat("z", 0f));
            }

            //transform array format
            if (data.isArray() && data.size == 3) {
                return new Mat3D().setToTranslation(new Vec3(data.asFloatArray()));
            }

            Mat3D mat = new Mat3D();

            //TODO this is kinda bad
            for (var val : data) {
                switch (val.name) {
                    case "translate", "trans" -> mat.translate(parserJson.readValue(Vec3.class, data));
                    case "scale", "scl" -> mat.scale(parserJson.readValue(Vec3.class, data));
                    case "rotate", "rot" ->
                            mat.rotate(parserJson.readValue(Vec3.class, data), data.getFloat("degrees", 0f));
                    case "multiply", "mul" -> mat.mul(parserJson.readValue(Mat3D.class, data));
                    case "x", "y", "z" -> {
                    }
                    default -> throw new RuntimeException("Unknown matrix transformation: '" + val.name + "'");
                }
            }

            return mat;
        });
        put(Vec3.class, (type, data)->{
            if (data.isArray()) return new Vec3(data.asFloatArray());
            return new Vec3(data.getFloat("x", 0f), data.getFloat("y", 0f), data.getFloat("z", 0f));
        });
        put(Sound.class, (type, data)->{
            if (fieldOpt(Sounds.class, data) != null) return fieldOpt(Sounds.class, data);
            if (Vars.headless) return new Sound();

            String name = "sounds/" + data.asString();
            String path = Vars.tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

            if (sounds.containsKey(path)) return ((SoundParameter) sounds.get(path).params).sound;
            var sound = new Sound();
            AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundParameter(sound));
            desc.errored = Throwable::printStackTrace;
            sounds.put(path, desc);
            return sound;
        });
        put(Objective.class, (type, data)->{
            if (data.isString()) {
                var cont = locateAny(data.asString());
                if (cont == null) throw new IllegalArgumentException("Unknown objective content: " + data.asString());
                return new Research((UnlockableContent) cont);
            }
            var oc = resolve(data.getString("type", ""), SectorComplete.class);
            data.remove("type");
            Objective obj = make(oc);
            readFields(obj, data);
            return obj;
        });
        put(Ability.class, (type, data)->{
            Class<? extends Ability> oc = resolve(data.getString("type", ""));
            data.remove("type");
            Ability obj = make(oc);
            readFields(obj, data);
            return obj;
        });
        put(Weapon.class, (type, data)->{
            var oc = resolve(data.getString("type", ""), Weapon.class);
            data.remove("type");
            var weapon = make(oc);
            readFields(weapon, data);
            weapon.name = currentMod.name + "-" + weapon.name;
            return weapon;
        });
        put(Consume.class, (type, data)->{
            var oc = resolve(data.getString("type", ""), Consume.class);
            data.remove("type");
            var consume = make(oc);
            readFields(consume, data);
            return consume;
        });
        put(ConsumeLiquidBase.class, (type, data)->{
            var oc = resolve(data.getString("type", ""), ConsumeLiquidBase.class);
            data.remove("type");
            var consume = make(oc);
            readFields(consume, data);
            return consume;
        });
    }};
    /**
     * Stores things that need to be parsed fully, e.g. reading fields of content.
     * This is done to accommodate binding of content names first.
     */
    private final Seq<Runnable> reads = new Seq<>();
    private final Seq<Runnable> postreads = new Seq<>();
    private final ObjectSet<Object> toBeParsed = new ObjectSet<>();

    LoadedMod currentMod = Ice.ice;
    Content currentContent;

    public final Json parserJson = new Json() {
        {
            IceJsonIO.INSTANCE.apply(this);
        }

        @Override
        public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData, Class keyType) {
            T t = internalRead(type, elementType, jsonData, keyType);
            /**t不等于null 不是包装器       type==null或者不是基元类型*/
            if (t != null && !Reflect.isWrapper(t.getClass()) && (type == null || !type.isPrimitive())) {
                /**检查对象是否有为null的属性*/
                checkNullFields(t);
            }
            return t;
        }

        private <T> T internalRead(Class<T> type, Class elementType, JsonValue jsonData, Class keyType) {

            if (type != null) {
                /**从classmap解析对象*/
                if (classParsers.containsKey(type)) {
                    try {
                        return (T) classParsers.get(type).parse(type, jsonData);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                //尝试解析 env 位
                if ((type == int.class || type == Integer.class) && jsonData.isArray()) {
                    int value = 0;
                    for (var str : jsonData) {
                        if (!str.isString())
                            throw new SerializationException("Integer bitfield values must all be strings. Found: " + str);
                        String field = str.asString();
                        value |= Reflect.<Integer>get(Env.class, field);
                    }

                    return (T) (Integer) value;
                }

                //尝试解析 “item/amount” 语法
                if (type == ItemStack.class && jsonData.isString() && jsonData.asString().contains("/")) {
                    String[] split = jsonData.asString().split("/");
                    return (T) fromJson(ItemStack.class, "{item: " + split[0] + ", amount: " + split[1] + "}");
                }
                //try to parse "payloaditem/amount" syntax
                if (type == PayloadStack.class && jsonData.isString() && jsonData.asString().contains("/")) {
                    String[] split = jsonData.asString().split("/");
                    int number = Strings.parseInt(split[1], 1);
                    UnlockableContent cont = content.unit(split[0]) == null ? content.block(split[0]) : content.unit(split[0]);
                    return (T) new PayloadStack(cont == null ? Blocks.router : cont, number);
                }

                //尝试解析 “liquid/amount” 语法
                if (jsonData.isString() && jsonData.asString().contains("/")) {
                    String[] split = jsonData.asString().split("/");
                    if (type == LiquidStack.class) {
                        return (T) fromJson(LiquidStack.class, "{liquid: " + split[0] + ", amount: " + split[1] + "}");
                    } else if (type == ConsumeLiquid.class) {
                        return (T) fromJson(ConsumeLiquid.class, "{liquid: " + split[0] + ", amount: " + split[1] + "}");
                    }
                }

                //try to parse Rect as array
                if (type == Rect.class && jsonData.isArray() && jsonData.size == 4) {
                    return (T) new Rect(jsonData.get(0).asFloat(), jsonData.get(1).asFloat(), jsonData.get(2).asFloat(), jsonData.get(3).asFloat());
                }

                if (Content.class.isAssignableFrom(type)) {
                    ContentType ctype = contentTypes.getThrow(type, ()->new IllegalArgumentException("No content type for class: " + type.getSimpleName()));
                    String prefix = currentMod != null ? currentMod.name + "-" : "";
                    T one = (T) Vars.content.getByName(ctype, prefix + jsonData.asString());
                    if (one != null) return one;
                    T two = (T) Vars.content.getByName(ctype, jsonData.asString());
                    if (two != null) return two;
                    throw new IllegalArgumentException("\"" + jsonData.name + "\": No " + ctype + " found with name '" + jsonData.asString() + "'.\nMake sure '" + jsonData.asString() + "' is spelled correctly, and that it really exists!\nThis may also occur because its file failed to parse.");
                }
            }
            return super.readValue(type, elementType, jsonData, keyType);
        }
    };

    private final ObjectMap<ContentType, TypeParser<?>> parsers = ObjectMap.of(ContentType.block, (TypeParser<Block>) (name, value)->{
                readBundle(ContentType.block, name, value);

                Block block;
                if (locate(ContentType.block, name) != null) {
                    if (value.has("type")) {
                        Log.warn("Warning: '" + currentMod.name + "-" + name + "' re-declares a type. This will be interpreted as a new block. If you wish to override a vanilla block, omit the 'type' section, as vanilla block `type`s cannot be changed.");
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
                read(()->{
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
                                case "liquid" -> block.consume((Consume) parserJson.readValue(ConsumeLiquid.class, child));
                                case "liquids" ->
                                        block.consume(child.isArray() ? new ConsumeLiquids(parserJson.readValue(LiquidStack[].class, child)) : parserJson.readValue(ConsumeLiquids.class, child));
                                case "coolant" -> block.consume((Consume) parserJson.readValue(ConsumeCoolant.class, child));
                                case "power" -> {
                                    if (child.isNumber()) {
                                        block.consumePower(child.asFloat());
                                    } else {
                                        block.consume((Consume) parserJson.readValue(ConsumePower.class, child));
                                    }
                                }
                                case "powerBuffered" -> block.consumePowerBuffered(child.asFloat());
                                default ->
                                        throw new IllegalArgumentException("Unknown consumption type: '" + child.name + "' for block '" + block.name + "'.");
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
            }, ContentType.unit, (TypeParser<UnitType>) (name, value)->{
                readBundle(ContentType.unit, name, value);

                UnitType unit;
                if (locate(ContentType.unit, name) == null) {

                    unit = make(resolve(value.getString("template", ""), UnitType.class), name);

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
                read(()->{
                    //添加 reconstructor 类型
                    if (value.has("requirements")) {
                        JsonValue rec = value.remove("requirements");

                        UnitReq req = parserJson.readValue(UnitReq.class, rec);

                        if (req.block instanceof Reconstructor r) {
                            if (req.previous != null) {
                                r.upgrades.add(new UnitType[]{req.previous, unit});
                            }
                        } else if (req.block instanceof UnitFactory f) {
                            f.plans.add(new UnitPlan(unit, req.time, req.requirements));
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
                        unit.controller = u->sup.get();
                        value.remove("defaultController");
                    }

                    //read extra default waves
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
            }, ContentType.weather, (TypeParser<Weather>) (name, value)->{
                Weather item;
                if (locate(ContentType.weather, name) != null) {
                    item = locate(ContentType.weather, name);
                    readBundle(ContentType.weather, name, value);
                } else {
                    readBundle(ContentType.weather, name, value);
                    item = make(resolve(getType(value), ParticleWeather.class), name);
                    value.remove("type");
                }
                currentContent = item;
                read(()->readFields(item, value));
                return item;
            }, ContentType.item, parser(ContentType.item, Item::new),

            ContentType.liquid, (TypeParser<Liquid>) (name, value)->{
                Liquid liquid;
                if (locate(ContentType.liquid, name) != null) {
                    liquid = locate(ContentType.liquid, name);
                    readBundle(ContentType.liquid, name, value);
                } else {
                    readBundle(ContentType.liquid, name, value);
                    liquid = make(resolve(value.getString("type", null), Liquid.class), name);
                    value.remove("type");
                }
                currentContent = liquid;
                read(()->readFields(liquid, value));
                return liquid;
            }, ContentType.status, parser(ContentType.status, StatusEffect::new), ContentType.sector, (TypeParser<SectorPreset>) (name, value)->{
                if (value.isString()) {
                    return locate(ContentType.sector, name);
                }

                if (!value.has("sector") || !value.get("sector").isNumber())
                    throw new RuntimeException("SectorPresets must have a sector number.");

                SectorPreset out = new SectorPreset(name, currentMod);

                currentContent = out;
                read(()->{
                    Planet planet = locate(ContentType.planet, value.getString("planet", "serpulo"));

                    if (planet == null)
                        throw new RuntimeException("Planet '" + value.getString("planet") + "' not found.");

                    out.initialize(planet, value.getInt("sector", 0));

                    value.remove("sector");
                    value.remove("planet");

                    readFields(out, value);
                });
                return out;
            }, ContentType.planet, (TypeParser<Planet>) (name, value)->{
                if (value.isString()) return locate(ContentType.planet, name);

                Planet parent = locate(ContentType.planet, value.getString("parent"));
                Planet planet = new Planet(name, parent, value.getFloat("radius", 1f), value.getInt("sectorSize", 0));

                if (value.has("mesh")) {
                    var mesh = value.get("mesh");
                    if (!mesh.isObject() && !mesh.isArray()) throw new RuntimeException("Meshes must be objects.");
                    value.remove("mesh");
                    planet.meshLoader = ()->{
                        //don't crash, just log an error
                        try {
                            return parseMesh(planet, mesh);
                        } catch (Exception e) {
                            Log.err(e);
                            return new ShaderSphereMesh(planet, Shaders.unlit, 2);
                        }
                    };
                }

                if (value.has("cloudMesh")) {
                    var mesh = value.get("cloudMesh");
                    if (!mesh.isObject() && !mesh.isArray()) throw new RuntimeException("Meshes must be objects.");
                    value.remove("cloudMesh");
                    planet.cloudMeshLoader = ()->{
                        //don't crash, just log an error
                        try {
                            return parseMesh(planet, mesh);
                        } catch (Exception e) {
                            Log.err(e);
                            return null;
                        }
                    };
                }

                //always one sector right now...
                planet.sectors.add(new Sector(planet, Ptile.empty));

                currentContent = planet;
                read(()->readFields(planet, value));
                return planet;
            });

    private Prov<Unit> unitType(JsonValue value) {
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

    private String getString(JsonValue value, String key) {
        if (value.has(key)) {
            return value.getString(key);
        } else {
            throw new IllegalArgumentException("You are missing a \"" + key + "\". It must be added before the file can be parsed.");
        }
    }

    private String getType(JsonValue value) {
        return getString(value, "type");
    }

    private <T extends Content> T find(ContentType type, String name) {
        Content c = Vars.content.getByName(type, name);
        if (c == null) c = Vars.content.getByName(type, currentMod.name + "-" + name);
        if (c == null) throw new IllegalArgumentException("No " + type + " found with name '" + name + "'");
        return (T) c;
    }

    private <T extends Content> TypeParser<T> parser(ContentType type, Func<String, T> constructor) {
        return (name, value)->{
            T item;
            if (locate(type, name) != null) {
                item = (T) locate(type, name);
                readBundle(type, name, value);
            } else {
                readBundle(type, name, value);
                item = constructor.get(name);
            }
            currentContent = item;
            read(()->readFields(item, value));
            return item;
        };
    }

    private void readBundle(ContentType type, String name, JsonValue value) {
        UnlockableContent cont = locate(type, name) instanceof UnlockableContent ? locate(type, name) : null;

        String entryName = cont == null ? type + "." + currentMod.name + "-" + name + "." : type + "." + cont.name + ".";
        I18NBundle bundle = Core.bundle;
        while (bundle.getParent() != null) bundle = bundle.getParent();

        if (value.has("name")) {
            if (!Core.bundle.has(entryName + "name")) {
                bundle.getProperties().put(entryName + "name", value.getString("name"));
                if (cont != null) cont.localizedName = value.getString("name");
            }
            value.remove("name");
        }

        if (value.has("description")) {
            if (!Core.bundle.has(entryName + "description")) {
                bundle.getProperties().put(entryName + "description", value.getString("description"));
                if (cont != null) cont.description = value.getString("description");
            }
            value.remove("description");
        }
    }

    /**
     * Call to read a content's extra info later.
     */
    private void read(Runnable run) {
        Content cont = currentContent;
        LoadedMod mod = currentMod;
        reads.add(()->{
            this.currentMod = mod;
            this.currentContent = cont;
            run.run();

            //check nulls after parsing
            if (cont != null) {
                toBeParsed.remove(cont);
                checkNullFields(cont);
            }
        });
    }

    private void init() {
        for (ContentType type : ContentType.all) {
            Seq<Content> arr = Vars.content.getBy(type);
            if (!arr.isEmpty()) {
                Class<?> c = arr.first().getClass();
                //获取 Base Content 类，跳过中间体
                while (!(c.getSuperclass() == Content.class || c.getSuperclass() == UnlockableContent.class || Modifier.isAbstract(c.getSuperclass().getModifiers()))) {
                    c = c.getSuperclass();
                }

                contentTypes.put(c, type);
            }
        }
    }

    private void attempt(Runnable run) {
        try {
            run.run();
        } catch (Throwable t) {
            Log.err(t);
            //don't overwrite double errors
            markError(currentContent, t);
        }
    }

    public void finishParsing() {
        reads.each(this::attempt);
        postreads.each(this::attempt);
        reads.clear();
        postreads.clear();
        toBeParsed.clear();
    }

    /**
     * 解析json文件中的内容。
     * * @param name文件的名称，不带扩展名
     */


    public void markError(Content content, Fi file, Throwable error) {
        Log.err("Error for @ / @:\n@\n", content, file, Strings.getStackTrace(error));

        content.minfo.mod = currentMod;
        content.minfo.sourceFile = file;
        content.minfo.error = makeError(error, file);
        content.minfo.baseError = error;
    }

    public void markError(Content content, Throwable error) {
        if (content.minfo != null && !content.hasErrored()) {
            markError(content, content.minfo.sourceFile, error);
        }
    }

    private String makeError(Throwable t, Fi file) {
        StringBuilder builder = new StringBuilder();
        builder.append("[lightgray]").append("File: ").append(file.name()).append("[]\n\n");

        if (t.getMessage() != null && t instanceof JsonParseException) {
            builder.append("[accent][[JsonParse][] ").append(":\n").append(t.getMessage());
        } else if (t instanceof NullPointerException) {
            builder.append(Strings.neatError(t));
        } else {
            Seq<Throwable> causes = Strings.getCauses(t);
            for (Throwable e : causes) {
                builder.append("[accent][[").append(e.getClass().getSimpleName().replace("Exception", "")).append("][] ").append(e.getMessage() != null ? e.getMessage().replace("mindustry.", "").replace("arc.", "") : "").append("\n");
            }
        }
        return builder.toString();
    }

    private <T extends MappableContent> T locate(ContentType type, String name) {
        T first = Vars.content.getByName(type, name); //尝试替换原版
        return first != null ? first : Vars.content.getByName(type, currentMod.name + "-" + name);
    }

    private <T extends MappableContent> T locateAny(String name) {
        for (ContentType t : ContentType.all) {
            var out = locate(t, name);
            if (out != null) {
                return (T) out;
            }
        }
        return null;
    }

    private GenericMesh[] parseMeshes(Planet planet, JsonValue array) {
        var res = new GenericMesh[array.size];
        for (int i = 0; i < array.size; i++) {
            //yes get is O(n) but it's practically irrelevant here
            res[i] = parseMesh(planet, array.get(i));
        }
        return res;
    }

    private GenericMesh parseMesh(Planet planet, JsonValue data) {
        if (data.isArray()) {
            return new MultiMesh(parseMeshes(planet, data));
        }

        String tname = Strings.capitalize(data.getString("type", "NoiseMesh"));

        return switch (tname) {
            //TODO NoiseMesh is bad
            case "NoiseMesh" ->
                    new NoiseMesh(planet, data.getInt("seed", 0), data.getInt("divisions", 1), data.getFloat("radius", 1f), data.getInt("octaves", 1), data.getFloat("persistence", 0.5f), data.getFloat("scale", 1f), data.getFloat("mag", 0.5f), Color.valueOf(data.getString("color1", data.getString("color", "ffffff"))), Color.valueOf(data.getString("color2", data.getString("color", "ffffff"))), data.getInt("colorOct", 1), data.getFloat("colorPersistence", 0.5f), data.getFloat("colorScale", 1f), data.getFloat("colorThreshold", 0.5f));
            case "SunMesh" -> {
                var cvals = data.get("colors").asStringArray();
                var colors = new Color[cvals.length];
                for (int i = 0; i < cvals.length; i++) {
                    colors[i] = Color.valueOf(cvals[i]);
                }

                yield new SunMesh(planet, data.getInt("divisions", 1), data.getInt("octaves", 1), data.getFloat("persistence", 0.5f), data.getFloat("scl", 1f), data.getFloat("pow", 1f), data.getFloat("mag", 0.5f), data.getFloat("colorScale", 1f), colors);
            }
            case "HexSkyMesh" ->
                    new HexSkyMesh(planet, data.getInt("seed", 0), data.getFloat("speed", 0), data.getFloat("radius", 1f), data.getInt("divisions", 3), Color.valueOf(data.getString("color", "ffffff")), data.getInt("octaves", 1), data.getFloat("persistence", 0.5f), data.getFloat("scale", 1f), data.getFloat("thresh", 0.5f));
            case "MultiMesh" -> new MultiMesh(parseMeshes(planet, data.get("meshes")));
            case "MatMesh" ->
                    new MatMesh(parseMesh(planet, data.get("mesh")), parserJson.readValue(Mat3D.class, data.get("mat")));
            default -> throw new RuntimeException("Unknown mesh type: " + tname);
        };
    }

    private PartProgress parseProgressOp(PartProgress base, String op, JsonValue data) {
        //I have to hard-code this, no easy way of getting parameter names, unfortunately
        return switch (op) {
            case "inv" -> base.inv();
            case "slope" -> base.slope();
            case "clamp" -> base.clamp();
            case "delay" -> base.delay(data.getFloat("amount"));
            case "sustain" ->
                    base.sustain(data.getFloat("offset", 0f), data.getFloat("grow", 0f), data.getFloat("sustain"));
            case "shorten" -> base.shorten(data.getFloat("amount"));
            case "compress" -> base.compress(data.getFloat("start"), data.getFloat("end"));
            case "add" ->
                    data.has("amount") ? base.add(data.getFloat("amount")) : base.add(parserJson.readValue(PartProgress.class, data.get("other")));
            case "blend" ->
                    base.blend(parserJson.readValue(PartProgress.class, data.get("other")), data.getFloat("amount"));
            case "mul" ->
                    data.has("amount") ? base.mul(data.getFloat("amount")) : base.mul(parserJson.readValue(PartProgress.class, data.get("other")));
            case "min" -> base.min(parserJson.readValue(PartProgress.class, data.get("other")));
            case "sin" ->
                    base.sin(data.has("offset") ? data.getFloat("offset") : 0f, data.getFloat("scl"), data.getFloat("mag"));
            case "absin" -> base.absin(data.getFloat("scl"), data.getFloat("mag"));
            case "curve" ->
                    data.has("interp") ? base.curve(parserJson.readValue(Interp.class, data.get("interp"))) : base.curve(data.getFloat("offset"), data.getFloat("duration"));
            default ->
                    throw new RuntimeException("Unknown operation '" + op + "', check PartProgress class for a list of methods.");
        };
    }

    <T> T make(Class<T> type) {
        try {
            Constructor<T> cons = type.getDeclaredConstructor();
            cons.setAccessible(true);
            return cons.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T make(Class<T> type, String name) {
        try {
            Constructor<T> cons = type.getDeclaredConstructor(String.class);
            cons.setAccessible(true);
            return cons.newInstance(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> Prov<T> supply(Class<T> type) {
        try {
            Constructor<T> cons = type.getDeclaredConstructor();
            return ()->{
                try {
                    return cons.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Object field(Class<?> type, JsonValue value) {
        return field(type, value.asString());
    }

    /**
     * Gets a field from a static class by name, throwing a descriptive exception if not found.
     */
    private Object field(Class<?> type, String name) {
        try {
            Object b = type.getField(name).get(null);
            if (b == null) throw new IllegalArgumentException(type.getSimpleName() + ": not found: '" + name + "'");
            return b;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Object fieldOpt(Class<?> type, JsonValue value) {
        try {
            return type.getField(value.asString()).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    void checkNullFields(Object object) {
        if (object == null || object instanceof Number || object instanceof String || toBeParsed.contains(object) || object.getClass().getName().startsWith("arc."))
            return;

        parserJson.getFields(object.getClass()).values().toSeq().each(field->{
            try {
                if (field.field.getType().isPrimitive()) return;

                if (!field.field.isAnnotationPresent(Nullable.class) && field.field.get(object) == null && !implicitNullable.contains(field.field.getType())) {
                    throw new RuntimeException("'" + field.field.getName() + "' in " + ((object.getClass().isAnonymousClass() ? object.getClass().getSuperclass() : object.getClass()).getSimpleName()) + " is missing! Object = " + object + ", field = (" + field.field.getName() + " = " + field.field.get(object) + ")");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void readFields(Object object, JsonValue jsonMap, boolean stripType) {
        if (stripType) jsonMap.remove("type");
        readFields(object, jsonMap);
    }

    void readFields(Object object, JsonValue jsonMap) {
        JsonValue research = jsonMap.remove("research");

        toBeParsed.remove(object);
        var type = object.getClass();
        var fields = parserJson.getFields(type);
        for (JsonValue child = jsonMap.child; child != null; child = child.next) {
            FieldMetadata metadata = fields.get(child.name().replace(" ", "_"));
            if (metadata == null) {
                if (ignoreUnknownFields) {
                    Log.warn("[@]:不可识别字段: @ (@)"+ jsonMap, currentContent.minfo.sourceFile.name(), child.name, type.getSimpleName());
                    continue;
                } else {
                    SerializationException ex = new SerializationException("Field not found: " + child.name + " (" + type.getName() + ")");
                    ex.addTrace(child.trace());
                    throw ex;
                }
            }
            Field field = metadata.field;
            try {
                field.set(object, parserJson.readValue(field.getType(), metadata.elementType, child, metadata.keyType));
            } catch (IllegalAccessException ex) {
                throw new SerializationException("Error accessing field: " + field.getName() + " (" + type.getName() + ")", ex);
            } catch (SerializationException ex) {
                ex.addTrace(field.getName() + " (" + type.getName() + ")");
                throw ex;
            } catch (RuntimeException runtimeEx) {
                SerializationException ex = new SerializationException(runtimeEx);
                ex.addTrace(child.trace());
                ex.addTrace(field.getName() + " (" + type.getName() + ")");
                throw ex;
            }
        }

        if (object instanceof UnlockableContent unlock && research != null) {

            //add research tech node
            String researchName;
            ItemStack[] customRequirements;

            //research can be a single string or an object with parent and requirements
            if (research.isString()) {
                researchName = research.asString();
                customRequirements = null;
            } else {
                researchName = research.getString("parent", null);
                customRequirements = research.has("requirements") ? parserJson.readValue(ItemStack[].class, research.get("requirements")) : null;
            }

            //remove old node
            TechNode lastNode = TechTree.all.find(t->t.content == unlock);
            if (lastNode != null) {
                lastNode.remove();
            }

            TechNode node = new TechNode(null, unlock, customRequirements == null ? ItemStack.empty : customRequirements);
            LoadedMod cur = currentMod;

            postreads.add(()->{
                currentContent = unlock;
                currentMod = cur;

                //add custom objectives
                if (research.has("objectives")) {
                    node.objectives.addAll(parserJson.readValue(Objective[].class, research.get("objectives")));
                }

                //all items have a produce requirement unless already specified
                if (object instanceof Item i && !node.objectives.contains(o->o instanceof Produce p && p.content == i)) {
                    node.objectives.add(new Produce(i));
                }

                //remove old node from parent
                if (node.parent != null) {
                    node.parent.children.remove(node);
                }

                if (customRequirements == null) {
                    node.setupRequirements(unlock.researchRequirements());
                }

                if (research.has("planet")) {
                    node.planet = find(ContentType.planet, research.getString("planet"));
                }

                if (research.getBoolean("root", false)) {
                    node.name = research.getString("name", unlock.name);
                    node.requiresUnlock = research.getBoolean("requiresUnlock", false);
                    TechTree.roots.add(node);
                } else {
                    if (researchName != null) {
                        //find parent node.
                        TechNode parent = TechTree.all.find(t->t.content.name.equals(researchName) || t.content.name.equals(currentMod.name + "-" + researchName) || t.content.name.equals(SaveVersion.mapFallback(researchName)));

                        if (parent == null) {
                            Log.warn("Content '" + researchName + "' isn't in the tech tree, but '" + unlock.name + "' requires it to be researched.");
                        } else {
                            //add this node to the parent
                            if (!parent.children.contains(node)) {
                                parent.children.add(node);
                            }
                            //reparent the node
                            node.parent = parent;
                        }
                    } else {
                        Log.warn(unlock.name + " is not a root node, and does not have a `parent: ` property. Ignoring.");
                    }
                }
            });
        }
    }

    /**
     * Tries to resolve a class from the class type map.
     */
    <T> Class<T> resolve(String base) {
        return resolve(base, null);
    }

    /**
     * 尝试从类类型 map 解析类。
     */
    public <T> Class<T> resolve(String base, Class<T> def) {
        //未指定基类
        if ((base == null || base.isEmpty()) && def != null) return def;

        //如果在全局映射中找到，则返回 Map 类
        String s = !base.isEmpty() && Character.isLowerCase(base.charAt(0)) ? Strings.capitalize(base) : base;
        var out = ClassMap.classes.get(s);
        if (out != null) return (Class<T>) out;

        //尝试将其解析为 Raw 类名
        if (base.indexOf('.') != -1) {
            try {
                return (Class<T>) Class.forName(base);
            } catch (Exception ignored) {
                //尝试使用 Mod 类加载器
                try {
                    return (Class<T>) Class.forName(base, true, mods.mainLoader());
                } catch (Exception ignore) {
                }
            }
        }

        if (def != null) {
            Log.warn("[@] No type '" + base + "' found, defaulting to type '" + def.getSimpleName() + "'", currentContent == null ? currentMod.name : "");
            return def;
        }
        throw new IllegalArgumentException("未找到类型: " + base);
    }

    private interface FieldParser {
        Object parse(Class<?> type, JsonValue value) throws Exception;
    }

    private interface TypeParser<T extends Content> {
        T parse(String name, JsonValue value) throws Exception;
    }

    //intermediate class for parsing
    static class UnitReq {
        public Block block;
        public ItemStack[] requirements = {};
        @Nullable
        public UnitType previous;
        public float time = 60f * 10f;
    }

}
