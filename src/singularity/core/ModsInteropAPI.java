package singularity.core;

import arc.files.Fi;
import arc.struct.ObjectMap;
import arc.struct.OrderedSet;
import arc.util.Log;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.mod.Mods;
import singularity.Sgl;

/**mod的交互式API主类型，该类管理了所有被注册的交互API模型，用于解析其他mod当中声明的交互式API调用描述声明
 * <br><strong>关于交互式API的调用声明：</strong> 所有的交互式API描述被记录在一个命名为‘singularity_api.json’或singularity_api.hjson'中，
 * 并被放置在mod文件的根目录，若其他mod需要调用奇点的交互式API，就只需要在mod压缩文件的根目录下添加此文件即可
 *
 * <p>具体来说，要调用某个确定的API，需要做的事就是在这个文件中添加一个键值对，键名即API名称，值则根据API模型的描述确定，如下：
 * <pre>{@code
 * {
 *   "$apiName": {...},//调用交互API，之后的{...}为传递给API的参数
 *   "$apiName": {...},
 *   "$apiName": "disabled",//禁用此API，这会停用该API可能本来存在的默认操作
 *   ...
 * }
 * }</pre>
 *
 * 如果你希望禁用所有的交互api，那么在此记录文件中添加键值对{@code "disabled_api":true} 即可，当你设置禁用为true后，其他所有api描述都不会生效，无论你是否提供了声明
 *
 * <p>另外，在API参数中会出现对content等的引用，关于content选择器，一般采取如下规范：
 * <ul>
 *   <li><strong>如果名称不定义mod名称前缀，则会优先选择本mod（调用API的mod）的content，其次是原版内容</strong></li>
 *   <li><strong>如果名称中包含了mod名称前缀，则会选择前缀限定的mod中的content，但是通常操作性的选择器不提倡开发者跨mod操作，这可能会有警告</strong></li>
 * </ul>
 *
 * 有一部分条目的配置会需要传入的是一个文本字符串作为参数，对于这类参数，您可以使用 {@code "@bundleName"} 的形式去选择本地化文本，否则会传入为原始文本*/
public class ModsInteropAPI {
  private final ObjectMap<Mods.LoadedMod, Jval> declares = new ObjectMap<>();
  private final OrderedSet<ConfigModel> models = new OrderedSet<>();

  public void addModel(ConfigModel model, boolean init){
    if (Sgl.config.enableModsInterops && models.add(model) && init){
      for (ObjectMap.Entry<Mods.LoadedMod, Jval> entry : declares) {
        try {
          if (entry.value.getBool("disable_api", false)) {
            model.disable(entry.key);
          } else model.parse(entry.key, entry.value);
        } catch (Throwable e){
          Log.err("[Singularity API] some error happened in interop declaring file, mod: " + entry.key.name + ";" + entry.key.meta.version + ", details: ");
          Log.err(e);
        }
      }
    }
  }

  public void init(){
    if (Sgl.config.loadInfo) {
      if (Sgl.config.enableModsInterops) {
        Log.info("[Singularity API] loading mod interop api");
      }
      else Log.info("[Singularity API] interop API was disabled");
    }

    if (!Sgl.config.enableModsInterops) return;

    for (Mods.LoadedMod mod : Vars.mods.list()) {
      if (mod.name.equals(Sgl.modName)) continue;

      Fi api = mod.root.child("singularity_api.json");
      api = api.exists()? api: mod.root.child("singularity_api.hjson");

      if (!api.exists()){
        if (Sgl.config.loadInfo && Sgl.config.debugMode) Log.info("[Debug] no interop declared, skip: " + mod.name + ":" + mod.meta.version);

        continue;
      }

      if (Sgl.config.loadInfo) Log.info("[Singularity API] interoping mod: " + mod.name + ":" + mod.meta.version);

      try {
        declares.put(mod, Jval.read(api.reader()));
      }catch (Throwable e){
        Log.err("[Singularity API] reading interop declaring file error, mod: " + mod.name + ";" + mod.meta.version + ", details: ");
        Log.err(e);
      }
    }
  }

  public void updateModels(){
    if (!Sgl.config.enableModsInterops) return;

    for (ObjectMap.Entry<Mods.LoadedMod, Jval> entry : declares) {
      boolean dis = entry.value.getBool("disable_api", false);
      for (ConfigModel model : models) {
        try {
          Jval cfg = entry.value.get(model.modelName);

          if (dis || cfg.isString() && cfg.asString().equals("disabled")){
            model.disable(entry.key);
          }
          else model.parse(entry.key, cfg);
        }catch (Throwable e){
          Log.err("[Singularity API] some error happened in interop declaring file, mod: " + entry.key.name + ";" + entry.key.meta.version + ", details: ");
          Log.err(e);
        }
      }
    }
  }

  public static <T extends Content> T selectContent(ContentType type, String name, Mods.LoadedMod mod) {
    return selectContent(type, name, mod, false);
  }

  @SuppressWarnings("unchecked")
  public static <T extends Content> T selectContent(ContentType type, String name, Mods.LoadedMod mod, boolean checkOwner) {
    Content content = Vars.content.getByName(type, mod.name + "-" + name);
    if (content == null) content = Vars.content.getByName(type, name);

    if (content == null)
      throw new RuntimeException("no such " + type.name() + " named '" + name + "'");

    if (checkOwner && content.minfo.mod != null && content.minfo.mod != mod){
      Log.warn("[Singularity API][Warn] mod " + mod.name + ": " + mod.meta.version + " operate other mod content");
    }

    return (T) content;
  }

  public static abstract class ConfigModel{
    public final String modelName;

    protected ConfigModel(String modelName) {
      this.modelName = modelName;
    }

    public abstract void parse(Mods.LoadedMod mod, Jval declaring);
    public abstract void disable(Mods.LoadedMod mod);
  }
}
