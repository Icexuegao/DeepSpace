package singularity.world;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.struct.Queue;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import universecore.components.blockcomp.BuildCompBase;
import universecore.util.path.BFSPathFinder;
import universecore.util.path.GenericPath;
import universecore.util.path.IPath;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class FinderContainerBase<T extends BuildCompBase> implements BFSPathFinder<T>{
  private final ObjectMap<T, PathPointer<T>> pointers = new ObjectMap<>();
  private final Queue<T> queue = new Queue<>();

  public ObjectSet<T> excluded = new ObjectSet<>();

  public void flow(T seed){
    eachVertices(seed, this::add);
  }

  public abstract void add(T t);

  @Override
  public boolean exclude(T t){
    return excluded.contains(t);
  }

  @Override
  public void reset(){
    pointers.clear();
    queue.clear();
  }

  @Override
  public boolean relateToPointer(T t, PathPointer<T> pathPointer){
    if(pointers.containsKey(t)) return false;
    pointers.put(t, pathPointer);
    return true;
  }

  @Override
  public TracePointer<T> getPointer(T t){
    return TracePointer.create(t);
  }

  @Override
  public T queueNext(){
    return queue.isEmpty()? null: queue.removeFirst();
  }

  @Override
  public void queueAdd(T t){
    queue.addFirst(t);
  }

  @SuppressWarnings("unchecked")
  @Override
  public IPath<T> createPath(){
    return Pools.obtain(PathImpl.class, PathImpl::new);
  }

  public static class TracePointer<S> extends PathPointer<S> implements Pool.Poolable{
    @SuppressWarnings("unchecked")
    public static <S> TracePointer<S> create(S self){
      TracePointer<S> res = Pools.obtain(TracePointer.class, TracePointer::new);
      res.self = self;
      return res;
    }

    public TracePointer(){
      super(null);
    }

    @Override
    public void reset(){
      self = null;
      previous = null;
    }
  }

  public static class PathImpl<S extends BuildCompBase> extends GenericPath<S> implements Pool.Poolable{
    private final LinkedList<S> path = new LinkedList<>();

    public void addFirst(S next) {
      path.addFirst(next);
    }

    public void addLast(S next) {
      path.addLast(next);
    }

    public S origin() {
      return path.getFirst();
    }

    public S destination() {
      return path.getLast();
    }

    public Iterator<S> iterator() {
      return path.iterator();
    }

    @Override
    public void reset(){
      path.clear();
    }
  }
}
