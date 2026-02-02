package universecore.util.path;

/**路径存储器类型的基本接口，实现应当赋予类相应的功能，扩展了可迭代接口，路径应当由起点至终点按顺序遍历所有路径的顶点。
 * <p>通常情况你可以使用基于{@link java.util.LinkedList}实现的{@link GenericPath}提供通用的路径存储器。
 *
 * @see GenericPath
 * @author EBwilson
 * @since 1.3*/
public interface IPath<Vert> extends Iterable<Vert>{
  /**将一个顶点从路径的起点插入并将其作为起点
   *
   * @param next 被添加的顶点*/
  void addFirst(Vert next);

  /**将一个顶点从路径的终点插入并将其作为起点
   *
   * @param next 被添加的顶点*/
  void addLast(Vert next);

  /**获得路径的起点，这应当正确的返回处于迭代顺序首位的节点
   *
   * @return 位于起点位置的顶点*/
  Vert origin();

  /**获得路径的终点，这应当正确的返回处于迭代顺序最后的节点
   *
   * @return 位于末尾位置的节点*/
  Vert destination();
}
