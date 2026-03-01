package universecore.util.path;

/**基于广度优先搜索的初步寻路实现，实现此接口需要提供必要的容器入口。
 * <p>此搜索<strong>无权值</strong>，产生的路径应为平权最短路径，通常情况下在没有权值的图中查找到的路径一定是最优解或最优之一。
 *
 * @author EBwilson
 * @since 1.3*/
public interface BFSPathFinder<Vert> extends PathFinder<Vert>{
  /**重置搜索状态或者（和）临时缓存,包括复位已经遍历过的顶点和边等*/
  void reset();

  /**将一个顶点关联到一个保存了参数信息的新的回溯指针，这个方法应当完成下述行为：
   * <p>1.若此顶点未与回溯指针关联，则创建一个新的回溯指针实例，并用传入的参数为此指针设置顶点和指针的前一个目标
   * <p>2.若顶点已关联到一个回溯指针那么什么都不做
   * <p>3.若本次操作中进行了顶点关联，则应当返回true,否则返回false
   *
   * @param vert 进行遍历检查的顶点
   * @param previous 该指针的前一个回溯指针，可以为空
   * @return 若顶点尚未与指针关联，则返回true,否则返回false*/
  boolean relateToPointer(Vert vert, PathPointer<Vert> previous);

  /**检查当前传入的节点是否是被排除的节点，若是则跳过该点
   * <p>此方法的实施需要进行重写，根据实际情况返回，默认永远不排除顶点
   *
   * @param vert 当前检查的顶点
   * @return 此节点是否应该排除*/
  default boolean exclude(Vert vert){
    return false;
  }

  /**获取顶点关联的回溯指针，若顶点尚未关联，则应当返回null
   *
   * @param vert 获取指针的顶点
   * @return 一个与顶点关联的指针，若未关联则为null*/
  PathPointer<Vert> getPointer(Vert vert);

  /**从搜索队列中读取下一个顶点，并将其从队列中弹出，若队列为空则返回null。
   * <p>此方法通与{@link BFSPathFinder#queueAdd(Object)}应当满足堆栈实现，<strong>更晚加入的顶点应当更优先被取出</strong>。
   *
   * @return 一个处于堆栈顶端的顶点，若没有顶点，则返回null*/
  Vert queueNext();

  /**向搜索队列添加一个顶点。
   * <p>此方法通与{@link BFSPathFinder#queueNext()}应当满足堆栈实现，<strong>更晚加入的顶点应当更优先被取出</strong>。
   *
   * @param next 添加到队列中的下一个顶点*/
  void queueAdd(Vert next);

  /**创建一个路径对象，实现时应当能够返回一条空白的路径，你可能还需要对{@link IPath}接口进行实现并返回它的实例，或者直接使用默认实现{@link GenericPath}
   *
   * @return 一条空路径
   * @see IPath
   * @see GenericPath*/
  IPath<Vert> createPath();

  /**一个标准的BFS寻路实现，通常搜寻到的路径是在无权值的图中最短的或最短之一
   *
   * @see PathFinder#findPath(Object, PathFindFunc.PathAcceptor) */
  @Override
  default void findPath(Vert origin, PathFindFunc.PathAcceptor<Vert> pathConsumer){
    reset();
    queueAdd(origin);
    relateToPointer(origin, null);

    Vert next;
    while((next = queueNext()) != null){
      PathPointer<Vert> pointer = getPointer(next);
      for(Vert vert: getLinkVertices(next)){
        if(!exclude(vert) && relateToPointer(vert, pointer)){
          queueAdd(vert);
        }
      }

      if(isDestination(origin, next)){
        PathPointer<Vert> tracePointer = pointer;
        IPath<Vert> path = createPath();
        path.addFirst(pointer.self);

        while((tracePointer = tracePointer.previous) != null){
          path.addFirst(tracePointer.self);
        }

        pathConsumer.accept(next, path);
      }
    }
  }

  /**基于BFS的图遍历实现，遍历顺序为从给出的起点向外扩散直到每一个顶点都被遍历过
   *
   * @see PathFinder#eachVertices(Object, PathFindFunc.VerticesAcceptor) */
  @Override
  default void eachVertices(Vert origin, PathFindFunc.VerticesAcceptor<Vert> vertConsumer){
    reset();
    queueAdd(origin);
    relateToPointer(origin, null);

    Vert v;
    while((v = queueNext()) != null){
      for(Vert vert: getLinkVertices(v)){
        if(!exclude(vert) && relateToPointer(vert, null)){
          queueAdd(vert);
        }
      }
      vertConsumer.accept(v);
    }
  }

  /**路径的回溯指针，每一个指针都保存了一个顶点和它的前一个指针的信息，以类似链表的形式进行迭代回溯，若前一个指针不存在，则说明已到达起点*/
  class PathPointer<Vert>{
    public PathPointer<Vert> previous;

    public Vert self;

    /**构造一个携带给出参数信息的指针对象*/
    public PathPointer(Vert self){
      this.self = self;
    }

    /**构造一个携带给出参数信息的指针对象*/
    public PathPointer(Vert self, PathPointer<Vert> previous){
      this.previous = previous;
      this.self = self;
    }
  }
}
