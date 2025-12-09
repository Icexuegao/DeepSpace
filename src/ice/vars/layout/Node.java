package ice.vars.layout;

import arc.struct.ObjectSet;
import arc.struct.Seq;
import ice.vars.researchs.ResearchProject;

public class Node {
  public float x, y;
  public float width, height;

  public final boolean isLineMark;
  public final ResearchProject project;

  public int contextIndex;
  public int contextDepth;

  public Seq<Node> parents = new Seq<>();
  public Seq<Node> children = new Seq<>();

  public Node(ResearchProject project) {
    this.isLineMark = false;
    this.project = project;
  }

  public Node(boolean isLineMark) {
    this.isLineMark = isLineMark;
    this.project = null;
  }

  public float getX(){
    return x + width/2;
  }

  public float getY() {
    return y + height/2;
  }

  public void setX(float x){
    this.x = x - width/2;
  }

  public void setY(float y) {
    this.y = y - height/2;
  }

  public void addChildren(Node node){
    children.add(node);
    node.parents.add(this);
  }

  public void addParent(Node node){
    parents.add(node);
    node.children.add(this);
  }

  public void delink(Node node){
    parents.remove(node);
    children.remove(node);
    node.parents.remove(this);
    node.children.remove(this);
  }

  public void visitTree(NodeAcceptor acceptor) {
    visitTree(0, acceptor);
  }

  public void visitTree(int baseDepth, NodeAcceptor acceptor){
    ObjectSet<Node> visited = new ObjectSet<>();
    visit(visited, baseDepth, acceptor);
  }

  protected void visit(ObjectSet<Node> visited, int depth, NodeAcceptor acceptor) {
    if (visited.add(this)) {
      acceptor.accept(depth, this);
      for (Node child : children) {
        child.visit(visited, depth + 1, acceptor);
      }
    }
  }

  public interface NodeAcceptor{
    void accept(int depth, Node node);
  }
}
