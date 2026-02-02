package universecore.util;

import arc.struct.*;

import java.util.*;

/**一些空的集合的存储类，一般用于对空集合有需要引用的时候，例如map的各自getDefault和默认初始化用法等
 * <p><strong>不要对这当中的任何集合进行手动操作，这是很愚蠢的，非常愚蠢</strong>*/
@SuppressWarnings({"unchecked", "rawtypes"})
public class Empties{
  private static final ArrayList NIL_ARRAY_LIST = new ArrayList();
  private static final LinkedList NIL_LINKED_LIST = new LinkedList();
  private static final Seq NIL_SEQ = new Seq();
  
  private static final HashMap NIL_HASH_MAP = new HashMap();
  private static final LinkedHashMap NIL_LINKED_HASH_MAP = new LinkedHashMap();
  private static final ObjectMap NIL_OBJECT_MAP = new ObjectMap();
  private static final OrderedMap NIL_ORDERED_MAP = new OrderedMap();
  
  private static final HashSet NIL_HASH_SET = new HashSet();
  private static final LinkedHashSet NIL_LINKED_HASH_SET = new LinkedHashSet();
  private static final ObjectSet NIL_OBJECT_SET = new ObjectSet();
  private static final OrderedSet NIL_ORDERED_SET = new OrderedSet();
  
  public static <T> List<T> nilList(){
    return Collections.EMPTY_LIST;
  }
  
  public static <T> ArrayList<T> nilListA(){
    return NIL_ARRAY_LIST;
  }
  
  public static <T> LinkedList<T> nilListL(){
    return NIL_LINKED_LIST;
  }
  
  public static <T> Seq<T> nilSeq(){
    return NIL_SEQ;
  }
  
  public static <K, V> Map<K, V> nilMap(){
    return Collections.EMPTY_MAP;
  }
  
  public static <K, V> HashMap<K, V> nilMapH(){
    return NIL_HASH_MAP;
  }
  
  public static <K, V> HashMap<K, V> nilMapLH(){
    return NIL_LINKED_HASH_MAP;
  }
  
  public static <K, V> ObjectMap<K, V> nilMapO(){
    return NIL_OBJECT_MAP;
  }
  
  public static <K, V> OrderedMap<K, V> nilMapOD(){
    return NIL_ORDERED_MAP;
  }
  
  public static <T> Set<T> nilSet(){
    return Collections.EMPTY_SET;
  }
  
  public static <T> HashSet<T> nilSetH(){
    return NIL_HASH_SET;
  }
  
  public static <T> LinkedHashSet<T> nilSetLH(){
    return NIL_LINKED_HASH_SET;
  }
  
  public static <T> ObjectSet<T> nilSetO(){
    return NIL_OBJECT_SET;
  }
  
  public static <T> OrderedSet<T> nilSetOD(){
    return NIL_ORDERED_SET;
  }
}
