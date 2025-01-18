package ice.parse.parses;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.SoundLoader;
import arc.audio.Sound;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.math.Interp;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.util.serialization.JsonValue;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.content.Loadouts;
import mindustry.ctype.ContentType;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootPattern;
import mindustry.game.Objectives;
import mindustry.game.Schematic;
import mindustry.game.Schematics;
import mindustry.gen.Sounds;
import mindustry.graphics.CacheLayer;
import mindustry.maps.generators.PlanetGenerator;
import mindustry.maps.planet.AsteroidGenerator;
import mindustry.type.AmmoType;
import mindustry.type.StatusEffect;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.BuildVisibility;

import static ice.parse.IceContentParser.*;

public class ClassParsers {
    public interface FieldParser {
        Object parse(Class<?> type, JsonValue value) throws Exception;
    }

    public static final ObjectMap<Class<?>, FieldParser> classPar = new ObjectMap<>() {{
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
        put(Units.Sortf.class, (type, data)->field(UnitSorts.class, data));
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
                throw new IllegalArgumentException("Unknown status 状态效果: '" + data.asString() + "'");
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
                return make(resolve(data.asString(),null));
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
        put(DrawPart.PartProgress.class, (type, data)->{
            //simple case: it's a string or number constant
            if (data.isString()) return field(DrawPart.PartProgress.class, data.asString());
            if (data.isNumber()) return DrawPart.PartProgress.constant(data.asFloat());

            if (!data.has("type")) {
                throw new RuntimeException("PartProgress object need a 'type' string field. Check the PartProgress class for a list of constants.");
            }

            DrawPart.PartProgress base = (DrawPart.PartProgress) field(DrawPart.PartProgress.class, data.getString("type"));

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

                        if (op != null) base = parseProgressOp(base, op.asString(), val);
                        i++;
                    }
                }

                return base;
            }

            //这是要调用的方法的名称
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

            if (sounds.containsKey(path)) return ((SoundLoader.SoundParameter) sounds.get(path).params).sound;
            var sound = new Sound();
            AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundLoader.SoundParameter(sound));
            desc.errored = Throwable::printStackTrace;
            sounds.put(path, desc);
            return sound;
        });
        put(Objectives.Objective.class, (type, data)->{
            if (data.isString()) {
                var cont = locateAny(data.asString());
                if (cont == null) throw new IllegalArgumentException("Unknown objective content: " + data.asString());
                return new Objectives.Research((UnlockableContent) cont);
            }
            var oc = resolve(data.getString("type", ""), Objectives.SectorComplete.class);
            data.remove("type");
            Objectives.Objective obj = make(oc);
            readFields(obj, data);
            return obj;
        });
        put(Ability.class, (type, data)->{
            Class<? extends Ability> oc = resolve(data.getString("type", ""),null);
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
}
