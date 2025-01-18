package ice.parse;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.files.Fi;
import arc.func.Func;
import arc.func.Prov;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
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
import ice.parse.parses.ContentTypeParsers;
import ice.parse.parses.IceClassMap;
import ice.parse.parses.IceJsonIO;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.TechTree;
import mindustry.content.TechTree.TechNode;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.part.DrawPart.PartProgress;
import mindustry.game.Objectives.Objective;
import mindustry.game.Objectives.Produce;
import mindustry.io.SaveVersion;
import mindustry.mod.Mods.LoadedMod;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.PayloadStack;
import mindustry.world.meta.Env;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static ice.parse.parses.ClassParsers.classPar;
import static mindustry.Vars.content;
import static mindustry.Vars.mods;

@SuppressWarnings("unchecked")
public class IceContentParser {
    public static Content parse(String name, String json, Fi file, ContentType type) throws Exception {
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
    public static boolean ignoreUnknownFields = true;
    public static ObjectMap<Class<?>, ContentType> contentTypes = new ObjectMap<>();
    public static ObjectSet<Class<?>> implicitNullable = ObjectSet.with(TextureRegion.class, TextureRegion[].class, TextureRegion[][].class, TextureRegion[][][].class);
    public static ObjectMap<String, AssetDescriptor<?>> sounds = new ObjectMap<>();
    /**
     * 存储需要完全解析的内容，例如读取内容字段。
     * 这样做是为了首先适应内容名称的绑定。
     */
    public static final Seq<Runnable> reads = new Seq<>();
    public static final Seq<Runnable> postreads = new Seq<>();
    public static final ObjectSet<Object> toBeParsed = new ObjectSet<>();

    public static LoadedMod currentMod = Ice.ice;
    public static Content currentContent;

    public static final Json parserJson = new Json() {

        @Override
        public <T> T readValue(Class<T> type, Class elementType, JsonValue jsonData, Class keyType) {
            {
                IceJsonIO.INSTANCE.apply(this);
            }
            T t = internalRead(type, elementType, jsonData, keyType);
            /**t不等于null 不是包装器       type==null或者不是基元类型*/
            if (t != null && !Reflect.isWrapper(t.getClass()) && (type == null || !type.isPrimitive())) {
                /**检查对象是否有为null的属性*/
                checkNullFields(t);
            }
            return t;
        }

        public <T> T internalRead(Class<T> type, Class elementType, JsonValue jsonData, Class keyType) {
            if (type != null) {
                /**从classmap解析对象*/
                if (classPar.containsKey(type)) {
                    try {
                        return (T) classPar.get(type).parse(type, jsonData);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                //尝试解析 env 位
                if ((type == int.class || type == Integer.class) && jsonData.isArray()) {
                    int value = 0;
                    for (var str : jsonData) {
                        if (!str.isString())
                            throw new SerializationException("整数位域值必须全部为字符串。发现： " + str);
                        String field = str.asString();
                        value |= Reflect.<Integer>get(Env.class, field);
                    }

                    return (T) (Integer) value;
                }


                //try to parse "payloaditem/amount" syntax
                if (type == PayloadStack.class && jsonData.isString() && jsonData.asString().contains("/")) {
                    String[] split = jsonData.asString().split("/");
                    int number = Strings.parseInt(split[1], 1);
                    UnlockableContent cont = content.unit(split[0]) == null ? content.block(split[0]) : content.unit(split[0]);
                    return (T) new PayloadStack(cont == null ? Blocks.router : cont, number);
                }

                //解析基本类型
                if (Content.class.isAssignableFrom(type)) {
                    ContentType ctype = contentTypes.getThrow(type, () -> new IllegalArgumentException("No content type for class: " + type.getSimpleName()));
                    String prefix = currentMod.name + "-";
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

    public static final ObjectMap<ContentType, ContentTypeParsers.TypeParser<?>> parsers = ContentTypeParsers.contentParser;

    public static String getString(JsonValue value, String key) {
        if (value.has(key)) {
            return value.getString(key);
        } else {
            throw new IllegalArgumentException("You are missing a \"" + key + "\". It must be added before the file can be parsed.");
        }
    }

    public static String getType(JsonValue value) {
        return getString(value, "type");
    }

    public static <T extends Content> T find(ContentType type, String name) {
        Content c = Vars.content.getByName(type, name);
        if (c == null) c = Vars.content.getByName(type, currentMod.name + "-" + name);
        if (c == null) throw new IllegalArgumentException("No " + type + " found with name '" + name + "'");
        return (T) c;
    }

    public static <T extends Content> ContentTypeParsers.TypeParser<T> parser(ContentType type, Func<String, T> constructor) {
        return (name, value) -> {
            T item;
            if (locate(type, name) != null) {
                item = (T) locate(type, name);
                readBundle(type, name, value);
            } else {
                readBundle(type, name, value);
                item = constructor.get(name);
            }
            currentContent = item;
            read(() -> readFields(item, value));
            return item;
        };
    }

    public static void readBundle(ContentType type, String name, JsonValue value) {
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
    public static void read(Runnable run) {
        Content cont = currentContent;
        LoadedMod mod = currentMod;
        reads.add(() -> {
            currentMod = mod;
            currentContent = cont;
            run.run();

            //解析后检查 Null
            if (cont != null) {
                toBeParsed.remove(cont);
                checkNullFields(cont);
            }
        });
    }

    public static void init() {
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

    public static void attempt(Runnable run) {
        try {
            run.run();
        } catch (Throwable t) {
            Log.err(t);
            //don't overwrite double errors
            markError(currentContent, t);
        }
    }

    public static void finishParsing() {
        reads.each(IceContentParser::attempt);
        postreads.each(IceContentParser::attempt);
        reads.clear();
        postreads.clear();
        toBeParsed.clear();
    }


    public static void markError(Content content, Fi file, Throwable error) {
        Log.err("[red]Error for [@] / [@][]:\n@\n", content, file, Strings.getStackTrace(error));
        content.minfo.mod = currentMod;
        content.minfo.sourceFile = file;
        content.minfo.error = makeError(error, file);
        content.minfo.baseError = error;
    }

    public static void markError(Content content, Throwable error) {
        if (content.minfo != null && !content.hasErrored()) {
            markError(content, content.minfo.sourceFile, error);
        }
    }

    public static String makeError(Throwable t, Fi file) {
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

    public static <T extends MappableContent> T locate(ContentType type, String name) {
        T first = Vars.content.getByName(type, name); //尝试替换原版
        return first != null ? first : Vars.content.getByName(type, currentMod.name + "-" + name);
    }

    public static <T extends MappableContent> T locateAny(String name) {
        for (ContentType t : ContentType.all) {
            var out = locate(t, name);
            if (out != null) {
                return (T) out;
            }
        }
        return null;
    }

    public static PartProgress parseProgressOp(PartProgress base, String op, JsonValue data) {
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

    public static <T> T make(Class<T> type) {
        try {
            Constructor<T> cons = type.getDeclaredConstructor();
            cons.setAccessible(true);
            return cons.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T make(Class<T> type, String name) {
        try {
            Constructor<T> cons = type.getDeclaredConstructor(String.class);
            cons.setAccessible(true);
            return cons.newInstance(name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> Prov<T> supply(Class<T> type) {
        try {
            Constructor<T> cons = type.getDeclaredConstructor();
            return () -> {
                try {
                    return cons.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e + "构造函数不可发访问！！！");
                }
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object field(Class<?> type, JsonValue value) {
        return field(type, value.asString());
    }

    /** 按名称从静态类中获取字段，如果未找到，则引发描述性异常。 */
    public static Object field(Class<?> type, String name) {
        try {
            Object b = type.getField(name).get(null);
            if (b == null) throw new IllegalArgumentException(type.getSimpleName() + ": 没有找到: " + name + " 属性");
            return b;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Object fieldOpt(Class<?> type, JsonValue value) {
        try {
            return type.getField(value.asString()).get(null);
        } catch (Exception e) {
            return null;
        }
    }

    public static void checkNullFields(Object object) {
        if (object == null || object instanceof Number || object instanceof String || toBeParsed.contains(object) || object.getClass().getName().startsWith("arc."))
            return;

        parserJson.getFields(object.getClass()).values().toSeq().each(field -> {
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

    public static void readFields(Object object, JsonValue jsonMap, boolean stripType) {
        if (stripType) jsonMap.remove("type");
        readFields(object, jsonMap);
    }

    public static void readFields(Object object, JsonValue jsonMap) {
        JsonValue research = jsonMap.remove("research");

        toBeParsed.remove(object);
        var type = object.getClass();
        var fields = parserJson.getFields(type);
        for (JsonValue child = jsonMap.child; child != null; child = child.next) {
            FieldMetadata metadata = fields.get(child.name().replace(" ", "_"));
            if (metadata == null) {
                if (ignoreUnknownFields) {
                    Log.warn("[@]:不可识别字段: @ (@)" + jsonMap, currentContent.minfo.sourceFile.name(), child.name, type.getSimpleName());
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
            TechNode lastNode = TechTree.all.find(t -> t.content == unlock);
            if (lastNode != null) {
                lastNode.remove();
            }

            TechNode node = new TechNode(null, unlock, customRequirements == null ? ItemStack.empty : customRequirements);
            LoadedMod cur = currentMod;

            postreads.add(() -> {
                currentContent = unlock;
                currentMod = cur;

                //add custom objectives
                if (research.has("objectives")) {
                    node.objectives.addAll(parserJson.readValue(Objective[].class, research.get("objectives")));
                }

                //all items have a produce requirement unless already specified
                if (object instanceof Item i && !node.objectives.contains(o -> o instanceof Produce p && p.content == i)) {
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
                        TechNode parent = TechTree.all.find(t -> t.content.name.equals(researchName) || t.content.name.equals(currentMod.name + "-" + researchName) || t.content.name.equals(SaveVersion.mapFallback(researchName)));

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


    /** 尝试从类类型 map 解析类。 */
    public static <T> Class<T> resolve(String base, Class<T> def) {
        //未指定基类
        if ((base == null || base.isEmpty()) && def != null) return def;
        if (base == null) base = "";
        //如果在全局映射中找到，则返回 Map 类
        String s = Character.isLowerCase(base.charAt(0)) ? Strings.capitalize(base) : base;
        var out = IceClassMap.classmap.get(s);
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


}
