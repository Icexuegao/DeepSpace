package universecore.util.path;

/**保存了路径搜索时使用的回调函数类型，这些函数被用于lambda构造可传递函数
 *
 * @author EBwilson
 * @since 1.3*/
public class PathFindFunc{
  /**路径接收器函数，用于搜索路径时将终点与路径对象传递给函数进行回调*/
  @FunctionalInterface
  public interface PathAcceptor<Vert>{
    /**回调函数入口，接收一个顶点和路径对象，分别表示路径的终点和路径信息*/
    void accept(Vert destination, IPath<Vert> path);
  }

  /**顶点接收器函数，用于搜索路径时将顶点对象传递给函数进行回调*/
  @FunctionalInterface
  public interface VerticesAcceptor<Vert>{
    /**回调函数入口，可以接收一个顶点对象*/
    void accept(Vert vert);
  }
}
