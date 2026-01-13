package universecore.util.colletion;

import arc.struct.ObjectMap;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**基于{@link ObjectMap}包装的java集合框架Map实现，用于在需要java规范并且需要ObjectMap不创建Node的特点的地方使用
 * @since 1.8.1
 * @author EBwilson */
@SuppressWarnings({"unchecked", "ReturnOfInnerClass"})
public class CollectionObjectMap<K, V> implements Map<K, V> {
  protected ObjectMap<K, V> map;

  private final Set<K> keys = new AbstractSet<K>() {
    public int size() {
      return map.size;
    }

    public void clear() {
      map.clear();
    }

    public Iterator<K> iterator() {
      return map.keys();
    }

    public boolean contains(Object o) {
      return containsKey(o);
    }

    public boolean remove(Object key) {
      return CollectionObjectMap.this.remove(key) != null;
    }

    public Object[] toArray() {
      return map.keys().toSeq().toArray();
    }

    public <T> T[] toArray(T[] a) {
      return map.keys().toSeq().toArray(a.getClass().getComponentType());
    }

    public void forEach(Consumer<? super K> action) {
      for (K k : this) {
        action.accept(k);
      }
    }
  };

  private final Collection<V> values = new AbstractCollection<V>() {
    @Override
    public Iterator<V> iterator() {
      return map.values();
    }

    @Override
    public int size() {
      return map.size;
    }

    public void clear() {
      CollectionObjectMap.this.clear();
    }

    public boolean contains(Object o) {
      return containsValue(o);
    }

    public Object[] toArray() {
      return map.values().toSeq().toArray();
    }

    public <T> T[] toArray(T[] a) {
      return map.values().toSeq().toArray(a.getClass().getComponentType());
    }

    public void forEach(Consumer<? super V> action) {
      map.values().forEach(action);
    }
  };

  private final Set<Entry<K, V>> entrySet = new AbstractSet<>() {
    private final Itr itr = new Itr();
    private final Ent ent = new Ent();

    public int size() {
      return map.size;
    }

    public void clear() {
      map.clear();
    }

    public Iterator<Entry<K, V>> iterator() {
      itr.entries = map.entries();
      return itr;
    }

    public boolean contains(Object o) {
      if (!(o instanceof Map.Entry<?, ?> e))
        return false;
      Object key = e.getKey();
      return CollectionObjectMap.this.containsKey(key);
    }

    public boolean remove(Object o) {
      if (o instanceof Map.Entry<?, ?> e) {
        Object key = e.getKey();
        return CollectionObjectMap.this.remove(key) != null;
      }
      return false;
    }

    @Override
    public void forEach(Consumer<? super Entry<K, V>> action) {
      map.entries().forEach(e -> {
        ent.entry = e;
        action.accept(ent);
      });
    }

    class Itr implements Iterator<Entry<K, V>> {
      ObjectMap.Entries<K, V> entries;

      @Override
      public boolean hasNext() {
        return entries.hasNext();
      }

      @Override
      public Entry<K, V> next() {
        ent.entry = entries.next();
        return ent;
      }
    }

    class Ent implements Entry<K, V> {
      ObjectMap.Entry<K, V> entry;

      @Override
      public K getKey() {
        return entry.key;
      }

      @Override
      public V getValue() {
        return entry.value;
      }

      @Override
      public V setValue(V value) {
        return put(entry.key, value);
      }
    }
  };

  public CollectionObjectMap() {
    setMap(16, 0.75f);
  }

  public CollectionObjectMap(int capacity) {
    setMap(capacity, 0.75f);
  }

  public CollectionObjectMap(int capacity, float loadFactor) {
    setMap(capacity, loadFactor);
  }

  public CollectionObjectMap(ObjectMap<? extends K, ? extends V> map) {
    setMap(16, 0.75f);
    this.map.putAll(map);
  }

  protected void setMap(int capacity, float loadFactor){
    map = new ObjectMap<>(capacity, loadFactor);
  }

  @Override
  public int size() {
    return map.size;
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey((K) key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value, false);
  }

  @Override
  public V get(Object key) {
    return map.get((K) key);
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return map.get((K) key, defaultValue);
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    V v = get(key);
    if (v == null) {
      V newValue;
      if ((newValue = mappingFunction.apply(key)) != null) {
        put(key, newValue);
        return newValue;
      }
    }

    return v;
  }

  @Override
  public V put(K key, V value) {
    return map.put(key, value);
  }

  @Override
  public V remove(Object key) {
    return map.remove((K) key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    m.forEach(this::put);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<K> keySet() {
    return keys;
  }

  @Override
  public Collection<V> values() {
    return values;
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return entrySet;
  }

}
