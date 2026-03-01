package universecore.util.path;

import java.util.Iterator;
import java.util.LinkedList;

/**基于{@link LinkedList}的通用路径实现，可以满足一般情况的路径信息存储需求*/
public class GenericPath<Vert> implements IPath<Vert>{
  private final LinkedList<Vert> path = new LinkedList<>();

  @Override
  public void addFirst(Vert next){
    path.addFirst(next);
  }

  @Override
  public void addLast(Vert next){
    path.addLast(next);
  }

  @Override
  public Vert origin(){
    return path.getFirst();
  }

  @Override
  public Vert destination(){
    return path.getLast();
  }

  @Override
  public Iterator<Vert> iterator(){
    return path.iterator();
  }
}
