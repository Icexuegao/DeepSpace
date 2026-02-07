package universecore.util;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.core.ContentLoader;
import mindustry.ctype.Content;
import mindustry.ctype.ContentType;
import mindustry.ctype.MappableContent;
import universecore.util.handler.EnumHandler;
import universecore.util.handler.FieldHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**contentType处理对象，用于创建新的内容类型，以及处理类型的显示排序
 *
 * @author EBwilson
 * @since 1.0*/
public class UncContentType{
  private final static FieldHandler<ContentLoader> fieldHandler = new FieldHandler<>(ContentLoader.class);
  private final static EnumHandler<ContentType> handler = new EnumHandler<>(ContentType.class);
  
  public static final ObjectMap<ContentType, UncContentType> allUncContentType = new ObjectMap<>();

  public static ContentType[] displayContentList = ContentType.all;

  static {
    for(ContentType value: ContentType.values()){
      allUncContentType.put(value, new UncContentType(value));
    }
  }
  
  public final ContentType value;
  public final int ordinal;

  public final boolean display;

  /**内部方法，用于创建与已有{@link ContentType}关联的标识*/
  private UncContentType(ContentType type){
    this.value = type;
    this.ordinal = type.ordinal();
    this.display = true;
  }

  /**创建一个新的实例，并绑定到一个新生成的对应的{@link ContentType}枚举实例
   *
   * @param name 该类型的名称
   * @param contentClass 这个contentType关联的内容类*/
  public UncContentType(String name, Class<? extends Content> contentClass){
    this(name, ContentType.values().length, contentClass);
  }

  /**创建一个新的实例，并绑定到一个新生成的对应的{@link ContentType}枚举实例
   *
   * @param name 该类型的名称
   * @param ordinal 这个类型在数据库中显示的位置序数
   * @param contentClass 这个contentType关联的内容类*/
  public UncContentType(String name, int ordinal, Class<? extends Content> contentClass){
    this(name, ordinal, contentClass, true);
  }

  /**创建一个新的实例，并绑定到一个新生成的对应的{@link ContentType}枚举实例
   *
   * @param name 该类型的名称
   * @param ordinal 这个类型在数据库中显示的位置序数
   * @param contentClass 这个contentType关联的内容类
   * @param display 是否在数据库内显示*/
  public UncContentType(String name, int ordinal, Class<? extends Content> contentClass, boolean display){
    value = handler.addEnumItemTail(name, contentClass);
    this.ordinal = ordinal;
    this.display = display;
  
    allUncContentType.put(value, this);
    
    FieldHandler.setValueDefault(ContentType.class, "all", ContentType.values());

    reloadDisplay();
  
    try{
      ObjectMap<String, MappableContent>[] contentNameMap = fieldHandler.getValue( Vars.content, "contentNameMap");
      Seq<Content>[] contentMap = fieldHandler.getValue(Vars.content, "contentMap");
    
      ArrayList<ObjectMap<String, MappableContent>> contentNameMapList = new ArrayList<>(Arrays.asList(contentNameMap));
      ArrayList<Seq<Content>> contentMapList = new ArrayList<>(Arrays.asList(contentMap));
    
      contentNameMapList.add(value.ordinal(), new ObjectMap<>());
      contentMapList.add(value.ordinal(), new Seq<>());
    
      fieldHandler.setValue(Vars.content, "contentNameMap", contentNameMapList.toArray(new ObjectMap[0]));
      fieldHandler.setValue(Vars.content, "contentMap", contentMapList.toArray(new Seq[0]));
    }
    catch(Throwable e){
      Log.err(e);
    }
  }

  protected static void reloadDisplay(){
    Seq<ContentType> list = new Seq<>(ContentType.values().length);

    for(ContentType type: ContentType.values()){
      UncContentType t = allUncContentType.get(type);
      if(!t.display) continue;
      list.add(t.value);
    }

    list.sort(Comparator.comparingInt(Enum::ordinal));

    displayContentList = list.toArray(ContentType.class);
  }
}
