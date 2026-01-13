package universecore.util.colletion;

import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;

/**基于{@link OrderedMap}包装的java集合框架有序Map实现，用于在需要java规范并且需要OrderedMap不创建Node的特点的地方使用
 * @since 1.8.1
 * @author EBwilson */
public class CollectionOrderedMap<K, V> extends CollectionObjectMap<K, V>{
  public Seq<K> orderedKeys;

  public CollectionOrderedMap() {
    setMap(16, 0.75f);
  }

  public CollectionOrderedMap(int capacity) {
    setMap(capacity, 0.75f);
  }

  public CollectionOrderedMap(int capacity, float loadFactor) {
    setMap(capacity, loadFactor);
  }

  public CollectionOrderedMap(ObjectMap<? extends K, ? extends V> map) {
    setMap(16, 0.75f);
    this.map.putAll(map);
  }

  @Override
  protected void setMap(int capacity, float loadFactor) {
    map = new OrderedMap<>(capacity, loadFactor);
    orderedKeys = ((OrderedMap<K, V>) map).orderedKeys();
  }
}
